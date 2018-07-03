package com.dudu.users;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

/**
 * Created by chaojiewang on 5/12/18.
 */
public class SQLTokenManagerTest extends TestBase {
    boolean ready;
    TokenManager tokenManager;

    @Before
    public void setup() {
        super.setup();

        try {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            JedisPool jedisPool = DBManager.getManager().getCacheRedisPool();
            if (!dbReady || source == null || jedisPool == null)
                return;

            SQLTokenManager.init(source, jedisPool);
            tokenManager = SQLTokenManager.getManager();
            ready = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createToken() throws Exception {
        Assume.assumeTrue(ready);
        Token token = SQLTokenManager.getManager().createToken(1);

        println("Id=" + token.getId());
        println("UserId=" + token.getUserId());
        println("Token=" + token.getToken());
        println("RefreshToken=" + token.getRefreshToken());
    }

    @Test
    public void checkToken() throws Exception {
        Assume.assumeTrue(ready);

        String token = "pRoBCg9mgOyRiSmGEMR3UaErxjSi00KbnA5+qojcLUA+y7XJFXQiu0kX4Xd+93OuIDAYK6Abge+P7NbN";
        long userId = SQLTokenManager.getManager().checkToken(token);

        println("UserId=" + userId);
    }

    @Test
    public void refreshToken() throws Exception {
        Assume.assumeTrue(ready);
        String refreshToken = "euvEchSi7TN4Pzzd6TMkUfUD8DrOTPPhw+Jwwg8DRWsKMSXQjQ6hAUddBTvs4bX9+f4suhyi23HqIVOW";
        Token token = tokenManager.refreshToken(refreshToken);
        println("Id=" + token.getId());
        println("UserId=" + token.getUserId());
        println("Token=" + token.getToken());
    }

}
