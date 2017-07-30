package org.wltea.analyzer.db;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wky77 on 2017/7/30.
 */
public class DBHelper {
    public static final Logger logger = Loggers.getLogger(DBHelper.class);

    public static String dbUrl = "";
    public static String userName = "";
    public static String userPwd = "";
    private Connection conn;
    public static Map<String, Date> lastImportTimeMap = new HashMap<>();

    private Connection getConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, userName, userPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public String getKey(String key) {
        conn = getConn();
        StringBuilder data = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("select * from t_ikdic where 1=1 ");
            Date lastImportTime = DBHelper.lastImportTimeMap.get(key);
            if (lastImportTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sql.append(" and updatetime > '" + sdf.format(lastImportTime) + "'");
            }
            sql.append(" and " + key + " != ''");
            lastImportTimeMap.put(key, new Date());
            System.out.println(sql.toString());
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String value = rs.getString(key);
                if (value != null && !"".equals(value)) {
                    data.append(value + ",");
                }
            }
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return data.toString();

    }
}
