package com.example.kamja2.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kamja2.R;
import com.example.kamja2.controller.RecipeDocActivity;
import com.example.kamja2.controller.SearchListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SavedFragment extends Fragment {

    String category, name;

    private List<String> recipeOrderList = new ArrayList<>();

    private TextView save, recipeName, recipeIntroduce, content1, content2;

    private ConstraintLayout recipeBox, mainBox;

    private EditText searchInput;

    private ImageView searchBtn;

    private boolean saveClicked = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private int previousId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

//        category = getIntent().getStringExtra("Category");
//
//        name = getIntent().getStringExtra("name");
        recipeName = view.findViewById(R.id.recipe_name);
        save = view.findViewById(R.id.save);
        recipeIntroduce = view.findViewById(R.id.recipe_introduce);
        content1 = view.findViewById(R.id.content1);
        content2 = view.findViewById(R.id.content2);
        recipeBox = view.findViewById(R.id.recipe_box);
        mainBox = view.findViewById(R.id.main_box);
        searchInput = view.findViewById(R.id.search_input);
        searchBtn = view.findViewById(R.id.search_btn);

        previousId = recipeBox.getId();

//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                category = searchInput.getText().toString();
//                Intent intent = new Intent(SearchListActivity.this, SearchListActivity.class);
//                intent.putExtra("Category", category);
//                finish();
//                startActivity(intent);
//            }
//        });


        db.collection("save")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println("파이어스토어에서 결과를 불러왔습니다!");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 새로운 레시피 박스 생성 및 설정
                                ConstraintLayout recipeBox = createRecipeBox(document);


                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(mainBox); // parentLayout is the ConstraintLayout in which recipeBox will be added
                                constraintSet.connect(recipeBox.getId(), ConstraintSet.START, mainBox.getId(), ConstraintSet.START);
                                constraintSet.connect(recipeBox.getId(), ConstraintSet.END, mainBox.getId(), ConstraintSet.END);
                                constraintSet.connect(recipeBox.getId(), ConstraintSet.TOP, previousId, previousId == view.findViewById(R.id.recipe_box).getId() ? ConstraintSet.TOP : ConstraintSet.BOTTOM);
                                constraintSet.setDimensionRatio(recipeBox.getId(), "w,1:2.5");

                                mainBox.addView(recipeBox);
                                constraintSet.applyTo(mainBox);

                                previousId = recipeBox.getId();

                            }
                        } else {
                            System.out.println("파이어스토에서 정보를 못갖고왔습니다!");
                            ;
                        }
                    }
                });


        // 레시피 아이템 클릭하면 해당 상세정보로 이동

        return view;
    }


    private ConstraintLayout createRecipeBox(QueryDocumentSnapshot document) {
        ConstraintLayout recipeBox = new ConstraintLayout(getActivity());
        recipeBox.setId(View.generateViewId());
        ConstraintLayout.LayoutParams recipeBoxParams = new ConstraintLayout.LayoutParams(
                0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        recipeBoxParams.topMargin = dpToPx(20);
        recipeBoxParams.bottomMargin = dpToPx(20);
        //layoutParams.setMargins(0, dpToPx(20), 0, 0);
        recipeBox.setLayoutParams(recipeBoxParams);
        recipeBox.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        recipeBox.setBackgroundResource(R.drawable.list_item_background);

        ConstraintLayout layout = new ConstraintLayout(getActivity());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = dpToPx(5);
        layout.setLayoutParams(layoutParams);
        layout.setId(View.generateViewId());

        // ImageView 생성 및 설정
        ImageView imageView = new ImageView(getActivity());
        imageView.setId(View.generateViewId());
        imageView.setImageResource(R.drawable.samkim);
        ConstraintLayout.LayoutParams imgParams = new ConstraintLayout.LayoutParams(0, dpToPx(50));
        imageView.setLayoutParams(imgParams);

        // Info Box (ConstraintLayout) 생성 및 설정
        ConstraintLayout infoBox = new ConstraintLayout(getActivity());
        infoBox.setId(View.generateViewId());
        ConstraintLayout.LayoutParams infoParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        infoBox.setLayoutParams(infoParams);

        String introduction = document.getString("introduction").toString();
        String ct1 = document.getString("content1");
        String ct2 = document.getString("content2");
        // TextViews 생성 및 설정
        TextView recipeIntroduce = createTextView1(introduction, 10, Typeface.NORMAL);
        recipeIntroduce.setId(View.generateViewId());
        recipeIntroduce.setSingleLine();
        TextView content1 = createTextView1(ct1, 10, Typeface.NORMAL);
        content1.setId(View.generateViewId());
        content1.setSingleLine();
        TextView content2 = createTextView1(ct2, 10, Typeface.NORMAL);
        content2.setId(View.generateViewId());
        content2.setSingleLine();

        TextView save = createTextView("저장", 12,Typeface.NORMAL );


        TextView name = createTextView(document.getString("recipeName"), 15,Typeface.BOLD);
        name.setTextColor(Color.BLACK);

        String recipeName1 = document.getString("recipeName");
        checkSaveStatus(recipeName1, save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSaveStatus(recipeName1, save);

                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });


        recipeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecipeDocActivity.class);
                intent.putExtra("recipeName", recipeName1); // 레시피 이름을 인텐트에 추가
                startActivity(intent); // 새 액티비티 시작
            }
        });





        // 뷰들을 부모 레이아웃에 추가
        layout.addView(imageView);
        layout.addView(infoBox);
        infoBox.addView(recipeIntroduce);
        infoBox.addView(content1);
        infoBox.addView(content2);

        recipeBox.addView(save);
        recipeBox.addView(name);
        recipeBox.addView(layout);

        ConstraintSet infoBoxSet = new ConstraintSet();
        infoBoxSet.clone(infoBox);

        infoBoxSet.connect(recipeIntroduce.getId(), ConstraintSet.TOP, infoBox.getId(), ConstraintSet.TOP);
        infoBoxSet.connect(recipeIntroduce.getId(), ConstraintSet.BOTTOM, content1.getId(), ConstraintSet.TOP);
        infoBoxSet.connect(recipeIntroduce.getId(), ConstraintSet.START, infoBox.getId(), ConstraintSet.START);

        infoBoxSet.connect(content1.getId(), ConstraintSet.TOP, recipeIntroduce.getId(), ConstraintSet.BOTTOM);
        infoBoxSet.connect(content1.getId(), ConstraintSet.BOTTOM, content2.getId(), ConstraintSet.TOP);
        infoBoxSet.connect(content1.getId(), ConstraintSet.START, infoBox.getId(), ConstraintSet.START);

        infoBoxSet.connect(content2.getId(), ConstraintSet.TOP, content1.getId(), ConstraintSet.BOTTOM);
        infoBoxSet.connect(content2.getId(), ConstraintSet.BOTTOM, infoBox.getId(), ConstraintSet.BOTTOM);
        infoBoxSet.connect(content1.getId(), ConstraintSet.START, infoBox.getId(), ConstraintSet.START);

        infoBoxSet.applyTo(infoBox);

        // 제약 설정
        ConstraintSet layoutSet = new ConstraintSet();
        layoutSet.clone(layout);
        layoutSet.connect(imageView.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START);
        layoutSet.connect(imageView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        layoutSet.constrainPercentWidth(imageView.getId(), 0.3f);

        layoutSet.connect(infoBox.getId(), ConstraintSet.START, imageView.getId(), ConstraintSet.END);
        layoutSet.connect(infoBox.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        layoutSet.constrainPercentWidth(infoBox.getId(), 0.7f);

        layoutSet.applyTo(layout);

        // 제약 설정
        ConstraintSet recipeBoxSet = new ConstraintSet();
        recipeBoxSet.clone(recipeBox);

        recipeBoxSet.connect(save.getId(), ConstraintSet.END, recipeBox.getId(), ConstraintSet.END);
        recipeBoxSet.connect(save.getId(), ConstraintSet.TOP, recipeBox.getId(), ConstraintSet.TOP);
        recipeBoxSet.connect(save.getId(), ConstraintSet.BOTTOM, name.getId(), ConstraintSet.TOP);

        recipeBoxSet.connect(name.getId(), ConstraintSet.TOP, save.getId(), ConstraintSet.BOTTOM);
        recipeBoxSet.connect(name.getId(), ConstraintSet.START, recipeBox.getId(), ConstraintSet.START);

        recipeBoxSet.connect(layout.getId(), ConstraintSet.TOP, name.getId(), ConstraintSet.BOTTOM);
        recipeBoxSet.connect(layout.getId(), ConstraintSet.START, recipeBox.getId(), ConstraintSet.START);

        recipeBoxSet.applyTo(recipeBox);

        return recipeBox;
    }

    private void setConstraints(ConstraintLayout recipeBox, ConstraintLayout mainBox) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainBox);

        // 예: constraintSet.connect(...);
        // mainBox에 대한 추가적인 제약 설정

        constraintSet.applyTo(mainBox);
    }

    private TextView createTextView(String text, int textSizeSp, int textStyle) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        textView.setTypeface(null, textStyle);
        textView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));
        textView.setId(View.generateViewId());
        return textView;
    }

    private TextView createTextView1(String text, int textSizeSp, int textStyle) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        textView.setTypeface(null, textStyle);
        textView.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));
        textView.setId(View.generateViewId());
        return textView;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    // Firestore에서 저장 상태 확인
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
    private void toggleSaveStatus(String recipeName, TextView save) {
        db.collection("save").whereEqualTo("recipeName", recipeName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {
                                // 저장되지 않은 상태 -> 저장하기
                                Map<String, Object> data = new HashMap<>();
                                data.put("recipeName", recipeName);
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

}
