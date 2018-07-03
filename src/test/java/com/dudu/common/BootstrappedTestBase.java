package com.dudu.common;

import com.dudu.database.DBManager;
import com.dudu.users.ApiEndpointChecker;
import com.dudu.users.SQLTokenManager;
import org.junit.Before;

import javax.sql.DataSource;

public class BootstrappedTestBase extends TestBase {
    protected String baseUrl;

    @Before
    public void setup() {
        super.setup();
        if (ready) {
            try {
                DBManager.init(properties);

                DataSource duduShoppingSource = DBManager.getManager().getDataSource(DBManager.DATABASE_DUDU_SHOPPING);
                SQLTokenManager.init(duduShoppingSource, DBManager.getManager().getCacheRedisPool());
                ApiEndpointChecker.configure(duduShoppingSource);

            } catch (Exception e) {
                ready = false;
                println(e.toString());
            }
        }
    }

}
