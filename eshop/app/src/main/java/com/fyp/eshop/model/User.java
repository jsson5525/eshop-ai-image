package com.fyp.eshop.model;

public class User {
    private String userid;
    private String email;
    private String family_name;
    private String given_name;
    private String phone_no;
    private String user_type;

    public User() {
    }

    public User(String userid, String email, String family_name, String given_name, String phone_no, String user_type) {
        this.userid = userid;
        this.email = email;
        this.family_name = family_name;
        this.given_name = given_name;
        this.phone_no = phone_no;
        this.user_type = user_type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
