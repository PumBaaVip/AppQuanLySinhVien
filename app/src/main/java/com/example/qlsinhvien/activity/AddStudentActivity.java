package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Thêm import này
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher; // Thêm import này
import androidx.activity.result.contract.ActivityResultContracts; // Thêm import này
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddStudentActivity extends AppCompatActivity {
    EditText edtName, edtCode, edtPhone, edtEmail;
    ImageView imgProfile; // 1. Khai báo ImageView ảnh đại diện
    Button btnSave;

    DatabaseHelper databaseHelper;

    // 2. Khai báo launcher để hứng kết quả chọn ảnh từ file máy tính/điện thoại
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

        // 3. Ánh xạ ImageView từ file XML sang
        imgProfile = findViewById(R.id.imgProfile);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // 4. Đăng ký bộ lắng nghe sự kiện chọn ảnh (Phải viết trước khi click)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData(); // Đường dẫn tệp ảnh đã chọn
                        try {
                            // Đọc file ảnh từ bộ nhớ/PC thành đối tượng InputStream
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // Hiển thị tấm ảnh vừa chọn lên ImageView trên giao diện
                            imgProfile.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Không thể mở ảnh này", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // 5. Cài đặt sự kiện: Click thẳng vào ảnh để chọn file ảnh trong máy
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // Chỉ lọc hiển thị các file là hình ảnh
            imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh sinh viên"));
        });

        // Button lưu
        btnSave.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String code = edtCode.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();

            // Kiểm tra rỗng
            if(name.isEmpty() || code.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // 6. Chuyển đổi ảnh đang hiển thị trên ImageView thành mảng byte[] để lưu SQLite
            byte[] imageByteArray = imageViewToByteArray(imgProfile);

            // 7. Thêm SQLite (Lưu ý: Bạn cần vào class DatabaseHelper thêm tham số byte[] vào hàm addStudent nhé)
            boolean result = databaseHelper.addStudent(name, code, phone, email, imageByteArray);

            if(result) {
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
     * Hàm phụ trợ chuyển đổi ảnh từ ImageView thành mảng byte[] để ghi vào cơ sở dữ liệu (kiểu BLOB)
     */
    private byte[] imageViewToByteArray(ImageView imageView) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Nén ảnh sang định dạng PNG để giữ nguyên chất lượng
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi xảy ra hoặc chưa chọn ảnh
        }
    }
}