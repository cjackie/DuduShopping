package com.dudu.users;

import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

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
            if (!dbReady || source == null)
                return;

            SQLTokenManager.init(source);
            tokenManager = SQLTokenManager.getManager();
            ready = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createToken() throws Exception {
        Assume.assumeTrue(ready);
        Token token = SQLTokenManager.getManager().createToken("1");

        println("Id=" + token.getId());
        println("UserId=" + token.getUserId());
        println("Token=" + token.getToken());
    }

    @Test
    public void getTokens() throws Exception {
        Assume.assumeTrue(ready);
        List<Token> tokens = tokenManager.getTokens("1");
        println(tokens.size());
    }

    @Test
    public void refreshToken() throws Exception {
        Assume.assumeTrue(ready);
        String refreshToken = "8xmTIRb8/Cfv60lMASnphFrE0mO2S2Y0L98wRsS2CjRmpQ4UNBnkTRRBW6mZRmXec6/RblXxqV2DkY/N";
        String userId = "1";
        Token token = tokenManager.refreshToken(userId, refreshToken);
        println("Id=" + token.getId());
        println("UserId=" + token.getUserId());
        println("Token=" + token.getToken());
    }

    @Test
    public void isValidToken() throws Exception {
        Assume.assumeTrue(ready);
        String userId = "1";
        String client = "y6nScPrZMEvbHS3HssTFU7iwMjVO+6fLC/owqXb4plykDaigvxWb7//OmXXMz2JowY9C68ES6Yq4/s45";
        boolean valid = tokenManager.isValidToken(userId, client);
        println(valid);
    }

}
