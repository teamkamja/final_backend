package com.example.kamja2.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kamja2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinSecondActivity extends AppCompatActivity {
    private EditText nicknameInput, birthInput;
    private TextView birthText;
    private Button nicknameButton, maleBtn, femaleBtn, etcBtn, completeButton;
    boolean maleClicked = false, femaleClicked=false, etcClicked=false;
    String gender;

    private View joinSecondScreen;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 파이어베이스
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void checkNicknameDuplication(String nickname) {
        db.collection("users")
                .whereEqualTo("nickname", nickname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // 중복 체크
                            if (task.getResult().isEmpty()) {
                                // 닉네임이 중복되지 않음
                                Toast.makeText(JoinSecondActivity.this, "사용 가능한 닉네임입니다", Toast.LENGTH_SHORT).show();
                            } else {
                                // 닉네임이 중복됨
                                Toast.makeText(JoinSecondActivity.this, "이미 존재하는 닉네임입니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 에러 처리
                        }
                    }
                });
    }

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    };

    private static final String DATE_PATTERN = "^\\d{4}.(0[1-9]|1[0-2]).(0[1-9]|[12][0-9]|3[01])$";

    public static boolean isValidDate(String date) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_second);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent  = getIntent();
        String email = intent.getStringExtra("email");
        String pw = intent.getStringExtra("pw");
        System.out.println("닉네임은 " + email + "이고, 비밀번호는 " + pw + "이다");


        joinSecondScreen  = findViewById(R.id.join_second_screen);
        birthText = findViewById(R.id.birth_text);
        nicknameInput = findViewById(R.id.nickname_input);
        birthInput = findViewById(R.id.birth_input);

        nicknameButton = findViewById(R.id.nickname_button);
        maleBtn = findViewById(R.id.male_button);
        femaleBtn = findViewById(R.id.female_button);
        etcBtn = findViewById(R.id.etc_button);

        completeButton = findViewById(R.id.complete_button);



        nicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNicknameDuplication(nicknameInput.getText().toString());
            }
        });

        String text = "생년월일 8자리";
        SpannableString spannableString = new SpannableString(text);


        // '8' 문자의 시작과 끝 인덱스 찾기
        int startIndex = text.indexOf("8");
        int endIndex = startIndex + 1;

// '8' 문자에 대한 스타일 설정
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#707070")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 진한 색상 적용

// TextView에 스타일 적용된 텍스트 설정
        birthText.setText(spannableString);


        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maleClicked = true;
                femaleClicked = false;
                etcClicked = false;

                gender = maleBtn.getText().toString();

                //클릭된 버튼의 배경 변경
                maleBtn.setBackgroundResource(R.drawable.join_gender_button_clicked);
                femaleBtn.setBackgroundResource(R.drawable.join_gender_button_normal);
                etcBtn.setBackgroundResource(R.drawable.join_gender_button_normal);

                //클릭된 버튼의 색깔 변경
                maleBtn.setTextColor(Color.parseColor("#19B3F5"));
                femaleBtn.setTextColor(Color.parseColor("#FFFFFF"));
                etcBtn.setTextColor(Color.parseColor("#FFFFFF"));

                //클릭된 버튼 텍스트 굵게 설정
                maleBtn.setTypeface(null, Typeface.BOLD);

            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maleClicked = false;
                femaleClicked = true;
                etcClicked = false;

                gender = femaleBtn.getText().toString();

                //클릭된 버튼의 배경 변경
                femaleBtn.setBackgroundResource(R.drawable.join_gender_button_clicked);
                maleBtn.setBackgroundResource(R.drawable.join_gender_button_normal);
                etcBtn.setBackgroundResource(R.drawable.join_gender_button_normal);

                //클릭된 버튼의 색깔 변경
                femaleBtn.setTextColor(Color.parseColor("#19B3F5"));
                maleBtn.setTextColor(Color.parseColor("#FFFFFF"));
                etcBtn.setTextColor(Color.parseColor("#FFFFFF"));

                //클릭된 버튼 텍스트 굵게 설정
                femaleBtn.setTypeface(null, Typeface.BOLD);
            }
        });

        etcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maleClicked = false;
                femaleClicked = false;
                etcClicked = true;

                gender = etcBtn.getText().toString();

                //클릭된 버튼의 배경 변경
                etcBtn.setBackgroundResource(R.drawable.join_gender_button_clicked);
                femaleBtn.setBackgroundResource(R.drawable.join_gender_button_normal);
                maleBtn.setBackgroundResource(R.drawable.join_gender_button_normal);

                //클릭된 버튼의 텍스트 색상 변경
                etcBtn.setTextColor(Color.parseColor("#19B3F5"));
                maleBtn.setTextColor(Color.parseColor("#FFFFFF"));
                femaleBtn.setTextColor(Color.parseColor("#FFFFFF"));

                //클릭된 버튼 텍스트 굵게 설정
                etcBtn.setTypeface(null, Typeface.BOLD);
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = nicknameInput.getText().toString();
                String birth = birthInput.getText().toString();

                System.out.println("maleClicked: " + maleClicked + " femalClicked: " + femaleClicked + " etcClicked: " + etcClicked);
                if (nickname.isEmpty()) {
                    Toast.makeText(JoinSecondActivity.this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (birth.isEmpty()) {
                    Toast.makeText(JoinSecondActivity.this, "생년월일을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(!isValidDate(birth)){
                    Toast.makeText(JoinSecondActivity.this, "생년월일은 8자리 숫자입니다", Toast.LENGTH_SHORT).show();
                }else if(maleClicked == false && femaleClicked == false && etcClicked == false) {
                    Toast.makeText(JoinSecondActivity.this, "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                }else {
                    System.out.println("닉네임은 " + nickname);
                    System.out.println("생일은 " + birth);
                    System.out.println("gender는 " + gender);

                    Map<String, Object> userInfo = new HashMap<>(); // 사용자가 입력한 정보들을 Map<String, Object> 형태로 저장
                    userInfo.put("email", email);
                    userInfo.put("pw", pw);
                    userInfo.put("nickname", nickname);
                    userInfo.put("birth", birth);
                    userInfo.put("gender", gender);

                    mAuth.createUserWithEmailAndPassword(email, pw)
                            .addOnCompleteListener(JoinSecondActivity.this, (OnCompleteListener<AuthResult>) task -> {
                                if (task.isSuccessful()) {
                                    db.collection("users").add(userInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(JoinSecondActivity.this, "가입 성공", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(JoinSecondActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("Error adding document: " + e);
                                                }
                                            });
                                    // 회원가입 성공

                                }
                                else{
                                    Toast.makeText(JoinSecondActivity.this, "가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            });



                }

                // 상단 코드 오류 발생해서 그냥 다음 버튼 누르면 이동하게 설정해놓음
//                Intent intent = new Intent(JoinSecondActivity.this, JoinLastActivity.class);
//                startActivity(intent);
            }


        });

        joinSecondScreen.setOnTouchListener(new View.OnTouchListener()
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
