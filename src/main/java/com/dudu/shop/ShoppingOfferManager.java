package com.dudu.shop;

import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;
import com.dudu.users.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by chaojiewang on 5/18/18.
 */
public class ShoppingOfferManager {
    public static final String OFFER_PLACED = "SO5";
    public static final String OFFER_SHOPPER_CANCELLED = "SO10";
    public static final String OFFER_PULLED = "SO15";
    public static final String OFFER_SHOPPER_REJECTED = "SO20";
    public static final String OFFER_SHOPPER_ACCEPTED = "SO25";

    private static final Logger logger = LogManager.getLogger(ShoppingOfferManager.class);
    private DataSource source;

    public ShoppingOfferManager(DataSource source) {
        this.source = source;
    }

    /**
     *
     * @param user
     * @param offer shoppingRequestId, text, and price
     * @return
     * @throws Exception
     */
    public ShoppingOffer create(User user, ShoppingOffer offer) throws Exception {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingOfferCreate");

            sp.addParameter("UserId", user.getUserId());
            sp.addParameter("Text", offer.getText());
            sp.addParameter("Price", offer.getPrice());
            sp.addParameter("ShoppingRequestId", offer.getShoppingRequestId());
            ZetaMap zmap = sp.execToZetaMaps().get(0);

            int error = zmap.getInt("Error");
            if (error != 0) {
                logger.error("Failed to create a shopping offer.");
                throw new IllegalArgumentException("Failed to create a shopping offer.");
            }

            return getShoppingOffer(zmap.getInt("ShoppingOfferId"));
        }
    }

    ShoppingOffer getShoppingOffer(long ShoppingOfferId) throws Exception {
        throw new NotImplementedException();
    }
}
