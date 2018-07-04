package com.dudu.users;


import com.dudu.common.CryptoUtil;
import com.dudu.common.RedisConstants;
import com.dudu.common.StandardObjectMapper;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Instant;
import java.util.*;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class SQLTokenManager implements TokenManager  {
    private static Logger logger = LogManager.getLogger(SQLTokenManager.class);
    private static final String TOKEN_OBJECT_BY_TOKEN = RedisConstants.CACHE_TOKEN_OBJECTS +"by_token_str/";
    private static int cacheTimeout; // in unit seconds.
    private static ObjectMapper objectMapper;
    private static DataSource source;
    private static JedisPool jedisPool;
    private static SQLTokenManager manager = new SQLTokenManager();
    private SQLTokenManager() { }

    public static void init(DataSource source, JedisPool jedisPool) throws Exception {
        SQLTokenManager.source = source;
        SQLTokenManager.jedisPool = jedisPool;
        objectMapper = StandardObjectMapper.getInstance();
        cacheTimeout = 60*60;
    }

    public static SQLTokenManager getManager() {
        return manager;
    }

    ////////////////////////////////////////////////////////////////////////////

    @Override
    public long checkToken(String token) throws Exception {

        try (Jedis jedis = jedisPool.getResource(); Connection conn = source.getConnection()) {
            // check cache
            String cache = jedis.get(TOKEN_OBJECT_BY_TOKEN + token);

            Token tokenObj;
            if (cache != null) {
                tokenObj = objectMapper.readValue(cache, Token.class);
            } else {
                // hit miss. read from database
                String select = "SELECT * FROM Tokens WHERE Token = ?";
                List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, select, token);
                if (zmaps.size() == 0)
                    throw new IllegalArgumentException("Unknown token: Token=" + token);

                tokenObj = Token.from(zmaps.get(0));

                // cache it
                jedis.set(TOKEN_OBJECT_BY_TOKEN + token, objectMapper.writeValueAsString(tokenObj));
                jedis.expire(TOKEN_OBJECT_BY_TOKEN + token, cacheTimeout);
            }

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

            UsersManager usersManager = new UsersManager(source, jedisPool);
            User user = usersManager.getUser(userId);
            String scope = user.getRawScopes();

            String sql = "INSERT INTO Tokens (UserId, Token, RefreshToken, ExpiresIn, IssuedAt, Scope) VALUES (?,?,?,?,?,?)";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execUpdateToZetaMaps(conn, sql, new String[]{"Id"}, userId, token, refreshToken, expiresIn, issuedAt, scope);

            BigDecimal id = (BigDecimal) zetaMaps.get(0).getObject("Id");
            sql = "SELECT * FROM Tokens WHERE Id = ?";
            zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, id.toBigIntegerExact().longValue());
            return Token.from(zetaMaps.get(0));
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
