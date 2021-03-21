package com.example.commentary.utils;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 链接数据库的工具类
 * 用于将云端数据库中的marker经纬度查询出来
 * */
public class DBUtils {

    static final String DRIVER = "com.mysql.jdbc.Driver";
    static final String user = "root";
    static final String password = "944343631Hyb@";
//    static final String DB_URL = "jdbc:mysql://101.200.192.103:3306/Commentary?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_URL = "jdbc:mysql://101.200.192.103:3306/Commentary";
    //打开链接
    private static Connection getConn(){
        Connection connection = null;
        try{
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(DB_URL, user, password);
            Log.e("数据库连接", "getConn: 成功 !");
        } catch (ClassNotFoundException | SQLException e) {
            Log.e("数据库连接", "getConn: 失败 !");
            e.printStackTrace();
        }
        return connection;
    }

    //获取marker经纬度
    public static ArrayList<HashMap<String, Object>> getData(){
        ArrayList<HashMap<String, Object>> data_list = new ArrayList<>();
        Connection connection = getConn();
        try{
            String sql = "select longitude, latitude, address from Address";
            if ( connection != null){
                PreparedStatement ps = connection.prepareStatement(sql);
//                java.sql.Statement stmt = connection.createStatement();
                if (ps != null){
                    ResultSet rs = ps.executeQuery(sql);
                    Log.i("数据库查询", "getData: 成功");
                    while (rs.next()){
                        HashMap<String, Object> data_map = new HashMap<>();
                        data_map.put("lng", rs.getFloat("longitude"));
                        data_map.put("lat", rs.getFloat("latitude"));
                        data_map.put("address", rs.getString("address"));
                        data_list.add(data_map);
                    }
                    Log.i("yingyingying", "getData:数据执行成功 ");
                }
                ps.close();
                connection.close();
                return data_list;
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //查询用户是否在数据库中
    public static String loginUser(String username, String password) {
        Connection connection = getConn();
        try{
            String sql = "select password from User where username=" + username;
            if (connection != null){
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet != null && password.equals(resultSet.getString(1))){
                    statement.close();
                    connection.close();
                    return "登录成功";
                }else if (resultSet != null && !password.equals(resultSet.getString(1))){
                    statement.close();
                    connection.close();
                    return "密码错误";
                }else {
                    sql = "insert into User VALUES " + username + ", " + password;
                    statement.executeQuery(sql);
                    statement.close();
                    connection.close();
                    return "注册成功";
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "异常";
    }

    //将新注册的用户写入数据库中
    public static String registerUser(String username, String password) {
        Connection connection = getConn();
        try{
            String sql = "insert into User VALUES " + username + ", " + password;
            if (connection != null){
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.executeQuery(sql);
                statement.close();
                connection.close();
                return "注册成功";
                }
            } catch (SQLException e) {
            e.printStackTrace();
        }
        return "异常";
    }

}
