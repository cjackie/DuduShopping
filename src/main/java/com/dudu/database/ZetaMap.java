package com.dudu.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by chaojiewang on 1/29/18.
 */
public class ZetaMap extends LinkedHashMap<String, Object> {
    private static final Logger logger = LogManager.getLogger(ZetaMap.class);

    public ZetaMap() {}

    public ZetaMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String columnName = meta.getColumnName(i);
            put(columnName, rs.getObject(columnName));
        }
    }

    public String getString(String col, String defaultVal) {
        if (!containsKey(col) || !(get(col) instanceof String)) {
            logger.warn("column " + col + " of type " + String.class.getName() + " not found.");
            return defaultVal;
        }

        return (String) get(col);
    }

    public String getString(String col) {
        return getString(col, "");
    }

    public int getInt(String col, int defaultVal) {
        if (!containsKey(col) || !(get(col) instanceof Integer)) {
            logger.warn("column " + col + " of type " + Integer.class.getName() + " not found.");
            return defaultVal;
        }

        return (Integer) get(col);
    }

    public int getInt(String col) {
        return getInt(col, 0);
    }

    public long getLong(String col, long defaultVal) {
        if (!containsKey(col) || !(get(col) instanceof Long)) {
            logger.warn("column " + col + " of type " + Long.class.getName() + " not found.");
            return defaultVal;
        }

        return (Long) get(col);
    }

    public long getLong(String col) {
        return getLong(col, 0);
    }

    public double getDouble(String col, double defaultVal) {
        if (!containsKey(col) || !(get(col) instanceof Double)) {
            logger.warn("column " + col + " of type " + Double.class.getName() + " not found.");
            return defaultVal;
        }

        return (Double) get(col);
    }

    public double getDouble(String col) {
        return getDouble(col, 0);
    }

    public Date getDate(String col, Date defaultVal) {
        if (!containsKey(col) || !(get(col) instanceof Date)) {
            logger.warn("column " + col + " of type " + String.class.getName() + " not found.");
            return defaultVal;
        }

        return (Date) get(col);
    }


    public Date getDate(String col) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);

        return getDate(col, calendar.getTime());
    }

}
