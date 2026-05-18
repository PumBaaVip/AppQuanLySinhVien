package com.example.qlsinhvien.activity;

import android.database.Cursor;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.adapter.StudenAdapter;
import com.example.qlsinhvien.database.DatabaseHelper;
import com.example.qlsinhvien.model.Student;
import java.util.ArrayList;

public class DanhSachActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Student> studentList;
    private StudenAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach);

        recyclerView = findViewById(R.id.recyclerViewStudent);
        databaseHelper = new DatabaseHelper(this);
        studentList = new ArrayList<>();

        loadStudents();

        adapter = new StudenAdapter(studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        studentList.clear(); // Xóa danh sách cũ tránh trùng lặp khi quay lại activity
        Cursor cursor = databaseHelper.getAllStudents();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                // SỬA: Lấy dữ liệu dạng String phù hợp với Database TEXT
                String code = cursor.getString(2);
                String phone = cursor.getString(3);
                String email = cursor.getString(4);

                studentList.add(new Student(id, name, code, phone, email));
            }
            cursor.close();
        }
    }
}