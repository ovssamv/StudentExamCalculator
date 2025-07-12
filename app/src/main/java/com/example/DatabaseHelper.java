package com.example;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.models.Announcement;
import com.example.models.Module;
import com.example.models.Student;
import com.example.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database constants
    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 9;

    // Table names
    private static final String TABLE_USERS = "users";
    public static final String TABLE_STUDENTS = "students";
    private static final String TABLE_GRADES = "grades";
    private static final String TABLE_ANNOUNCEMENTS = "announcements";

    // Column groups
    // User columns
    private static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_USER_CUSTOM_ID = "custom_id";
    private static final String KEY_USER_FULL_NAME = "full_name";
    private static final String KEY_USER_DATE_OF_BIRTH = "date_of_birth";

    // Student columns
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_STUDENT_NAME = "name";
    public static final String KEY_STUDENT_EMAIL = "email";

    // Grade columns
    private static final String KEY_GRADE_ID = "id";
    private static final String KEY_GRADE_STUDENT_ID = "student_id";
    private static final String KEY_GRADE_MODULE_NAME = "module_name";
    private static final String KEY_GRADE_TD_SCORE = "td_score";
    private static final String KEY_GRADE_TP_SCORE = "tp_score";
    private static final String KEY_GRADE_EXAM_SCORE = "exam_score";

    // User types
    public static final String USER_TYPE_STUDENT = "student";
    public static final String USER_TYPE_TEACHER = "teacher";
    public static final String USER_TYPE_ADMIN = "admin";

    // Announcement columns
    private static final String KEY_ANNOUNCEMENT_ID = "id";
    private static final String KEY_ANNOUNCEMENT_TITLE = "title";
    private static final String KEY_ANNOUNCEMENT_CONTENT = "content";
    private static final String KEY_ANNOUNCEMENT_DATE = "date";
    private static final String KEY_ANNOUNCEMENT_TEACHER_NAME = "teacher_name";
    private static final String KEY_ANNOUNCEMENT_TEACHER_ID = "teacher_id";
    //report
    private static final String TABLE_GRADE_REPORTS = "grade_reports";
    private static final String KEY_REPORT_ID = "id";
    private static final String KEY_REPORT_STUDENT_ID = "student_id";
    private static final String KEY_REPORT_MODULE_NAME = "module_name";
    private static final String KEY_REPORT_TYPE = "report_type"; // "TD", "TP", or "EXAM"
    private static final String KEY_REPORT_ISSUE = "issue_description";
    private static final String KEY_REPORT_STATUS = "status"; // "PENDING", "REVIEWED"
    private static final String KEY_REPORT_DATE = "report_date";
    // SQL statements
    private static final String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_STUDENT_ID + " TEXT UNIQUE," +
            KEY_STUDENT_NAME + " TEXT," +
            KEY_STUDENT_EMAIL + " TEXT" + ")";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" +
            KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_USER_NAME + " TEXT UNIQUE," +
            KEY_USER_EMAIL + " TEXT UNIQUE," +
            KEY_USER_PASSWORD + " TEXT," +
            KEY_USER_TYPE + " TEXT," +
            KEY_USER_CUSTOM_ID + " TEXT," +
            KEY_USER_FULL_NAME + " TEXT," +
            KEY_USER_DATE_OF_BIRTH + " TEXT" +
            ")";

    private static final String CREATE_GRADES_TABLE = "CREATE TABLE " + TABLE_GRADES + "(" +
            KEY_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_GRADE_STUDENT_ID + " TEXT," +
            KEY_GRADE_MODULE_NAME + " TEXT," +
            KEY_GRADE_TD_SCORE + " REAL," +
            KEY_GRADE_TP_SCORE + " REAL," +
            KEY_GRADE_EXAM_SCORE + " REAL," +
            "UNIQUE(" + KEY_GRADE_STUDENT_ID + ", " + KEY_GRADE_MODULE_NAME + ") ON CONFLICT REPLACE" +
            ")";

    private static final String CREATE_ANNOUNCEMENTS_TABLE = "CREATE TABLE " + TABLE_ANNOUNCEMENTS + "(" +
            KEY_ANNOUNCEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ANNOUNCEMENT_TITLE + " TEXT," +
            KEY_ANNOUNCEMENT_CONTENT + " TEXT," +
            KEY_ANNOUNCEMENT_DATE + " TEXT," +
            KEY_ANNOUNCEMENT_TEACHER_NAME + " TEXT," +
            KEY_ANNOUNCEMENT_TEACHER_ID + " TEXT" +
            ")";
    private static final String CREATE_GRADE_REPORTS_TABLE = "CREATE TABLE " + TABLE_GRADE_REPORTS + "(" +
            KEY_REPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_REPORT_STUDENT_ID + " TEXT," +
            KEY_REPORT_MODULE_NAME + " TEXT," +
            KEY_REPORT_TYPE + " TEXT," +
            KEY_REPORT_ISSUE + " TEXT," +
            KEY_REPORT_STATUS + " TEXT DEFAULT 'PENDING'," +
            KEY_REPORT_DATE + " TEXT" +
            ")";
    // Singleton instance
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_GRADES_TABLE);
        db.execSQL(CREATE_ANNOUNCEMENTS_TABLE);
        db.execSQL(CREATE_GRADE_REPORTS_TABLE);

        // Add predefined admin account
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_USER_NAME, "admin");
        adminValues.put(KEY_USER_EMAIL, "admin@example.com");
        adminValues.put(KEY_USER_PASSWORD, "admin");
        adminValues.put(KEY_USER_TYPE, USER_TYPE_ADMIN);
        adminValues.put(KEY_USER_CUSTOM_ID, "ADMIN001");
        adminValues.put(KEY_USER_FULL_NAME, "System Administrator");
        adminValues.put(KEY_USER_DATE_OF_BIRTH, "01/01/2000");

        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle schema changes
        if (oldVersion < 5 && newVersion >= 5) {
            // Add columns for full name and date of birth
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_FULL_NAME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_DATE_OF_BIRTH + " TEXT");
        }

        // For major schema changes, recreate affected tables
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            db.execSQL(CREATE_STUDENTS_TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
            db.execSQL(CREATE_GRADES_TABLE);
        }

        // Add user_type and custom_id for old versions
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_TYPE +
                    " TEXT DEFAULT '" + USER_TYPE_STUDENT + "'");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_CUSTOM_ID + " TEXT");
        }

        if (oldVersion < 6) {
            db.execSQL(CREATE_ANNOUNCEMENTS_TABLE);
        }

        // Add admin account if upgrading to version 7
        if (oldVersion < 7 && newVersion >= 7) {
            // Check if admin account already exists
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID},
                    KEY_USER_NAME + "=?", new String[]{"admin"}, null, null, null);

            boolean adminExists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }

            if (!adminExists) {
                ContentValues adminValues = new ContentValues();
                adminValues.put(KEY_USER_NAME, "admin");
                adminValues.put(KEY_USER_EMAIL, "admin@example.com");
                adminValues.put(KEY_USER_PASSWORD, "admin");
                adminValues.put(KEY_USER_TYPE, USER_TYPE_ADMIN);
                adminValues.put(KEY_USER_CUSTOM_ID, "ADMIN001");
                adminValues.put(KEY_USER_FULL_NAME, "System Administrator");
                adminValues.put(KEY_USER_DATE_OF_BIRTH, "01/01/2000");

                db.insert(TABLE_USERS, null, adminValues);
            }

        }
        if (oldVersion < 8) {
            db.execSQL(CREATE_GRADE_REPORTS_TABLE);
        }
    }

    // User methods
    public boolean registerUser(String username, String email, String password,
                                String userType, String customId, String fullName, String dateOfBirth) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, username);
            values.put(KEY_USER_EMAIL, email);
            values.put(KEY_USER_PASSWORD, password);
            values.put(KEY_USER_TYPE, userType);
            values.put(KEY_USER_CUSTOM_ID, customId);
            values.put(KEY_USER_FULL_NAME, fullName);
            values.put(KEY_USER_DATE_OF_BIRTH, dateOfBirth);

            long id = db.insertOrThrow(TABLE_USERS, null, values);
            
            if (id == -1) {
                throw new Exception("Failed to insert user record");
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    public User getUserDetails(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {KEY_USER_NAME, KEY_USER_EMAIL, KEY_USER_TYPE,
                KEY_USER_CUSTOM_ID, KEY_USER_FULL_NAME, KEY_USER_DATE_OF_BIRTH};
        String selection = KEY_USER_NAME + " = ?";
        String[] selectionArgs = {username};

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL)));
                user.setUserType(cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE)));
                user.setCustomId(cursor.getString(cursor.getColumnIndex(KEY_USER_CUSTOM_ID)));
                user.setFullName(cursor.getString(cursor.getColumnIndex(KEY_USER_FULL_NAME)));
                user.setDateOfBirth(cursor.getString(cursor.getColumnIndex(KEY_USER_DATE_OF_BIRTH)));
                return user;
            }
        }
        return null;
    }

    public boolean updateUserProfile(String username, String newUsername, String email,
                                     String fullName, String dateOfBirth) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check if username is changing and not already taken
        if (!username.equals(newUsername)) {
            if (isUsernameTaken(newUsername)) return false;
            values.put(KEY_USER_NAME, newUsername);
        }

        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_FULL_NAME, fullName);
        values.put(KEY_USER_DATE_OF_BIRTH, dateOfBirth);

        int rowsAffected = db.update(TABLE_USERS, values, KEY_USER_NAME + " = ?",
                new String[]{username});

        // Update student records if username changed
        if (!username.equals(newUsername) && rowsAffected > 0) {
            String userType = getUserType(username);
            String customId = getUserCustomId(username);

            if (USER_TYPE_STUDENT.equals(userType)) {
                ContentValues studentValues = new ContentValues();
                studentValues.put(KEY_STUDENT_NAME, newUsername);
                db.update(TABLE_STUDENTS, studentValues, KEY_STUDENT_ID + " = ?",
                        new String[]{customId});
            }
        }

        return rowsAffected > 0;
    }

    // Student methods
    public boolean addStudent(Student student) {
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, student.getId());
        values.put(KEY_STUDENT_NAME, student.getName());
        values.put(KEY_STUDENT_EMAIL, student.getEmail());

        return getWritableDatabase().insert(TABLE_STUDENTS, null, values) != -1;
    }

    public Cursor getAllStudents() {
        return getReadableDatabase().query(TABLE_STUDENTS,
                new String[]{"_id", KEY_STUDENT_ID, KEY_STUDENT_NAME, KEY_STUDENT_EMAIL},
                null, null, null, null, KEY_STUDENT_NAME + " ASC");
    }

    // Query helpers
    @SuppressLint("Range")
    private String getStringValue(String table, String column, String whereColumn, String whereValue) {
        try (Cursor cursor = getReadableDatabase().query(
                table, new String[]{column}, whereColumn + " = ?",
                new String[]{whereValue}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(column));
            }
        }
        return null;
    }

    private boolean exists(String table, String whereColumn, String whereValue) {
        try (Cursor cursor = getReadableDatabase().query(
                table, new String[]{"1"}, whereColumn + " = ?",
                new String[]{whereValue}, null, null, null)) {

            return cursor != null && cursor.getCount() > 0;
        }
    }

    // User lookups
    public boolean isUsernameTaken(String username) {
        return exists(TABLE_USERS, KEY_USER_NAME, username);
    }

    public boolean isEmailRegistered(String email) {
        return exists(TABLE_USERS, KEY_USER_EMAIL, email);
    }

    public boolean isCustomIdTaken(String customId, String userType) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_USERS, new String[]{KEY_USER_ID},
                KEY_USER_CUSTOM_ID + " = ? AND " + KEY_USER_TYPE + " = ?",
                new String[]{customId, userType}, null, null, null)) {

            return cursor != null && cursor.getCount() > 0;
        }
    }

    public boolean checkUser(String username, String password) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_USERS, new String[]{KEY_USER_ID},
                KEY_USER_NAME + " = ? AND " + KEY_USER_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null)) {

            return cursor != null && cursor.getCount() > 0;
        }
    }

    public String getUserEmail(String username) {
        return getStringValue(TABLE_USERS, KEY_USER_EMAIL, KEY_USER_NAME, username);
    }

    public String getUserType(String username) {
        return getStringValue(TABLE_USERS, KEY_USER_TYPE, KEY_USER_NAME, username);
    }

    public String getUserCustomId(String username) {
        return getStringValue(TABLE_USERS, KEY_USER_CUSTOM_ID, KEY_USER_NAME, username);
    }

    // Grade methods
    @SuppressLint("Range")
    public double[] getStudentModuleGrades(String studentId, String moduleName) {
        double[] scores = {0, 0, 0}; // [TD, TP, Exam]

        String query = "SELECT " + KEY_GRADE_TD_SCORE + ", " + KEY_GRADE_TP_SCORE + ", "
                + KEY_GRADE_EXAM_SCORE + " FROM " + TABLE_GRADES
                + " WHERE " + KEY_GRADE_STUDENT_ID + " = ? AND "
                + KEY_GRADE_MODULE_NAME + " = ?";

        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{studentId, moduleName})) {
            if (cursor != null && cursor.moveToFirst()) {
                scores[0] = cursor.getDouble(cursor.getColumnIndex(KEY_GRADE_TD_SCORE));
                scores[1] = cursor.getDouble(cursor.getColumnIndex(KEY_GRADE_TP_SCORE));
                scores[2] = cursor.getDouble(cursor.getColumnIndex(KEY_GRADE_EXAM_SCORE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scores;
    }

    public boolean saveStudentModuleGrades(String studentId, String moduleName,
                                           double tdScore, double tpScore, double examScore) {
        ContentValues values = new ContentValues();
        values.put(KEY_GRADE_STUDENT_ID, studentId);
        values.put(KEY_GRADE_MODULE_NAME, moduleName);
        values.put(KEY_GRADE_TD_SCORE, tdScore);
        values.put(KEY_GRADE_TP_SCORE, tpScore);
        values.put(KEY_GRADE_EXAM_SCORE, examScore);

        // insert if not exists, update if exists
        return getWritableDatabase().replace(TABLE_GRADES, null, values) != -1;
    }

    public List<Module> getModulesWithGrades(String studentId) {
        List<Module> modules = new ArrayList<>();
        String query = "SELECT " + KEY_GRADE_MODULE_NAME + ", " + KEY_GRADE_TD_SCORE + ", "
                + KEY_GRADE_TP_SCORE + ", " + KEY_GRADE_EXAM_SCORE
                + " FROM " + TABLE_GRADES + " WHERE " + KEY_GRADE_STUDENT_ID + " = ?";

        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{studentId})) {
            if (cursor.moveToFirst()) {
                do {
                    Module module = new Module(cursor.getString(0), 1.0); // Default coefficient
                    module.setTdScore(cursor.getDouble(1));
                    module.setTpScore(cursor.getDouble(2));
                    module.setExamScore(cursor.getDouble(3));
                    modules.add(module);
                } while (cursor.moveToNext());
            }
        }
        return modules;
    }

    // Announcement methods
    public long addAnnouncement(Announcement announcement) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ANNOUNCEMENT_TITLE, announcement.getTitle());
        values.put(KEY_ANNOUNCEMENT_CONTENT, announcement.getContent());
        values.put(KEY_ANNOUNCEMENT_DATE, announcement.getDate());
        values.put(KEY_ANNOUNCEMENT_TEACHER_NAME, announcement.getTeacherName());
        values.put(KEY_ANNOUNCEMENT_TEACHER_ID, announcement.getTeacherId());

        return db.insert(TABLE_ANNOUNCEMENTS, null, values);
    }

    public boolean updateAnnouncement(Announcement announcement) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ANNOUNCEMENT_TITLE, announcement.getTitle());
        values.put(KEY_ANNOUNCEMENT_CONTENT, announcement.getContent());
        values.put(KEY_ANNOUNCEMENT_DATE, announcement.getDate());
        values.put(KEY_ANNOUNCEMENT_TEACHER_NAME, announcement.getTeacherName());
        values.put(KEY_ANNOUNCEMENT_TEACHER_ID, announcement.getTeacherId());

        int rowsAffected = db.update(TABLE_ANNOUNCEMENTS, values,
                KEY_ANNOUNCEMENT_ID + " = ?",
                new String[]{String.valueOf(announcement.getId())});

        return rowsAffected > 0;
    }

    public boolean deleteAnnouncement(int announcementId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(TABLE_ANNOUNCEMENTS,
                KEY_ANNOUNCEMENT_ID + " = ?",
                new String[]{String.valueOf(announcementId)});

        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ANNOUNCEMENTS + " ORDER BY " + KEY_ANNOUNCEMENT_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Announcement announcement = new Announcement();
                announcement.setId(cursor.getInt(cursor.getColumnIndex(KEY_ANNOUNCEMENT_ID)));
                announcement.setTitle(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TITLE)));
                announcement.setContent(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_CONTENT)));
                announcement.setDate(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_DATE)));
                announcement.setTeacherName(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_NAME)));
                announcement.setTeacherId(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_ID)));

                announcements.add(announcement);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return announcements;
    }

    @SuppressLint("Range")
    public List<Announcement> getAnnouncementsByTeacher(String teacherId) {
        List<Announcement> announcements = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ANNOUNCEMENTS +
                " WHERE " + KEY_ANNOUNCEMENT_TEACHER_ID + " = ?" +
                " ORDER BY " + KEY_ANNOUNCEMENT_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{teacherId});
        if (cursor.moveToFirst()) {
            do {
                Announcement announcement = new Announcement();
                announcement.setId(cursor.getInt(cursor.getColumnIndex(KEY_ANNOUNCEMENT_ID)));
                announcement.setTitle(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TITLE)));
                announcement.setContent(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_CONTENT)));
                announcement.setDate(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_DATE)));
                announcement.setTeacherName(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_NAME)));
                announcement.setTeacherId(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_ID)));

                announcements.add(announcement);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return announcements;
    }

    @SuppressLint("Range")
    public Announcement getAnnouncementById(int announcementId) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ANNOUNCEMENTS + " WHERE " + KEY_ANNOUNCEMENT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(announcementId)});
        Announcement announcement = null;

        if (cursor.moveToFirst()) {
            announcement = new Announcement();
            announcement.setId(cursor.getInt(cursor.getColumnIndex(KEY_ANNOUNCEMENT_ID)));
            announcement.setTitle(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TITLE)));
            announcement.setContent(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_CONTENT)));
            announcement.setDate(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_DATE)));
            announcement.setTeacherName(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_NAME)));
            announcement.setTeacherId(cursor.getString(cursor.getColumnIndex(KEY_ANNOUNCEMENT_TEACHER_ID)));
        }
        cursor.close();

        return announcement;
    }

    @SuppressLint("Range")
    public String getUserNameByCustomId(String customId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_CUSTOM_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{customId})) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add to DatabaseHelper.java
    public long addGradeReport(String studentId, String moduleName, String type, String issue) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REPORT_STUDENT_ID, studentId);
        values.put(KEY_REPORT_MODULE_NAME, moduleName);
        values.put(KEY_REPORT_TYPE, type);
        values.put(KEY_REPORT_ISSUE, issue);
        values.put(KEY_REPORT_DATE, java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));

        return db.insert(TABLE_GRADE_REPORTS, null, values);
    }

    @SuppressLint("Range")
    public List<Map<String, String>> getGradeReports() {
        List<Map<String, String>> reports = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT r.*, u." + KEY_USER_FULL_NAME + " FROM " + TABLE_GRADE_REPORTS +
                " r JOIN " + TABLE_USERS + " u ON r." + KEY_REPORT_STUDENT_ID +
                " = u." + KEY_USER_CUSTOM_ID +
                " ORDER BY r." + KEY_REPORT_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> report = new HashMap<>();
                report.put("id", cursor.getString(cursor.getColumnIndex(KEY_REPORT_ID)));
                report.put("studentId", cursor.getString(cursor.getColumnIndex(KEY_REPORT_STUDENT_ID)));
                report.put("studentName", cursor.getString(cursor.getColumnIndex(KEY_USER_FULL_NAME)));
                report.put("moduleName", cursor.getString(cursor.getColumnIndex(KEY_REPORT_MODULE_NAME)));
                report.put("type", cursor.getString(cursor.getColumnIndex(KEY_REPORT_TYPE)));
                report.put("issue", cursor.getString(cursor.getColumnIndex(KEY_REPORT_ISSUE)));
                report.put("status", cursor.getString(cursor.getColumnIndex(KEY_REPORT_STATUS)));
                report.put("date", cursor.getString(cursor.getColumnIndex(KEY_REPORT_DATE)));

                reports.add(report);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return reports;
    }

    public boolean updateReportStatus(String reportId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REPORT_STATUS, status);

        int rowsAffected = db.update(TABLE_GRADE_REPORTS, values,
                KEY_REPORT_ID + " = ?", new String[]{reportId});

        return rowsAffected > 0;
    }
}