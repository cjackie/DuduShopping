package com.dudu.authorization;


import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import com.dudu.users.User;
import com.dudu.users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class SQLTokenManager implements TokenManager {
    private static Logger logger = LogManager.getLogger(SQLTokenManager.class);
    private static DataSource source;

    private static SQLTokenManager manager = new SQLTokenManager();
    private SQLTokenManager() {}

    public static void init(DataSource source) throws Exception {
        SQLTokenManager.source = source;
    }

    public static SQLTokenManager getManager() {
        return manager;
    }

    ////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isValidToken(String clientId, String secret) throws Exception {
        Token token = getToken(clientId);
        return token != null && token.getToken().equals(secret);
    }

    @Override
    public Token getToken(String clientId) throws Exception {
        if (clientId == null)
            throw new IllegalArgumentException("clientId can't be null");

        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Tokens WHERE IsValid = 1 AND UserId = ? AND DATEADD(SECOND, ExpiresIn, IssuedAt) < SYSDATETIME() ORDER BY DATEADD(SECOND, ExpiresIn, IssuedAt) DESC ";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, clientId);

            if (zetaMaps.size() == 0) {
                logger.warn("no token available");
                throw new IllegalArgumentException("clientId " + clientId + " has no valid token. please create it first");
            }

            if (zetaMaps.size() > 1)
                logger.warn("more than 1 active token to " + clientId);

            return Token.from(zetaMaps.get(0));
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
            Token existingToken = getToken(clientId);
            if (existingToken != null)
                invalidate(existingToken);

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
            return Token.from(zetaMaps.get(0));
        }
    }

    @Override
    public Token refreshToken(String clientId, String refreshToken) throws Exception {
        try (Connection conn = source.getConnection()) {
            Token token = getToken(clientId);
            if (!token.getRefreshToken().equals(refreshToken))
                throw new RuntimeException("Invalid refresh token");

            invalidate(token);
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
}
