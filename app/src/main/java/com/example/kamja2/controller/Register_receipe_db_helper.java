package com.example.kamja2.controller;

import static com.example.kamja2.controller.DB_register_receipe.MATERIAL;
import static com.example.kamja2.controller.DB_register_receipe.MATERIAL_NAME;
import static com.example.kamja2.controller.DB_register_receipe.MATERIAL_PRICE;
import static com.example.kamja2.controller.DB_register_receipe.PRICE;
import static com.example.kamja2.controller.DB_register_receipe.RECEIPE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Register_receipe_db_helper {
    private DB_register_receipe DB_register_receipe;

    //DB_register_receipe 페이지에서 가져오기
    public Register_receipe_db_helper(Context context) {
        DB_register_receipe = new DB_register_receipe(context);


    }

    //데이터 베이스 작성할 수 있게 해주는 함수
    public void insertDB(String receipe_name, String one_line, String material, String content, int price) {
        SQLiteDatabase db = DB_register_receipe.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RECEIPE_NAME, receipe_name);
        values.put(DB_register_receipe.ONE_LINE, one_line);
        values.put(MATERIAL, material);
        values.put(PRICE, price); // 추가: 가격 정보를 ContentValues에 추가
        values.put(DB_register_receipe.CONTENT, content);

        db.insert(DB_register_receipe.TABLE_NAME, null, values);
        db.close();
    }

    //중복 확인을 위한 함수
    public boolean receipe_nameexist(String receipe_name) {
        SQLiteDatabase db = DB_register_receipe.getReadableDatabase();

        Cursor cursor = db.query(DB_register_receipe.TABLE_NAME,
                null,
                RECEIPE_NAME + "=?",
                new String[]{receipe_name},
                null,
                null,
                null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;


    }

    //레시피 삽입 함수
    private void insertReceipe(String receipe_name, String one_line, String material, String content) {
        SQLiteDatabase db = DB_register_receipe.getWritableDatabase();

        String sql = "INSERT INTO " + DB_register_receipe.TABLE_NAME +
                "(" + RECEIPE_NAME + ", " +
                DB_register_receipe.ONE_LINE + ", " +
                MATERIAL + ", " + // 수정: MATERIAL을 material로 변경
                PRICE + ", " + // 추가: 가격 정보를 삽입하기 위해
                DB_register_receipe.CONTENT + ") VALUES (?, ?, ?, ?)";

        db.execSQL(sql, new Object[]{receipe_name, one_line, material, content});

        Log.d("DatabaseInsert", "Inserted: " +
                "receipe_name=" + receipe_name +
                ", one_line=" + one_line +
                ", material=" + material +
                ", content=" + content
        );
        db.close();
    }

    //가격과 재료명 가져오는 메서드
    public String[] getNameAndPriceForMaterial(String material) {
        SQLiteDatabase db = DB_register_receipe.getReadableDatabase();

        // 재료 테이블에서 이름, 가격, 재료를 조회
        String[] columns = {MATERIAL_NAME, MATERIAL_PRICE}; // 수정: 컬럼 이름을 수정
        String selection = MATERIAL_NAME + "=?";
        String[] selectionArgs = {material};

        Cursor cursor = db.query(DB_register_receipe.TABLE_NAME_MATERIAL, columns, selection, selectionArgs, null, null, null);

        String[] result = new String[2]; // 결과를 담을 배열

        try {
            if (cursor.moveToFirst()) {
                int price = cursor.getInt(cursor.getColumnIndex(MATERIAL_PRICE)); // 수정: 컬럼 이름을 수정
                String materialName = cursor.getString(cursor.getColumnIndex(MATERIAL_NAME)); // 수정: 컬럼 이름을 수정

                result[0] = materialName;
                result[1] = price + "원";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return result;
    }



}
