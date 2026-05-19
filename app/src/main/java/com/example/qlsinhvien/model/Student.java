package com.example.qlsinhvien.model;
import java.io.Serializable;

public class Student implements Serializable {
    private int id;
    private String name, studentCode, phone, email;
    private byte[] image;

    public Student(int id, String name, String studentCode, String phone, String email, byte[] image) {
        this.id = id;
        this.name = name;
        this.studentCode = studentCode;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getStudentCode() { return studentCode; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public byte[] getImage() { return image; }
}