package com.utc.giaodich.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role; // 'BUYER' hoặc 'SELLER'

    public User() {}

    public User(int id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Huân hãy dùng phím tắt Cmd + N (trên Mac) để tự tạo Getter và Setter cho các trường trên nhé
    // Đừng gõ tay cho phí sức của một Master!


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }
}