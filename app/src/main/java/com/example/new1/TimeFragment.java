package com.example.new1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeFragment extends Fragment {

    private TextView timerTextView;
    private Button chooseDateTimeButton;
    private Button startTimerButton;
    private Button stopTimerButton;
    private TextView stopTimesTextView;
    private TextView startTimeTextView;
    private TextView stopTimeTextView;
    private CountDownTimer countDownTimer;

    private long currentTime = 0; // 현재까지의 경과 시간
    private long baseTime = 0; // 기준 시간
    private long startTime = 0; // 시작 시간
    private List<Long> stopTimes = new ArrayList<>();

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);

        timerTextView = view.findViewById(R.id.timerTextView);
        chooseDateTimeButton = view.findViewById(R.id.chooseDateTimeButton);
        startTimerButton = view.findViewById(R.id.startTimerButton);
        stopTimerButton = view.findViewById(R.id.stopTimerButton);
        stopTimesTextView = view.findViewById(R.id.stopTimesTextView);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        stopTimeTextView = view.findViewById(R.id.stopTimeTextView);

        chooseDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDateTime();
            }
        });

        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });

        return view;
    }

    private void chooseDateTime() {
        // 여기에 사용자에게 날짜와 시간을 선택하도록 하는 코드를 추가
    }

    private void startTimer() {
        startTime = System.currentTimeMillis() / 1000; // 현재 시간을 초로 변환하여 저장
        baseTime = currentTime; // 시작 시간을 기준 시간으로 설정

        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentTime = (System.currentTimeMillis() / 1000) - baseTime;

                long hours = currentTime / 3600;
                long minutes = (currentTime % 3600) / 60;
                long seconds = currentTime % 60;

                timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                // 끝나면 아무 작업 필요 없음
            }
        };

        countDownTimer.start();
        startTimerButton.setEnabled(false);
        stopTimerButton.setEnabled(true);

        // 시작 시간을 표시
        updateStartTime();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            stopTimes.add(currentTime); // 현재 시간 저장

            // 저장된 중지 시간을 표시
            displayStopTimes();

            currentTime = 0; // 타이머를 0초로 초기화
            timerTextView.setText("00:00:00");

            // 종료 시간을 표시
            updateStopTime();
        }
        startTimerButton.setEnabled(true);
        stopTimerButton.setEnabled(false);
    }

    private void displayStopTimes() {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
        for (Long time : stopTimes) {
            String formattedTime = sdf.format(new Date((time * 1000) + baseTime * 1000));
            // 밀리초를 초로 변환하고 기준 시간을 더함
            stringBuilder.append(formattedTime).append("\n");
        }
        stopTimesTextView.setText(stringBuilder.toString());
    }

    private void updateStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
        String formattedStartTime = sdf.format(new Date(startTime * 1000));
        startTimeTextView.setText("Start Time: " + formattedStartTime);
    }

    private void updateStopTime() {
        if (!stopTimes.isEmpty()) {
            long lastStopTime = stopTimes.get(stopTimes.size() - 1);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
            String formattedStopTime = sdf.format(new Date((lastStopTime + baseTime) * 1000));
            stopTimeTextView.setText("Stop Time: " + formattedStopTime);
        } else {
            stopTimeTextView.setText("Stop Time: N/A");
        }
    }
}
