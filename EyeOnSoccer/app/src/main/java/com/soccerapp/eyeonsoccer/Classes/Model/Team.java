package com.soccerapp.eyeonsoccer.Classes.Model;

/**
 * Created by Asad on 26/10/2015.
 */
public class Team {
    private String mName;
    private int mMatchesPlayed;
    private int mWins;
    private int mDraws;
    private int mLoss;
    private int mGoalDiff;
    private int mPoints;
    private int mRank;

    public int getRank() {
        return mRank;
    }

    public void setRank(int mRank) {
        this.mRank = mRank;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getMatchesPlayed() {
        return mMatchesPlayed;
    }

    public void setMatchesPlayed(int mMatchesPlayed) {
        this.mMatchesPlayed = mMatchesPlayed;
    }

    public int getWins() {
        return mWins;
    }

    public void setWins(int mWins) {
        this.mWins = mWins;
    }

    public int getDraws() {
        return mDraws;
    }

    public void setDraws(int mDraws) {
        this.mDraws = mDraws;
    }

    public int getLoss() {
        return mLoss;
    }

    public void setLoss(int mLoss) {
        this.mLoss = mLoss;
    }

    public int getGoalDiff() {
        return mGoalDiff;
    }

    public void setGoalDiff(int mGoalDiff) {
        this.mGoalDiff = mGoalDiff;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int mPoints) {
        this.mPoints = mPoints;
    }
}
