package com.example.new1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView userEmailTextView;
    private EditText editHeight;
    private EditText editWeight;
    private Button saveDataButton;
    private TextView historyTextView;
    private ListView historyListView;
    private Button logoutButton;

    private String userId;
    private ArrayList<String> weightHistory = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);
        saveDataButton = view.findViewById(R.id.saveDataButton);
        historyTextView = view.findViewById(R.id.historyTextView);
        historyListView = view.findViewById(R.id.historyListView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Firebase에서 현재 사용자 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            userEmailTextView.setText("Email: " + user.getEmail());
        }

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // ListView에 데이터 연결
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, weightHistory);
        historyListView.setAdapter(adapter);

        // Firestore에서 사용자 데이터 가져와서 화면에 표시
        loadUserData();

        return view;
    }

    private void loadUserData() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 사용자 데이터가 있는 경우
                    String height = document.getString("height");
                    String weight = document.getString("weight");

                    editHeight.setText(height);
                    editWeight.setText(weight);

                    // 몸무게 히스토리 가져와서 표시
                    if (document.contains("weightHistory")) {
                        ArrayList<String> weightHistoryFromFirestore = (ArrayList<String>) document.get("weightHistory");
                        weightHistory.addAll(weightHistoryFromFirestore);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void saveData() {
        String height = editHeight.getText().toString();
        String weight = editWeight.getText().toString();

        // 데이터 예시
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String entry = currentDate + " - Height: " + height + "cm, Weight: " + weight + "kg";
        weightHistory.add(0, entry); // 새로운 데이터를 리스트 맨 위에 추가
        adapter.notifyDataSetChanged(); // ListView 갱신

        // Firestore에 데이터 업데이트
        updateUserDataInFirestore(height, weight, weightHistory);
    }

    private void updateUserDataInFirestore(String height, String weight, ArrayList<String> weightHistory) {
        DocumentReference userRef = db.collection("users").document(userId);

        // 사용자 데이터를 Map으로 만들기
        Map<String, Object> userData = new HashMap<>();
        userData.put("height", height);
        userData.put("weight", weight);
        userData.put("weightHistory", weightHistory);

        // Firestore에 데이터 업데이트
        userRef.update(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 데이터 업데이트 성공
            } else {
                // 데이터 업데이트 실패
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Firebase 로그아웃

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        requireActivity().finish();  // 앱 종료
    }
}

