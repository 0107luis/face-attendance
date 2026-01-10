package com.example.studentattendanceterminal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class AttendanceDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "attendance.db";
    public static final int DB_VERSION = 4;

    // 与服务器 MySQL 结构对齐的本地表名（SQLite）
    public static final String TABLE_CHECKINS = "AttendanceRecord"; // 原checkins映射为AttendanceRecord
    public static final String TABLE_STUDENTS = "Student";
    public static final String TABLE_COURSES = "Course";

    // 统一字段映射（命名采用MySQL脚本中的字段名）
    public static final String COL_TIMESTAMP = "CheckInTime"; // INTEGER(EPOCH millis)
    public static final String COL_LATITUDE = "ActualLatitude";
    public static final String COL_LONGITUDE = "ActualLongitude";
    public static final String COL_IMAGE = "VerificationPhoto"; // BLOB
    public static final String COL_STUDENT_ID = "StudentID";
    public static final String COL_COURSE_ID = "CourseID";
    public static final String COL_SUCCESS = "AttendanceResult"; // TEXT: 正常/迟到/早退/缺勤/请假
    // 注意：各表主键名不同，查询时不再使用 BaseColumns._ID 统一列

    private static AttendanceDbHelper instance;

    public static synchronized AttendanceDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AttendanceDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private AttendanceDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 启用外键约束
        db.execSQL("PRAGMA foreign_keys = ON");

        // 学生表（对应 MySQL Student）
        String sqlStudents = "CREATE TABLE IF NOT EXISTS " + TABLE_STUDENTS + " (" +
                "studentid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_number TEXT UNIQUE NOT NULL, " +
                "student_name TEXT NOT NULL, " +
                "major_class TEXT, " +
                "userid INTEGER, " +
                "created_date INTEGER" +
                ")";
        db.execSQL(sqlStudents);

        // 课程表（对应 MySQL Course，保留必要字段）
        String sqlCourses = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " (" +
                "CourseID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CourseName TEXT NOT NULL, " +
                "CourseCode TEXT UNIQUE, " +
                "Credits REAL DEFAULT 0, " +
                "Semester TEXT, " +
                "Description TEXT, " +
                "CreatedDate INTEGER" +
                ")";
        db.execSQL(sqlCourses);

        // 考勤记录表（对应 MySQL AttendanceRecord），为兼容当前APP保留 CourseID 直接关联
        String sqlAttendance = "CREATE TABLE IF NOT EXISTS " + TABLE_CHECKINS + " (" +
                "RecordID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIMESTAMP + " INTEGER NOT NULL, " +
                COL_LATITUDE + " REAL, " +
                COL_LONGITUDE + " REAL, " +
                "VerificationPhotoURL TEXT, " +
                COL_IMAGE + " BLOB, " +
                "ConfidenceScore REAL, " +
                COL_SUCCESS + " TEXT NOT NULL, " +
                "TaskID INTEGER, " +
                COL_STUDENT_ID + " INTEGER, " +
                COL_COURSE_ID + " INTEGER, " +
                "Remark TEXT, " +
                "CreatedDate INTEGER, " +
                "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(studentid) ON DELETE SET NULL, " +
                "FOREIGN KEY(" + COL_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(CourseID) ON DELETE SET NULL" +
                ")";
        db.execSQL(sqlAttendance);

        seedPresetData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单策略：直接重建所有表（生产环境请迁移数据）
        // 兼容旧版本遗留表名
        db.execSQL("DROP TABLE IF EXISTS checkins");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKINS);
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS courses");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }

    public long insertCheckin(long timestamp, double latitude, double longitude, byte[] imageBytes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TIMESTAMP, timestamp);
        cv.put(COL_LATITUDE, latitude);
        cv.put(COL_LONGITUDE, longitude);
        cv.put(COL_IMAGE, imageBytes);
        cv.put(COL_SUCCESS, "正常");
        return db.insert(TABLE_CHECKINS, null, cv);
    }

    public long insertCheckinWithCourse(long timestamp, double latitude, double longitude, byte[] imageBytes,
                                        Long courseId, Long studentId, boolean success) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TIMESTAMP, timestamp);
        cv.put(COL_LATITUDE, latitude);
        cv.put(COL_LONGITUDE, longitude);
        cv.put(COL_IMAGE, imageBytes);
        if (courseId != null) cv.put(COL_COURSE_ID, courseId);
        if (studentId != null) cv.put(COL_STUDENT_ID, studentId);
        cv.put(COL_SUCCESS, success ? "正常" : "缺勤");
        return db.insert(TABLE_CHECKINS, null, cv);
    }

    public void upsertStudent(Long id, String number, String name, String majorClass, Long userId, Long createdDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (id != null) cv.put("studentid", id);
        cv.put("student_number", number);
        cv.put("student_name", name);
        cv.put("major_class", majorClass);
        if (userId != null) cv.put("userid", userId);
        if (createdDate != null) cv.put("created_date", createdDate);
        db.insertWithOnConflict(TABLE_STUDENTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long insertCourse(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CourseName", name);
        return db.insert(TABLE_COURSES, null, cv);
    }

    public long insertCourseIfNotExists(String name) {
        SQLiteDatabase db = getWritableDatabase();
        // 先查询是否已存在
        android.database.Cursor c = db.rawQuery("SELECT CourseID FROM " + TABLE_COURSES + " WHERE CourseName = ? LIMIT 1", new String[]{name});
        if (c != null && c.moveToFirst()) {
            long id = c.getLong(0);
            c.close();
            return id;
        }
        if (c != null) c.close();
        // 不存在则插入
        return insertCourse(name);
    }

    private void seedCourses(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_COURSES + "(CourseName) VALUES ('高等数学')");
        db.execSQL("INSERT INTO " + TABLE_COURSES + "(CourseName) VALUES ('大学英语')");
        db.execSQL("INSERT INTO " + TABLE_COURSES + "(CourseName) VALUES ('计算机基础')");
    }

    private void seedStudents(SQLiteDatabase db) {
        long now = System.currentTimeMillis();
        ContentValues cv = new ContentValues();

        cv.put("student_number", "2021001001");
        cv.put("student_name", "张一");
        cv.put("major_class", "计算机2101班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);

        cv.clear();
        cv.put("student_number", "2021001002");
        cv.put("student_name", "李二");
        cv.put("major_class", "计算机2101班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);

        cv.clear();
        cv.put("student_number", "2021001003");
        cv.put("student_name", "张三");
        cv.put("major_class", "计算机2102班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);

        cv.clear();
        cv.put("student_number", "2021001004");
        cv.put("student_name", "李四");
        cv.put("major_class", "计算机2102班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);

        cv.clear();
        cv.put("student_number", "2021001005");
        cv.put("student_name", "王五");
        cv.put("major_class", "计算机2103班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);

        cv.clear();
        cv.put("student_number", "2021001006");
        cv.put("student_name", "赵六");
        cv.put("major_class", "计算机2103班");
        cv.put("created_date", now);
        db.insert(TABLE_STUDENTS, null, cv);
    }

    private void seedPresetData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            seedCourses(db);
            seedStudents(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // 公开方法：在运行时确保示例学生已写入（若为空则写入）
    public void ensureSeedStudents() {
        SQLiteDatabase db = getWritableDatabase();
        android.database.Cursor c = db.rawQuery("SELECT COUNT(1) FROM " + TABLE_STUDENTS, null);
        int count = 0;
        if (c != null) { if (c.moveToFirst()) count = c.getInt(0); c.close(); }
        if (count == 0) {
            seedStudents(db);
        }
    }

    public java.util.List<Course> getAllCourses() {
        java.util.List<Course> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        android.database.Cursor c = db.rawQuery("SELECT CourseID, CourseName FROM " + TABLE_COURSES + " ORDER BY CourseID ASC", null);
        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String name = c.getString(1);
                list.add(new Course(id, name));
            }
            c.close();
        }
        return list;
    }
    
    public java.util.List<Course> getCoursesByStudentId(Long studentId) {
        // 这个方法暂时返回所有课程，因为本地数据库可能没有学生选课关联表
        // 实际应该从服务器获取，这里作为备用
        return getAllCourses();
    }

    public java.util.List<CheckinDisplay> getCheckinDisplayList() {
        java.util.List<CheckinDisplay> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.RecordID, c." + COL_TIMESTAMP + ", c." + COL_SUCCESS + ", co.CourseName FROM " + TABLE_CHECKINS + " c " +
                "LEFT JOIN " + TABLE_COURSES + " co ON c." + COL_COURSE_ID + " = co.CourseID ORDER BY c." + COL_TIMESTAMP + " DESC";
        android.database.Cursor cur = db.rawQuery(sql, null);
        if (cur != null) {
            while (cur.moveToNext()) {
                long id = cur.getLong(0);
                long ts = cur.getLong(1);
                String result = cur.getString(2);
                boolean success = "正常".equals(result);
                String courseName = cur.getString(3);
                list.add(new CheckinDisplay(id, courseName, success, ts));
            }
            cur.close();
        }
        return list;
    }
    
    /**
     * 根据学生ID获取签到记录列表（只返回该学生的记录）
     */
    public java.util.List<CheckinDisplay> getCheckinDisplayListByStudentId(Long studentId) {
        if (studentId == null) {
            return new java.util.ArrayList<>();
        }
        
        java.util.List<CheckinDisplay> list = new java.util.ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.RecordID, c." + COL_TIMESTAMP + ", c." + COL_SUCCESS + ", co.CourseName FROM " + TABLE_CHECKINS + " c " +
                "LEFT JOIN " + TABLE_COURSES + " co ON c." + COL_COURSE_ID + " = co.CourseID " +
                "WHERE c." + COL_STUDENT_ID + " = ? ORDER BY c." + COL_TIMESTAMP + " DESC";
        android.database.Cursor cur = db.rawQuery(sql, new String[]{String.valueOf(studentId)});
        if (cur != null) {
            while (cur.moveToNext()) {
                long id = cur.getLong(0);
                long ts = cur.getLong(1);
                String result = cur.getString(2);
                boolean success = "正常".equals(result);
                String courseName = cur.getString(3);
                list.add(new CheckinDisplay(id, courseName, success, ts));
            }
            cur.close();
        }
        return list;
    }

    public StudentSimple findStudentByNumber(String number) {
        SQLiteDatabase db = getReadableDatabase();
        android.database.Cursor c = db.rawQuery("SELECT studentid, student_number, student_name FROM " + TABLE_STUDENTS + " WHERE student_number = ? LIMIT 1", new String[]{number});
        try {
            if (c != null && c.moveToFirst()) {
                long id = c.getLong(0);
                String num = c.getString(1);
                String name = c.getString(2);
                return new StudentSimple(id, num, name);
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public static class StudentSimple {
        public final long id;
        public final String number;
        public final String name;
        public StudentSimple(long id, String number, String name) {
            this.id = id; this.number = number; this.name = name;
        }
    }

    public static class Course {
        public final long id;
        public final String name;
        public Course(long id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public static class CheckinDisplay {
        public final long id;
        public final String courseName;
        public final boolean success;
        public final long timestamp;
        public CheckinDisplay(long id, String courseName, boolean success, long timestamp) {
            this.id = id; this.courseName = courseName; this.success = success; this.timestamp = timestamp;
        }
    }
}