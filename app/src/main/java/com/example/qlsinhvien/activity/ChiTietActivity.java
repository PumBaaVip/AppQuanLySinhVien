package com.example.qlsinhvien.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    private Button btnEdit, btnDelete; // Thêm biến nút
    private DatabaseHelper db;
    private int studentId; // Lưu lại ID để dùng cho Sửa/Xóa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        // 1. Ánh xạ
        txtTen = findViewById(R.id.txtChiTietTen);
        txtMSSV = findViewById(R.id.txtChiTietMSSV);
        txtSDT = findViewById(R.id.txtChiTietSDT);
        txtEmail = findViewById(R.id.txtChiTietEmail);
        imgProfile = findViewById(R.id.imgProfile);
        btnEdit = findViewById(R.id.btnEdit);     // Cần khai báo trong XML
        btnDelete = findViewById(R.id.btnDelete); // Cần khai báo trong XML

        db = new DatabaseHelper(this);

        // 2. NHẬN DỮ LIỆU
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);

        if (studentId != -1) {
            loadStudentData();
        } else {
            Toast.makeText(this, "Lỗi ID sinh viên!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 3. XỬ LÝ SỰ KIỆN NÚT
        btnDelete.setOnClickListener(v -> showDeleteDialog());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ChiTietActivity.this, EditStudentActivity.class);
            intent.putExtra("STUDENT_ID", studentId);
            startActivity(intent);
        });
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
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Hàm hiển thị hộp thoại xác nhận xóa
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (db.deleteStudent(studentId)) {
                        Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Quay về màn hình danh sách
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Cập nhật lại dữ liệu khi quay lại từ màn hình Sửa
    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData();
    }
}