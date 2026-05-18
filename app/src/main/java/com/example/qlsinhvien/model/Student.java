package com.example.qlsinhvien.model;

public class Student {
    private int id;
    private String name;
    private String code;
    private String phone;
    private String email;

    public Student(int id, String name, String code, String phone, String email) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}