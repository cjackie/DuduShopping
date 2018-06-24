package com.dudu.users;

import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ApiEndpointChecker {
    public static ApiEndpointChecker getInstance() {
        return instance;
    }
    private static ApiEndpointChecker instance = new ApiEndpointChecker();
    private static Set<String> apiEndpoints;

    public static void configure(DataSource source) throws Exception {
        try (Connection conn = source.getConnection()){
            String select = "SELECT * FROM ApiEndpoints";
            List<ZetaMap> zmaps = DBHelper.getHelper().execToZetaMaps(conn, select);

            apiEndpoints = new HashSet<>();
            for (ZetaMap zmap : zmaps) {
                String endpoint = zmap.getString("Endpoint");
                String method = zmap.getString("Method");
                String scope = zmap.getString("Scope");
                apiEndpoints.add(key(method, endpoint, scope));
            }
        }
    }

    public static String key(String method, String endpoint, String scope) {
        return method + ";" + endpoint + ";" + scope;
    }

    private ApiEndpointChecker() {}


    public boolean check(String apiEndpoint, String method, String scope) {
        if (apiEndpoints == null)
            throw new IllegalStateException("ApiEndpointChecker is not configured");

        return apiEndpoints.contains(key(apiEndpoint, method, scope));
    }

    public boolean check(String apiEndpoint, String method, List<String> scopes) {
        if (apiEndpoints == null)
            throw new IllegalStateException("ApiEndpointChecker is not configured");

        for (String scope : scopes) {
            if (apiEndpoints.contains(key(apiEndpoint, method, scope)))
                return true;
        }
        return false;
    }
}
