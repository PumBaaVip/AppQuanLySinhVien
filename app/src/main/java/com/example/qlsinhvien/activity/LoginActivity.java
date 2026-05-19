package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    // Sử dụng private để đóng gói dữ liệu
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initView();
        setupListeners();

        databaseHelper = new DatabaseHelper(this);
    }

    private void initView() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
    }

    private void setupListeners() {
        // Nút đăng nhập
        btnLogin.setOnClickListener(v -> performLogin());

        // Nhảy từ User sang Pass
        edtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (isAction(actionId, event)) {
                edtPassword.requestFocus();
                return true;
            }
            return false;
        });

        // Đăng nhập khi ấn Done ở ô Pass
        edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (isAction(actionId, event)) {
                performLogin();
                return true;
            }
            return false;
        });

        // Chuyển sang màn hình Đăng ký
        txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    // Hàm tiện ích để kiểm tra hành động phím Enter
    private boolean isAction(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
    }

    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.loginUser(username, password)) {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // CHUẨN: Xóa ngăn xếp Activity để người dùng không quay lại màn hình Login được
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}