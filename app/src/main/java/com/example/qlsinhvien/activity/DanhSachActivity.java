package com.example.qlsinhvien.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.adapter.StudentAdapter;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;

import java.util.ArrayList;

public class DanhSachActivity extends AppCompatActivity {
    GridView listView;
    EditText edtSearch;
    ArrayList<Student> studentList;
    ArrayList<Student> allStudentsList;
    StudentAdapter adapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach);

        // Ánh xạ ID
        listView = findViewById(R.id.lvStudents);
        edtSearch = findViewById(R.id.edtSearch);
        db = new DatabaseHelper(this);

        studentList = new ArrayList<>();
        allStudentsList = new ArrayList<>();

        // Khởi tạo adapter
        adapter = new StudentAdapter(this, studentList);
        listView.setAdapter(adapter);

        // Bắt sự kiện khi click vào một sinh viên trên lưới/danh sách
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = studentList.get(position);

            Intent intent = new Intent(DanhSachActivity.this, ChiTietActivity.class);
            // Gửi kèm toàn bộ dữ liệu của sinh viên sang màn hình chi tiết
            intent.putExtra("DATA_STUDENT", selectedStudent);

            startActivity(intent);
        });

        // Bắt sự kiện gõ phím vào thanh tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        studentList.clear();
        allStudentsList.clear();

        Cursor cursor = db.getAllStudents();
        if (cursor != null) {
            // Lấy chỉ số cột bằng tên để tránh lỗi sai vị trí
            int colId = cursor.getColumnIndex(DatabaseHelper.STUDENT_ID);
            int colName = cursor.getColumnIndex(DatabaseHelper.STUDENT_NAME);
            int colCode = cursor.getColumnIndex(DatabaseHelper.STUDENT_CODE);
            int colPhone = cursor.getColumnIndex(DatabaseHelper.STUDENT_PHONE);
            int colEmail = cursor.getColumnIndex(DatabaseHelper.STUDENT_EMAIL);
            int colImage = cursor.getColumnIndex(DatabaseHelper.STUDENT_IMAGE);

            while (cursor.moveToNext()) {
                Student student = new Student(
                        cursor.getInt(colId),
                        cursor.getString(colName),
                        cursor.getString(colCode),
                        cursor.getString(colPhone),
                        cursor.getString(colEmail),
                        (colImage != -1) ? cursor.getBlob(colImage) : null
                );
                studentList.add(student);
                allStudentsList.add(student);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    // Hàm lọc danh sách khi tìm kiếm
    private void filterList(String text) {
        studentList.clear();

        if (text.isEmpty()) {
            studentList.addAll(allStudentsList);
        } else {
            text = text.toLowerCase();
            for (Student student : allStudentsList) {
                if (student.getName().toLowerCase().contains(text) ||
                        student.getStudentCode().toLowerCase().contains(text)) {
                    studentList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}