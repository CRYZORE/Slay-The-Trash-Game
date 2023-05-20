package com.slaythetrash.Models;

public class User {
    private String login, password, record;

    public User(){}

    public User(String login,String password, String record){
        this.login = login;
        this.password = password;
        this.record = record;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRecord() {
        return record;
    }
    public void setRecord(String record) {
        this.record = record;
    }
}

