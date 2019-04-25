package com.yc.orm;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yanchen
 * @ClassName Dao
 * @Date 2019/4/25 15:45
 */
public abstract class Dao {
    public static <T> List<T> select(T condition) {
        Connection conn =null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            // 1.加载驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 2.获取连接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/orm", "root", "123456");

            // 3.获取语句集
            String sql = parseSql(condition);
            statement = conn.prepareStatement(sql);

            // 4.得到结果集
            rs = statement.executeQuery();

            // 5.解析并返回结果集
            return parseResult(condition, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 6.关闭资源
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static <T> String parseSql(T condition) throws Exception {
        Class<T> clazz = (Class<T>) condition.getClass();
        String tableName = clazz.getSimpleName();
        if(clazz.isAnnotationPresent(Table.class)) {
            tableName = clazz.getAnnotation(Table.class).name();
        }

        StringBuffer sqlSB = new StringBuffer("select * from " + tableName + " where 1 = 1 ");

        for (Field field : clazz.getDeclaredFields()) {
            String columnName = field.getName();
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(condition);
            } catch (Exception e) {}

            if (fieldValue == null) continue;

            if(field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            }

            sqlSB.append(" and " + columnName + " = " + fieldValue);

        }
        System.out.println(sqlSB.toString());

        return sqlSB.toString();
    }

    private static <T> List<T> parseResult(T condition, ResultSet rs) throws Exception {
        Map<String, String> columnFieldMap = parseFieldColumnMap(condition);
        List<T> result = new ArrayList<T>();

        while (rs.next()) {
            Class<T> clazz = (Class<T>) condition.getClass();
            T o = clazz.newInstance();

            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnName(i);

                Field field = clazz.getDeclaredField(columnFieldMap.get(columnName));
                field.setAccessible(true);
                field.set(o, rs.getObject(columnName));

            }
            result.add(o);

        }

        return result;
    }

    private static <T> Map<String, String> parseFieldColumnMap(T condition) {
        Map<String, String> fieldColumnMap = new HashMap<String, String>(64);
        Class<T> clazz = (Class<T>) condition.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            fieldColumnMap.put(field.getName(), field.getName());

            if (field.isAnnotationPresent(Column.class))
                fieldColumnMap.put(field.getAnnotation(Column.class).name(), field.getName());
        }

        return fieldColumnMap;
    }
}
