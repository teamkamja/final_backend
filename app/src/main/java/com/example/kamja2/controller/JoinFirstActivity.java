package com.example.kamja2.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kamja2.model.User;
import com.example.kamja2.repository.DBHelper;
import com.example.kamja2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinFirstActivity extends AppCompatActivity {
    private EditText emailInput, pwInput, pwCheckInput;
    private Button nextButton;
    private DBHelper dbHelper;
    private User user;

    private View joinFirstScreen;

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    };

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z]).{8,})";

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_first);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        joinFirstScreen = findViewById(R.id.join_first_screen);
        nextButton = findViewById(R.id.next_button);
        emailInput = findViewById(R.id.email_input);
        pwInput = findViewById(R.id.pw_input);
        pwCheckInput = findViewById(R.id.pwCheck_input);



        dbHelper = new DBHelper(this);
        user = new User();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String pw = pwInput.getText().toString();
                String pwCheck = pwCheckInput.getText().toString();

                if(email.isEmpty() || !validateEmail(email)){
                    Toast.makeText(JoinFirstActivity.this, "유효하지 않는 이메일 주소입니다", Toast.LENGTH_SHORT).show();
                }else if(pw.isEmpty() || !validatePassword(pw)) {
                    Toast.makeText(JoinFirstActivity.this, "비밀번호는 8자 이상, 숫자와 문자를 포함해야 합니다", Toast.LENGTH_SHORT).show();
                }else if(pwCheck.isEmpty()){
                    Toast.makeText(JoinFirstActivity.this, "비밀번를 재입력 해주세요", Toast.LENGTH_SHORT).show();
                }else if(!pw.equals(pwCheck)){
                    Toast.makeText(JoinFirstActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(JoinFirstActivity.this, JoinSecondActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("pw", pw);
                    startActivity(intent);
                    finishAffinity();

                }

//                Intent intent = new Intent(JoinFirstActivity.this, JoinSecondActivity.class);
//                startActivity(intent);


            }
        });

        joinFirstScreen.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard();
                return false;
            }
        });

//        joinButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = inputEmail.getText().toString();
//                String password = inputPassword.getText().toString();
//                String passwordCheck = inputPasswordCheck.getText().toString();
//                String nickname = inputNickname.getText().toString();
//
//                user.setUsername(email);
//                user.setPassword(password);
//                user.setNickname(nickname);
//
//                if(email.isEmpty() || !validateEmail(email)){
//                    Toast.makeText(JoinFirstActivity.this, "유효하지 않는 이메일 주소입니다", Toast.LENGTH_SHORT).show();
//                }else if(password.isEmpty() || !validatePassword(password)) {
//                    Toast.makeText(JoinFirstActivity.this, "비밀번호는 8자 이상, 숫자와 문자를 포함해야 합니다", Toast.LENGTH_SHORT).show();
//                }else if(passwordCheck.isEmpty()){
//                    Toast.makeText(JoinFirstActivity.this, "비밀번호 확인 미입력", Toast.LENGTH_SHORT).show();
//                }else if(!password.equals(passwordCheck)){
//                    Toast.makeText(JoinFirstActivity.this, "비밀번호 불일치", Toast.LENGTH_SHORT).show();
//                }else if(nickname.isEmpty()){
//                    Toast.makeText(JoinFirstActivity.this, "닉네임 미입력", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(JoinFirstActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
//
//                    // 데이터베이스에 입력 정보 저장하는 코드 작성
//                    dbHelper.addUser(user);
//
//                    finish();
//                }
//            }
//        });


    }
}
