package com.example.gridlayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameFinished extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_finished);

        // Retrieve the data from previous activity
        Intent in = getIntent();
        String secondsUsed = in.getStringExtra("secondsUsed");
        String gameResult = in.getStringExtra("gameResult");
        // Show game results in new activity
        TextView finalTimeCount = findViewById(R.id.final_time_count);
        finalTimeCount.setText(secondsUsed);
        TextView finalGameResult = findViewById(R.id.final_game_result);
        finalGameResult.setText(gameResult);
    }

    public void sendMessage(View view) {
        Intent gameOver = new Intent(GameFinished.this, MainActivity.class);
        finish();
        startActivity(gameOver);
    }
}
