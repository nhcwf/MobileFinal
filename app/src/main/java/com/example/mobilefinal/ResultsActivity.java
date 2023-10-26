package com.example.mobilefinal;

import static com.example.mobilefinal.DatabaseHelper.DATABASE_NAME;
import static com.example.mobilefinal.DatabaseHelper.DATABASE_VERSION;
import static com.example.mobilefinal.LoginActivity.USERNAME_BUNDLE;
import static com.example.mobilefinal.LoginActivity.USERNAME_STRING;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity {
    ListView highscores;
    Button logout, back;
    private GameSessionViewAdapter gameSessionViewAdapter;
    ArrayList<GameSession> gameSessionArrayList;
    DatabaseHelper database;
    int scoresCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        database = new DatabaseHelper(ResultsActivity.this, DATABASE_NAME, null, DATABASE_VERSION);
        scoresCount = database.getScoresCount();
        Toast.makeText(this, String.format("%d", scoresCount), Toast.LENGTH_SHORT).show();
        gameSessionArrayList = new ArrayList<GameSession>(scoresCount);
        for (int i = 1; i <= scoresCount; i++) {
            GameSession gs = new GameSession();
            database.setGameSession(gs, i);

            gameSessionArrayList.add(gs);
        }
        sort(gameSessionArrayList);


        highscores = (ListView) findViewById(R.id.lv_highscores_list);
        gameSessionViewAdapter = new GameSessionViewAdapter(gameSessionArrayList, getUsernameString());
        highscores.setAdapter(gameSessionViewAdapter);

        logout = (Button) findViewById(R.id.btn_logout);
        logout.setOnClickListener(logoutOnClickListener);

        back = (Button) findViewById(R.id.btn_result_back);
        back.setOnClickListener(backOnClickListener);
    }

    View.OnClickListener logoutOnClickListener = v -> {
        // Return to Login screen
        startActivity(new Intent(ResultsActivity.this, LoginActivity.class));
    };

    View.OnClickListener backOnClickListener = v -> {
        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(USERNAME_STRING, getUsernameString());
        intent.putExtra(USERNAME_BUNDLE, bundle);

        startActivity(intent);
    };

    public String getUsernameString() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(USERNAME_BUNDLE);

        String username = "";
        if (bundle != null) { username = Objects.requireNonNull(bundle).getString(USERNAME_STRING); }
        return username;
    }

    private void sort(ArrayList<GameSession> gameSessions) {
        Collections.sort(gameSessions, Comparator.comparingInt(GameSession::getScore).reversed()
                .thenComparingLong(GameSession::getPlaytimeMillisecond)
                .thenComparingInt(GameSession::getId));
    }
}
