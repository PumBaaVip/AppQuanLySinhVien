package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns; // THÊM IMPORT NÀY ĐỂ CHECK EMAIL CHUẨN ANDROID
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddStudentActivity extends AppCompatActivity {
    EditText edtName, edtCode, edtPhone, edtEmail;
    ImageView imgProfile;
    Button btnSave;

    DatabaseHelper databaseHelper;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);

        // Ánh xạ các trường nhập liệu cũ
        edtName = findViewById(R.id.edtName);
        edtCode = findViewById(R.id.edtCode);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        imgProfile = findViewById(R.id.imgProfile);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Đăng ký bộ lắng nghe sự kiện chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgProfile.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Không thể mở ảnh này", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Click thẳng vào ảnh để chọn file ảnh trong máy
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh sinh viên"));
        });

        // Button lưu
        btnSave.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String code = edtCode.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();

            // 1. Kiểm tra rỗng toàn bộ form
            if (name.isEmpty() || code.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. PHẦN THÊM MỚI: Ràng buộc số điện thoại phải đủ 10 số và bắt đầu bằng số 0
            // Biểu thức chính quy ^0[0-9]{9}$ kiểm tra bắt đầu bằng số 0 và theo sau là 9 chữ số khác
            if (!phone.matches("^0[0-9]{9}$")) {
                edtPhone.setError("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0");
                edtPhone.requestFocus(); // Đưa con trỏ chuột tập trung vào ô lỗi này
                return;
            }

            // 3. PHẦN THÊM MỚI: Ràng buộc Email phải đúng định dạng chuẩn (vd: abc@domain.com)
            // Sử dụng mẫu kiểm tra email tích hợp sẵn vô cùng chính xác của Android hệ thống
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Địa chỉ Email không hợp lệ (Ví dụ: sv@gmail.com)");
                edtEmail.requestFocus(); // Đưa con trỏ chuột tập trung vào ô lỗi này
                return;
            }

            // Chuyển đổi ảnh đang hiển thị trên ImageView thành mảng byte[] để lưu SQLite
            byte[] imageByteArray = imageViewToByteArray(imgProfile);

            // Thêm SQLite
            boolean result = databaseHelper.addStudent(name, code, phone, email, imageByteArray);

            if (result) {
                Toast.makeText(this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();

                // Clear form
                edtName.setText("");
                edtCode.setText("");
                edtPhone.setText("");
                edtEmail.setText("");
                imgProfile.setImageResource(R.mipmap.ic_launcher); // Reset về ảnh mặc định

            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm phụ trợ chuyển đổi ảnh từ ImageView thành mảng byte[]
     */
    private byte[] imageViewToByteArray(ImageView imageView) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}