package com.dudu.shop;

import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;
import com.dudu.users.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Created by chaojiewang on 5/13/18.
 */
public class ShoppingRequestManager {
    private DataSource source;

    public ShoppingRequestManager(DataSource source) {
        this.source = source;
    }

    public int acceptRequest(User user, long shoppingRequestId, long shoppingOfferId) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingRequestAccept");
            sp.addParameter("UserId", user.getUserId());
            sp.addParameter("ShoppingRequestId", shoppingRequestId);
            sp.addParameter("ShoppingOfferId", shoppingOfferId);
            List<ZetaMap> zetaMaps = sp.execToZetaMaps();
            return zetaMaps.get(0).getInt("Error");
        }
    }

    public int cancelRequest(User user, long shoppingRequestId) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingRequestCancel");
            sp.addParameter("UserId", user.getUserId());
            sp.addParameter("ShoppingRequestId", shoppingRequestId);

            List<ZetaMap> zetaMaps = sp.execToZetaMaps();
            return zetaMaps.get(0).getInt("Error");
        }
    }

    public ShoppingRequest createRequest(User user, String text) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingRequestCreate");
            sp.addParameter("UserId", user.getUserId());
            sp.addParameter("Text", text);
            List<ZetaMap> zetaMaps = sp.execToZetaMaps();

            ZetaMap zmap = zetaMaps.get(0);
            long shoppingRequestId = zmap.getLong("ShoppingRequestId");
            int error = zmap.getInt("Error");

            if (error != 0)
                throw new RuntimeException("Failed to create a request");

            return getRequest(shoppingRequestId);
        }
    }

    ShoppingRequest getRequest(long id) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM ShoppingRequests WHERE ShoppingRequestId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, id);
            return ShoppingRequest.from(zetaMaps.get(0));
        }
    }
}
