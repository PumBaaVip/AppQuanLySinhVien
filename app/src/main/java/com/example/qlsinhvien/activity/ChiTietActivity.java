package com.example.qlsinhvien.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;
import android.graphics.BitmapFactory;

public class ChiTietActivity extends AppCompatActivity {
    private TextView txtTen, txtMSSV, txtSDT, txtEmail;
    private ImageView imgProfile;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        // Ánh xạ
        txtTen = findViewById(R.id.txtChiTietTen);
        txtMSSV = findViewById(R.id.txtChiTietMSSV);
        txtSDT = findViewById(R.id.txtChiTietSDT);
        txtEmail = findViewById(R.id.txtChiTietEmail);
        imgProfile = findViewById(R.id.imgProfile);

        db = new DatabaseHelper(this);

        // NHẬN DỮ LIỆU
        int studentId = getIntent().getIntExtra("STUDENT_ID", -1);

        if (studentId != -1) {
            // Truy vấn database để lấy object Student đầy đủ
            Student student = db.getStudentById(studentId);

            if (student != null) {
                hienThiDuLieu(student);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin sinh viên trong DB!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lỗi ID sinh viên!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình nếu không có dữ liệu
        }
    }

    private void hienThiDuLieu(Student s) {
        txtTen.setText("Tên: " + s.getName());
        txtMSSV.setText("MSSV: " + s.getStudentCode());
        txtSDT.setText("SĐT: " + s.getPhone());
        txtEmail.setText("Email: " + s.getEmail());

        // Hiển thị ảnh
        if (s.getImage() != null && s.getImage().length > 0) {
            imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(s.getImage(), 0, s.getImage().length));
        } else {
            // Lưu ý: Đừng dùng ic_launcher cho ảnh đại diện
            // Hãy copy 1 file ảnh (vd: avatar.png) vào thư mục drawable và dùng R.drawable.avatar
            imgProfile.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}