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
    private GridView lvStudents;
    private EditText edtSearch;
    private DatabaseHelper db;

    // QUAN TRỌNG: 2 danh sách riêng biệt
    private ArrayList<Student> allStudents; // Lưu trữ toàn bộ dữ liệu từ DB
    private ArrayList<Student> displayList; // Chỉ chứa dữ liệu đang hiển thị/lọc
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach);

        initView();
        setupSearch();
    }

    private void initView() {
        lvStudents = findViewById(R.id.lvStudents);
        edtSearch = findViewById(R.id.edtSearch);
        db = new DatabaseHelper(this);

        allStudents = new ArrayList<>();
        displayList = new ArrayList<>();

        // Adapter TRỎ VÀO displayList
        adapter = new StudentAdapter(this, displayList);
        lvStudents.setAdapter(adapter);

        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = displayList.get(position);
            Intent intent = new Intent(DanhSachActivity.this, ChiTietActivity.class);
            intent.putExtra("STUDENT_ID", selectedStudent.getId());
            startActivity(intent);
        });
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        displayList.clear(); // Xóa danh sách hiển thị
        if (text.isEmpty()) {
            displayList.addAll(allStudents); // Hiển thị lại tất cả
        } else {
            String query = text.toLowerCase().trim();
            for (Student s : allStudents) {
                if (s.getName().toLowerCase().contains(query) ||
                        s.getStudentCode().toLowerCase().contains(query)) {
                    displayList.add(s);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Cập nhật lại giao diện
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        allStudents.clear();
        Cursor cursor = db.getAllStudents();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_NAME);
            int codeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_CODE);
            int phoneIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_PHONE);
            int emailIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_EMAIL);
            int imageIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_IMAGE);

            while (cursor.moveToNext()) {
                allStudents.add(new Student(
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

        // Cập nhật displayList giống allStudents và báo adapter
        displayList.clear();
        displayList.addAll(allStudents);
        adapter.notifyDataSetChanged();
    }
}