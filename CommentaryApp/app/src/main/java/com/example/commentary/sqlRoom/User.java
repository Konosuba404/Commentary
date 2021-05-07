package com.example.commentary.sqlRoom;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String username;
    @NonNull
    public String password;

}
