package com.example.mobilefinal;

public class GameSession {
    private int id;
    private int score;
    private long playtimeMillisecond;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getPlaytimeMillisecond() {
        return playtimeMillisecond;
    }

    public void setPlaytime(long playtimeMillisecond) {
        this.playtimeMillisecond = playtimeMillisecond;
    }
}
