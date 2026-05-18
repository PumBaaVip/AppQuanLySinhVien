package com.example.qlsinhvien.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlsinhvien.R;
import com.example.qlsinhvien.model.Student;
import java.util.ArrayList;

public class StudenAdapter extends RecyclerView.Adapter<StudenAdapter.StudentViewHolder> {

    private ArrayList<Student> studentList;

    public StudenAdapter(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // SỬA: Nạp đúng layout item_student thay vì activity_danh_sach
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_danh_sach, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.txtName.setText(student.getName());
        holder.txtCode.setText("MSSV: " + student.getCode());
        holder.txtPhone.setText("SĐT: " + student.getPhone());
        holder.txtEmail.setText("Email: " + student.getEmail());
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCode, txtPhone, txtEmail;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            // SỬA: Ánh xạ đúng ID từ file item_student.xml
            txtName = itemView.findViewById(R.id.edtName);
            txtCode = itemView.findViewById(R.id.edtCode);
            txtPhone = itemView.findViewById(R.id.edtPhone);
            txtEmail = itemView.findViewById(R.id.edtEmail);
        }
    }
}