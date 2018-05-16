package com.dudu.common;

import com.dudu.database.DBManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chaojiewang on 5/12/18.
 */
public class TestBase {

    protected boolean dbReady;
    public void setup() {
        try {
            String conf = System.getenv("DB_CONF");
            if (conf == null)
                conf = "./conf/db.conf";

            try (InputStream in = new FileInputStream(conf)) {
                Properties properties = new Properties();
                properties.load(in);

                DBManager.init(properties);
                dbReady = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void println(Object o) {
        System.out.println(o);
    }
}
