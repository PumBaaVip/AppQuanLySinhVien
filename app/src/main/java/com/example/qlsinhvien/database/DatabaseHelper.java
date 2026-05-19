package com.example.qlsinhvien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // 1. ĐỔI DATABASE_VERSION THÀNH 2 ĐỂ HỆ THỐNG CẬP NHẬT THÊM CỘT ẢNH
    private static final String DATABASE_NAME = "StudentManager.db";
    private static final int DATABASE_VERSION = 4;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // Bảng students
    public static final String TABLE_STUDENTS = "students";
    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_CODE = "student_code";
    public static final String STUDENT_PHONE = "phone";
    public static final String STUDENT_EMAIL = "email";

    // 2. KHAI BÁO TÊN CỘT ẢNH MỚI
    public static final String STUDENT_IMAGE = "image";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT UNIQUE, "
                + PASSWORD + " TEXT)";

        // 3. THÊM CỘT ẢNH (STUDENT_IMAGE BLOB) VÀO CÂU LỆNH TẠO BẢNG
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDENT_NAME + " TEXT, "
                + STUDENT_CODE + " TEXT, "
                + STUDENT_PHONE + " TEXT, "
                + STUDENT_EMAIL + " TEXT, "
                + STUDENT_IMAGE + " BLOB)";

        db.execSQL(createUsersTable);
        db.execSQL(createStudentsTable);

        // THÊM TÀI KHOẢN MẶC ĐỊNH
        db.execSQL("INSERT INTO " + TABLE_USERS +
                " (username, password) VALUES ('admin', '123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // ĐĂNG KÝ
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // ĐĂNG NHẬP
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery((
                        "SELECT * FROM " + TABLE_USERS +
                                " WHERE username=? AND password=?"),
                new String[]{username, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 4. SỬA HÀM THÊM SINH VIÊN: Nhận thêm tham số `byte[] image`
    public boolean addStudent(String name, String code, String phone, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(STUDENT_NAME, name);
        values.put(STUDENT_CODE, code);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_EMAIL, email);
        values.put(STUDENT_IMAGE, image); // Đẩy mảng byte ảnh vào database

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }

    // LẤY DANH SÁCH SINH VIÊN
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENTS,
                null
        );
    }

    // XÓA SINH VIÊN
    public boolean deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                TABLE_STUDENTS,
                STUDENT_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return result > 0;
    }

    // 5. SỬA HÀM UPDATE SINH VIÊN: Nhận thêm tham số `byte[] image` để sau này bạn sửa ảnh được luôn
    public boolean updateStudent(int id, String name, String code, String phone, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(STUDENT_NAME, name);
        values.put(STUDENT_CODE, code);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_EMAIL, email);
        values.put(STUDENT_IMAGE, image); // Cập nhật lại mảng byte ảnh mới

        int result = db.update(
                TABLE_STUDENTS,
                values,
                STUDENT_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        return result > 0;
    }
}