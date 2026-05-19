package com.example.qlsinhvien.model;

import java.io.Serializable;

public class Student implements Serializable {
    private int id;
    private String name;
    private String studentCode;
    private String phone;
    private String email;
    private byte[] image; // 1. Thêm thuộc tính lưu mảng byte ảnh

    // 2. Cập nhật lại Constructor đầy đủ tham số
    public Student(int id, String name, String studentCode, String phone, String email, byte[] image) {
        this.id = id;
        this.name = name;
        this.studentCode = studentCode;
        this.phone = phone;
        this.email = email;
        this.image = image;
    }

    // 3. Tạo Getter và Setter cho thuộc tính image mới thêm
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    // Các Getter/Setter cũ của bạn giữ nguyên phía dưới...
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStudentCode() { return studentCode; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}