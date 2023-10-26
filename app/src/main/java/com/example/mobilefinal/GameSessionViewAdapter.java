package com.example.mobilefinal;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GameSessionViewAdapter extends BaseAdapter {
    public final ArrayList<GameSession> gameSessions;

    public GameSessionViewAdapter(ArrayList<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }

    public ArrayList<GameSession> getPlaySessions() {
        return gameSessions;
    }

    @Override
    public int getCount() {
        return gameSessions.size();
    }

    @Override
    public Object getItem(int position) {
        return gameSessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return gameSessions.get(position).getId();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View sessionView = convertView;

        if (sessionView == null) {
            sessionView = View.inflate(parent.getContext(), R.layout.listview_play_session, null);
        }

        GameSession gameSession = (GameSession) getItem(position);
        ((TextView) sessionView.findViewById(R.id.tv_order)).setText(String.format("#%d", gameSession.getId()));
        ((TextView) sessionView.findViewById(R.id.tv_score)).setText(String.format("Score: %d", gameSession.getScore()));
        ((TextView) sessionView.findViewById(R.id.tv_playtime)).setText(String.format("Duration: %d.%ds", gameSession.getPlaytimeMillisecond() / 1000, gameSession.getPlaytimeMillisecond() % 1000));

        return sessionView;
    }
}
