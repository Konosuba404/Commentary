package com.example.commentary.sqlDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelp extends SQLiteOpenHelper {
    private static final String DATABASENAME="Commentary.db3";//数据库名称
    private static final int DATABASEVERSION=1;//版本
    private static final String TABLENAME="User";//数据表名称
    //定义构造方法
    public MyDataBaseHelp(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建数据表
        String sql=" CREATE TABLE "+TABLENAME+"("+
                "username  VARCHAR(50)  PRIMARY KEY ,"+
                "password  VARCHAR(50) NOT NULL)";
        //执行SQL语句
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE EXISTS"+TABLENAME;
        db.execSQL(sql);
        this.onCreate(db);
    }

}
