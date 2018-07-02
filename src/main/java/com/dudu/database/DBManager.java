package com.dudu.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * Created by chaojiewang on 7/1/17.
 */
public class DBManager {
    public static final String DATABASE_DUDU_SHOPPING = "DuduShopping";
    private static Logger logger = LogManager.getLogger(DBManager.class);
    private static JedisPool chatRoomRedisPool;

    private static DBManager ourInstance = new DBManager();
    private static Map<String, HikariDataSource> dataSources;

    public static DBManager getManager() {
        return ourInstance;
    }

    private DBManager() {}

    public static void init(Properties properties) throws Exception {
        dataSources = new LinkedHashMap<>();
        String databases = properties.getProperty("databases");
        for (String dbName : databases.split(",")) {
            dbName = dbName.trim();
            // read one database config
            String jdbcUrl = properties.getProperty("databases." + dbName + ".jdbcUrl").trim();
            String driver = properties.getProperty("databases." + dbName + ".driver").trim();
            String username = properties.getProperty("databases." + dbName + ".username").trim();
            String password = properties.getProperty("databases." + dbName + ".password").trim();

            String dbProperties = properties.getProperty("databases." + dbName + ".properties");
            Map<String, String> dbPropertiesMap = new LinkedHashMap<>();
            if (dbProperties != null) {
                for (String dbProp : dbProperties.split(",")) {
                    dbProp = dbProp.trim();
                    String val = properties.getProperty("databases." + dbName + ".properties." + dbProp).trim();
                    dbPropertiesMap.put(dbProp, val);
                }
            }

            // adding to dataSources
            HikariConfig config = new HikariConfig();
            config.setUsername(username);
            config.setPassword(password);
            config.setJdbcUrl(jdbcUrl);
            config.setConnectionTestQuery("SELECT 1");
            config.setDriverClassName(driver);
            for (String prop: dbPropertiesMap.keySet())
                config.addDataSourceProperty(prop, dbPropertiesMap.get(prop));
            HikariDataSource source = new HikariDataSource(config);
            dataSources.put(dbName, source);
        }

        try {
            String host = properties.getProperty("chatroom.redis.host");
            String port = properties.getProperty("chatroom.redis.port");

            chatRoomRedisPool = new JedisPool(host, Integer.parseInt(port));
        } catch (Exception e) {
            logger.error("fail to initialize chat room redis: ", e);
        }

        if (dataSources.get(DATABASE_DUDU_SHOPPING) == null)
            throw new IllegalArgumentException("Missing dudu shopping database");
    }

    public DataSource getDataSource(String dbName) {
        return dataSources.get(dbName);
    }

    public JedisPool getChatRoomRedisPool() {
        return chatRoomRedisPool;
    }

    public void setChatRoomRedisPool(JedisPool chatRoomRedisPool) {
        DBManager.chatRoomRedisPool = chatRoomRedisPool;
    }
}
