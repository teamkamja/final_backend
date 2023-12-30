package com.example.kamja2.controller;
//레시피 등록 페이지

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.kamja2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register_receipe extends AppCompatActivity {
    private EditText receipeNameInput, introduceInput, ingredientNameInput, ingredientNameInput2, ingredientPriceInput, ingredientPriceInput2, contentInput;
    private Button checkBtn, addBtn, registerBtn;
    private TextView totalPrice, backBtn;

    private List<EditText> allPriceInputs = new ArrayList<>();

    private List<EditText> allProducts = new ArrayList<>();

    private List<String> allCategories = new ArrayList<>();
    private int checkCount = 0;

    private ArrayAdapter<String> adapter;

    String[] items = new String[]{"종류선택", "삼각김밥", "도시락", "샌드위치", "햄버거", "핫바", "컵라면"};

    private View registerScreen, lastAddedLayout;

    private ConstraintLayout registerBox, registerFirst, registerSecond;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_receipe);
//레이아웃 객체들 가져오기
        registerScreen = findViewById(R.id.register_screen);
        receipeNameInput = findViewById(R.id.name_input);
        introduceInput = findViewById(R.id.introduce_input);
        ingredientNameInput = findViewById(R.id.ingredient_name_input);
        ingredientNameInput2 = findViewById(R.id.ingredient_name_input2);
        ingredientPriceInput = findViewById(R.id.ingredient_price_input);
        ingredientPriceInput2 = findViewById(R.id.ingredient_price_input2);
        contentInput = findViewById(R.id.content_input);
        checkBtn = findViewById(R.id.check_btn);
        addBtn = findViewById(R.id.add_btn);
        registerBtn = findViewById(R.id.register_btn);
        totalPrice = findViewById(R.id.total_price);
        registerBox = findViewById(R.id.register_box);
        registerFirst = findViewById(R.id.register_first);
        registerSecond = findViewById(R.id.register_second);
        backBtn = findViewById(R.id.back_btn);
        Spinner spinner = findViewById(R.id.spinner1);
        Spinner spinner2 = findViewById(R.id.spinner2);

        adapter = new ArrayAdapter<>(Register_receipe.this, R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = items[position];
                // selectedItem을 사용하여 필요한 작업 수행
                if(position != 0){
                    allCategories.add(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });

        spinner2.setAdapter(adapter);
        spinner2.setSelection(0);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = items[position];
                // selectedItem을 사용하여 필요한 작업 수행
                if(position != 0){
                    allCategories.add(selectedItem);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });

        receipeNameInput.requestFocus();

        lastAddedLayout = registerSecond;

        allProducts.add(ingredientNameInput);
        allProducts.add(ingredientNameInput2);

        ingredientPriceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateTotalPrice();
            }


        });

        allPriceInputs.add(ingredientPriceInput);

        ingredientPriceInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateTotalPrice();
            }


        });

        allPriceInputs.add(ingredientPriceInput2);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard();
                return false;
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(receipeNameInput.getText().length() == 0){
                    Toast.makeText(Register_receipe.this, "레시피 이름을 작성해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    if(checkCount % 2 == 0){
                        Toast.makeText(Register_receipe.this, "중복된 레시피 이름입니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Register_receipe.this, "사용 가능한 레시피 이름입니다", Toast.LENGTH_SHORT).show();
                    }

                }
                checkCount = checkCount + 1;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View firstChildLayout = registerBox.getChildAt(1);

                System.out.println("클릭됨");

                ConstraintLayout newRegisterLayout = new ConstraintLayout(Register_receipe.this);
                newRegisterLayout.setId(View.generateViewId()); // 동적으로 ID 생성

                // 새로운 ConstraintLayout의 레이아웃 파라미터 설정
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                newRegisterLayout.setLayoutParams(layoutParams);
                newRegisterLayout.setBackgroundResource(R.drawable.introduce_receipe_background);
                newRegisterLayout.setPadding(
                        dpToPx(10),
                        dpToPx(10),
                        dpToPx(10),
                        dpToPx(10)
                );

                Spinner newSpinner = createSpinner();
                newSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = items[position];
                        // selectedItem을 사용하여 필요한 작업 수행
                        if(position != 0){
                            allCategories.add(selectedItem);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // 아무것도 선택되지 않았을 때의 처리
                    }
                });

                EditText newIngredientNameInput = createEditText("재료(상품명)을 입력해주세요");
                allProducts.add(newIngredientNameInput);

                EditText newIngredientPriceInput = createEditText("가격을 입력해주세요");
                newIngredientPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                newIngredientPriceInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        updateTotalPrice();
                    }
                });

                allPriceInputs.add(newIngredientPriceInput);

                TextView newPriceLabel = createTextView();

                newRegisterLayout.addView(newSpinner);
                newRegisterLayout.addView(newIngredientNameInput);
                newRegisterLayout.addView(newIngredientPriceInput);
                newRegisterLayout.addView(newPriceLabel);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(newRegisterLayout);

                // newSpinner에 대한 제약 설정
                constraintSet.connect(newSpinner.getId(), ConstraintSet.TOP, newRegisterLayout.getId(), ConstraintSet.TOP);
                constraintSet.connect(newSpinner.getId(), ConstraintSet.BOTTOM, newIngredientNameInput.getId(), ConstraintSet.TOP);
                constraintSet.connect(newSpinner.getId(), ConstraintSet.START, newIngredientNameInput.getId(), ConstraintSet.START);
                constraintSet.connect(newSpinner.getId(), ConstraintSet.END, newIngredientNameInput.getId(), ConstraintSet.END);

                // newIngredientNameInput에 대한 제약 설정
                constraintSet.connect(newIngredientNameInput.getId(), ConstraintSet.TOP, newSpinner.getId(), ConstraintSet.BOTTOM);
                constraintSet.connect(newIngredientNameInput.getId(), ConstraintSet.BOTTOM, newIngredientPriceInput.getId(), ConstraintSet.TOP);
                constraintSet.connect(newIngredientNameInput.getId(), ConstraintSet.START, newIngredientNameInput.getId(), ConstraintSet.START);

                // newIngredientPriceInput에 대한 제약 설정
                constraintSet.connect(newIngredientPriceInput.getId(), ConstraintSet.TOP, newIngredientNameInput.getId(), ConstraintSet.BOTTOM);
                constraintSet.connect(newIngredientPriceInput.getId(), ConstraintSet.START, newIngredientPriceInput.getId(), ConstraintSet.START);

                // newPriceLabel에 대한 제약 설정
                constraintSet.connect(newPriceLabel.getId(), ConstraintSet.TOP, newIngredientPriceInput.getId(), ConstraintSet.TOP);
                constraintSet.connect(newPriceLabel.getId(), ConstraintSet.BOTTOM, newIngredientPriceInput.getId(), ConstraintSet.BOTTOM);
                constraintSet.connect(newPriceLabel.getId(), ConstraintSet.END, newIngredientPriceInput.getId(), ConstraintSet.END);

                constraintSet.applyTo(newRegisterLayout);

                // 부모 레이아웃에 새로운 레이아웃 추가
                registerBox.addView(newRegisterLayout,  registerBox.indexOfChild(lastAddedLayout)+1);

                ConstraintSet constraintSet1 = new ConstraintSet();
                constraintSet1.clone(registerBox);
                constraintSet1.connect(lastAddedLayout.getId(), ConstraintSet.BOTTOM, newRegisterLayout.getId(), ConstraintSet.TOP);
                constraintSet1.connect(newRegisterLayout.getId(), ConstraintSet.TOP, lastAddedLayout.getId(), ConstraintSet.BOTTOM, 10);
                constraintSet1.connect(newRegisterLayout.getId(), ConstraintSet.BOTTOM, findViewById(R.id.add_btn).getId(), ConstraintSet.TOP, 10);
                constraintSet1.connect(newRegisterLayout.getId(), ConstraintSet.START, registerBox.getId(), ConstraintSet.START);
                constraintSet1.connect(newRegisterLayout.getId(), ConstraintSet.END, registerBox.getId(), ConstraintSet.END);
                constraintSet1.connect(findViewById(R.id.add_btn).getId(), ConstraintSet.TOP, lastAddedLayout.getId(), ConstraintSet.BOTTOM);
                constraintSet1.applyTo(registerBox);

                lastAddedLayout = newRegisterLayout;


            }


        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> allNames = new ArrayList<>();
                List<Integer> allPrices = new ArrayList<>();

                for(int i=0; i<allProducts.size(); i++){
                    if(allProducts.get(i).getText().length() != 0){
                        allNames.add(allProducts.get(i).getText().toString());
                    }
                }

                for(int i=0; i<allPriceInputs.size(); i++){
                    if(allPriceInputs.get(i).getText().length() != 0){
                        allPrices.add(Integer.parseInt(allPriceInputs.get(i).getText().toString()));
                    }
                }




                if(receipeNameInput.getText().length() == 0){
                    Toast.makeText(Register_receipe.this, "레시피 이름을 작성해주세요", Toast.LENGTH_SHORT).show();
                }else if(introduceInput.getText().length() == 0){
                    Toast.makeText(Register_receipe.this, "레시피 소개를 작성해주세요", Toast.LENGTH_SHORT).show();
                }else if(introduceInput.getText().length() == 0){
                    Toast.makeText(Register_receipe.this, "레시피 소개를 작성해주세요", Toast.LENGTH_SHORT).show();
                }else if(allCategories.size() == 0){
                    Toast.makeText(Register_receipe.this, "종류를 선택해주세요", Toast.LENGTH_SHORT).show();
                }else if(allNames.size() == 0){
                    Toast.makeText(Register_receipe.this, "재료를 등록해주세요", Toast.LENGTH_SHORT).show();
                }else if(allPrices.size() == 0 || (allNames.size() != allPrices.size())){
                    Toast.makeText(Register_receipe.this, "가격을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if(contentInput.getText().length() == 0) {
                    Toast.makeText(Register_receipe.this, "레시피 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i=0; i<allNames.size(); i++){
                        System.out.println("재료" + i + ": " + allNames.get(i));
                    }

                    String categories = "";
                    String ingredients = "";
                    String prices = "";

                    for(int i=0; i<allCategories.size(); i++){
                        categories = categories + allCategories.get(i) + ",";
                    }

                    for(int i=0; i<allNames.size(); i++){
                        ingredients = ingredients + allNames.get(i) + ",";
                    }

                    for(int i=0; i<allPrices.size(); i++){
                        prices = prices + allPrices.get(i) + ",";
                    }

                    Map<String, Object> recipe = new HashMap<>();
                    recipe.put("name", receipeNameInput.getText().toString());
                    recipe.put("introduction", introduceInput.getText().toString());
                    recipe.put("categories", allCategories);
                    recipe.put("ingredients", allNames);
                    recipe.put("prices", allPrices);
                    recipe.put("time", Timestamp.now());
                    recipe.put("content", contentInput.getText().toString());

                    // Firestore에 데이터 저장
                    db.collection("recipes").add(recipe)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Register_receipe.this, "레시피가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error adding document", e);
                                    Toast.makeText(Register_receipe.this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    SharedPreferences sharedPref = getSharedPreferences("recipe", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("name", receipeNameInput.getText().toString());
//// 여기에 다른 데이터를 저장하는 코드를 추가합니다.
                    editor.apply();

//                    System.out.println(sharedPref.getAll());

                    finish();
                }
            }
        });


    }

    private void updateTotalPrice() {
        int totalPrice = 0;
        for (EditText priceInput : allPriceInputs) {
            String priceText = priceInput.getText().toString();
            if (!priceText.isEmpty()) {
                totalPrice += Integer.parseInt(priceText);
            }
        }
        TextView total_price = findViewById(R.id.total_price);
        total_price.setText(String.valueOf(totalPrice) + " ￦");
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public Spinner createSpinner(){
        Spinner newSpinner = new Spinner(Register_receipe.this);
        newSpinner.setId(View.generateViewId());
        newSpinner.setAdapter(adapter);
        newSpinner.setSelection(0);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.matchConstraintPercentWidth = 0.5f;
        layoutParams.setMarginEnd(dpToPx(10)); // android:layout_marginEnd="10dp"

        // Spinner에 레이아웃 파라미터 적용
        newSpinner.setLayoutParams(layoutParams);
        newSpinner.setBackgroundResource(R.drawable.spinner_background);
        return newSpinner;
    }

    public EditText createEditText(String hintText){
        EditText newIngredientNameInput = new EditText(Register_receipe.this);
        newIngredientNameInput.setId(View.generateViewId());
        newIngredientNameInput.setHint(hintText);
        newIngredientNameInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        newIngredientNameInput.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                dpToPx(0)
        ));
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        newIngredientNameInput.setPadding(dpToPx(10), dpToPx(10),dpToPx(10), dpToPx(10));

        return newIngredientNameInput;
    };

    public TextView createTextView(){
        TextView newPriceLabel = new TextView(Register_receipe.this);
        newPriceLabel.setId(View.generateViewId());
        newPriceLabel.setText("￦");
        //newPriceLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.rightMargin = dpToPx(15);
        newPriceLabel.setLayoutParams(layoutParams);

        return newPriceLabel;
    }



}
