package com.example.kamja2.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kamja2.R;
import com.example.kamja2.controller.LoginActivity;
import com.example.kamja2.controller.Register_receipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class MeFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private TextView emailView, genderView, ageView;

    private String gender, birth;

    private Button logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);


        Button register_receipe_page = view.findViewById(R.id.register_receipe_page);
        emailView = view.findViewById(R.id.email);
        genderView = view.findViewById(R.id.gender);
        ageView = view.findViewById(R.id.age);

        logoutBtn = view.findViewById(R.id.logout_btn);

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        if(mAuth.getCurrentUser() != null){
            String email =  mAuth.getCurrentUser().getEmail();
            emailView.setText(email);

            Query user = firestore.collection("users").whereEqualTo("email", email);
            System.out.println("query user는 " + user);
            user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 여기에서 document 객체를 사용하여 필요한 데이터를 처리합니다.
                            gender = document.getString("gender");
                            birth = document.getString("birth");
                            System.out.println("정답: " + birth.substring(0,4));
                            Integer birth_year = Integer.parseInt(birth.substring(0,4));
                            Integer age = year - birth_year;
                            genderView.setText("성별    " + gender);
                            ageView.setText("나이    " + age + "세");
                        }
                    } else {
                        // 쿼리 실패 시 처리
                        System.out.println("Error getting documents: " +  task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


        register_receipe_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getActivity(), Register_receipe.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(mAuth.getCurrentUser() == null){
            logoutBtn.setText("로그인하러 가기");
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            });
        }else{
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("MyApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();
                    mAuth.signOut();
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            });

        }
        return view;
    }
}
