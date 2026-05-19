package com.example.qlsinhvien.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;

public class MainActivity extends AppCompatActivity {
    // 1. Khai báo nút
    private Button btnAddStudent, btnListStudent, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnListStudent = findViewById(R.id.btnListStudent);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        // Chuyển sang form Thêm sinh viên
        btnAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddStudentActivity.class));
        });

        // Chuyển sang form Danh sách sinh viên
        btnListStudent.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DanhSachActivity.class));
        });

        // Xử lý Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn thoát khỏi ứng dụng?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        // 1. Xóa trạng thái đăng nhập
        SharedPreferences pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        pref.edit().clear().apply();

        // 2. Quay về màn hình Login và xóa sạch lịch sử
        Intent intent = new Intent(this, LoginActivity.class);
        // Cờ này giúp người dùng không thể nhấn Back để quay lại app sau khi đăng xuất
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}