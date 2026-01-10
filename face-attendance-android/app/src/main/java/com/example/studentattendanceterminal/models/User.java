package com.example.studentattendanceterminal.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private Long id;
    
    private String username;
    
    @SerializedName("realName")
    private String name;
    
    private Integer roleId;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public Integer getRoleId() { return roleId; }
}

