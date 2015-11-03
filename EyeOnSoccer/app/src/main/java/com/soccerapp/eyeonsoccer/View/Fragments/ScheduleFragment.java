package com.soccerapp.eyeonsoccer.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.Model.Fixture;
import com.soccerapp.eyeonsoccer.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Asad on INDEX_ONE7/INDEX_ONEINDEX_ZERO/2INDEX_ZEROINDEX_ONE5. Represents schedule fragment
 */
public class ScheduleFragment extends Fragment {

    private final String TEAM_ALL = "all";
    private final String KET_TIME = "time";
    private RecyclerView mFixturesList; // Contains list of fixtures
    private BroadcastReceiver mReceiver; // To detect league name change broadcast 
    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<Fixture> mFixtures;
    private FixtureAdapter mFixtureAdapter;
    private View mScheduleView; // Schedule page

    private final String KEY_FIXTURES = "fixtures";
    private Spinner mTeamsSpinner;
    private ArrayAdapter mTeamsSpinnerAdapter;
    private Spinner mMonthsSpinner;
    private ArrayAdapter mMonthsSpinnerAdapter;
    private TextView mNoDataMessage;

    private final int INDEX_ZERO = 0;
    private final int INDEX_ONE = 1;
    private final String KEY_LOG = "tes";

    /**
     * on create, set up broadcast receiver
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
    }

    /**
     * Get action bar title
     *
     * @return
     */
    private String getActionBarTitle() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle().toString();
    }

    /**
     * Set up schedule view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mScheduleView = inflater.inflate(R.layout.schedule_fragment, container, false);
        mNoDataMessage = (TextView) mScheduleView.findViewById(R.id.no_fixture_data);
        mTeamsSpinner = (Spinner) mScheduleView.findViewById(R.id.teams_spinner);
        mMonthsSpinner = (Spinner) mScheduleView.findViewById(R.id.months_spinner);
        setMonthsSpinner(savedInstanceState);
        setTeamsSpinner(savedInstanceState);
        mFixturesList = (RecyclerView) (mScheduleView.findViewById(R.id.fixture_list));

        //If fragment has been created before and league hasnt changed since, load saved data from bundle
        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_FIXTURES)
                && ((String) savedInstanceState.getString(Constants.KEY_ACTION_BAR_TITLE))
                .equals(getActionBarTitle())) {
            mFixtures = (ArrayList<Fixture>) savedInstanceState.getSerializable(KEY_FIXTURES);

        } else { //Fetch new data from web
            mFixtures = new ArrayList<Fixture>();
            fetchData(); //Fetch data for teams
        }

        mFixtureAdapter = new FixtureAdapter(getActivity(), mFixtures);
        mFixturesList.setClickable(true);
        mFixturesList.setAdapter(mFixtureAdapter);
        mFixturesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFixturesList.setHasFixedSize(true);

        return mScheduleView;
    }

    /**
     * Sets teams spinner
     *
     * @param savedInstanceState
     */
    private void setTeamsSpinner(Bundle savedInstanceState) {
        // Create an ArrayAdapter for to store teams for spinner
        mTeamsSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Global.sortedTeamNameList);
        mTeamsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTeamsSpinner.setAdapter(mTeamsSpinnerAdapter);

        //If league name hasnt changed, load saved data from bundle
        if (savedInstanceState != null)
            mTeamsSpinner.setSelection(savedInstanceState.getInt(Constants.KEY_TEAMS_SPINNER), false);
        else mTeamsSpinner.setSelection(INDEX_ZERO, false);

        mTeamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**Handles item touch events
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFixtures.clear();
                fetchData();
                mFixtureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Sets up months spinner
     *
     * @param savedInstanceState
     */
    private void setMonthsSpinner(Bundle savedInstanceState) {

        mMonthsSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Constants.MONTHS);

        mMonthsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthsSpinner.setAdapter(mMonthsSpinnerAdapter);

        //If fragment is has been created before and month hasnt changed since, load saved data from bundle
        if (savedInstanceState != null)
            mMonthsSpinner.setSelection(savedInstanceState.getInt(Constants.KEY_MONTHS_SPINNER), false);
        else mMonthsSpinner.setSelection(INDEX_ZERO, false);

        mMonthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**Handle item selection events
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFixtures.clear();
                fetchData();
                mFixtureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Given the month name, return month number
     *
     * @param month
     * @return
     */
    private String getMonthNum(String month) {
        String monthNum = (Arrays.asList(Constants.MONTHS).indexOf(month) + INDEX_ONE) + "";
        monthNum = monthNum.length() < 2 ? INDEX_ZERO + monthNum : monthNum;
        return monthNum;
    }

    /**
     * Set broadcasr receiver
     */
    private void setupBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mFixtures.clear();
                fetchData();
                mFixtureAdapter.notifyDataSetChanged();
            }
        };
        mLocalBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity().getApplicationContext());
        mLocalBroadcastManager.registerReceiver(mReceiver, Constants.INTENT_FILTER);
    }

    /**
     * Saves desired data to spinners
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mFixtures.isEmpty()) outState.putSerializable(KEY_FIXTURES, mFixtures);
        String title = getActionBarTitle();
        outState.putString(Constants.KEY_ACTION_BAR_TITLE, title);
        outState.putInt(Constants.KEY_MONTHS_SPINNER, mMonthsSpinner.getSelectedItemPosition());
        outState.putInt(Constants.KEY_TEAMS_SPINNER, mTeamsSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * Starts async task with appropriate parameters to get new data from web
     */
    private void fetchData() {
        String leagueName = ((AppCompatActivity) getActivity())
                .getSupportActionBar().getTitle().toString();
        String month = mMonthsSpinner.getSelectedItem().toString();
        String team = "";
        //On creation of fragment, teams spinner will be empty. So select team to 'all'
        try {
            team = mTeamsSpinner.getSelectedItem().toString().trim();
        } catch (NullPointerException e) {
            team = TEAM_ALL;
        }
        mNoDataMessage.setVisibility(ListView.GONE);
        new FixtureDataAsync(mScheduleView).executeOnExecutor
                (AsyncTask.THREAD_POOL_EXECUTOR,
                        leagueName,
                        mFixtures,
                        month,
                        team);
    }

    /**
     * Unregister broadcast receiver
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    /********************************************************************************
     * AsyncTask
     ********************************************************************************/

    /**
     * Async task to get new schedule data from web
     */
    private class FixtureDataAsync extends AsyncTask<Object, Void, Void> {

        private final String MONTH_FORMAT = "MMMM";
        private final java.lang.String PARSING_DONE_MESSAGE = "team parsing done";
        private final String KEY_HEADING_4 = "h4";
        private final String KEY_SEPARATOR = "separator";
        private final String KEY_AWAY_SCORE = "away-score";
        private final String KEY_HOME_SCORE = "home-score";
        private final String KEY_HOME_TEAM_SCORE = "score-home-team";
        private final String KEY_AWAY_TEAM_SCORE = "score-away-team";
        private final String KEY_TEAM_LOGO = "team-logo";
        private final String KEY_STATUS = "status";
        private final String KEY_DATE_CONTAINER = "date-container";
        private final String CLASS_TEAM_NAME = "team-name";
        private final String CLASS_TEAM_SCORE = "team-score";
        private final String SRC_ATTR = "src";
        private ProgressBar mProgressBar;

        private View mView;

        public FixtureDataAsync(View view) {
            this.mView = view;
        }

        /**
         * Show progress bar and hide list
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) (mView.findViewById(R.id.progress_bar_schedule));
            mProgressBar.setIndeterminate(true);
            mTeamsSpinner.setVisibility(Spinner.GONE);
            mFixturesList.setVisibility(RecyclerView.GONE);
            mMonthsSpinner.setVisibility(Spinner.GONE);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        /**
         * Get new schedule data from web
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            String leagueName = (String) params[INDEX_ZERO];
            List<Fixture> fixtureObjList = (List<Fixture>) params[INDEX_ONE];
            String month = ((String) params[2]).trim();
            String team = ((String) params[3]).trim();

            String fixtureLink = "";
            if (team.equalsIgnoreCase(TEAM_ALL)) { // 'All' means get schedule of entire league for specific month
                fixtureLink = getLeagueFixtureLink(leagueName, month);

            } else {
                fixtureLink = getTeamFixtureLink(team);
            }
            Log.d(KEY_LOG, fixtureLink);

            try {
                //Fetch document from web
                Document doc = Jsoup.connect(fixtureLink).userAgent(Constants.USER_AGENT).get();
                Log.d(KEY_LOG, doc.html());

                if (team.equalsIgnoreCase(TEAM_ALL)) {
                    Elements fixtureGroups = doc.getElementsByClass(Constants.LEAGUE_FIXTURES_GROUP);
                    getLeagueFixtureData(fixtureObjList, fixtureGroups);
                } else {
                    Elements fixtures = doc.getElementsByClass(Constants.TEAM_FIXTURES_CLASS);
                    getTeamFixturesData(fixtureObjList, fixtures, month);
                }

                Log.d(KEY_LOG, doc.html());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) // Wait for teams list to populate
            {
                if (!Global.sortedTeamNameList.isEmpty()) {
                    break;
                }
            }

            return null;
        }

        /**
         * Parse retrieved html page to get schedule data
         *
         * @param fixtureList
         * @param fixtureGroups
         */
        private void getLeagueFixtureData(List<Fixture> fixtureList, Elements fixtureGroups) {
            int numberOfFixtures = fixtureGroups.size();

            for (int index = INDEX_ZERO; index < numberOfFixtures; index++) {

                /**********HTML parsing to get data************/

                Element fixtureGroup = fixtureGroups.get(index); //Group represents date group

                /**************************Html parsing********************************/

                String fixtureDate = fixtureGroup.select(KEY_HEADING_4).last().text();
                Elements fixtures = fixtureGroup.getElementsByClass(Constants.LEAGUE_FIXTURES_CLASS);

                for (Element fixture : fixtures) {

                    String time = time = fixture.getElementsByClass(KET_TIME).get(INDEX_ZERO).text();

                    String homeTeam = fixture.getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ZERO).child(INDEX_ZERO).text();
                    String homeTeamLogo = fixture.getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ZERO).child(INDEX_ZERO).child(INDEX_ZERO).attr(SRC_ATTR);

                    String awayTeam = fixture.getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ONE).child(INDEX_ZERO).text();
                    String awayTeamLogo = fixture.getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ONE).child(INDEX_ZERO).child(INDEX_ZERO).attr(SRC_ATTR);

                    String homeScore = fixture.getElementsByClass(CLASS_TEAM_SCORE).get(INDEX_ZERO).child(INDEX_ZERO).text();
                    String awayScore = fixture.getElementsByClass(CLASS_TEAM_SCORE).get(INDEX_ONE).child(INDEX_ZERO).text();

                    /***************************HTML parsing*******************************/

                    //Object to store fixture data
                    Fixture fixtureObj = new Fixture(fixtureDate, null, time, homeTeam,
                            homeTeamLogo, homeScore, awayTeam,
                            awayTeamLogo, awayScore, null);

                    fixtureList.add(fixtureObj);
                }
                Log.e(KEY_LOG, PARSING_DONE_MESSAGE);
            }
        }

        /**
         * Given league name and month, return link to get schedule data from
         *
         * @param leagueName
         * @param month
         * @return
         */
        private String getLeagueFixtureLink(String leagueName, String month) {

            return String.format(Constants.LEAGUE_SCHEDULE_WEBLINK,
                    leagueName.toLowerCase().replaceAll(" ", "-").trim(),
                    getLeagueCode(getActionBarTitle()),
                    getDate(month));
        }

        /**
         * Given the month, return league date
         *
         * @param month
         * @return
         */
        private String getDate(String month) {
            return getYear(month) + getMonthNum(month);
        }

        /**
         * Given the league name, return league code
         *
         * @param currentActionBarTitle
         * @return
         */
        private String getLeagueCode(String currentActionBarTitle) {
            return Constants.TEAM_CODES[Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle)];
        }

        /**
         * Given the team, return link to get schedule for team from
         *
         * @param team
         * @return
         */
        private String getTeamFixtureLink(String team) {
            while (true) {
                if (!Global.teamList.isEmpty()) break; //team objects are stored in teamList
            }
            String homeLink = Global.teamList.get(Global.teamNameList.indexOf(team)).getHomeLink();
            return homeLink.replace("index", "fixtures");
        }

        /**
         * Parse html document to get team schedule data
         *
         * @param fixtureList
         * @param fixtures
         * @param month
         */
        private void getTeamFixturesData(List<Fixture> fixtureList, Elements fixtures, String month) {

            int numberOfFixtures = fixtures.size();

            //INDEX_ONE to ignore fixtures header
            for (int index = INDEX_ONE; index < numberOfFixtures; index++) {

                /**********HTML parsing to get data************/
                Element fixture = fixtures.get(index);

                String date = fixture.getElementsByClass(KEY_DATE_CONTAINER).get(INDEX_ZERO).getElementsByClass("date").get(INDEX_ZERO).text();

                // Only select fixtures of selected month
                if (!date.substring(INDEX_ZERO, 2).equalsIgnoreCase(month.substring(INDEX_ZERO, 2)))
                    continue;

                String status = null;
                String time = null;

                try {
                    status = fixture.getElementsByClass(KEY_DATE_CONTAINER).get(INDEX_ZERO).getElementsByClass(KEY_STATUS).get(INDEX_ZERO).text();
                    time = fixture.getElementsByClass(KEY_DATE_CONTAINER).get(INDEX_ZERO).getElementsByClass(KET_TIME).get(INDEX_ZERO).text();
                } catch (Exception e) {
                    //Continue as one of them needs to be null;
                }

                String homeTeam = fixture.getElementsByClass(KEY_HOME_TEAM_SCORE).get(INDEX_ZERO).getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ZERO).text();
                String homeTeamLogo = fixture.getElementsByClass(KEY_HOME_TEAM_SCORE).get(INDEX_ZERO).getElementsByClass(KEY_TEAM_LOGO).get(INDEX_ZERO).child(INDEX_ZERO).attr(SRC_ATTR);

                String awayTeam = fixture.getElementsByClass(KEY_AWAY_TEAM_SCORE).get(INDEX_ZERO).getElementsByClass(CLASS_TEAM_NAME).get(INDEX_ZERO).text();
                String awayTeamLogo = fixture.getElementsByClass(KEY_AWAY_TEAM_SCORE).get(INDEX_ZERO).getElementsByClass(KEY_TEAM_LOGO).get(INDEX_ZERO).child(INDEX_ZERO).attr(SRC_ATTR);

                String homeScore = fixture.getElementsByClass(KEY_HOME_SCORE).get(INDEX_ZERO).text();
                String awayScore = fixture.getElementsByClass(KEY_AWAY_SCORE).get(INDEX_ZERO).text();
                String separator = fixture.getElementsByClass(KEY_SEPARATOR).get(INDEX_ZERO).text();

                /***************************HTML parsing*******************************/

                Log.d(KEY_LOG, PARSING_DONE_MESSAGE);

                //Object to store fixture data
                Fixture fixtureObj = new Fixture(date, status, time, homeTeam,
                        homeTeamLogo, homeScore, awayTeam,
                        awayTeamLogo, awayScore, separator);

                fixtureList.add(fixtureObj);
            }
        }

        /**
         * Given the month, get year. Months between Jan and Jul are considered months of next year
         *
         * @param month
         * @return
         */
        private int getYear(String month) {

            int year = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR);
            try {
                Date date = new SimpleDateFormat(MONTH_FORMAT).parse(month); //Get only name of month
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                //League years exists between August and May
                if (cal.get(Calendar.MONTH) < Calendar.AUGUST) return (year + INDEX_ONE);
            } catch (ParseException e) {
                Log.e(KEY_LOG, e.getMessage());
            }

            return year;
        }

        /**
         * Show spinners and list, and hide progress bars
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //setTeamsSpinner(mSavedInstanceState);
            mTeamsSpinnerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFixturesList.setVisibility(RecyclerView.VISIBLE);
            mTeamsSpinner.setVisibility(Spinner.VISIBLE);
            mMonthsSpinner.setVisibility(Spinner.VISIBLE);
        }
    }

    /**
     * Async task to load team logos from web
     */
    private class ImageLoadAsyncTask extends AsyncTask<Object, Void, Void> {
        private FixtureHolder mHolder;
        private Bitmap mFirstTeamBitmap;
        private View mFixtureRow;
        private ImageView mImageFirstTeam;
        private ImageView mImageSecondTeam;
        private ProgressBar mFirstTeamProgressBar;
        private ProgressBar mSecondTeamProgressBar;
        private Bitmap mSecondTeamBitmap;

        /**
         * Constructor
         *
         * @param newsRow
         */
        public ImageLoadAsyncTask(View newsRow) {
            this.mFixtureRow = newsRow;
        }

        /**
         * Show progress bars and hide logos
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImageFirstTeam = (ImageView) mFixtureRow.findViewById(R.id.team1_logo);
            mImageFirstTeam.setVisibility(ImageView.GONE);
            mFirstTeamProgressBar = (ProgressBar) mFixtureRow.findViewById(R.id.image_progress_bar_1);
            mFirstTeamProgressBar.setIndeterminate(true);
            mFirstTeamProgressBar.setVisibility(ProgressBar.VISIBLE);

            mImageSecondTeam = (ImageView) mFixtureRow.findViewById(R.id.team2_logo);
            mImageSecondTeam.setVisibility(ImageView.GONE);
            mSecondTeamProgressBar = (ProgressBar) mFixtureRow.findViewById(R.id.image_progress_bar_2);
            mSecondTeamProgressBar.setIndeterminate(true);
            mSecondTeamProgressBar.setVisibility(ProgressBar.VISIBLE);

        }

        /**
         * Show logos, hide progress bars and set logo to appropriate imageView
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHolder.getFirstTeamLogo().setImageBitmap(mFirstTeamBitmap);
            mHolder.getSecondTeamLogo().setImageBitmap(mSecondTeamBitmap);

            mFirstTeamProgressBar.setVisibility(ProgressBar.GONE);
            mSecondTeamProgressBar.setVisibility(ProgressBar.GONE);
            mImageFirstTeam.setVisibility(ImageView.VISIBLE);
            mImageSecondTeam.setVisibility(ImageView.VISIBLE);
        }

        /**
         * Load image from web as bitmap
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            mHolder = (FixtureHolder) params[INDEX_ZERO];
            int position = (int) params[INDEX_ONE];

            try {
                mFirstTeamBitmap = ImageLoader.getInstance().
                        loadImageSync(mFixtures.get(position).getHomeTeamLogo());
                mSecondTeamBitmap = ImageLoader.getInstance().
                        loadImageSync(mFixtures.get(position).getAwayTeamLogo());

            } catch (NullPointerException e) {
                Log.d(KEY_LOG, "No image found at " + position);
            }
            return null;
        }
    }

    /***********************************************************************************
     * Adapter
     ***********************************************************************************/

    /**
     * Adapter to manage fixture rows
     */
    private class FixtureAdapter extends RecyclerView.Adapter<FixtureHolder> {
        private List<Fixture> mFixtures;
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private View mFixtureRow;
        private FixtureHolder mFixtureHolder;

        /**
         * Constructor
         *
         * @param context
         * @param fixtures
         */
        public FixtureAdapter(Context context, List<Fixture> fixtures) {
            this.mFixtures = fixtures;
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        /**
         * Returns view holder
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public FixtureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mFixtureRow = mLayoutInflater.inflate(R.layout.fixture_row, parent, false);
            mFixtureHolder = new FixtureHolder(mFixtureRow);
            return mFixtureHolder;
        }

        /**
         * Binds data to holder fields
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(FixtureHolder holder, int position) {
            holder.getFirstTeamName().setText(mFixtures.get(position).getHomeTeam());
            holder.getSecondTeamName().setText(mFixtures.get(position).getAwayTeam());
            holder.getDate().setText(mFixtures.get(position).getDate());
            holder.getFirstTeamScore().setText(mFixtures.get(position).getHomeTeamScore());
            holder.getSecondTeamScore().setText(mFixtures.get(position).getAwayTeamScore());

            //For some, time can be null as past fixtures will only have status and not time
            if (mFixtures.get(position).getTime() != null && !mFixtures.get(position).getTime().isEmpty()) {
                holder.getStatus().setText(mFixtures.get(position).getTime());
            } else {
                holder.getStatus().setText(mFixtures.get(position).getStatus());
            }

            new ImageLoadAsyncTask(mFixtureRow).execute(holder, position);
        }

        /**
         * Returns number of fixture rows in list
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mFixtures.size();
        }
    }

    /******************************************************************************************
     * Holder
     ******************************************************************************************/

    /**
     * Holder to store fixture data
     */
    private class FixtureHolder extends RecyclerView.ViewHolder {

        private TextView mFirstTeamName;
        private TextView mSecondTeamName;
        private ImageView mFirstTeamLogo;
        private ImageView mSecondTeamLogo;
        private TextView mFirstTeamScore;
        private TextView mSecondTeamScore;
        private TextView mStatus;
        private TextView mDate;

        /**
         * Constructor
         *
         * @param itemView
         */
        public FixtureHolder(View itemView) {
            super(itemView);

            mFirstTeamName = (TextView) itemView.findViewById(R.id.team1_name);
            mSecondTeamName = (TextView) itemView.findViewById(R.id.team2_name);
            mFirstTeamLogo = (ImageView) itemView.findViewById(R.id.team1_logo);
            mSecondTeamLogo = (ImageView) itemView.findViewById(R.id.team2_logo);
            mFirstTeamScore = (TextView) itemView.findViewById(R.id.team1_score);
            mSecondTeamScore = (TextView) itemView.findViewById(R.id.team2_score);
            mStatus = (TextView) itemView.findViewById(R.id.status);
            mDate = (TextView) itemView.findViewById(R.id.date);
        }

        public TextView getFirstTeamName() {
            return mFirstTeamName;
        }

        public TextView getSecondTeamName() {
            return mSecondTeamName;
        }

        public ImageView getFirstTeamLogo() {
            return mFirstTeamLogo;
        }

        public ImageView getSecondTeamLogo() {
            return mSecondTeamLogo;
        }

        public TextView getFirstTeamScore() {
            return mFirstTeamScore;
        }

        public TextView getSecondTeamScore() {
            return mSecondTeamScore;
        }

        public TextView getStatus() {
            return mStatus;
        }

        public TextView getDate() {
            return mDate;
        }
    }
}
