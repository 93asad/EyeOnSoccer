package com.soccerapp.eyeonsoccer.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.Model.Team;
import com.soccerapp.eyeonsoccer.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Asad on 17/10/2015. Represents table fragment
 */
public class TableFragment extends Fragment {

    private RecyclerView mClubs;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private TeamAdapter mTeamAdapter;
    private View mTableView;

    /**
     * On create, et broadcast receiver
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
    }

    /**
     * Set broadcast receiver
     */
    private void setupBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            /**
             * On receive get new table data from web
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                new TableDataAsync(mTableView).execute();
                mTeamAdapter.notifyDataSetChanged();
            }
        };
        mLocalBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity().getApplicationContext());
        mLocalBroadcastManager.registerReceiver(mReceiver, Constants.INTENT_FILTER);
    }

    /**
     * Set table view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTableView = inflater.inflate(R.layout.table_fragment, container, false);
        new TableDataAsync(mTableView).execute();
        mTeamAdapter = new TeamAdapter(getActivity(), Global.teamList);

        mClubs = (RecyclerView) (mTableView.findViewById(R.id.clubs_list));
        mClubs.setAdapter(mTeamAdapter);
        mClubs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mClubs.setHasFixedSize(true);

        return mTableView;
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
     * Async task to to wait for global team list to populate
     */
    private class TableDataAsync extends AsyncTask<Object, Void, Void> {

        private ProgressBar mProgressBar;

        private View mView;
        private View mHeading;

        /**
         * Constructor
         *
         * @param view
         */
        public TableDataAsync(View view) {
            this.mView = view;
        }

        /**
         * Show progress bar and hide list
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) (mView.findViewById(R.id.progress_bar_table));
            mProgressBar.setIndeterminate(true);
            mHeading = mView.findViewById(R.id.table_heading);
            mHeading.setVisibility(LinearLayout.GONE);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        /**
         * Wait fot global team list to populate
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            while (true) {
                if (!Global.teamList.isEmpty()) break;
            }
            return null;
        }

        /**
         * Show list and hide progress bar and update teams spinner
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTeamAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(ProgressBar.GONE);
            mHeading.setVisibility(LinearLayout.VISIBLE);
            mClubs.setVisibility(ListView.VISIBLE);
        }
    }

    /***********************************************************************************
     * Adapter
     ***********************************************************************************/

    /**
     * Adapter to manage team data
     */
    private class TeamAdapter extends RecyclerView.Adapter<TeamHolder> {
        private List<Team> mTeams;
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private View mTeamRow;
        private TeamHolder mTeamHolder;

        /**
         * Constructor
         *
         * @param context
         * @param teams
         */
        public TeamAdapter(Context context, List<Team> teams) {
            this.mTeams = teams;
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        /**
         * Inflate team row
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mTeamRow = mLayoutInflater.inflate(R.layout.team_row, parent, false);
            mTeamHolder = new TeamHolder(mTeamRow);
            return mTeamHolder;
        }

        /**
         * Bind team data to holder fields
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(TeamHolder holder, int position) {
            holder.getName().setText(mTeams.get(position).getName());
            holder.getRank().setText(mTeams.get(position).getRank());
            holder.getMatchesPlayed().setText(mTeams.get(position).getMatchesPlayed());
            holder.getWins().setText(mTeams.get(position).getWins());
            holder.getDraws().setText(mTeams.get(position).getDraws());
            holder.getLoss().setText(mTeams.get(position).getLoss());
            holder.getGoalDiff().setText(mTeams.get(position).getGoalDiff());
            holder.getPoints().setText(mTeams.get(position).getPoints());
        }

        /**
         * Get number of teams
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mTeams.size();
        }
    }

    /******************************************************************************************
     * Holder
     ******************************************************************************************/

    /**
     * Holder to store team data
     */
    private class TeamHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mMatchesPlayed;
        private TextView mWins;
        private TextView mDraws;
        private TextView mLoss;
        private TextView mGoalDiff;
        private TextView mPoints;
        private TextView mRank;

        /**
         * Constructor
         *
         * @param itemView
         */
        public TeamHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.table_team_name);
            mMatchesPlayed = (TextView) itemView.findViewById(R.id.table_matches_played);
            mWins = (TextView) itemView.findViewById(R.id.table_wins);
            mDraws = (TextView) itemView.findViewById(R.id.table_draw);
            mLoss = (TextView) itemView.findViewById(R.id.table_loss);
            mGoalDiff = (TextView) itemView.findViewById(R.id.table_goal_diff);
            mPoints = (TextView) itemView.findViewById(R.id.table_points);
            mRank = (TextView) itemView.findViewById(R.id.table_rank);
        }

        public TextView getName() {
            return mName;
        }

        public void setName(TextView mName) {
            this.mName = mName;
        }

        public TextView getMatchesPlayed() {
            return mMatchesPlayed;
        }

        public TextView getWins() {
            return mWins;
        }

        public TextView getDraws() {
            return mDraws;
        }

        public TextView getLoss() {
            return mLoss;
        }

        public TextView getGoalDiff() {
            return mGoalDiff;
        }

        public TextView getPoints() {
            return mPoints;
        }

        public TextView getRank() {
            return mRank;
        }

    }
}
