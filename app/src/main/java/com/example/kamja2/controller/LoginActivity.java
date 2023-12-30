package com.example.kamja2.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamja2.R;
import com.example.kamja2.model.User;
import com.example.kamja2.repository.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView find_id, find_pw, join;
    private EditText inputId, inputPassword;
    private Button loginButton, joinButton;
    private DBHelper dbHelper;
    private User user;

    private View loginScreen;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loginScreen = findViewById(R.id.login_screen);
        inputId = (EditText) findViewById(R.id.id);
        inputId.requestFocus();
        inputPassword = (EditText) findViewById(R.id.pw);
        loginButton = (Button) findViewById(R.id.login_button);
        join = findViewById(R.id.join);

        dbHelper = new DBHelper(this);
        user = new User();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputId.getText().toString();
                String password = inputPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                         //로그인 성공
                                        SharedPreferences sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // 로그인 실패

                                        Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinFirstActivity.class);

                startActivity(intent);
                finishAffinity();

            }
        });

        loginScreen.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard();
                return false;
            }
        });


    }
}