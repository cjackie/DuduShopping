package com.dudu.users;

import com.dudu.common.TestBase;
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
        Token token = SQLTokenManager.getManager().createToken(1);

        println("Id=" + token.getId());
        println("UserId=" + token.getUserId());
        println("Token=" + token.getToken());
        println("RefreshToken=" + token.getRefreshToken());
    }

    @Test
    public void checkToken() throws Exception {
        Assume.assumeTrue(ready);

        String token = "Er3c4vEwq7iU5qAAb5RD0KVEDJjrwi3r8+BmMXsh9dtDGXcET+KIjdxbbHQ246MN/UDOsYc/dK9J97hu";
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
