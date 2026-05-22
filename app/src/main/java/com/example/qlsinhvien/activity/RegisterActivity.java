package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtConfirmPassword, edtLinkedStudentId;
    RadioGroup rgRole;
    RadioButton rbStudent;
    Button btnRegister;
    TextView txtBackLogin;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseHelper = new DatabaseHelper(this);

        initViews();

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            // Xử lý quyền và ID
            int role = (rgRole.getCheckedRadioButtonId() == R.id.rbStudent) ? 1 : 0;
            int linkedId = -1;

            // Nếu là sinh viên, bắt buộc nhập ID sinh viên để liên kết
            if (role == 1) {
                String idStr = edtLinkedStudentId.getText().toString().trim();
                if (idStr.isEmpty()) {
                    edtLinkedStudentId.setError("Vui lòng nhập ID sinh viên của bạn");
                    return;
                }
                linkedId = Integer.parseInt(idStr);
            }

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm registerUser đã cập nhật tham số
            boolean result = databaseHelper.registerUser(username, password, role, linkedId);

            if (result) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        });

        txtBackLogin.setOnClickListener(v -> finish());
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtLinkedStudentId = findViewById(R.id.edtLinkedStudentId); // Cần thêm vào XML
        rgRole = findViewById(R.id.rgRole);                         // Cần thêm vào XML
        rbStudent = findViewById(R.id.rbStudent);                   // Cần thêm vào XML
        btnRegister = findViewById(R.id.btnRegister);
        txtBackLogin = findViewById(R.id.txtBackLogin);
    }
}