package com.soccerapp.eyeonsoccer.Model;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;

import java.io.Serializable;

/**
 * Represents fixture object
 */
public class Fixture implements Serializable {

    private String mHomeTeam;
    private String mAwayTeam;
    private String mHomeTeamLogo;
    private String mAwayTeamLogo;
    private String mHomeTeamScore;
    private String mAwayTeamScore;
    private String mDate;
    private String mStatus;
    private String mTime;
    private String mSeparator;

    /**
     * Constructor to create fixture object
     *
     * @param date
     * @param status
     * @param time
     * @param homeTeam
     * @param homeTeamLogo
     * @param homeScore
     * @param awayTeam
     * @param awayTeamLogo
     * @param awayScore
     * @param separator
     */
    public Fixture(String date, String status, String time, String homeTeam, String homeTeamLogo, String homeScore, String awayTeam, String awayTeamLogo, String awayScore, String separator) {
        mHomeTeam = homeTeam;
        mAwayTeam = awayTeam;
        mHomeTeamLogo = homeTeamLogo;
        mAwayTeamLogo = awayTeamLogo;
        mHomeTeamScore = homeScore;
        mAwayTeamScore = awayScore;
        mDate = date;
        mStatus = status;
        mTime = time;
        mSeparator = separator;
    }

    /**
     * Get home team name
     * @return
     */
    public String getHomeTeam() {
        return mHomeTeam;
    }

    /**
     * Get away team name
     *
     * @return
     */
    public String getAwayTeam() {
        return mAwayTeam;
    }

    /**Get home team logo
     *
     * @return
     */
    public String getHomeTeamLogo() {
        return mHomeTeamLogo;
    }

    /**Get away team logo
     *
     * @return
     */
    public String getAwayTeamLogo() {
        return mAwayTeamLogo;
    }

    /**Get home team score
     *
     * @return
     */
    public String getHomeTeamScore() {
        return mHomeTeamScore;
    }

    /**Get away team score
     *
     * @return
     */
    public String getAwayTeamScore() {
        return mAwayTeamScore;
    }

    /**Get date of fixture
     *
     * @return
     */
    public String getDate() {
        return mDate;
    }

    /**Get status. Can be upcoming or past
     *
     * @return
     */
    public String getStatus() {
        return mStatus;
    }

    /**Get time of fixture
     *
     * @return
     */
    public String getTime() {
        return mTime;
    }

    /**Get separator. Can be 'v' or vertical line
     *
     * @return
     */
    public String getSeparator() {
        return mSeparator;
    }
}
