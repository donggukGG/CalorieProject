package com.example.new1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.Tasks;
import java.util.concurrent.ExecutionException;

import java.util.HashMap;
import java.util.Map;

public class DiaryFragment extends Fragment {

    private DatabaseReference foodsRef;

    private EditText foodEditText1, foodEditText2, foodEditText3, foodEditText4;
    private Button saveButton;
    private TextView recommendedCaloriesTextView, remainingCaloriesTextView, consumedCaloriesTextView;

    private Map<String, Integer> consumedCaloriesMap = new HashMap<>();
    private Map<String, EditText> foodEditTextMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        foodsRef = firebaseDatabase.getReference("foods");

        foodEditText1 = view.findViewById(R.id.foodEditText1);
        foodEditText2 = view.findViewById(R.id.foodEditText2);
        foodEditText3 = view.findViewById(R.id.foodEditText3);
        foodEditText4 = view.findViewById(R.id.foodEditText4);

        saveButton = view.findViewById(R.id.saveButton);
        recommendedCaloriesTextView = view.findViewById(R.id.recommendedCaloriesTextView);
        remainingCaloriesTextView = view.findViewById(R.id.remainingCaloriesTextView);
        consumedCaloriesTextView = view.findViewById(R.id.consumedCaloriesTextView);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalories();
            }
        });

        return view;
    }

    private String getCategoryForFood(String foodName) {
        if (containsFood(foodName, "김치찌개", "된장찌개", "갈비찜", "제육볶음", "순대국밥", "뼈해장국", "비빔밥", "불고기", "된장찌개", "김치찌개", "고등어조림", "계란찜", "갈비찜")) {
            return "한식";
        } else if (containsFood(foodName, "짜장면", "짬뽕", "탕수육", "탕수육", "크림새우", "짬뽕", "짜장면", "오징어냉채", "새우볶음밥", "마파두부", "마라탕", "깐풍기", "고추잡채")) {
            return "중식";
        } else if (containsFood(foodName, "텐동", "타코야끼", "초밥", "우동", "오코노미야끼", "야끼소바", "메밀소바", "라면", "돈까스", "가라아게")) {
            return "일식";
        } else if (containsFood(foodName, "햄버거", "피자", "팬케이크", "파스타", "스프", "스파게티", "스테이크", "리조또", "뇨끼", "감바스")) {
            return "양식";
        } else {
            return "간식";
        }
    }

    private boolean containsFood(String foodName, String... foods) {
        for (String food : foods) {
            if (foodName.contains(food)) {
                return true;
            }
        }
        return false;
    }


    private void calculateCalories() {
        String foodName1 = foodEditText1.getText().toString().trim().toLowerCase();
        String foodName2 = foodEditText2.getText().toString().trim().toLowerCase();
        String foodName3 = foodEditText3.getText().toString().trim().toLowerCase();
        String foodName4 = foodEditText4.getText().toString().trim().toLowerCase();

        calculateCaloriesForFood(foodName1, foodEditText1);
        calculateCaloriesForFood(foodName2, foodEditText2);
        calculateCaloriesForFood(foodName3, foodEditText3);
        calculateCaloriesForFood(foodName4, foodEditText4);
    }

    private void calculateCaloriesForFood(String foodName, EditText editText) {
        DatabaseReference userFoodsRef = foodsRef.child(getCategoryForFood(foodName));

        userFoodsRef.child(foodName).child("calories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int consumedCalories = dataSnapshot.getValue(Integer.class);
                    Log.d("DiaryFragment", "음식명: " + foodName + ", 소비된 칼로리: " + consumedCalories);

                    // Firebase에서 데이터를 받아온 후 계산된 칼로리 값을 사용하여 업데이트
                    handleCaloriesData(foodName, consumedCalories);

                    // 사용자가 입력한 음식의 칼로리를 표시
                    editText.setText(consumedCalories + " 칼로리");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DiaryFragment", "Firebase에서 데이터 가져오기 실패", databaseError.toException());
                // 에러 처리
            }
        });
    }

    private void handleCaloriesData(String foodName, int calories) {
        int currentCalories = consumedCaloriesMap.containsKey(foodName) ?
                consumedCaloriesMap.get(foodName) : 0;
        consumedCaloriesMap.put(foodName, currentCalories + calories);

        // onDataChange 내에서 updateTotalCaloriesTextView 호출
        updateTotalCaloriesTextView();
    }

    private void updateTotalCaloriesTextView() {
        int recommendedCalories = 2900;
        int totalConsumedCalories = 0;

        for (Map.Entry<String, Integer> entry : consumedCaloriesMap.entrySet()) {
            totalConsumedCalories += entry.getValue();
        }

        int remainingCalories = recommendedCalories - totalConsumedCalories;

        consumedCaloriesTextView.setText("섭취한 칼로리: " + totalConsumedCalories);
        remainingCaloriesTextView.setText("남은 칼로리: " + remainingCalories);
    }
}















