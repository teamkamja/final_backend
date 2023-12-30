package com.example.kamja2.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.kamja2.R;
import com.example.kamja2.controller.SearchListActivity;

public class SearchFragment extends Fragment {

    Button tagSearch, wordSearch;
    Button[] categoryButtons = new Button[6];
    Integer[] categoryIDs = {R.id.btnKimbap, R.id.btnLunchBox, R.id.btnSandwich, R.id.btnHamburger, R.id.btnHotbar, R.id.btnCupRamen};
    EditText word;
    String category = ""; // 태그
    String name = ""; // 검색명

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        tagSearch = view.findViewById(R.id.btnTagSearch);
        wordSearch = view.findViewById(R.id.btnWordSearch);
        word = view.findViewById(R.id.edtWord);
        int greenColor = ContextCompat.getColor(requireContext(), R.color.green2);
        int whiteColor = ContextCompat.getColor(requireContext(), R.color.white);
        ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
        ColorStateList colorStateListWithe = ColorStateList.valueOf(whiteColor);
        category = "";
        name = "";
        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i] = view.findViewById(categoryIDs[i]);
        }
        for (int i = 0; i < categoryButtons.length; i++) {
            final int index = i;
            categoryButtons[index].setBackgroundTintList(colorStateListWithe);
            categoryButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    category = categoryButtons[index].getText().toString();
                    for (int j = 0; j < categoryButtons.length; j++) {
                        final int index2 = j;
                        categoryButtons[index2].setBackgroundTintList(colorStateListWithe);
                        categoryButtons[index2].setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)));
                    }
                    categoryButtons[index].setBackgroundTintList(colorStateList);
                    categoryButtons[index].setTextColor(colorStateListWithe);
                }
            });
        }

        // 검색버튼: 누른 버튼의 텍스트 값 = 카테고리를 전달하며 인텐트 이동
        tagSearch.setOnClickListener(new View.OnClickListener() {
            //최소 1개 선택 필수
            @Override
            public void onClick(View v) {
                if (!category.equals("")) {
                    name = word.getText().toString();
                    Intent intent = new Intent(requireActivity(), SearchListActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("Category", category);
                    startActivity(intent);
                }
            }
        });

        wordSearch.setOnClickListener(new View.OnClickListener() {
            //최소 1개 선택 필수
            @Override
            public void onClick(View v) {
                name = word.getText().toString();
                if (!name.equals("")) {
                    Intent intent = new Intent(requireActivity(), SearchListActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("Category", category);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    // 다시 불러 왔을 경우 실행 (ex: 검색 조건 변경, 정령 변경)
    @Override
    public void onResume() {
        super.onResume();
        int greenColor = ContextCompat.getColor(requireContext(), R.color.green2);
        int whiteColor = ContextCompat.getColor(requireContext(), R.color.white);
        ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
        ColorStateList colorStateListWithe = ColorStateList.valueOf(whiteColor);
        category = "";
        name = "";
        for (int j = 0; j < categoryButtons.length; j++) {
            final int index2 = j;
            categoryButtons[index2].setBackgroundTintList(colorStateListWithe);
            categoryButtons[index2].setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)));
        }
    }
}
