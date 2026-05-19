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

    // TẠO LỚP VIEWHOLDER ĐỂ LƯU TRỮ CÁC VIEW (GIẢM THIỂU FINDVIEWBYID)
    private static class ViewHolder {
        ImageView imgStudent;
        TextView txtName;
        TextView txtCode;
    }

    @Override
    public int getCount() { return list.size(); }
    @Override
    public Object getItem(int position) { return list.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);

            // Khởi tạo holder
            holder = new ViewHolder();
            holder.imgStudent = convertView.findViewById(R.id.imgStudent);
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtCode = convertView.findViewById(R.id.txtCode);

            // Gắn holder vào convertView
            convertView.setTag(holder);
        } else {
            // Lấy lại holder đã gắn
            holder = (ViewHolder) convertView.getTag();
        }

        Student s = list.get(position);

        // Đổ dữ liệu
        holder.txtName.setText(s.getName());
        holder.txtCode.setText("MSSV: " + s.getStudentCode());

        // Xử lý ảnh
        if (s.getImage() != null && s.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(s.getImage(), 0, s.getImage().length);
            holder.imgStudent.setImageBitmap(bitmap);
        } else {
            holder.imgStudent.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}