package com.example.qlsinhvien.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qlsinhvien.R;
import com.example.qlsinhvien.model.Student;
import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Student> list;

    public StudentAdapter(Context context, ArrayList<Student> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }
    @Override
    public Object getItem(int position) { return list.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        }

        Student s = list.get(position);

        // Ánh xạ các thành phần trên một dòng
        ImageView imgStudent = convertView.findViewById(R.id.imgStudent);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtCode = convertView.findViewById(R.id.txtCode);

        // Đổ dữ liệu chữ lên giao diện
        txtName.setText(s.getName());
        txtCode.setText("MSSV: " + s.getStudentCode());

        // XỬ LÝ ĐỔ ẢNH: Giải mã mảng byte[] từ SQLite thành Bitmap để hiển thị lên danh sách
        if (s.getImage() != null && s.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(s.getImage(), 0, s.getImage().length);
            imgStudent.setImageBitmap(bitmap);
        } else {
            // Nếu sinh viên chưa có ảnh, hiện ảnh robot mặc định để không bị trống giao diện
            imgStudent.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}