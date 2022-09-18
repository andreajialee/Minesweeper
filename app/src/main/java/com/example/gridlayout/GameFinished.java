package com.example.gridlayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GameFinished extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_finished);
    }

    public void sendMessage(View view) {
        Intent gameOver = new Intent(GameFinished.this, MainActivity.class);
        finish();
        startActivity(gameOver);
    }
}
