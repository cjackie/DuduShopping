package com.dudu;

import com.dudu.database.DBManager;
import com.dudu.users.ApiEndpointChecker;
import com.dudu.users.SQLTokenManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class WebInit extends HttpServlet {
    private static Logger logger = LogManager.getLogger(WebInit.class);

    @Override
    public void init() {
        logger.info("init");
        try (InputStream in = getServletContext().getResourceAsStream("/WEB-INF/db.conf")) {
            Properties properties = new Properties();
            properties.load(in);

            DBManager.init(properties);

            DataSource duduShoppingSource = DBManager.getManager().getDataSource(DBManager.DATABASE_DUDU_SHOPPING);
            JedisPool cache = DBManager.getManager().getCacheRedisPool();

            SQLTokenManager.init(duduShoppingSource, cache);
            ApiEndpointChecker.configure(duduShoppingSource);

        } catch (Exception e) {
            logger.error("Failed to init the application: ", e);
        }
    }

    @Override
    public void destroy() {

    }
}
