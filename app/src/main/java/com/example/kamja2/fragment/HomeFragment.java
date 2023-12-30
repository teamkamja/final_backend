package com.example.kamja2.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kamja2.R;
import com.example.kamja2.controller.MainActivity;
import com.example.kamja2.controller.RecipeDocActivity;
import com.example.kamja2.controller.Register_receipe;
import com.example.kamja2.controller.SearchListActivity;
import com.example.kamja2.controller.Search_home;


public class HomeFragment extends Fragment {


    private ConstraintLayout searchRecipe, askChatbot;
    private Button search_receipe;
    private TextView shortcut,  newText1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreference = getActivity().getSharedPreferences("recipe", Context.MODE_PRIVATE);
        String newName = sharedPreference.getString("name", "");

        shortcut = view.findViewById(R.id.shortcut);
        search_receipe = view.findViewById(R.id.search_receipe);
        newText1 = view.findViewById(R.id.new1);

        newText1.setText(newName);

//바로가기 하면은 리스트 목록으로 연결
        shortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RecipeDocActivity.class);
                    intent.putExtra("recipeName", "최강참치마요1");
                    startActivity(intent);

                }
            });

        //레시피 찾기 페이지 연결
        search_receipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // fragment에서 fragment 넘어갈 때 쓰는 코드
                Fragment searchFragment = new SearchFragment();

                // FragmentManager를 통한 FragmentTransaction 시작
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // 현재 Fragment를 SearchFragment로 교체
                transaction.replace(R.id.home, searchFragment); // 'fragment_container'는 호스팅 액티비티의 레이아웃 파일 내 Fragment를 담는 컨테이너의 ID

                // Transaction 커밋
                transaction.commit();

                if (getActivity() instanceof MainActivity) { // fragment 이동할 때 네비게이션 탭 상태도 변경하는 코드
                    ((MainActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_search);
                }
            }
        });

        // fragment에서 fragment 넘어갈 때 쓰는 코드
//        Fragment searchFragment = new SearchFragment();
//
//        // FragmentManager를 통한 FragmentTransaction 시작
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        // 현재 Fragment를 SearchFragment로 교체
//        transaction.replace(R.id.home, searchFragment); // 'fragment_container'는 호스팅 액티비티의 레이아웃 파일 내 Fragment를 담는 컨테이너의 ID
//
//        // Transaction 커밋
//        transaction.commit();
//
//        if (getActivity() instanceof MainActivity) { // fragment 이동할 때 네비게이션 탭 상태도 변경하는 코드
//            ((MainActivity) getActivity()).selectBottomNavigationItem(R.id.navigation_search);
//        }







        return view;
    }
}
