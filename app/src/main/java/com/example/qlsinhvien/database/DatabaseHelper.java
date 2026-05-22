package com.example.qlsinhvien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.qlsinhvien.model.Student;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentManager.db";
    // Đã tăng version lên 5 để tự động cập nhật cấu trúc bảng mới
    private static final int DATABASE_VERSION = 5;

    // --- Bảng users ---
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_ROLE = "role"; // 0: Admin/Giảng viên, 1: Sinh viên
    public static final String LINKED_STUDENT_ID = "linked_student_id"; // ID liên kết với bảng students (-1 nếu là Admin)

    // --- Bảng students ---
    public static final String TABLE_STUDENTS = "students";
    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_CODE = "student_code";
    public static final String STUDENT_PHONE = "phone";
    public static final String STUDENT_EMAIL = "email";
    public static final String STUDENT_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users với các cột phân quyền mới
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT UNIQUE, "
                + PASSWORD + " TEXT, "
                + USER_ROLE + " INTEGER, "
                + LINKED_STUDENT_ID + " INTEGER)";

        // Tạo bảng students giữ nguyên
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDENT_NAME + " TEXT, "
                + STUDENT_CODE + " TEXT, "
                + STUDENT_PHONE + " TEXT, "
                + STUDENT_EMAIL + " TEXT, "
                + STUDENT_IMAGE + " BLOB)";

        db.execSQL(createUsersTable);
        db.execSQL(createStudentsTable);

        // Thêm tài khoản Admin mặc định (role = 0, linked_student_id = -1)
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, role, linked_student_id) VALUES ('admin', '123', 0, -1)");

        // (Tùy chọn) Thêm một tài khoản sinh viên mẫu để bạn test (role = 1, linked_student_id = 1)
        // db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password, role, linked_student_id) VALUES ('sinhvien1', '123', 1, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // --- ĐĂNG KÝ (Đã cập nhật để phân quyền) ---
    public boolean registerUser(String username, String password, int role, int linkedStudentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        values.put(USER_ROLE, role);
        values.put(LINKED_STUDENT_ID, linkedStudentId);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // --- ĐĂNG NHẬP LẤY PHIÊN (SESSION) ---
    // Hàm này thay thế cho loginUser cũ. Trả về Cursor để lấy được role và linked_student_id
    public Cursor getUserSession(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{USER_ID, USER_ROLE, LINKED_STUDENT_ID}, // Chỉ lấy các cột cần thiết
                USERNAME + "=? AND " + PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
    }

    // --- HÀM THÊM SINH VIÊN ---
    public boolean addStudent(String name, String code, String phone, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, name);
        values.put(STUDENT_CODE, code);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_EMAIL, email);
        values.put(STUDENT_IMAGE, image);
        return db.insert(TABLE_STUDENTS, null, values) != -1;
    }

    // --- LẤY DANH SÁCH ---
    public Cursor getAllStudents() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);
    }

    // --- LẤY SINH VIÊN THEO ID ---
    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, null, STUDENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Student student = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                student = new Student(
                        cursor.getInt(cursor.getColumnIndexOrThrow(STUDENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_EMAIL)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(STUDENT_IMAGE))
                );
            }
            cursor.close();
        }
        return student;
    }

    // --- CẬP NHẬT ---
    public boolean updateStudent(int id, String name, String code, String phone, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, name);
        values.put(STUDENT_CODE, code);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_EMAIL, email);
        if (image != null && image.length > 0) values.put(STUDENT_IMAGE, image);

        return db.update(TABLE_STUDENTS, values, STUDENT_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- XÓA ---
    public boolean deleteStudent(int id) {
        return getWritableDatabase().delete(TABLE_STUDENTS, STUDENT_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}