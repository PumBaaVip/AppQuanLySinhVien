package com.example.qlsinhvien.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;

public class EditStudentActivity extends AppCompatActivity {
    // 1. Khai báo thành phần giao diện
    private EditText edtName, edtCode, edtPhone, edtEmail;
    private Button btnSave;

    // 2. Biến phụ trợ
    private int studentId;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        initViews();
        setupDatabase();
        loadStudentData();

        btnSave.setOnClickListener(v -> handleSave());
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtCode = findViewById(R.id.edtCode);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupDatabase() {
        db = new DatabaseHelper(this);
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
    }

    private void loadStudentData() {
        if (studentId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sinh viên!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Student s = db.getStudentById(studentId);
        if (s != null) {
            edtName.setText(s.getName());
            edtCode.setText(s.getStudentCode());
            edtPhone.setText(s.getPhone());
            edtEmail.setText(s.getEmail());
        }
    }

    private void handleSave() {
        hideKeyboard(); // Ẩn bàn phím sau khi bấm Lưu

        String name = edtName.getText().toString().trim();
        String code = edtCode.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // Validation: Kiểm tra dữ liệu rỗng
        if (name.isEmpty() || code.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tên và MSSV", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật Database
        boolean isUpdated = db.updateStudent(studentId, name, code, phone, email, null);

        if (isUpdated) {
            Toast.makeText(this, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình, quay về ChiTietActivity
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm tiện ích để ẩn bàn phím khi thực hiện hành động
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}