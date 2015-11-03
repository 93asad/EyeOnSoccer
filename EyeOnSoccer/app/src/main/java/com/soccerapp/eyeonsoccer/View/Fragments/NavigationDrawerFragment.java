package com.soccerapp.eyeonsoccer.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.Model.League;
import com.soccerapp.eyeonsoccer.R;

import java.util.Collections;
import java.util.List;

/**
 * Class represents drawer fragment
 */
public class NavigationDrawerFragment extends Fragment {

    private DrawerLayout mDrawLayout;
    private RecyclerView mRecyclerView;
    private LeagueAdapter mLeagueAdapter;

    /**
     * Constructor
     */
    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View drawer = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) drawer.findViewById(R.id.drawer_list);

        mLeagueAdapter = new LeagueAdapter(getActivity(), Global.leagues());
        mRecyclerView.setAdapter(mLeagueAdapter);

        //Setup recycler view click detection mechanism
        setupClickDetection();

        // Show items in list one below another
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        return drawer;
    }

    /**
     * Set click detection using gesture detector
     */
    private void setupClickDetection() {
        //Detect single tap on screen
        final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    /**Manage tap up events
                     *
                     * @param e
                     * @return
                     */
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
        setupOnItemTouchListener(gestureDetector);
    }

    /**
     * Setup item click listener
     *
     * @param gestureDetector
     */
    private void setupOnItemTouchListener(final GestureDetector gestureDetector) {
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

            /**Catch interception touch events
             *
             * @param recyclerView
             * @param motionEvent
             * @return
             */
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                //Gey view under touch using coordinates
                View league = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                //If touch event, then set actionbar title and close navigation drawer
                if (league != null && gestureDetector.onTouchEvent(motionEvent)) {
                    String message = Constants.LEAGUE_SELECTED_MESSAGE;
                    Global.selectedLeagueName = ((TextView) league.
                            findViewById(R.id.league_name)).getText().toString();
                    message = String.format(message, Global.selectedLeagueName);
                    Global.showToast(getActivity(), message);
                    mDrawLayout.closeDrawers();

                    return true;
                }
                return false;
            }

            /**Catch touch events
             *
             * @param recyclerView
             * @param motionEvent
             */
            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });
    }

    /**
     * Set up drawer layout
     *
     * @param drawerLayout
     */
    public void setup(DrawerLayout drawerLayout) {
        mDrawLayout = drawerLayout;
    }


    /**************************************************************************************
     * Adapter
     **************************************************************************************/

    /**
     * Adapter to manage league rows
     */
    private class LeagueAdapter extends RecyclerView.Adapter<LeagueHolder> {

        private LayoutInflater mLayoutInflater;
        private LeagueHolder mLeagueHolder;
        private View mLeagueRow;
        private List<League> mLeagues = Collections.emptyList(); //to avoid null pointer exception

        /**
         * Constructor
         *
         * @param context
         * @param leagues
         */
        public LeagueAdapter(Context context, List<League> leagues) {
            mLayoutInflater = LayoutInflater.from(context);
            mLeagues = leagues;
        }

        /**
         * Inflate league row
         *
         * @param viewGroup
         * @param i
         * @return
         */
        @Override
        public LeagueHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            mLeagueRow = mLayoutInflater.inflate(R.layout.league_row, viewGroup, false);
            mLeagueHolder = new LeagueHolder(mLeagueRow);
            return mLeagueHolder;
        }

        /**
         * Bind data to league holder fields
         *
         * @param leagueHolder
         * @param index
         */
        @Override
        public void onBindViewHolder(LeagueHolder leagueHolder, int index) {
            leagueHolder.getLeagueName().setText(mLeagues.get(index).getName());
            leagueHolder.getLeagueLogo().setImageResource(mLeagues.get(index).getLogoId());
        }

        /**
         * Return number of leagues
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mLeagues.size();
        }
    }

    /*******************************************************************************
     * Holder
     ********************************************************************************/

    /**
     * Holder to store league data
     */
    private class LeagueHolder extends RecyclerView.ViewHolder {
        private ImageView mLeagueLogo;
        private TextView mLeagueName;

        /**
         * Constructor
         *
         * @param itemView
         */
        public LeagueHolder(View itemView) {
            super(itemView);

            mLeagueLogo = (ImageView) itemView.findViewById(R.id.league_logo);
            mLeagueName = (TextView) itemView.findViewById(R.id.league_name);
        }

        /**
         * Get league logo
         *
         * @return
         */
        public ImageView getLeagueLogo() {
            return mLeagueLogo;
        }

        /**
         * Get league name
         *
         * @return
         */
        public TextView getLeagueName() {
            return mLeagueName;
        }
    }

}
