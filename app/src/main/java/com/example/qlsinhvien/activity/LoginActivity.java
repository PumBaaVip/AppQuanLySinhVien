package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor; // BẮT BUỘC THÊM THƯ VIỆN NÀY ĐỂ ĐỌC DỮ LIỆU TỪ SQLITE
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
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
    private CheckBox cbRemember;
    private DatabaseHelper databaseHelper;

    // Đặt tên file cấu hình lưu trữ bộ nhớ tạm (Dùng chung cho Ghi nhớ mật khẩu & Phân quyền)
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
        cbRemember = findViewById(R.id.cbRemember);
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

    // Tự động tải lại thông tin đăng nhập cũ (nếu có)
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

    // ĐÃ NÂNG CẤP: Xử lý Đăng nhập + Ghi nhớ mật khẩu + Lưu phân quyền Session
    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // THAY ĐỔI: Gọi getUserSession thay vì loginUser để lấy quyền hạn
        Cursor cursor = databaseHelper.getUserSession(username, password);

        if (cursor != null && cursor.moveToFirst()) {
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // 1. Trích xuất thông tin quyền hạn từ Cursor
            int role = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_ROLE));
            int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.LINKED_STUDENT_ID));

            // 2. Xử lý ghi nhớ phiên làm việc vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Lưu quyền (Bắt buộc lưu để phân quyền ở MainActivity)
            editor.putInt("USER_ROLE", role);
            editor.putInt("USER_STUDENT_ID", studentId);

            // Xử lý tính năng "Ghi nhớ mật khẩu"
            if (cbRemember.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("remember", true);
            } else {
                // Chỉ xóa tài khoản/mật khẩu, không dùng editor.clear() để tránh xóa mất quyền vừa lưu ở trên
                editor.remove("username");
                editor.remove("password");
                editor.putBoolean("remember", false);
            }
            editor.apply();

            cursor.close(); // Đóng cursor để giải phóng bộ nhớ

            // 3. Chuyển hướng màn hình và xóa ngăn xếp
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
        }
    }
}