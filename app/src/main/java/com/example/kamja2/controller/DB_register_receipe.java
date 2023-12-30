package com.example.kamja2.controller;
//레시피 등록 페이지 필요한 DB

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kamja2.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DB_register_receipe extends SQLiteOpenHelper {
    //데이터 베이스 명
    private static final String DATABASE_NAME = "register_receipe";
    private static final int DATABASE_VERSION = 7;

    private String tableName[] = {"cupRamen","sandwich","lunchBox","hotbar","hamburger","samgim"};

    private Context context;

    // table 이름
    public static final String TABLE_NAME = "receipe";

    // 구별할 id
    public static final String COLUMN_ID = "_id";

    // 레시피 이름
    public static final String RECEIPE_NAME = "receipe_name";

    // 레시피 한줄 소개
    public static final String ONE_LINE = "oneline";

    // 레시피 전체 재료
    public static final String MATERIAL = "material";

    // 레시피 전체  가격
    public static final String PRICE = "price";

    // 레시피 내용
    public static final String CONTENT = "content";
    //대표 이미지 저장
    public static final String IMAGE = "image";

    // 재료 테이블
    public static final String TABLE_NAME_MATERIAL = "material_table";
    public static final String COLUMN_ID_MATERIAL = "_id_material";
    //쟈료 이름( 각 개별 )
    public static final String MATERIAL_NAME = "material_name";

    //재료 가격( 각 개별 )
    public static final String MATERIAL_PRICE = "material_price";

    //재료 이미지 (개별)
    public static final String MATERIAL_IMAGE = "material_image_id";

    //관계 테이블 생성
    // 관계 테이블
    public static final String TABLE_NAME_RECIPE_MATERIAL_RELATION = "recipe_material_relation";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_MATERIAL_ID = "material_id";


    public DB_register_receipe(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (context == null) {
            Log.e("DB_register_receipe", "Context is NULL!");
        } else {
            Log.d("DB_register_receipe", "Context: " + context.toString());
        }

        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RECEIPE_NAME + " TEXT,"
                + ONE_LINE + " TEXT,"
                + MATERIAL + " TEXT,"
                + PRICE + " INTEGER,"
                + CONTENT + " TEXT,"
                + IMAGE + " BLOB)"; // 바이트 배열로 이미지 저장

        db.execSQL(CREATE_TABLE);

        // 재료 테이블 생성
        String CREATE_TABLE_MATERIAL = "CREATE TABLE " + TABLE_NAME_MATERIAL + "("
                + COLUMN_ID_MATERIAL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MATERIAL_NAME + " TEXT,"
                + MATERIAL_IMAGE + " TEXT,"  // 이미지를 문자열로 저장
                + MATERIAL_PRICE + " INTEGER)";

        db.execSQL(CREATE_TABLE_MATERIAL);

        // 레시피-재료 관계 테이블 생성
        String CREATE_TABLE_RECIPE_MATERIAL_RELATION = "CREATE TABLE " + TABLE_NAME_RECIPE_MATERIAL_RELATION + "("
                + COLUMN_RECIPE_ID + " INTEGER,"
                + COLUMN_MATERIAL_ID + " INTEGER)";
        db.execSQL(CREATE_TABLE_RECIPE_MATERIAL_RELATION);



        AssetManager assetManager = context.getAssets();
        for (int i=0;i<1;i++)
        {
            try {
                InputStream inputStream = assetManager.open(tableName[i]+".csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                br.readLine(); // 헤더 라인 스킵
                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(","); // 쉼표로 분리하여 데이터 추출
                    ContentValues values = new ContentValues();
                    values.put("_id_material", Integer.parseInt(columns[0]));
                    values.put("material_name", columns[1]);
                    values.put("material_price", Float.valueOf(columns[2]));
                    values.put("material_image_id", String.valueOf(columns[8]));
                    db.insert(TABLE_NAME_MATERIAL, null, values);
                    Log.i("진행로그","추가완료");
                }
                br.close();
            } catch (IOException | NumberFormatException e) {
                Log.e("불러오기 실패",e.toString());
                e.printStackTrace();
            }
        }
        //임시 데이터 삽입
        insertInitialData(db);
        insertInitialMaterialData(db);

    }
    // 이미지를 바이트 배열로 변환하여 저장
    public long saveImageToDatabase(byte[] imageData) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IMAGE, imageData);

        long result = db.insert(TABLE_NAME, null, values);

        db.close();

        return result;
    }

    // 바이트 배열로 저장된 이미지를 불러오기
    public byte[] loadImageFromDatabase(long rowId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {IMAGE};
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(rowId)};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        byte[] imageData = null;

        if (cursor.moveToFirst()) {
            imageData = cursor.getBlob(cursor.getColumnIndex(IMAGE));
        }

        cursor.close();
        db.close();

        return imageData;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MATERIAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECIPE_MATERIAL_RELATION);
        onCreate(db);
    }

    //자동완성 기능 구현
    public List<String> getAutoCompleteData() {
        List<String> autoCompleteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + MATERIAL_NAME + ", " + MATERIAL_PRICE + ", " + MATERIAL_IMAGE + " FROM " + TABLE_NAME_MATERIAL, null);

        if (cursor.moveToFirst()) {
            do {
                String materialName = cursor.getString(cursor.getColumnIndex(MATERIAL_NAME));
                int materialPrice = cursor.getInt(cursor.getColumnIndex(MATERIAL_PRICE));
                String materialImage = cursor.getString(cursor.getColumnIndex(MATERIAL_IMAGE));

                // 여기에서 필요한 작업 수행            } while (cursor.moveToNext());
                autoCompleteList.add(materialName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return autoCompleteList;
    }

    private void insertInitialData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(RECEIPE_NAME, "맛있는 도시락");
        values.put(ONE_LINE, "도시락과 라면");
        values.put(MATERIAL, "도시락");
        values.put(PRICE, 10000);
        values.put(CONTENT, "도시락 어쩌구");
        values.put(IMAGE, R.drawable.select_image_lunchbox);
        db.insert(TABLE_NAME, null, values);

        values.clear();

        values.put(RECEIPE_NAME, "Pineapple Recipe");
        values.put(ONE_LINE, "Delicious pineapple recipe");
        values.put(MATERIAL, "PINEAPPLE");
        values.put(PRICE, 15);
        values.put(CONTENT, "Detailed pineapple recipe content");
        db.insert(TABLE_NAME, null, values);


//        values.put(IMAGE, R.drawable.select_image_noodle);
//        values.put(IMAGE, R.drawable.select_image_samgak);
//        values.put(IMAGE, R.drawable.select_image_sandwich);
//        values.put(IMAGE, R.drawable.select_image_coke);
//        values.put(IMAGE, R.drawable.select_image_hotbar);
//        values.put(IMAGE, R.drawable.select_image_hamburger);


        // 다른 초기 데이터도 필요하다면 계속해서 추가
    }

    // 재료 테이블에 재료 추가
    public void addMaterial(SQLiteDatabase db, String materialName, int materialImage, int materialPrice) {
        ContentValues values = new ContentValues();
        values.put(MATERIAL_NAME, materialName);
        values.put(MATERIAL_IMAGE, String.valueOf(materialImage));
        values.put(MATERIAL_PRICE, materialPrice);

        db.insert(TABLE_NAME_MATERIAL, null, values);
    }

    // 레시피와 재료 간의 관계 설정
    public void addRecipeMaterialRelation(long recipeId, String materialName) {
        SQLiteDatabase db = getWritableDatabase();

        // 재료 테이블에서 재료 이름으로부터 ID를 가져옴
        long materialId = getMaterialId(materialName);

        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipeId);
        values.put(COLUMN_MATERIAL_ID, materialId);

        db.insert(TABLE_NAME_RECIPE_MATERIAL_RELATION, null, values);

        db.close();
    }
    // 재료 테이블에 초기 데이터 삽입
    private void insertInitialMaterialData(SQLiteDatabase db) {
        addMaterial(db, "도시락", R.drawable.select_image_lunchbox, 5800);
        addMaterial(db, "핫바", R.drawable.select_image_hotbar, 1800);
    }

    // 재료 이름으로부터 ID를 가져오는 메서드 추가
    private long getMaterialId(String materialName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_ID_MATERIAL};
        String selection = MATERIAL_NAME + "=?";
        String[] selectionArgs = {materialName};
        Cursor cursor = db.query(TABLE_NAME_MATERIAL, columns, selection, selectionArgs, null, null, null);

        long materialId = -1;
        if (cursor.moveToFirst()) {
            materialId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID_MATERIAL));
        }

        cursor.close();
        db.close();

        return materialId;
    }

}