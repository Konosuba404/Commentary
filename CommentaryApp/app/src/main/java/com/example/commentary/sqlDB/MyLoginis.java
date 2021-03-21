package com.example.commentary.sqlDB;

//存储在本地实现状态保存
public class MyLoginis {
    private static boolean statue;
    private static String username;
    public MyLoginis(boolean status, String username){
        this.statue = status;
        this.username = username;
    }
    public static boolean getStatue(){
        return statue;
    }

    public static String getUsername() {
        return username;
    }
}
