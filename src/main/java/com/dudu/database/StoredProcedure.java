package com.dudu.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by chaojiewang on 1/30/18.
 */
public class StoredProcedure {
    private Connection con;
    private String spName;
    private Map<String, Object> parameters;

    public StoredProcedure(Connection con, String spName) {
        this.con = con;
        this.spName = spName;
        this.parameters = new LinkedHashMap<>();
    }

    public void addParameter(String name, Object value) {
        if (value instanceof Date)
            parameters.put(name, new java.sql.Timestamp(((Date) value).getTime()));
        else if (value instanceof Character)
            parameters.put(name, value.toString());
        else
            parameters.put(name, value);
    }

    public List<ZetaMap> execToZetaMaps() throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("{ call ").append(spName).append("(");
        if (parameters.size() != 0) {
            List<String> keys = new ArrayList<>(parameters.keySet());

            for (int i = 0; i < keys.size(); i++) {
                if (i == keys.size()-1)
                    sql.append("@").append(keys.get(i)).append("=?");
                else
                    sql.append("?,");
            }
        }
        sql.append(")}");

        try (CallableStatement sp = con.prepareCall(sql.toString())) {
            for (String param : parameters.keySet()) {
                Object val = parameters.get(param);
                if (val instanceof Character)
                    val = val.toString();
                sp.setObject(param, val);
            }

            sp.execute();
            ResultSet rs = sp.getResultSet();
            if (rs == null)
                return new ArrayList<>();

            List<ZetaMap> maps = new ArrayList<>();
            while (rs.next())
                maps.add(new ZetaMap(rs));

            return maps;
        }
    }
}
