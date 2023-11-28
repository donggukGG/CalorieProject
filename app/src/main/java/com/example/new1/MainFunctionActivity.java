package com.example.new1;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainFunctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_function);

        // 사용자 로그인 상태 확인
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // 로그인되지 않은 상태면 LoginActivity로 이동
            Intent intent = new Intent(MainFunctionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 사용자가 로그인된 상태면 추가 작업 수행
            // 여기에는 현재 로그인된 사용자의 정보를 사용하여 필요한 작업을 수행할 수 있습니다.
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("다이어리"));
        tabLayout.addTab(tabLayout.newTab().setText("단식 타이머"));
        tabLayout.addTab(tabLayout.newTab().setText("프로필"));

        loadFragment(new DiaryFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new DiaryFragment();
                        break;
                    case 1:
                        selectedFragment = new TimeFragment();
                        break;
                    case 2:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}