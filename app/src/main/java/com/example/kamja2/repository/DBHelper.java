package com.example.kamja2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kamja2.model.User;
import com.example.kamja2.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db"; //DB 이름 설정
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // users 테이블 생성
        String sql = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, nickname TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // users 테이블 수정
        // 데이터베이스 업그레이드 로직
    }

    // 회원가입 정보 저장
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());

        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        values.put("password", hashedPassword);
        values.put("nickname", user.getNickname());

        db.insert("users", null, values);
        db.close();
    }

    // 로그인 정보 검증
    public boolean checkUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        Cursor cursor = db.query("users", new String[]{"id"}, "username=? AND password=?", new String[]{user.getUsername(), hashedPassword}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // 로그인된 사용자 정보 가져오기
    public User getLoginUser(String username) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, "username=?", new String[]{username}, null, null, null);


        // 데이터 추출 및 저장
        if (cursor != null && cursor.moveToFirst()) {
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            // ... 기타 필요한 정보를 User 객체에 설정 ...
            cursor.close();
            System.out.println("로그인 된 사용자의 이메일은 " + user.getUsername() + "입니다.");
            return user;
        }
        cursor.close();
        db.close();


        return null;
    }
}


