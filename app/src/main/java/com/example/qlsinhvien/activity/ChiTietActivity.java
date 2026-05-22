package com.example.qlsinhvien.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;

public class ChiTietActivity extends AppCompatActivity {
    private TextView txtTen, txtMSSV, txtSDT, txtEmail;
    private ImageView imgProfile;
    private Button btnEdit, btnDelete;
    private DatabaseHelper db;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        // 1. Ánh xạ View
        txtTen = findViewById(R.id.txtChiTietTen);
        txtMSSV = findViewById(R.id.txtChiTietMSSV);
        txtSDT = findViewById(R.id.txtChiTietSDT);
        txtEmail = findViewById(R.id.txtChiTietEmail);
        imgProfile = findViewById(R.id.imgProfile);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DatabaseHelper(this);

        // 2. Nhận dữ liệu ID từ Activity trước gửi sang
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);

        if (studentId != -1) {
            loadStudentData();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy mã sinh viên!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 3. Cấu hình phân quyền (Ẩn nút nếu là Sinh viên)
        applyPermissions();

        // 4. Xử lý sự kiện
        btnDelete.setOnClickListener(v -> showDeleteDialog());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietActivity.this, EditStudentActivity.class);
            intent.putExtra("STUDENT_ID", studentId);
            startActivity(intent);
        });
    }

    private void applyPermissions() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        int role = prefs.getInt("USER_ROLE", 0); // 0 là Admin, 1 là Sinh viên

        if (role == 1) {
            // Ẩn nút Sửa và Xóa đối với sinh viên
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void loadStudentData() {
        Student student = db.getStudentById(studentId);
        if (student != null) {
            txtTen.setText("Tên: " + student.getName());
            txtMSSV.setText("MSSV: " + student.getStudentCode());
            txtSDT.setText("SĐT: " + student.getPhone());
            txtEmail.setText("Email: " + student.getEmail());

            if (student.getImage() != null && student.getImage().length > 0) {
                imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(student.getImage(), 0, student.getImage().length));
            }
        }
    }

    private void showDeleteDialog() {
        // Kiểm tra an toàn trước khi xóa
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (prefs.getInt("USER_ROLE", 0) == 1) {
            Toast.makeText(this, "Bạn không có quyền thực hiện chức năng này!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (db.deleteStudent(studentId)) {
                        Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData();
    }
}