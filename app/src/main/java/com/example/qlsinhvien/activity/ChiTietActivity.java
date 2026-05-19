package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Thêm import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher; // Thêm import Launcher
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ChiTietActivity extends AppCompatActivity {

    TextView txtChiTietTen, txtChiTietMSSV, txtChiTietSDT, txtChiTietEmail;
    ImageView imgChiTietProfile; // Khai báo ImageView trên màn hình chi tiết
    Button btnEdit, btnDelete;
    DatabaseHelper databaseHelper;
    Student student;

    // Launcher dùng để hứng ảnh khi người dùng chọn đổi ảnh trong Dialog sửa
    private ActivityResultLauncher<Intent> dialogImageLauncher;
    // Biến tạm để giữ ImageView trong Dialog đang mở nhằm cập nhật lại giao diện tạm thời
    private ImageView imgDialogTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi Tiết Sinh Viên");
        }

        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần giao diện
        txtChiTietTen = findViewById(R.id.txtChiTietTen);
        txtChiTietMSSV = findViewById(R.id.txtChiTietMSSV);
        txtChiTietSDT = findViewById(R.id.txtChiTietSDT);
        txtChiTietEmail = findViewById(R.id.txtChiTietEmail);
        imgChiTietProfile = findViewById(R.id.imgProfile); // Lưu ý: Cần chắc chắn XML chi tiết cũng có ID này nhé

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Đăng ký bộ lắng nghe sự kiện chọn ảnh đổi thông tin trong Dialog
        dialogImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && imgDialogTemp != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgDialogTemp.setImageBitmap(bitmap); // Hiển thị ảnh mới chọn lên ô ảnh của Dialog
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // NHẬN DỮ LIỆU
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DATA_STUDENT")) {
            student = (Student) intent.getSerializableExtra("DATA_STUDENT");

            // THÊM DÒNG NÀY ĐỂ BẢO VỆ
            if (student != null) {
                hienThiDuLieu();
            } else {
                Toast.makeText(this, "Lỗi: Không nhận được dữ liệu sinh viên!", Toast.LENGTH_SHORT).show();
            }
        }

        // ================= XỬ LÝ SỰ KIỆN NÚT XÓA =================
        btnDelete.setOnClickListener(v -> {
            if (student == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getName() + " không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        boolean check = databaseHelper.deleteStudent(student.getId());
                        if (check) {
                            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        // ================= XỬ LÝ SỰ KIỆN NÚT SỬA =================
        btnEdit.setOnClickListener(v -> {
            if (student == null) return;

            // Nạp layout nhập liệu activity_add_student để dùng làm form chỉnh sửa
            View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_add_student, null);

            Button btnSaveOld = dialogView.findViewById(R.id.btnSave);
            if (btnSaveOld != null) {
                btnSaveOld.setVisibility(View.GONE);
            }

            // Ánh xạ các trường nhập liệu trong Dialog
            EditText edtName = dialogView.findViewById(R.id.edtName);
            EditText edtCode = dialogView.findViewById(R.id.edtCode);
            EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
            EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
            imgDialogTemp = dialogView.findViewById(R.id.imgProfile); // Ánh xạ ImageView của form sửa

            // Đổ dữ liệu chữ hiện tại vào Dialog
            edtName.setText(student.getName());
            edtCode.setText(student.getStudentCode());
            edtPhone.setText(student.getPhone());
            edtEmail.setText(student.getEmail());

            // Đổ dữ liệu hình ảnh hiện tại vào Dialog (nếu có)
            if (student.getImage() != null && imgDialogTemp != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(student.getImage(), 0, student.getImage().length);
                imgDialogTemp.setImageBitmap(bitmap);
            }

            // Thiết lập sự kiện click vào ảnh trong Dialog để chọn ảnh mới từ máy tính / điện thoại
            if (imgDialogTemp != null) {
                imgDialogTemp.setOnClickListener(imgView -> {
                    Intent intentPick = new Intent(Intent.ACTION_GET_CONTENT);
                    intentPick.setType("image/*");
                    dialogImageLauncher.launch(Intent.createChooser(intentPick, "Chọn ảnh mới"));
                });
            }

            // Hiện hộp thoại cập nhật thông tin
            new AlertDialog.Builder(this)
                    .setTitle("Cập nhật thông tin")
                    .setView(dialogView)
                    .setPositiveButton("Cập nhật", (dialog, which) -> {
                        String name = edtName.getText().toString().trim();
                        String code = edtCode.getText().toString().trim();
                        String phone = edtPhone.getText().toString().trim();
                        String email = edtEmail.getText().toString().trim();

                        if (name.isEmpty() || code.isEmpty()) {
                            Toast.makeText(this, "Tên và MSSV không được để trống", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Lấy mảng byte ảnh hiện tại từ ImageView trong Dialog để cập nhật vào database
                        byte[] newImageByteArray = imageViewToByteArray(imgDialogTemp);

                        // SỬA ĐỔI CHÍNH: Gọi hàm updateStudent với đủ 6 tham số (Đã sửa lỗi gạch đỏ ở đây)
                        boolean check = databaseHelper.updateStudent(student.getId(), name, code, phone, email, newImageByteArray);
                        if (check) {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                            // Cập nhật lại đối tượng student cục bộ để hiển thị ngay ra giao diện hiện tại
                            student = new Student(student.getId(), name, code, phone, email, newImageByteArray);
                            hienThiDuLieu();
                        } else {
                            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    // Đổ dữ liệu từ đối tượng Student lên màn hình chi tiết (bao gồm cả chữ lẫn hình ảnh)
    private void hienThiDuLieu() {
        if (student != null) {
            txtChiTietTen.setText("Họ và tên: " + student.getName());
            txtChiTietMSSV.setText("MSSV: " + student.getStudentCode());
            txtChiTietSDT.setText("Số điện thoại: " + student.getPhone());
            txtChiTietEmail.setText("Email: " + student.getEmail());

            if (imgChiTietProfile != null) {
                byte[] imageBytes = student.getImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    try {
                        // Giải mã an toàn
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imgChiTietProfile.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        // Nếu lỗi dữ liệu ảnh, gán ảnh mặc định
                        imgChiTietProfile.setImageResource(R.mipmap.ic_launcher);
                    }
                } else {
                    imgChiTietProfile.setImageResource(R.mipmap.ic_launcher);
                }
            }
        }
    }

    // Hàm phụ trợ chuyển đổi đối tượng ảnh hiển thị từ ImageView sang định dạng byte[]
    private byte[] imageViewToByteArray(ImageView imageView) {
        if (imageView == null) return null;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}