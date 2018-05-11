package com.dudu.authorization;

import com.dudu.database.DBManager;
import org.junit.Before;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chaojiewang on 5/9/18.
 */
public class TestSQLTokenManager {
    SQLTokenManager manager;

    @Before
    public void setup() {
        try {
            String conf = System.getenv("DB_CONF");
            if (conf == null)
                conf = "./conf/db.conf";

            try (InputStream in = new FileInputStream(conf)) {
                Properties properties = new Properties();
                properties.load(in);

                DBManager.init(properties);
                SQLTokenManager.init(DBManager.getManager().getDataSource("DuduShopping"));
                manager = SQLTokenManager.getManager();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void createToken() throws Exception {
        Token token = manager.createToken("jack");

    }
}
