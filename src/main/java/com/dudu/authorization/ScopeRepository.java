package com.dudu.authorization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaojiewang on 5/7/18.
 */
public class ScopeRepository {
    public static final String SCOPE_CUSTOMER = "customer";
    public static final String SCOPE_SALE_AGENT = "sale agent";

    public static ScopeRepository getInstance() {
        return instance;
    }

    private static final ScopeRepository instance = new ScopeRepository();
    private ScopeRepository() {}

    /////////////////////////////////////////
    public String scope(String userId) {
        List<String> scope = new ArrayList<String>();
        switch (userId.substring(0, 2)) {
            case DatabaseConstant.USER_ID_PREFIX_CUSTOMER:
                scope.add(SCOPE_CUSTOMER);
                break;

            case DatabaseConstant.USER_ID_PREFIX_SALE_AGENT:
                scope.add(SCOPE_SALE_AGENT);
                break;

            default:
                return "";
        }

        return String.join(",", scope);
    }

}
