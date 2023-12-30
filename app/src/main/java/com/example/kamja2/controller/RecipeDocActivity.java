package com.example.kamja2.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.kamja2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDocActivity extends AppCompatActivity {

    private TextView recipeName, recipeIntroduce, backBtn, save;

    private List<String> ingredientList = new ArrayList<>(), orderList = new ArrayList<>(), priceList = new ArrayList<>(), nameList = new ArrayList<>();

    private ConstraintLayout commentBox,orderListBox, ingredientsDetail;

    private View recipeDetailScreen;

    private EditText commentInput;

    private String nickname, name, introduction, getName;

    private ImageView sendBtn;
    private int previousId, count = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String ct1, ct2, category;
    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_document);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        category = getIntent().getStringExtra("category");
        if(getIntent().getStringExtra("recipeName").equals("최강참치마요1")){
            getName = "최강참치마요";
        }else{
            getName = getIntent().getStringExtra("recipeName");
        }

        System.out.println("가져온 레시피명은 " + getName);
        recipeDetailScreen = findViewById(R.id.recipe_detail_screen);
        backBtn = findViewById(R.id.back_btn);
        recipeName = findViewById(R.id.recipe_name);
        recipeIntroduce = findViewById(R.id.recipe_introduce);
        orderListBox = findViewById(R.id.order_list_box);
        ingredientsDetail = findViewById(R.id.ingredients_detail);
        commentInput = findViewById(R.id.comment_input);
        sendBtn = findViewById(R.id.send_btn);
        commentBox = findViewById(R.id.comment_bottom_box);
        save = findViewById(R.id.save);

        checkSaveStatus(getName, save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSaveStatus(getName, introduction, ct1, ct2, save);
            }
        });

        db.collection("recipes").whereEqualTo("name", getName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name = document.getString("name");
                        introduction = document.getString("introduction");

                       List<Number> prices = (List<Number>) document.get("prices");
                       if(prices != null){
                           for(Number price : prices){
                            priceList.add(String.valueOf(price));
                           }
                       }

                       List<String> ingredients = (List<String>) document.get("ingredients");
                       if(ingredients != null){
                           for(String ingredient : ingredients){
                               nameList.add(String.valueOf(ingredient));
                           }
                       }

                       String[] orders = document.getString("content").split("\n");
                       ct1 = orders[0];
                       ct2 = orders[1];
                       for(String order : orders){
                           orderList.add(order);
                       }

                        recipeName.setText(name);
                        recipeIntroduce.setText(introduction);

                        System.out.println("결과는 " + priceList);

                        ConstraintSet constraintSet = new ConstraintSet();
                        int previousTextViewId = findViewById(R.id.order_list_box).getId(); // 시작점은 부모 레이아웃의 상단

                        for (int i = 0; i < orderList.size(); i++) {
                            TextView textView = new TextView(RecipeDocActivity.this);
                            textView.setId(View.generateViewId()); // 각 TextView에 고유한 ID 생성
                            textView.setText(orderList.get(i));
                            textView.setPadding(0,10,0,10);

                            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(layoutParams);

                            orderListBox.addView(textView);

                            constraintSet.clone(orderListBox);
                            constraintSet.connect(textView.getId(), ConstraintSet.TOP,
                                    previousTextViewId, previousTextViewId == findViewById(R.id.order_list_box).getId() ? ConstraintSet.TOP : ConstraintSet.BOTTOM);
                            constraintSet.applyTo(orderListBox);

                            previousTextViewId = textView.getId(); // 다음 TextView를 위해 ID 업데이트
                        }

                        ConstraintSet constraintSet2 = new ConstraintSet();
                        int previousTextViewId2 = findViewById(R.id.ingredients_detail).getId(); // 시작점은 부모 레이아웃의 상단

                        for (int i = 0; i < nameList.size(); i++) {
                            TextView nameView = new TextView(RecipeDocActivity.this);
                            nameView.setId(View.generateViewId()); // 각 TextView에 고유한 ID 생성
                            nameView.setText(nameList.get(i));
                            nameView.setPadding(0,10,0, 10);

                            TextView priceView = new TextView(RecipeDocActivity.this);
                            priceView.setId(View.generateViewId()); // 각 TextView에 고유한 ID 생성
                            priceView.setText(priceList.get(i)+"￦");
                            priceView.setPadding(0,10,0, 10);

                            TextView line = new TextView(RecipeDocActivity.this);
                            line.setId(View.generateViewId());

                            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.bottomMargin = dpToPx(20);
                            nameView.setLayoutParams(layoutParams);
                            //priceView.setLayoutParams(layoutParams);

//            ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_PARENT, 20);
//            line.setLayoutParams(layoutParams);

                            ingredientsDetail.addView(nameView);
                            ingredientsDetail.addView(priceView);
                            //ingredientsDetail.addView(line);

                            constraintSet2.clone(ingredientsDetail);

                            constraintSet2.connect(nameView.getId(), ConstraintSet.START, ingredientsDetail.getId(), ConstraintSet.START);
                            constraintSet2.connect(nameView.getId(), ConstraintSet.TOP, previousTextViewId2, previousTextViewId2 == findViewById(R.id.ingredients_detail).getId() ? ConstraintSet.TOP : ConstraintSet.BOTTOM);

                            // priceView 제약 설정
                            constraintSet2.connect(priceView.getId(), ConstraintSet.END, ingredientsDetail.getId(), ConstraintSet.END);
                            constraintSet2.connect(priceView.getId(), ConstraintSet.TOP, nameView.getId(), ConstraintSet.TOP);

                            //line 제약 설정
//            constraintSet2.connect(line.getId(), ConstraintSet.TOP, nameView.getId(), ConstraintSet.BOTTOM);
//            constraintSet2.connect(line.getId(), ConstraintSet.START, ingredientsDetail.getId(), ConstraintSet.START);
//            constraintSet2.connect(line.getId(), ConstraintSet.END, ingredientsDetail.getId(), ConstraintSet.END);

                            // 제약 적용
                            constraintSet2.applyTo(ingredientsDetail);

                            // 다음 뷰를 위한 ID 업데이트
                            previousTextViewId2 = nameView.getId();
                        }



                    }
                } else {
                    // 쿼리 실패 처리
                    Log.d("RecipeDocActivity", "Error getting documents: ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        nickname = document.getString("nickname");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        previousId = findViewById(R.id.line2).getId();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentInput.getText().toString();
                commentInput.setText("");

                System.out.println("clicked");

                ConstraintLayout commentChildBox = new ConstraintLayout(RecipeDocActivity.this);
                commentChildBox.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                ));
                commentChildBox.setPadding(dpToPx(15),dpToPx(15),dpToPx(15),dpToPx(15));
                commentChildBox.setId(View.generateViewId());

                ConstraintLayout userInfoBox = new ConstraintLayout(RecipeDocActivity.this);
                userInfoBox.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                ));
                userInfoBox.setId(View.generateViewId());

                TextView nicknameView = new TextView(RecipeDocActivity.this);
                nicknameView.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                ));
                nicknameView.setId(View.generateViewId());
                nicknameView.setText(nickname);

                TextView bar = new TextView(RecipeDocActivity.this);
                ConstraintLayout.LayoutParams barParams =  new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                barParams.leftMargin = dpToPx(10);
                barParams.rightMargin = dpToPx(10);
                bar.setText("|");
                bar.setLayoutParams(barParams);
                bar.setId(View.generateViewId());

                TextView timeView = new TextView(RecipeDocActivity.this);
                ConstraintLayout.LayoutParams timeParams =  new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                //timeParams.leftMargin = dpToPx(10);
                timeView.setLayoutParams(timeParams);
                timeView.setText("지금");
                timeView.setTextSize(10);
                timeView.setId(View.generateViewId());

                userInfoBox.addView(nicknameView);
                userInfoBox.addView(bar);
                userInfoBox.addView(timeView);

                ConstraintSet userInfoSet = new ConstraintSet();
                userInfoSet.clone(userInfoBox);

                userInfoSet.connect(nicknameView.getId(), ConstraintSet.START, userInfoBox.getId(), ConstraintSet.START);
                userInfoSet.connect(nicknameView.getId(), ConstraintSet.TOP, userInfoBox.getId(), ConstraintSet.TOP);

                userInfoSet.connect(bar.getId(), ConstraintSet.START, nicknameView.getId(), ConstraintSet.END);
                userInfoSet.connect(bar.getId(), ConstraintSet.END, timeView.getId(), ConstraintSet.START);
                userInfoSet.connect(bar.getId(), ConstraintSet.TOP, userInfoBox.getId(), ConstraintSet.TOP);

                userInfoSet.connect(timeView.getId(), ConstraintSet.END, userInfoBox.getId(), ConstraintSet.END);
                userInfoSet.connect(timeView.getId(), ConstraintSet.TOP, userInfoBox.getId(), ConstraintSet.TOP);
                userInfoSet.connect(timeView.getId(), ConstraintSet.BOTTOM, userInfoBox.getId(), ConstraintSet.BOTTOM);

                userInfoSet.applyTo(userInfoBox);

                // 코멘트 TextView 생성 및 설정
                TextView commentTextView = new TextView(RecipeDocActivity.this);
                commentTextView.setId(View.generateViewId());
                commentTextView.setText(comment);
                ConstraintLayout.LayoutParams commentParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                commentParams.topMargin = dpToPx(10);
                commentParams.startToStart = commentChildBox.getId();
                commentParams.topToBottom = userInfoBox.getId();
                commentTextView.setLayoutParams(commentParams);

                // 하단 레이아웃 생성 및 설정
                ConstraintLayout bottomLayout = new ConstraintLayout(RecipeDocActivity.this);
                bottomLayout.setId(View.generateViewId());
                ConstraintLayout.LayoutParams bottomLayoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                bottomLayoutParams.topMargin = dpToPx(10);
                bottomLayoutParams.startToStart = commentChildBox.getId();
                bottomLayoutParams.topToBottom = commentTextView.getId();
                bottomLayoutParams.bottomToBottom = commentChildBox.getId();
                bottomLayout.setLayoutParams(bottomLayoutParams);

                // 좋아요 아이콘 ImageView 생성 및 설정
                ImageView likeIcon = new ImageView(RecipeDocActivity.this);
                likeIcon.setId(View.generateViewId());
                likeIcon.setImageResource(R.drawable.heart); // 여기에는 하트 이미지가 있는 drawable 파일을 넣으세요
                likeIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                ConstraintLayout.LayoutParams likeIconParams = new ConstraintLayout.LayoutParams(
                        dpToPx(20),
                        dpToPx(20)
                );
                likeIconParams.startToStart = bottomLayout.getId();
                likeIconParams.endToStart = likeIcon.getId();
                likeIconParams.topToTop = bottomLayout.getId();
                likeIcon.setLayoutParams(likeIconParams);

                // 좋아요 카운트 TextView 생성 및 설정
                TextView likeCountTextView = new TextView(RecipeDocActivity.this);
                likeCountTextView.setId(View.generateViewId());
                likeCountTextView.setText("0");
                likeCountTextView.setTextColor(Color.parseColor("#333333"));
                likeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                ConstraintLayout.LayoutParams likeCountParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                likeCountParams.bottomToBottom = bottomLayout.getId();
                likeCountParams.startToEnd = likeIcon.getId();
                likeCountParams.topToTop = bottomLayout.getId();
                likeCountTextView.setLayoutParams(likeCountParams);

                // 뷰들을 부모 레이아웃에 추가
                commentChildBox.addView(userInfoBox);
                commentChildBox.addView(commentTextView);
                commentChildBox.addView(bottomLayout);
                bottomLayout.addView(likeIcon);
                bottomLayout.addView(likeCountTextView);
                commentBox.addView(commentChildBox);

                ConstraintSet commentBoxSet = new ConstraintSet();
                commentBoxSet.clone(commentBox);

                commentBoxSet.connect(commentChildBox.getId(), ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM);
                commentBoxSet.connect(commentChildBox.getId(), ConstraintSet.START, previousId, ConstraintSet.START);

                commentBoxSet.applyTo(commentBox);

                previousId = commentBox.getId();



            }
        });



        recipeDetailScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard();
                return false;
            }
        });

        commentInput.requestFocus();

        // < 화살표 클릭시 이전 화면으로 돌아가기
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra("recipeName").equals("최강참치마요1")){
                    finish();
                }else{
                    Intent intent = new Intent(RecipeDocActivity.this, SearchListActivity.class);
                    intent.putExtra("Category", category);
                    finish();
                    startActivity(intent);
                }

            }
        });

        //내부저장소에서 MyRecipes 정보 가져오기
//        SharedPreferences sharedPref = getSharedPreferences("MyRecipes", Context.MODE_PRIVATE);
//        String name = sharedPref.getString("recipeName", "");
//        String introduce = sharedPref.getString("recipeIntroduce", "");
//
//        // 레시피 재료 이름들 리스트
//        String names = sharedPref.getString("recipeIngredients", "").substring(1, sharedPref.getString("recipeIngredients", "").length() - 1);
//        String[] allNames = names.split(", ");
//        for (int i = 0; i < allNames.length; i++) {
//            nameList.add(allNames[i].toString());
//        }
//
//        // 레시피 재료 가격들 리스트
//        String prices = sharedPref.getString("recipePrices", "").substring(1, sharedPref.getString("recipePrices", "").length() - 1);
//        String[] priceAll = prices.split(", ");
//        for (int i = 0; i < priceAll.length; i++) {
//            priceList.add(priceAll[i].toString());
//        }
//
//        // 레시피 내용(순서) 리스트
//        String contents = sharedPref.getString("recipeContent", "");
//        String[] contentList = contents.split("\n");
//        for (int i = 0; i < contentList.length; i++) {
//            orderList.add(contentList[i].toString());
//        }


    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void checkSaveStatus(String recipeName, TextView save) {
        db.collection("save").whereEqualTo("recipeName", recipeName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()) {
                            save.setTextColor(Color.parseColor("#FF0000")); // 저장된 상태
                        } else {
                            save.setTextColor(Color.parseColor("#ACACAC")); // 저장되지 않은 상태
                        }
                    }
                });
    }

    // 저장 상태 토글 및 Firestore 업데이트
    private void toggleSaveStatus(String recipeName, String introduction, String ct1, String ct2, TextView save) {
        db.collection("save").whereEqualTo("recipeName", recipeName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {
                                // 저장되지 않은 상태 -> 저장하기
                                Map<String, Object> data = new HashMap<>();
                                data.put("email", mAuth.getCurrentUser().getEmail());
                                data.put("recipeName", recipeName);
                                data.put("introduction", introduction);
                                data.put("content1", ct1);
                                data.put("content2", ct2);
                                db.collection("save").add(data);
                                save.setTextColor(Color.parseColor("#FF0000")); // 색상 변경
                            } else {
                                // 저장된 상태 -> 삭제하기
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("save").document(document.getId()).delete();
                                }
                                save.setTextColor(Color.parseColor("#ACACAC")); // 색상 변경
                            }
                        }
                    }
                });
    }

//        ingredients = findViewById(R.id.ingredients);
//        recipe = findViewById(R.id.lineRecipe);
//        reviwes = findViewById(R.id.reviewLayout);
//        btnBack = findViewById(R.id.btnBack);
//        btnSava = findViewById(R.id.btnSave);
//        edtComment = findViewById(R.id.edtCommnet);
//        btnComment = findViewById(R.id.btnComment);
//
//        InName = findViewById(R.id.InName);
//        InPrice = findViewById(R.id.InPrice);
//        PriceSum = findViewById(R.id.PriceSum); //￦
//        MenuRecipe = findViewById(R.id.txtRecipe);
//        MenuScript = findViewById(R.id.script);
//        MenuName = findViewById(R.id.txtMenuName);
//        like = findViewById(R.id.likes);
//        comments = findViewById(R.id.comments);
//        saves = findViewById(R.id.saves);
//
//        id = Integer.valueOf(getIntent().getStringExtra("id"));
//        s_name = getIntent().getStringExtra("name");
//        s_script = getIntent().getStringExtra("script");
//        s_price = getIntent().getStringExtra("price");
//        s_recipe = getIntent().getStringExtra("recipe").replace(";","\n\n");;
//
//        // TODO: 서브메뉴시 구분 필요
//        InPrice.setText(s_price+"￦");
//        InName.setText(s_name);
//        PriceSum.setText(s_price+"￦");
//
//        MenuName.setText(s_name);
//        MenuScript.setText(s_script);
//        MenuRecipe.setText(s_recipe);
//        like.setText("좋아요" + (20-id)*3);
//        comments.setText("댓글" + (13-id)*3);
//        saves.setText("저장" + (15-id)*3);
//
//        // 뒤로가기
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        // 저장하기 - 임시
//        int greenColor = ContextCompat.getColor(this, R.color.green2);
//        ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
//        btnSava.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btnSava.setBackgroundTintList(colorStateList);
//            }
//        });
//
//
//        Log.i("id",id.toString());
//        // 댓글 불러오기
//        // TODO: 차후 데이터베이스 연동
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ViewGroup parentLayout = reviwes; // 부모 레이아웃의 ID
//        int cnt = 4;
//        for (int i=0; i<cnt; i++)
//        {
//            View childLayout = inflater.inflate(R.layout.menu_review, null);
//            parentLayout.addView(childLayout);
//            LinearLayout head = childLayout.findViewById(R.id.head);
//            head.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
//            TextView nickName = childLayout.findViewById(R.id.nickName);
//            nickName.setText(nickNameData[i+id]);
//            TextView best = childLayout.findViewById(R.id.best);
//            best.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
//            TextView comment = childLayout.findViewById(R.id.comment);
//            comment.setText(commentData[i+id]);
//            TextView likeCNT = childLayout.findViewById(R.id.likeCount);
//            likeCNT.setText("좋아요"+((5-i)*2*(5-i) +7));
//        }
//        // 댓글 등록
//        btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View childLayout = inflater.inflate(R.layout.menu_review, null);
//                parentLayout.addView(childLayout);
//                LinearLayout head = childLayout.findViewById(R.id.head);
//                head.setVisibility(View.VISIBLE);
//                TextView nickName = childLayout.findViewById(R.id.nickName);
//                // 사용자 이름 설정
//                // TODO: 로그인이랑 연동
//                nickName.setText("user-14885");
//                TextView best = childLayout.findViewById(R.id.best);
//                best.setVisibility(View.GONE);
//                TextView comment = childLayout.findViewById(R.id.comment);
//                comment.setText(edtComment.getText().toString());
//                TextView likeCNT = childLayout.findViewById(R.id.likeCount);
//                likeCNT.setText("좋아요"+0);
//            }
//        });
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//    String nickNameData[] = {"친절한 감자씨","우리동네 맛집왕","리아코","승희","맛있는거에만 리뷰 씀","깐깐한 미식가","김태공","포도송이","권우상","미식의 기쁨","Hoon","게맛을 아는 자","로제파스타","별빛이슬","바다여행자","꽃향기","아재로스","제이나"};
//    String commentData[] = {"양이 많아서 좋아요.","저녁마다 이거 사먹어요.","가성비가 좋아서 자주 먹어요.","맛이 미쳤습니다.","퇴근길에 자주 사먹어요.","유튜브에서 이거 보면 무조건 사먹어요"
//            ,"간단하게 먹을 수 있어서 최고","언제나 신뢰할 수 있는 선택","나름의 특별한 맛을 찾을 수 있어요","간식으로 먹기 딱 좋은 간단한 패스트푸드"," 삶에 활력을 더해줘요","간편하면서도 맛있어",
//    "입안에서 녹아내릴 것 같아요, 정말 맛있어요!","향이 정말 환상적이에요.","향이 정말 환상적이에요","먹는 내내 행복해져요","정말 중독성이 강해요","혼자 먹는 게 아까운 맛이에요","정말 눈과 입을 모두 즐겁게 해주네요"};

}