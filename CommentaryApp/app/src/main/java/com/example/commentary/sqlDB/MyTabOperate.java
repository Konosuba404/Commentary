package com.example.commentary.sqlDB;

import android.database.sqlite.SQLiteDatabase;

public class MyTabOperate {
    private static final String TABLENAME = "User";// 数据表名称
    private SQLiteDatabase db = null;// SQLiteDatabase

    public MyTabOperate(SQLiteDatabase db) {
        this.db = db;
    }

    public void insert(String username, String password) {
        String sql = " insert into " + TABLENAME + "(username,password) values ('"
                + username + "','" + password + "')";
        //执行SQL语句
        db.execSQL(sql);
        db.close();
    }

}
