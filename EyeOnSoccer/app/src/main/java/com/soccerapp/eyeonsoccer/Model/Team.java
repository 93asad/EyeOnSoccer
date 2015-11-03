package com.soccerapp.eyeonsoccer.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Asad on 26/10/2015. Represents team object
 */
public class Team implements Serializable {
    private String mName;
    private String mMatchesPlayed;
    private String mWins;
    private String mDraws;
    private String mLoss;
    private String mGoalDiff;
    private String mPoints;
    private String mRank;
    private String mHomeLink;

    /**
     * Constructor to construct team object
     *
     * @param name
     * @param rank
     * @param matchesPlayed
     * @param wins
     * @param draws
     * @param loss
     * @param goalDiff
     * @param points
     * @param teamHomeLink
     */
    public Team(String name, String rank, String matchesPlayed, String wins, String draws, String loss, String goalDiff, String points, String teamHomeLink) {
        mName = name;
        mRank = rank;
        mMatchesPlayed = matchesPlayed;
        mWins = wins;
        mDraws = draws;
        mLoss = loss;
        mGoalDiff = goalDiff;
        mPoints = points;
        mHomeLink = teamHomeLink;
    }

    /**
     * Get number of matches played
     *
     * @return
     */
    public String getMatchesPlayed() {
        return mMatchesPlayed;
    }

    /**
     * Get name
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * Get number of wins
     *
     * @return
     */
    public String getWins() {
        return mWins;
    }

    /**
     * Get number of losses
     *
     * @return
     */
    public String getLoss() {
        return mLoss;
    }

    /**
     * Get number of draws
     *
     * @return
     */
    public String getDraws() {
        return mDraws;
    }

    /**
     * Get goal difference
     *
     * @return
     */
    public String getGoalDiff() {
        return mGoalDiff;
    }

    /**
     * Get rank in league table
     *
     * @return
     */
    public String getRank() {
        return mRank;
    }

    /**
     * Get points
     *
     * @return
     */
    public String getPoints() {
        return mPoints;
    }

    /**
     * Get homepage link
     *
     * @return
     */
    public String getHomeLink() {
        return mHomeLink;
    }
}
