package com.dudu.shop;

import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingMatchSearcher {
    private static final Logger logger = LogManager.getLogger(ShoppingMatchSearcher.class);
    private DataSource source;

    public ShoppingMatchSearcher(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @param userId
     * @return
     */
    public List<ShoppingMatch> searchMatches(long userId) {
       try (Connection conn = source.getConnection()) {
           StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingMatchQuery");
           sp.addParameter("UserId", userId);

           List<ZetaMap> zetaMaps = sp.execToZetaMaps();
           List<ShoppingMatch> matches = new ArrayList<>();
           for (ZetaMap zetaMap : zetaMaps)
                matches.add(ShoppingMatch.from(zetaMap));

           return matches;
       } catch (SQLException e) {
           logger.warn("Failed to search", e);
           return Collections.emptyList();
       }
    }

    /**
     * offers to @requestId
     * @param shoppingRequestId
     * @return
     */
    public List<ShoppingOffer> searchCandidates(long shoppingRequestId) {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM ShoppingOffers WHERE ShoppingRequestId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, shoppingRequestId);

            List<ShoppingOffer> offers = new ArrayList<>();
            for (ZetaMap zetaMap : zetaMaps)
                offers.add(ShoppingOffer.from(zetaMap));

            return offers;
        } catch (Exception e) {
            logger.warn("Failed to shopping offers");
            return Collections.emptyList();
        }
    }
}
