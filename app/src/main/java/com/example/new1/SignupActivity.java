package com.example.new1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final EditText heightEditText = findViewById(R.id.heightEditText);
        final EditText weightEditText = findViewById(R.id.weightEditText);
        final EditText ageEditText = findViewById(R.id.ageEditText);
        Button signupButton = findViewById(R.id.signupButton);
        Button backButton = findViewById(R.id.backButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                final String height = heightEditText.getText().toString();
                final String weight = weightEditText.getText().toString();
                final String age = ageEditText.getText().toString();

                // Firebase Authentication을 사용하여 회원가입
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원가입 성공
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                                    // Firestore에 사용자 정보 저장
                                    saveUserDataToFirestore(user.getUid(), email, height, weight, age);

                                    // 회원가입 후 MainFunctionActivity로 이동
                                    Intent intent = new Intent(SignupActivity.this, MainFunctionActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // 회원가입 실패
                                    Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 뒤로가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void saveUserDataToFirestore(String userId, String email, String height, String weight, String age) {
        // "users" 컬렉션 안에 사용자 정보 저장
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("height", height);
        userData.put("weight", weight);
        userData.put("age", age);

        db.collection("users").document(userId)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Firestore에 데이터 저장 성공
                        } else {
                            // Firestore에 데이터 저장 실패
                        }
                    }
                });
    }
}
