package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView txtBackLogin;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtBackLogin = findViewById(R.id.txtBackLogin);

        // Nút đăng ký
        btnRegister.setOnClickListener(v -> {

            String username =
                    edtUsername.getText().toString().trim();

            String password =
                    edtPassword.getText().toString().trim();

            String confirm =
                    edtConfirmPassword.getText().toString().trim();

            // Kiểm tra rỗng
            if(username.isEmpty()
                    || password.isEmpty()
                    || confirm.isEmpty()) {

                Toast.makeText(this,
                        "Nhập đầy đủ thông tin",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Kiểm tra mật khẩu
            if(!password.equals(confirm)) {

                Toast.makeText(this,
                        "Mật khẩu không khớp",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Lưu SQLite
            boolean result =
                    databaseHelper.registerUser(
                            username,
                            password);

            if(result) {

                Toast.makeText(this,
                        "Đăng ký thành công",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(
                        this,
                        LoginActivity.class));

                finish();

            } else {

                Toast.makeText(this,
                        "Tài khoản đã tồn tại",
                        Toast.LENGTH_SHORT).show();

            }

        });

        txtBackLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edtUsername), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}