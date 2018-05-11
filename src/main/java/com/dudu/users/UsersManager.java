package com.dudu.users;

import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by chaojiewang on 5/10/18.
 */
public class UsersManager {

    public static final char USER_ROLE_CUSTOMER = 'C';
    public static final char USER_ROLE_SALE_AGENT = 'S';
    public static final String SCOPE_CUSTOMER = "customer";
    public static final String SCOPE_SALE_AGENT = "sale agent";

    private DataSource source;

    public UsersManager(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @param login
     * @param password
     * @param role
     * @return
     * @throws Exception
     */
    public User login(String login, String password, char role) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "UserLogin");
            sp.addParameter("Login", login);
            sp.addParameter("Password", password);
            sp.addParameter("Role", role);
            List<ZetaMap> zmaps = sp.execToZetaMaps();
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Failed to login: " + login);

            int error = zmaps.get(0).getInt("Error");
            if (error != 0)
                throw new IllegalArgumentException("Failed to login: " + login + ". Error=" + error);

            return getUser(login, password, role);
        }
    }

    /**
     *
     * @param login
     * @param password plain password
     * @param role
     * @return
     */
    public User getUser(String login, String password, char role) throws Exception {
        try (Connection conn = source.getConnection()) {
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, "SELECT * FROM Users WHERE Login = ? AND password = ? AND role = ?", login, password, role);
            return User.from(zetaMaps.get(0));
        }
    }

    /**
     * internal use
     * @param userId
     * @return
     * @throws Exception
     */
    public User getUser(long userId) throws Exception {
        try (Connection conn = source.getConnection()) {
            List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, "SELECT * FROM Users WHERE UserId = ?", userId);
            return User.from(zmaps.get(0));
        }
    }

    /**
     *
     * @param login
     * @param password plain password
     * @param role
     * @param scope
     * @param address
     * @return
     * @throws Exception
     */
    public User createUser(String login, String password, char role, String scope, String address) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_CreateUser");
            sp.addParameter("Login", login);
            sp.addParameter("Password", CryptoUtil.sha256base64(password));
            sp.addParameter("Role", role);
            sp.addParameter("Scope", scope);
            sp.addParameter("Address", address != null ? address : "");
            List<ZetaMap> zmaps = sp.execToZetaMaps();
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Failed to create user [" + login + "]");

            ZetaMap zmap = zmaps.get(0);

            long userId = zmap.getLong("UserId");
            return getUser(userId);
        }
    }
}
