package com.dudu.token;


import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class SQLTokenManager implements TokenManager {
    private static Logger logger = LogManager.getLogger(SQLTokenManager.class);
    private static DataSource source;

    private static SQLTokenManager manager = new SQLTokenManager();
    private SQLTokenManager() {}

    public static void init(DataSource source) throws Exception {
        SQLTokenManager.source = source;
    }

    public static SQLTokenManager getManager() {
        return manager;
    }

    ////////////////////////////////////////////////////////////////////////////
    private ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<>();

    @Override
    public boolean isValidToken(String clientId, String secret) {
        Token token = getToken(clientId);
        return token != null && token.getSecret().equals(secret);
    }

    @Override
    public Token getToken(String clientId) {
        Token token = tokens.get(clientId);

        if (token != null) {
            // check if token expires or not
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(token.getCreatedOn().getTime());
            calendar.add(Calendar.SECOND, token.getGoodFor());
            if (calendar.getTime().after(new Date()))
                return token;
        }

        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Tokens WHERE Login = ? AND DATEADD(SECOND, GoodFor, CreatedOn) < SYSDATETIME() ORDER BY DATEADD(SECOND, GoodFor, CreatedOn) DESC ";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, clientId);
            if (zetaMaps.size() == 0)
                return null;

            ZetaMap zmap = zetaMaps.get(0);
            token = Token.from(zmap);
            tokens.put(clientId, token);
            return token;
        } catch (SQLException e) {
            logger.warn(e);
            return null;
        }
    }

    @Override
    public Token createToken(String clientId) {
        try (Connection conn = source.getConnection()) {

            Random random = new Random();
            byte bytes[] = new byte[60];
            random.nextBytes(bytes);
            String secret = CryptoUtil.base64(bytes);
            Date createdOn = new Date();
            int goodFor = 2*60*60; // two hours


            String sql = "INSERT INTO Tokens (DuduCustomer, Secret, CreatedOn, GoodFor) VALUES (?,?,?,?)";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execUpdateToZetaMaps(conn, sql, new String[]{"Id"}, clientId, secret, createdOn, goodFor);
            if (zetaMaps.size() == 0)
                return null;

            int id = zetaMaps.get(0).getInt("Id");
            sql = "SELECT * FROM Tokens WHERE Id = ?";
            zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, id);
            if (zetaMaps.size() == 0)
                return null;

            return Token.from(zetaMaps.get(0));
        } catch (Exception e) {
            logger.warn(e);
            return null;
        }
    }
}
