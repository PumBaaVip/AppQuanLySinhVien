package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.adapter.StudentAdapter;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;
import java.util.ArrayList;

public class DanhSachActivity extends AppCompatActivity {
    // 1. Sử dụng private để đóng gói dữ liệu
    private GridView lvStudents;
    private ArrayList<Student> studentList;
    private StudentAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach);

        initView();
        setupAdapter();
        setupListener();
    }

    private void initView() {
        lvStudents = findViewById(R.id.lvStudents);
        db = new DatabaseHelper(this);
        studentList = new ArrayList<>();
    }

    private void setupAdapter() {
        adapter = new StudentAdapter(this, studentList);
        lvStudents.setAdapter(adapter);
    }

    private void setupListener() {
        // Xử lý sự kiện click: Chỉ truyền ID
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = studentList.get(position);

            Intent intent = new Intent(DanhSachActivity.this, ChiTietActivity.class);
            // TRUYỀN ID - Cách này đảm bảo không bị lỗi dữ liệu quá lớn
            intent.putExtra("STUDENT_ID", selectedStudent.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại màn hình này (Ví dụ: sau khi xóa hoặc sửa xong)
        loadData();
    }

    private void loadData() {
        studentList.clear();
        Cursor cursor = db.getAllStudents();

        if (cursor != null) {
            // Sử dụng getColumnIndex để lấy đúng cột, an toàn hơn dùng số thứ tự
            int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_NAME);
            int codeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_CODE);
            int phoneIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_PHONE);
            int emailIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_EMAIL);
            int imageIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_IMAGE);

            while (cursor.moveToNext()) {
                studentList.add(new Student(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(codeIndex),
                        cursor.getString(phoneIndex),
                        cursor.getString(emailIndex),
                        cursor.getBlob(imageIndex)
                ));
            }
            cursor.close();
        }
        // Cập nhật lại giao diện
        adapter.notifyDataSetChanged();
    }
}