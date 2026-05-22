package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddStudentActivity extends AppCompatActivity {
    private EditText edtName, edtCode, edtPhone, edtEmail;
    private ImageView imgProfile;
    private Button btnSave;

    private DatabaseHelper databaseHelper;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // 1. Ánh xạ View
        edtName = findViewById(R.id.edtName);
        edtCode = findViewById(R.id.edtCode);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        imgProfile = findViewById(R.id.imgProfile);

        databaseHelper = new DatabaseHelper(this);

        // 2. Thiết lập chọn ảnh
        setupImagePicker();

        // 3. Xử lý lưu
        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Uri imageUri = result.getData().getData();
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgProfile.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Toast.makeText(this, "Không thể load ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });
    }

    private void validateAndSave() {
        String name = edtName.getText().toString().trim();
        String code = edtCode.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // Kiểm tra trống
        if (name.isEmpty() || code.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra SĐT (10 số, bắt đầu bằng 0)
        if (!phone.matches("^0[0-9]{9}$")) {
            edtPhone.setError("SĐT không hợp lệ (10 số, bắt đầu bằng 0)");
            edtPhone.requestFocus();
            return;
        }

        // Kiểm tra Email đuôi @gmail.com
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            edtEmail.setError("Email bắt buộc phải có đuôi @gmail.com");
            edtEmail.requestFocus();
            return;
        }

        // Lưu vào Database
        byte[] imageByteArray = imageViewToByteArray(imgProfile);
        boolean result = databaseHelper.addStudent(name, code, phone, email, imageByteArray);

        if (result) {
            Toast.makeText(this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
            resetForm();
        } else {
            Toast.makeText(this, "Lỗi: Không thể lưu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        edtName.setText("");
        edtCode.setText("");
        edtPhone.setText("");
        edtEmail.setText("");
        imgProfile.setImageResource(R.mipmap.ic_launcher);
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Nén ảnh xuống 50% chất lượng để tiết kiệm bộ nhớ
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}