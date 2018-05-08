package com.dudu.authorization;

import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by chaojiewang on 5/6/18.
 */
public class AuthServer {
    public static final String USER_TYPE_CUSTOMERS = "customers";
    public static final String USER_TYPE_SALE_AGENTS = "sale agent";

    private DataSource source;


    public AuthServer(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @return
     */
    public Token authenticate(String userType, String username, String password) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql;
            if (userType.equals(USER_TYPE_CUSTOMERS))
                sql = "SELECT * FROM Customers WHERE Login = ? AND password = ?";
            else
                sql = "SELECT * FROM SaleAgents WHERE Login = ? AND password = ?";
            List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, sql, username, CryptoUtil.sha256base64(password));
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Authenticate " + username + " failed");

            Token token = getDefaultTokenManager().createToken(username);
            if (token == null)
                throw new IllegalArgumentException("Failed to create token");

            return token;
        }
    }

    protected TokenManager getDefaultTokenManager() {
        return SQLTokenManager.getManager();
    }
}
