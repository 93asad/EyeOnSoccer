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
import com.nostra13.universalimageloader.core.assist.ImageSize;
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
 * Created by Asad on 17/10/2015.
 */
public class ScheduleFragment extends Fragment {

    private RecyclerView mFixturesList;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<Fixture> mFixtures;
    private FixtureAdapter mFixtureAdapter;
    private View mScheduleView;

    private final String KEY_FIXTURES = "fixtures";
    private Spinner mTeamsSpinner;
    private ArrayAdapter mTeamsSpinnerAdapter;
    private Spinner mMonthsSpinner;
    private ArrayAdapter mMonthsSpinnerAdapter;
    private TextView mNoDataMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
    }

    private String getActionBarTitle() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mScheduleView = inflater.inflate(R.layout.schedule_fragment, container, false);
        mNoDataMessage = (TextView) mScheduleView.findViewById(R.id.no_fixture_data);
        mTeamsSpinner = (Spinner) mScheduleView.findViewById(R.id.teams_spinner);
        mMonthsSpinner = (Spinner) mScheduleView.findViewById(R.id.months_spinner);
        setMonthsSpinner(savedInstanceState);
        setTeamsSpinner(savedInstanceState);
        mFixturesList = (RecyclerView) (mScheduleView.findViewById(R.id.fixture_list));
        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_FIXTURES)
                && ((String) savedInstanceState.getString(Global.KEY_ACTION_BAR_TITLE))
                .equals(getActionBarTitle())) {
            mFixtures = (ArrayList<Fixture>) savedInstanceState.getSerializable(KEY_FIXTURES);

        } else {
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

    private void setTeamsSpinner(Bundle savedInstanceState) {
        // Create an ArrayAdapter for to store teams for spinner
        mTeamsSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Global.sortedTeamNameList);
        mTeamsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTeamsSpinner.setAdapter(mTeamsSpinnerAdapter);

        if (savedInstanceState != null)
            mTeamsSpinner.setSelection(savedInstanceState.getInt(Constants.KEY_TEAMS_SPINNER), false);
        else mTeamsSpinner.setSelection(0, false);

        mTeamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setMonthsSpinner(Bundle savedInstanceState) {

        //Only select those months during which leagues are active
        //List<String> months = Constants.MONTHS;

        // Empty in case of june and july, because of break
        //if (months.isEmpty()) {
        // mMonthsSpinnerAdapter = new ArrayAdapter(getActivity(),
        //        android.R.layout.simple_spinner_item, Constants.MONTHS);
        //} else {
        mMonthsSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Constants.MONTHS);
        // }

        mMonthsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthsSpinner.setAdapter(mMonthsSpinnerAdapter);

        if (savedInstanceState != null)
            mMonthsSpinner.setSelection(savedInstanceState.getInt(Constants.KEY_MONTHS_SPINNER), false);
        else mMonthsSpinner.setSelection(0, false);

        mMonthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private String getMonthNum(String month) {
        String monthNum = (Arrays.asList(Constants.MONTHS).indexOf(month) + 1) + "";
        monthNum = monthNum.length() < 2 ? 0 + monthNum : monthNum;
        return monthNum;
    }


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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mFixtures.isEmpty()) outState.putSerializable(KEY_FIXTURES, mFixtures);
        String title = getActionBarTitle();
        outState.putString(Global.KEY_ACTION_BAR_TITLE, title);
        outState.putInt(Constants.KEY_MONTHS_SPINNER, mMonthsSpinner.getSelectedItemPosition());
        outState.putInt(Constants.KEY_TEAMS_SPINNER, mTeamsSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    private void fetchData() {
        String leagueName = ((AppCompatActivity) getActivity())
                .getSupportActionBar().getTitle().toString();
        String month = mMonthsSpinner.getSelectedItem().toString();
        String team = "";
        try {
            team = mTeamsSpinner.getSelectedItem().toString().trim();
        } catch (NullPointerException e) {
            team = "all";
        }
        mNoDataMessage.setVisibility(ListView.GONE);
        new FixtureDataAsync(mScheduleView).executeOnExecutor
                (AsyncTask.THREAD_POOL_EXECUTOR,
                        leagueName,
                        mFixtures,
                        month,
                        team);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    /********************************************************************************
     * AsyncTask
     ********************************************************************************/

    private class FixtureDataAsync extends AsyncTask<Object, Void, Void> {

        private final String SOURCE_ATTRIBUTE = "src";
        private ProgressBar mProgressBar;

        private View mView;

        public FixtureDataAsync(View view) {
            this.mView = view;
        }

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

        @Override
        protected Void doInBackground(Object... params) {
            String leagueName = (String) params[0];
            List<Fixture> fixtureObjList = (List<Fixture>) params[1];
            String month = ((String) params[2]).trim();
            String team = ((String) params[3]).trim();

            String fixtureLink = "";
            if (team.equalsIgnoreCase("all")) {
                fixtureLink = getLeagueFixtureLink(leagueName, month);

            } else {
                fixtureLink = getTeamFixtureLink(team);
            }
            Log.d("test", fixtureLink);

            try {
                Document doc = Jsoup.connect(fixtureLink).userAgent(Constants.USER_AGENT).get();
                Log.d("test", doc.html());

                if (team.equalsIgnoreCase("all")) {
                    Elements fixtureGroups = doc.getElementsByClass(Constants.LEAGUE_FIXTURES_GROUP);
                    getLeagueFixtureData(fixtureObjList, fixtureGroups);
                } else {
                    Elements fixtures = doc.getElementsByClass(Constants.TEAM_FIXTURES_CLASS);
                    //Element image = doc.select("img[class=lr-logo-img lr-standings-logo-img").first();
                    getTeamFixturesData(fixtureObjList, fixtures, month);
                }

                Log.d("clear", doc.html());
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

        private void getLeagueFixtureData(List<Fixture> fixtureList, Elements fixtureGroups) {
            int numberOfFixtures = fixtureGroups.size();

            for (int index = 0; index < numberOfFixtures; index++) {

                /**********HTML parsing to get data************/
                Element fixtureGroup = fixtureGroups.get(index);

                Log.d("test", "Parsing fixtures");

                /**************************Html parsing********************************/

                String fixtureDate = fixtureGroup.select("h4").last().text();
                Elements fixtures = fixtureGroup.getElementsByClass(Constants.LEAGUE_FIXTURES_CLASS);

                for (Element fixture : fixtures) {

                    String time = time = fixture.getElementsByClass("time").get(0).text();

                    String homeTeam = fixture.getElementsByClass("team-name").get(0).child(0).text();
                    String homeTeamLogo = fixture.getElementsByClass("team-name").get(0).child(0).child(0).attr("src");

                    String awayTeam = fixture.getElementsByClass("team-name").get(1).child(0).text();
                    String awayTeamLogo = fixture.getElementsByClass("team-name").get(1).child(0).child(0).attr("src");

                    String homeScore = fixture.getElementsByClass("team-score").get(0).child(0).text();
                    String awayScore = fixture.getElementsByClass("team-score").get(1).child(0).text();

                    /***************************HTML parsing*******************************/

                    Log.d("", "");

                    //Object to store fixture data
                    Fixture fixtureObj = new Fixture(fixtureDate, null, time, homeTeam,
                            homeTeamLogo, homeScore, awayTeam,
                            awayTeamLogo, awayScore, null);

                    fixtureList.add(fixtureObj);
                }
                Log.e("","");
            }
        }

        private String getLeagueFixtureLink(String leagueName, String month) {

            return String.format(Constants.LEAGUE_SCHEDULE_WEBLINK,
                    leagueName.toLowerCase().replaceAll(" ", "-").trim(),
                    getLeagueCode(getActionBarTitle()),
                    getDate(month));
        }

        private String getDate(String month) {
            return getYear(month) + getMonthNum(month);
        }

        private String getLeagueCode(String currentActionBarTitle) {
            return Constants.TEAM_CODES[Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle)];
        }

        private String getTeamFixtureLink(String team) {
            while (true) {
                if (!Global.teamList.isEmpty()) break;
            }
            String t = team;
            String homeLink = Global.teamList.get(Global.teamNameList.indexOf(team)).getHomeLink();
            homeLink.replace("index", "fixtures");
            return homeLink.replace("index", "fixtures");
        }

        private void getTeamFixturesData(List<Fixture> fixtureList, Elements fixtures, String month) {

            int numberOfFixtures = fixtures.size();
            String fixtureDate = "";

            for (int index = 1; index < numberOfFixtures; index++) {

                /**********HTML parsing to get data************/
                Element fixture = fixtures.get(index);

                Log.d("test", "Parsing fixtures");

                /**************************Html parsing********************************/

                String date = fixture.getElementsByClass("date-container").get(0).getElementsByClass("date").get(0).text();

                // Only select fixtures of selected month
                if (!date.substring(0, 2).equalsIgnoreCase(month.substring(0, 2))) continue;

                String status = null;
                String time = null;

                try {
                    status = fixture.getElementsByClass("date-container").get(0).getElementsByClass("status").get(0).text();
                    time = fixture.getElementsByClass("date-container").get(0).getElementsByClass("time").get(0).text();
                } catch (Exception e) {
                    //Continue as one of them needs to be null;
                }

                String homeTeam = fixture.getElementsByClass("score-home-team").get(0).getElementsByClass("team-name").get(0).text();
                String homeTeamLogo = fixture.getElementsByClass("score-home-team").get(0).getElementsByClass("team-logo").get(0).child(0).attr("src");

                String awayTeam = fixture.getElementsByClass("score-away-team").get(0).getElementsByClass("team-name").get(0).text();
                String awayTeamLogo = fixture.getElementsByClass("score-away-team").get(0).getElementsByClass("team-logo").get(0).child(0).attr("src");

                String homeScore = fixture.getElementsByClass("home-score").get(0).text();
                String awayScore = fixture.getElementsByClass("away-score").get(0).text();
                String separator = fixture.getElementsByClass("separator").get(0).text();

                /***************************HTML parsing*******************************/

                Log.d("", "");

                //Object to store fixture data
                Fixture fixtureObj = new Fixture(date, status, time, homeTeam,
                        homeTeamLogo, homeScore, awayTeam,
                        awayTeamLogo, awayScore, separator);

                fixtureList.add(fixtureObj);
            }
        }

        private int getYear(String month) {

            int year = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR);
            try {
                Date date = new SimpleDateFormat("MMMM").parse(month);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                if (cal.get(Calendar.MONTH) < Calendar.AUGUST) return (year + 1);
            } catch (ParseException e) {
                Log.e("test", e.getMessage());
            }

            return year;
        }

        private boolean fixtureList(Element row) {
            return row.attr("class").equals("matches__group");
        }

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

    private class ImageLoadAsyncTask extends AsyncTask<Object, Void, Void > {
        private FixtureHolder mHolder;
        private Bitmap mFirstTeamBitmap;
        private View mFixtureRow;
        private ImageView mImageFirstTeam;
        private ImageView mImageSecondTeam;
        private ProgressBar mFirstTeamProgressBar;
        private ProgressBar mSecondTeamProgressBar;
        private Bitmap mSecondTeamBitmap;

        public ImageLoadAsyncTask(View newsRow) {
            this.mFixtureRow = newsRow;
        }

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

        @Override
        protected Void doInBackground(Object... params) {
            mHolder = (FixtureHolder)params[0];
            int position  = (int)params[1];

            try {
                mFirstTeamBitmap = ImageLoader.getInstance().
                        loadImageSync(mFixtures.get(position).getHomeTeamLogo());
                mSecondTeamBitmap = ImageLoader.getInstance().
                        loadImageSync(mFixtures.get(position).getAwayTeamLogo());

            } catch (NullPointerException e) {
                Log.d("test", "No image found at " + position);
            }
            return null;
        }
    }

    /***********************************************************************************
     * Adapter
     ***********************************************************************************/

    private class FixtureAdapter extends RecyclerView.Adapter<FixtureHolder> {
        private List<Fixture> mFixtures;
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private View mFixtureRow;
        private FixtureHolder mFixtureHolder;

        public FixtureAdapter(Context context, List<Fixture> fixtures) {
            this.mFixtures = fixtures;
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public FixtureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mFixtureRow = mLayoutInflater.inflate(R.layout.fixture_row, parent, false);
            mFixtureHolder = new FixtureHolder(mFixtureRow);
            return mFixtureHolder;
        }

        @Override
        public void onBindViewHolder(FixtureHolder holder, int position) {
            holder.getFirstTeamName().setText(mFixtures.get(position).getHomeTeam());
            holder.getSecondTeamName().setText(mFixtures.get(position).getAwayTeam());
            holder.getDate().setText(mFixtures.get(position).getDate());
            holder.getFirstTeamScore().setText(mFixtures.get(position).getHomeTeamScore());
            holder.getSecondTeamScore().setText(mFixtures.get(position).getAwayTeamScore());
            if (mFixtures.get(position).getTime() != null && !mFixtures.get(position).getTime().isEmpty()) {
                holder.getStatus().setText(mFixtures.get(position).getTime());
            }
            else {
                holder.getStatus().setText(mFixtures.get(position).getStatus());
            }

            new ImageLoadAsyncTask(mFixtureRow).execute(holder, position);
        }

        @Override
        public int getItemCount() {
            return mFixtures.size();
        }
    }

    /******************************************************************************************
     * Holder
     ******************************************************************************************/

    private class FixtureHolder extends RecyclerView.ViewHolder {

        private TextView mFirstTeamName;
        private TextView mSecondTeamName;
        private ImageView mFirstTeamLogo;
        private ImageView mSecondTeamLogo;
        private TextView mFirstTeamScore;
        private TextView mSecondTeamScore;
        private TextView mStatus;
        private TextView mDate;

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
