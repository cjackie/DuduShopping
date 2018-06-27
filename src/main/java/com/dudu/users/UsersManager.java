package com.dudu.users;

import com.dudu.cache.Cache;
import com.dudu.cache.FifoCache;
import com.dudu.common.CryptoUtil;
import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 *
 * Created by chaojiewang on 5/10/18.
 */
public class UsersManager {

    public static final char USER_ROLE_CUSTOMER = 'C';
    public static final char USER_ROLE_SALE_AGENT = 'S';
    public static final String SCOPE_CUSTOMER = "customer";
    public static final String SCOPE_SALE_AGENT = "sale agent";
    private static final String SALT = "pom^bc&yjena!~sixdb42*)sjd";
    private static Logger logger = LogManager.getLogger(UsersManager.class);

    // proximally 1MB in size at peak
    private static Cache<User> usersCache = new FifoCache<>(10000);

    private DataSource source;

    public UsersManager(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    public User login(String login, String password) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_UserLogin");
            sp.addParameter("Login", login);
            sp.addParameter("Password", saltedHash(password));
            List<ZetaMap> zmaps = sp.execToZetaMaps();
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Failed to login: " + login);

            int error = zmaps.get(0).getInt("Error");
            if (error != 0)
                throw new IllegalArgumentException("Failed to login: " + login + ". Error=" + error);

            return getUser(login, password);
        }
    }

    /**
     *
     * @param login
     * @param password plain password
     * @return
     */
    public User getUser(String login, String password) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Users WHERE Login = ? AND password = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, login, saltedHash(password));
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
        // check cache.
        User user = usersCache.get(String.valueOf(userId));
        if (user != null) {
            return user;
        }

        try (Connection conn = source.getConnection()) {
            List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, "SELECT * FROM Users WHERE UserId = ?", userId);
            user = User.from(zmaps.get(0));
            usersCache.cache(String.valueOf(user.getUserId()), user);
            return user;
        }
    }

    /**
     *
     * @param login
     * @param password plain password
     * @param role
     * @param scopes
     * @param address
     * @return
     * @throws Exception
     */
    public User createUser(String login, String password, char role, String scopes, String address) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_CreateUser");
            sp.addParameter("Login", login);
            sp.addParameter("Password", saltedHash(password));
            sp.addParameter("Role", role);
            sp.addParameter("Scopes", scopes);
            sp.addParameter("Address", address != null ? address : "");
            List<ZetaMap> zmaps = sp.execToZetaMaps();
            if (zmaps.size() == 0)
                throw new IllegalArgumentException("Failed to create user [" + login + "]");

            ZetaMap zmap = zmaps.get(0);

            long userId = zmap.getLong("UserId");
            return getUser(userId);
        }
    }

    private String saltedHash(String password) {
        return CryptoUtil.sha256base64(password + SALT);
    }
}
