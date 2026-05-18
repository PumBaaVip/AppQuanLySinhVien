package com.example.qlsinhvien.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

public class AddStudentActivity extends AppCompatActivity {
    EditText edtName, edtCode, edtPhone, edtEmail;
    Button btnSave;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        // Ánh xạ
        edtName = findViewById(R.id.edtName);
        edtCode = findViewById(R.id.edtCode);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);

        btnSave = findViewById(R.id.btnSave);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Button lưu
        btnSave.setOnClickListener(v -> {

            String name =
                    edtName.getText().toString().trim();

            String code =
                    edtCode.getText().toString().trim();

            String phone =
                    edtPhone.getText().toString().trim();

            String email =
                    edtEmail.getText().toString().trim();

            // Kiểm tra rỗng
            if(name.isEmpty()
                    || code.isEmpty()
                    || phone.isEmpty()
                    || email.isEmpty()) {

                Toast.makeText(this,
                        "Nhập đầy đủ thông tin",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Thêm SQLite
            boolean result =
                    databaseHelper.addStudent(
                            name,
                            code,
                            phone,
                            email);

            if(result) {

                Toast.makeText(this,
                        "Thêm sinh viên thành công",
                        Toast.LENGTH_SHORT).show();

                // Clear form
                edtName.setText("");
                edtCode.setText("");
                edtPhone.setText("");
                edtEmail.setText("");

            } else {

                Toast.makeText(this,
                        "Thêm thất bại",
                        Toast.LENGTH_SHORT).show();

            }

        });


    }
}