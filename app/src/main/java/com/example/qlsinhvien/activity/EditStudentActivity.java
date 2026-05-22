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
import com.example.qlsinhvien.model.Student;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditStudentActivity extends AppCompatActivity {
    private EditText edtName, edtCode, edtPhone, edtEmail;
    private ImageView imgProfile;
    private Button btnSave;

    private int studentId;
    private DatabaseHelper db;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        initViews();
        setupImagePicker();
        db = new DatabaseHelper(this);

        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
        loadStudentData();

        btnSave.setOnClickListener(v -> handleSave());

        // Click vào ảnh để thay đổi ảnh mới
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh mới"));
        });
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtCode = findViewById(R.id.edtCode);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        imgProfile = findViewById(R.id.imgEditProfile); // Đảm bảo ID này có trong layout
        btnSave = findViewById(R.id.btnSave);
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
                            Toast.makeText(this, "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void loadStudentData() {
        if (studentId == -1) return;

        Student s = db.getStudentById(studentId);
        if (s != null) {
            edtName.setText(s.getName());
            edtCode.setText(s.getStudentCode());
            edtPhone.setText(s.getPhone());
            edtEmail.setText(s.getEmail());

            if (s.getImage() != null && s.getImage().length > 0) {
                imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(s.getImage(), 0, s.getImage().length));
            }
        }
    }

    private void handleSave() {
        String name = edtName.getText().toString().trim();
        String code = edtCode.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (name.isEmpty() || code.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tên và MSSV", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu ảnh từ ImageView hiện tại để lưu
        byte[] imageByteArray = imageViewToByteArray(imgProfile);

        boolean isUpdated = db.updateStudent(studentId, name, code, phone, email, imageByteArray);

        if (isUpdated) {
            Toast.makeText(this, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}