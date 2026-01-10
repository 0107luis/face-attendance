package com.facial.recognition.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Table(name="User")
@Entity
public class User {
    @Id        //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    @JsonProperty("userId")
    private Integer UserID;

    @Column(name = "Username")
    @JsonProperty("username")
    private String userName;       //用户名

    @Column(name = "Password")
    private String password;

    @Column(name = "RealName")
    private String realName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Email")
    private String email;

    @Column(name = "RoleID")
    @JsonProperty("roleId")
    private Integer RoleID;
    
    @Column(name = "IsActive")
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @Column(name = "CreatedDate")
    @JsonProperty("createdDate")
    private java.time.LocalDateTime createdDate;
    
    //get,set,tostring方法的实现
    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer userID) {
        UserID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRoleID() {
        return RoleID;
    }

    public void setRoleID(Integer roleID) {
        RoleID = roleID;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public java.time.LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(java.time.LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "UserID=" + UserID +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", realName='" + realName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", RoleID=" + RoleID +
                ", isActive=" + isActive +
                ", createdDate=" + createdDate +
                '}';
    }
}

