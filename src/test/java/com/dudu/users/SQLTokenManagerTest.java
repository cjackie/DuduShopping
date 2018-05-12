package com.dudu.users;

import com.dudu.database.DBManager;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * Created by chaojiewang on 5/12/18.
 */
public class SQLTokenManagerTest extends TestBase {
    boolean ready;

    @Before
    public void setup() {
        super.setup();

        try {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            if (!dbReady || source == null)
                return;

            SQLTokenManager.init(source);
            ready = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createToken() throws Exception {
        Assume.assumeTrue(ready);
        Token token = SQLTokenManager.getManager().createToken("jack");
    }


}
