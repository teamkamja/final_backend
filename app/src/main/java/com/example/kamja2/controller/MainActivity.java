package com.example.kamja2.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.kamja2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // 로그인이 안 되어있다면 LoginActivity를 시작한다
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // MainActivity를 종료하여 뒤로 가기 버튼을 눌렀을 때 MainActivity로 돌아오지 않도록 한다
            return; // 로그인 액티비티로 이동 후 이후의 코드 실행 방지
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.nav_view);

        // NavHostFragment의 참조를 가져온다.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.container_main);

        // NavController를 가져온다. 하단 네비게이션바 관련 코드, 각각의 아이템을 클릭하면 해당 fragment로 이동
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // BottomNavigationView와 NavController를 연결한다.
        if (navController != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }


    }

    public void selectBottomNavigationItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

}
