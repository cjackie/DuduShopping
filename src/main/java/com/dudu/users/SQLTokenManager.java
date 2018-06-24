package com.dudu.users;


import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Instant;
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
        BackgroundService.getInstance().scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
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
    public long checkToken(String token) throws Exception {
        try (Connection conn = source.getConnection()) {
            String select = "SELECT * FROM Tokens WHERE Token = ?";
            List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, select, token);
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Unknown token: Token=" + token);

            Token tokenObj = Token.from(zmaps.get(0));
            if (!isValidToken(tokenObj))
                throw new IllegalArgumentException("Token is not valid: token Id=" + tokenObj.getId());

            return tokenObj.getUserId();
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public Token createToken(long userId) throws Exception {
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
            User user = usersManager.getUser(userId);
            String scope = user.getRawScopes();

            String sql = "INSERT INTO Tokens (UserId, Token, RefreshToken, ExpiresIn, IssuedAt, Scope) VALUES (?,?,?,?,?,?)";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execUpdateToZetaMaps(conn, sql, new String[]{"Id"}, userId, token, refreshToken, expiresIn, issuedAt, scope);

            BigDecimal id = (BigDecimal) zetaMaps.get(0).getObject("Id");
            sql = "SELECT * FROM Tokens WHERE Id = ?";
            zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, id.toBigIntegerExact().longValue());
            Token tokenObj = Token.from(zetaMaps.get(0));
            // cache
            cachedTokens.put(tokenObj.getToken(), tokenObj);

            return tokenObj;
        }
    }

    @Override
    public Token refreshToken(String refreshToken) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Tokens WHERE RefreshToken = ? AND IsValid = 1";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, refreshToken);
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("refresh token for client not found: RefreshToken=" + refreshToken);

            Token oldToken = Token.from(zetaMaps.get(0));
            // check on the refresh token
            Date issuedAt = oldToken.getIssuedAt();

            // check expiration time
            if (issuedAt.toInstant().plusSeconds(oldToken.getExpiresIn()*2).compareTo(Instant.now()) <= 0)
                throw new IllegalArgumentException("refresh token is expired");

            // issue new token
            return createToken(oldToken.getUserId());
        }
    }

    private void invalidate(Token token) throws Exception {
        if (token == null)
            return;

        try (Connection conn = source.getConnection()) {
            int ret = DBHelper.getHelper().execUpdate(conn, "UPDATE Tokens SET IsValid = 0 WHERE Token = ?", token.getToken());
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
}
