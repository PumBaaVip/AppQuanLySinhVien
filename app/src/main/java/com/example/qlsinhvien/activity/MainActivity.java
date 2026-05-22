package com.example.qlsinhvien.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;

public class MainActivity extends AppCompatActivity {
    private Button btnAddStudent, btnListStudent, btnLogout;

    private SharedPreferences sharedPreferences;
    private int userRole = 0; // 0: Admin, 1: Sinh viên
    private int studentId = -1; // ID sinh viên liên kết

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Đọc phiên đăng nhập
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        userRole = sharedPreferences.getInt("USER_ROLE", 0);
        studentId = sharedPreferences.getInt("USER_STUDENT_ID", -1);

        initViews();
        setupListeners();
        applyPermissions();
    }

    private void initViews() {
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnListStudent = findViewById(R.id.btnListStudent);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        // Nút Thêm sinh viên
        btnAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddStudentActivity.class));
        });

        // Nút danh sách / Thông tin cá nhân
        btnListStudent.setOnClickListener(v -> {
            if (userRole == 1) {
                // ĐÃ SỬA: Dùng ChiTietActivity thay vì StudentDetailActivity
                Intent intent = new Intent(MainActivity.this, ChiTietActivity.class);
                intent.putExtra("STUDENT_ID", studentId);
                startActivity(intent);
            } else {
                // Admin xem toàn bộ danh sách
                startActivity(new Intent(MainActivity.this, DanhSachActivity.class));
            }
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void applyPermissions() {
        if (userRole == 1) {
            // Nếu là sinh viên thì ẩn nút thêm và đổi tên nút danh sách
            btnAddStudent.setVisibility(View.GONE);
            btnListStudent.setText("Thông tin cá nhân");
        }
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
        // Xóa sạch session
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}