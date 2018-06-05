package com.dudu.shop;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

public class ShoppingMatchSearcherTest extends TestBase {

    ShoppingMatchSearcher searcher;

    @Before
    public void setup() {
        super.setup();

        if (dbReady) {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            searcher = new ShoppingMatchSearcher(source);
        }
    }

    @Test
    public void searchMatches() throws Exception {
        Assume.assumeTrue(searcher != null);

        long userId = 1;
        List<ShoppingMatch> matches = searcher.searchMatches(1);
        println(matches.size());
    }

    @Test
    public void searchCandidates() throws Exception {
        Assume.assumeTrue(searcher != null);

        long requestId = 1;
        List<ShoppingOffer> offers = searcher.searchCandidates(requestId);
        println(offers.size());
    }
}
