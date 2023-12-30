package com.example.kamja2.controller;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 3;
    private Context context;
    private String tableName[] = {"samgim","sandwich","lunchBox","hotbar","hamburger","cupRamen"};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        Log.i("진행로그","1");
        db.execSQL("CREATE TABLE samgim (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");
        db.execSQL("CREATE TABLE sandwich (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");
        db.execSQL("CREATE TABLE lunchBox (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");
        db.execSQL("CREATE TABLE hotbar (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");
        db.execSQL("CREATE TABLE hamburger (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");
        db.execSQL("CREATE TABLE cupRamen (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price INTEGER," +
                "kcal INTEGER," +
                "carbon REAL," +
                "protein REAL," +
                "fat REAL," +
                "recipe TEXT," +
                "script TEXT"+
                ");");




        // AssetManager를 사용하여 CSV 파일 읽기,
        // 파일경로 app/assets/xxx.csv
        AssetManager assetManager = context.getAssets();
        for (int i=0;i<tableName.length;i++)
        {
            try {
                InputStream inputStream = assetManager.open(tableName[i]+".csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                br.readLine(); // 헤더 라인 스킵
                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 쉼표로 분리하여 데이터 추출
                    ContentValues values = new ContentValues();
                    values.put("_id", Integer.parseInt(columns[0]));
                    values.put("name", columns[1]);
                    values.put("price", Float.valueOf(columns[2]));
                    values.put("kcal", Float.valueOf(columns[3]));
                    values.put("carbon", Float.valueOf(columns[4]));
                    values.put("protein", Float.valueOf(columns[5]));
                    values.put("fat", Float.valueOf(columns[6]));
                    values.put("recipe", columns[7]);
                    values.put("script", columns[8]);
                    db.insert(tableName[i], null, values);
                    Log.i("진행로그","추가완료");
                }
                br.close();
            } catch (IOException | NumberFormatException e) {
                Log.e("불러오기 실패",e.toString());
                e.printStackTrace();
            }
        }

    }
    // 기존에 생성된 테이블들을 불러오는 코드
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // 카테고리랑 예산만으로 필터링
    public Cursor getDataByCategoryAndName(String categoryValue, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        // 카테고리가 매개변수와 같고 예산이 매개변수보다 작은 것
        String[] projection = null;

        String selection ;
        String[] selectionArgs;
        Cursor cursor;
        if(categoryValue.equals("")) {
            selection = "name LIKE ?";
            selectionArgs = new String[]{"%" +name };
        }
        else{
            selection = "name LIKE ?";
            selectionArgs = new String[]{ "%" +name +"%"};
        }
        cursor = db.query(
                categoryValue, // 테이블 이름
                projection, // 가져올 컬럼들, null인 경우 모든 컬럼을 가져옴
                selection, // 조건절
                selectionArgs, // 조건 값
                null, // groupBy
                null, // having
                null // orderBy
        );

        // 이후 cursor를 반환하거나 사용할 작업 수행
        return cursor;
    }
    public Cursor getDataByName( String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        Log.i("이름 검색진입",name);
        // 카테고리가 매개변수와 같고 예산이 매개변수보다 작은 것
        String[] projection = null;
        String selection = "name LIKE ?";
        String[] selectionArgs = {"%" +name };
        /// 임의 테이블에서 해당 그자 들어간 줄 반환
        //cursor = db.rawQuery("SELECT * FROM samgim WHERE name LIKE '%" + name + "%'",null);
        String sql = "SELECT * FROM samgim WHERE name LIKE '%" + name + "%' " +
                "UNION ALL SELECT * FROM sandwich WHERE name LIKE '%" + name + "%'" +
                "UNION ALL SELECT * FROM lunchBox WHERE name LIKE '%" + name + "%'" +
                "UNION ALL SELECT * FROM hotbar WHERE name LIKE '%" + name + "%'" +
                "UNION ALL SELECT * FROM hamburger WHERE name LIKE '%" + name + "%'" +
                "UNION ALL SELECT * FROM cupRamen WHERE name LIKE '%" + name + "%'";
        cursor = db.rawQuery(sql,null);

        //cursor = db.rawQuery("SELECT * FROM samgim",null);

        // 이후 cursor를 반환하거나 사용할 작업 수행
        return cursor;
    }
    public Cursor getDataByCategory(String categoryValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        Log.i("태그 검색 진입",categoryValue);
        if(!categoryValue.equals(""))
            cursor = db.rawQuery("SELECT * FROM "+categoryValue+"",null);
        else
            cursor = db.rawQuery("SELECT * FROM samgim",null);
        return cursor;
    }
}

