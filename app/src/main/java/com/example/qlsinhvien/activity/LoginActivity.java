package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.content.SharedPreferences; // THÊM THƯ VIỆN
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox; // THÊM THƯ VIỆN
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private CheckBox cbRemember; // THÊM KHAI BÁO CHECKBOX
    private DatabaseHelper databaseHelper;

    // Đặt tên file cấu hình lưu trữ bộ nhớ tạm
    private static final String PREFS_NAME = "loginPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Khởi tạo SharedPreferences trước khi cấu hình View
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initView();
        setupListeners();
        loadSavedLoginInfo(); // Tự động kiểm tra và điền dữ liệu đã lưu

        databaseHelper = new DatabaseHelper(this);
    }

    private void initView() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        cbRemember = findViewById(R.id.cbRemember); // ÁNH XẠ CHECKBOX
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

    // TÍNH NĂNG MỚI: Tự động tải lại thông tin đăng nhập cũ (nếu có)
    private void loadSavedLoginInfo() {
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            String savedUser = sharedPreferences.getString("username", "");
            String savedPass = sharedPreferences.getString("password", "");

            edtUsername.setText(savedUser);
            edtPassword.setText(savedPass);
            cbRemember.setChecked(true);
        }
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

            // TÍNH NĂNG MỚI: Xử lý lưu hoặc xóa trạng thái bộ nhớ đệm
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRemember.isChecked()) {
                // Nếu tích chọn "Ghi nhớ", lưu lại tên tài khoản, mật khẩu
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("remember", true);
            } else {
                // Nếu bỏ tích, xóa sạch dữ liệu đăng nhập cũ đã lưu trong máy
                editor.clear();
            }
            editor.apply(); // Chạy ngầm tiến trình ghi dữ liệu vào máy

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