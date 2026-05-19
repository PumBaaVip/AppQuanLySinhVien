package com.example.qlsinhvien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.qlsinhvien.model.Student;

public class DatabaseHelper extends SQLiteOpenHelper {

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
    public static final String STUDENT_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT UNIQUE, "
                + PASSWORD + " TEXT)";

        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDENT_NAME + " TEXT, "
                + STUDENT_CODE + " TEXT, "
                + STUDENT_PHONE + " TEXT, "
                + STUDENT_EMAIL + " TEXT, "
                + STUDENT_IMAGE + " BLOB)";

        db.execSQL(createUsersTable);
        db.execSQL(createStudentsTable);

        // Thêm tài khoản mặc định
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password) VALUES ('admin', '123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // --- ĐĂNG KÝ ---
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // --- ĐĂNG NHẬP ---
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Kiểm tra đúng username và password
        Cursor cursor = db.query(TABLE_USERS, null, USERNAME + "=? AND " + PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
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

    // --- LẤY SINH VIÊN THEO ID (AN TOÀN HƠN) ---
    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, null, STUDENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Student student = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Dùng getColumnIndexOrThrow để tránh lỗi sai vị trí cột
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