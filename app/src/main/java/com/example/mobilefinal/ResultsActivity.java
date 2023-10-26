package com.example.mobilefinal;

import static com.example.mobilefinal.DatabaseHelper.DATABASE_NAME;
import static com.example.mobilefinal.DatabaseHelper.DATABASE_VERSION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    ListView highscores;
    Button mLogout;
    private GameSessionViewAdapter gameSessionViewAdapter;
    ArrayList<GameSession> gameSessionArrayList;
    DatabaseHelper database;
    int highscoresSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        database = new DatabaseHelper(ResultsActivity.this, DATABASE_NAME, null, DATABASE_VERSION);
        highscoresSize = database.getScoresCount();
        Toast.makeText(this, String.format("%d", highscoresSize), Toast.LENGTH_SHORT).show();
        gameSessionArrayList = new ArrayList<GameSession>(highscoresSize);
        for (int i = 1; i <= highscoresSize; i++) {
            GameSession ps = new GameSession();
            database.setGameSession(ps, i);

            gameSessionArrayList.add(ps);
        }

        highscores = (ListView) findViewById(R.id.lv_highscores_list);
        gameSessionViewAdapter = new GameSessionViewAdapter(gameSessionArrayList);
        highscores.setAdapter(gameSessionViewAdapter);

        mLogout = (Button) findViewById(R.id.btn_logout);
        mLogout.setOnClickListener(logoutOnClickListener);
    }

    View.OnClickListener logoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Return to Login screen
            startActivity(new Intent(ResultsActivity.this, LoginActivity.class));
        }
    };

}
