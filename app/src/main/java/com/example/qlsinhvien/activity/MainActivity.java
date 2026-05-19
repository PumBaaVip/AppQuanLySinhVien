package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;

public class MainActivity extends AppCompatActivity {
    Button btnAddStudent;
    Button btnDanhSachSinhVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Ánh xạ
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnDanhSachSinhVien = findViewById(R.id.btnListStudent);
        // Chuyển form thêm sinh viên
        btnAddStudent.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(MainActivity.this,
                                    AddStudentActivity.class);

                    startActivity(intent);
                });

        Button btnList = findViewById(R.id.btnListStudent); // ID nút của bạn
        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DanhSachActivity.class);
            startActivity(intent);
        });



    }
}