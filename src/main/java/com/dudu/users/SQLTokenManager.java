package com.dudu.users;


import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class SQLTokenManager implements TokenManager, Runnable  {
    private static Logger logger = LogManager.getLogger(SQLTokenManager.class);
    private static DataSource source;
    private static SQLTokenManager manager = new SQLTokenManager();
    private SQLTokenManager() {
        BackgroundService.getInstance().scheduleAtFixedRate(this, 0, 5000, TimeUnit.MILLISECONDS);
    }

    public static void init(DataSource source) throws Exception {
        SQLTokenManager.source = source;
    }

    public static SQLTokenManager getManager() {
        return manager;
    }

    ////////////////////////////////////////////////////////////////////////////
    private ConcurrentHashMap<String, Token> cachedTokens = new ConcurrentHashMap<>();

    @Override
    public boolean isValidToken(String clientId, String secret) throws Exception {

        // check cache
        Token token = cachedTokens.get(tokenKey(clientId, secret));

        if (token != null)
            return isValidToken(token);

        // no found, check on database
        String sql = "SELECT * FROM Tokens WHERE UserId = ? AND Token = ?";
        try (Connection conn = source.getConnection()) {
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, clientId, secret);
            if (zetaMaps.size() == 0)
                return false;

            token = Token.from(zetaMaps.get(0));
            return isValidToken(token);
        }
    }

    @Override
    public List<Token> getTokens(String clientId) throws Exception {
        if (clientId == null)
            throw new IllegalArgumentException("clientId can't be null");

        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Tokens WHERE IsValid = 1 AND UserId = ? AND DATEADD(SECOND, ExpiresIn, IssuedAt) < SYSDATETIME() ORDER BY DATEADD(SECOND, ExpiresIn, IssuedAt) DESC ";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, clientId);

            if (zetaMaps.size() == 0) {
                logger.warn("no token available");
                return new ArrayList<>();
            }

            List<Token> tokens = new ArrayList<>();
            for (ZetaMap zmap : zetaMaps)
                tokens.add(Token.from(zmap));

            return tokens;
        }
    }

    /**
     *
     * @param clientId
     * @return
     */
    @Override
    public Token createToken(String clientId) throws Exception {
        try (Connection conn = source.getConnection()) {
            Random random = new Random();
            byte bytes[] = new byte[60];
            random.nextBytes(bytes);
            String token = CryptoUtil.base64(bytes);
            random.nextBytes(bytes);
            String refreshToken = CryptoUtil.base64(bytes);
            Date issuedAt = new Date();
            int expiresIn = 60*60; // two hours

            UsersManager usersManager = new UsersManager(source);
            User user = usersManager.getUser(Long.parseLong(clientId));
            String scope = user.getScope();

            String sql = "INSERT INTO Tokens (UserId, Token, RefreshToken, ExpiresIn, IssueAt, Scope) VALUES (?,?,?,?,?,?)";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execUpdateToZetaMaps(conn, sql, new String[]{"Id"}, clientId, token, refreshToken, expiresIn, issuedAt, scope);

            int id = zetaMaps.get(0).getInt("Id");
            sql = "SELECT * FROM Tokens WHERE Id = ?";
            zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, id);
            Token tokenObj = Token.from(zetaMaps.get(0));
            // cache
            cachedTokens.put(tokenKey(clientId, tokenObj.getToken()), tokenObj);

            return tokenObj;
        }
    }

    @Override
    public Token refreshToken(String clientId, String refreshToken) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Tokens WHERE UserId = ? AND RefreshToken = ? AND IsValid = 1";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, clientId, refreshToken);
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("refresh token for client not found: " + clientId);

            Token oldToken = Token.from(zetaMaps.get(0));
            // check on the refresh token
            Date issueAt = oldToken.getIssuedAt();
            if (issueAt.toInstant().plusSeconds(oldToken.getExpiresIn()*2).compareTo(Instant.now()) <= 0)
                throw new IllegalArgumentException("refresh token is expired");

            // new token
            return createToken(clientId);
        }
    }

    private void invalidate(Token token) throws Exception {
        if (token == null)
            return;

        try (Connection conn = source.getConnection()) {
            int ret = DBHelper.getHelper().execUpdate(conn, "UPDATE Tokens SET IsValid = 0 WHERE Token = ? AND UserId", token.getToken(), token.getUserId());
            if (ret != 1)
                throw new IllegalArgumentException("Failed to invalidate token " + token);
        }
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        List<String> toBeDeleted = new ArrayList<>();
        for (String key : cachedTokens.keySet()) {
            Token token = cachedTokens.get(key);
            if (!isValidToken(token))
                toBeDeleted.add(key);
        }

        for (String key : toBeDeleted) {
            cachedTokens.remove(key);
        }
        logger.info("Took " + (System.currentTimeMillis() - now) + " ms to clean up tokens");
    }

    private boolean isValidToken(Token token) {
        if (token != null) {
            // check on expiration time
            Date date = token.getIssuedAt();
            Instant expireAt = date.toInstant().plusSeconds(token.getExpiresIn());
            return expireAt.compareTo(Instant.now()) > 0;
        } else
            return false;
    }

    private String tokenKey(String clientId, String secret) {
        return clientId + ":" + secret;
    }
}
