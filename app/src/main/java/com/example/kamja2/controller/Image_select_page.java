package com.example.kamja2.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kamja2.R;

public class Image_select_page extends AppCompatActivity {

    private ImageButton  hotbar, noodle, samgak, lunchbox, hamburger, coke;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_ingredients);
        ImageButton sandwich = findViewById(R.id.sandwhich);
        coke = findViewById(R.id.coke);
        hamburger = findViewById(R.id.hamburger);
        lunchbox = findViewById(R.id.lunchbox);
        samgak = findViewById(R.id.samgak);
        noodle = findViewById(R.id.noodle);
        hotbar = findViewById(R.id.hotbar);


        //샌드위치 버튼 선택시
        sandwich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_sandwich);
                startActivity(intent);
                finish();
            }
        });
        //콜라 선택시
        coke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_coke);
                startActivity(intent);
                finish();
            }
        });

        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_hamburger);
                startActivity(intent);
                finish();
            }
        });
        lunchbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_lunchbox);
                startActivity(intent);
                finish();
            }
        });
        samgak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_samgak);
                startActivity(intent);
                finish();
            }
        });
        noodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_noodle);
                startActivity(intent);
                finish();
            }
        });
        hotbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_select_page.this, Register_receipe.class);
                intent.putExtra("imageResourceId", R.drawable.select_image_hotbar);
                startActivity(intent);
                finish();
            }
        });
    }
}
