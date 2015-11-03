package com.soccerapp.eyeonsoccer.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.Model.Fixture;
import com.soccerapp.eyeonsoccer.Model.News;
import com.soccerapp.eyeonsoccer.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
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
 * Created by Asad on 17/10/2015. Represents news fragment
 */
public class NewsFragment extends Fragment {

    private RecyclerView mNewsList;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<News> mNews;
    private NewsAdapter mNewsAdapter;
    private View mNewsView;

    private final String KEY_NEWS = "news";
    private Spinner mTeamsSpinner;
    private ArrayAdapter mTeamsSpinnerAdapter;

    /**
     * Setup image viewer and broadcast receiver
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
        setupImageViewer();
        setRetainInstance(true);
    }

    /**
     * Setup Image viewer
     */
    private void setupImageViewer() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build(); //Enable caching

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
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
     * Set news view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mNewsView = inflater.inflate(R.layout.news_fragment, container, false);
        mTeamsSpinner = (Spinner) mNewsView.findViewById(R.id.teams_spinner_news);

        setTeamsSpinner(savedInstanceState);
        mNewsList = (RecyclerView) (mNewsView.findViewById(R.id.news_list));

        //Set data from bundle if league hasnt changed
        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_NEWS)
                && ((String) savedInstanceState.getString(Constants.KEY_ACTION_BAR_TITLE))
                .equals(getActionBarTitle())) {
            mNews = (ArrayList<News>) savedInstanceState.getSerializable(KEY_NEWS);

        } else {
            mNews = new ArrayList<News>();
            fetchData(); //Fetch data for teams
        }

        mNewsAdapter = new NewsAdapter(getActivity(), mNews);
        mNewsList.setClickable(true);
        mNewsList.setAdapter(mNewsAdapter);
        mNewsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewsList.setHasFixedSize(true);

        return mNewsView;
    }

    /**
     * Set teams spinner
     *
     * @param savedInstanceState
     */
    private void setTeamsSpinner(Bundle savedInstanceState) {
        // Create an ArrayAdapter for to store teams for spinner
        mTeamsSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, Global.sortedTeamNameList);
        mTeamsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTeamsSpinner.setAdapter(mTeamsSpinnerAdapter);

        //Set data from bundle if league hasnt changed
        if (savedInstanceState != null)
            mTeamsSpinner.setSelection(savedInstanceState.getInt(Constants.KEY_TEAMS_SPINNER), false);
        else mTeamsSpinner.setSelection(0, false);

        mTeamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**Handle spinner item selection events
             *
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNews.clear();
                fetchData();
                mNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Set broadcast receiver
     */
    private void setupBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            /**On event received, get new data from web
             *
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                mNews.clear();
                fetchData();
                mNewsAdapter.notifyDataSetChanged();
            }
        };
        mLocalBroadcastManager = LocalBroadcastManager.getInstance
                (getActivity().getApplicationContext());
        mLocalBroadcastManager.registerReceiver(mReceiver, Constants.INTENT_FILTER);
    }

    /**
     * Save data in bundle
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mNews.isEmpty()) outState.putSerializable(KEY_NEWS, mNews);
        String title = getActionBarTitle();
        outState.putString(Constants.KEY_ACTION_BAR_TITLE, title);
        outState.putInt(Constants.KEY_TEAMS_SPINNER, mTeamsSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * Starts async task to get new news data using appropriate parameters
     */
    private void fetchData() {
        String leagueName = ((AppCompatActivity) getActivity())
                .getSupportActionBar().getTitle().toString();

        String team = "";
        try {
            team = mTeamsSpinner.getSelectedItem().toString().trim();
        } catch (Exception e) {
            team = "all";
        }

        new NewsDataAsync(mNewsView).executeOnExecutor
                (AsyncTask.THREAD_POOL_EXECUTOR,
                        leagueName,
                        mNews,
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
     * Async task to get new news data from web
     */
    private class NewsDataAsync extends AsyncTask<Object, Void, Void> {

        private final CharSequence KEY_RSS = "rss";
        private final String KEY_INDEX = "index";
        private final String KEY_LOG = "clear";
        private final String KEY_NEWS_ITEM = "item";
        private final String KEY_TITLE = "title";
        private final String KEY_LINK = "link";
        private final String KEY_DATE = "pubDate";
        private final java.lang.String KEY_URL = "url";
        private final String KEY_IMAGE = "enclosure";
        private ProgressBar mProgressBar;

        private View mView;

        public NewsDataAsync(View view) {
            this.mView = view;
        }

        /**
         * Hide list and shoe progress bar
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) (mView.findViewById(R.id.progress_bar_news));
            mProgressBar.setIndeterminate(true);
            mTeamsSpinner.setVisibility(Spinner.GONE);
            mNewsList.setVisibility(RecyclerView.GONE);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        /**
         * Given team name, get team news link
         *
         * @param team
         * @return
         */
        private String getTeamNewsLink(String team) {
            while (true) {
                if (!Global.teamList.isEmpty())
                    break; //Links are in news object contained in global teamlist
            }
            String homeLink = Global.teamList.get(Global.teamNameList.indexOf(team)).getHomeLink();
            return homeLink.replace(KEY_INDEX, KEY_RSS);
        }

        /**
         * Given league name,get league code
         *
         * @param currentActionBarTitle
         * @return
         */
        private String getLeagueCode(String currentActionBarTitle) {
            return Constants.TEAM_CODES[Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle)];
        }

        /**
         * Given league name, get league news link
         *
         * @param leagueName
         * @return
         */
        private String getLeagueNewsLink(String leagueName) {

            return String.format(Constants.LEAGUE_NEWS_WEBLINK,
                    leagueName.toLowerCase().replaceAll(" ", "-").trim(),
                    getLeagueCode(getActionBarTitle()));
        }

        /**
         * Fetch news data from retrived news document
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            String leagueName = (String) params[0];
            List<News> newsObjList = (List<News>) params[1];
            String team = ((String) params[2]).trim();

            //Page contains all feeds web-links for all leagues and teams
            //Document newsFeedsPage = getNewsFeedPage();

            String newsLink = "";
            if (team.equalsIgnoreCase("all")) {
                newsLink = getLeagueNewsLink(leagueName);
            } else {
                newsLink = getTeamNewsLink(team);
            }
            Log.d("test", newsLink);

            // Extract data from rss feed
            getRssData(newsLink, newsObjList);

            while (true) // Wait for teams list to populate
            {
                if (!Global.teamList.isEmpty()) break;
            }

            return null;
        }

        /**
         * Parse news document to get news
         *
         * @param newsLink
         * @param newsObjList
         */
        private void getRssData(String newsLink, List<News> newsObjList) {
            try {
                Document doc = Jsoup.connect(newsLink).userAgent(Constants.USER_AGENT).get();
                Document doc1 = Jsoup.parse(doc.html(), "", Parser.xmlParser());
                Elements items = doc1.select(KEY_NEWS_ITEM);

                for (int index = 0; index < items.size(); index++) {
                    String title = items.get(index).select(KEY_TITLE).first().text();
                    String link = items.get(index).select(KEY_LINK).first().text();
                    String date = items.get(index).select(KEY_DATE).first().text();
                    String imageLink = null;
                    try {
                        imageLink = items.get(index).select(KEY_IMAGE).first().attr(KEY_URL);
                    } catch (NullPointerException e) {
                        // Some news might not have image
                    }
                    newsObjList.add(new News(title, imageLink, date, link));
                }
                Log.d(KEY_LOG, doc.html());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Show list and hide progress bar
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTeamsSpinnerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(ProgressBar.GONE);
            mNewsList.setVisibility(RecyclerView.VISIBLE);
            mTeamsSpinner.setVisibility(Spinner.VISIBLE);
        }

    }

    /**
     * Async task to get news image from web
     */
    private class ImageLoadAsyncTask extends AsyncTask<Object, Void, Void> {
        private NewsHolder mHolder;
        private Bitmap mImageBitmap;
        private View mNewsRow;
        private ImageView mImage;
        private ProgressBar mProgressBar;

        /**
         * Constructor
         *
         * @param newsRow
         */
        public ImageLoadAsyncTask(View newsRow) {
            this.mNewsRow = newsRow;
        }

        /**
         * Show progress bar and hide image
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImage = (ImageView) mNewsRow.findViewById(R.id.image_news);
            mImage.setVisibility(ImageView.GONE);
            mProgressBar = (ProgressBar) mNewsRow.findViewById(R.id.image_progress_bar);
            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);

        }

        /**
         * Show image and hide progress bar and set image
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHolder.getNewsImage().setImageBitmap(mImageBitmap);

            mProgressBar.setVisibility(ProgressBar.GONE);
            mImage.setVisibility(ImageView.VISIBLE);
        }

        /**
         * Get Image from web as bitmap
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            mHolder = (NewsHolder) params[0];
            int position = (int) params[1];

            try {

                mImageBitmap = ImageLoader.getInstance().loadImageSync(mNews.get(position).getImageLink());

            } catch (NullPointerException e) { //Image may not exist for some news
            }
            return null;
        }
    }
    /***********************************************************************************
     * Adapter
     ***********************************************************************************/

    /**
     * Adapter to manage news data
     */
    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<News> mNews;
        private LayoutInflater mLayoutInflater;
        private View mNewsRow;
        private NewsHolder mNewsHolder;

        /**
         * Comnstructor
         *
         * @param context
         * @param news
         */
        public NewsAdapter(Context context, List<News> news) {
            this.mNews = news;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        /**
         * Inflates news row
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mNewsRow = mLayoutInflater.inflate(R.layout.news_row, parent, false);
            mNewsHolder = new NewsHolder(mNewsRow);
            return mNewsHolder;
        }

        /**
         * Bind data to holder fields
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(NewsHolder holder, int position) {
            holder.getTitle().setText(mNews.get(position).getTitle());
            holder.getDate().setText(mNews.get(position).getDate());
            if (mNews.get(position).getImageLink() != null) { //Some news might not have image links
                new ImageLoadAsyncTask(mNewsRow).execute(holder, position);

            } else holder.getNewsImage().setImageResource(R.mipmap.ic_launcher);
        }

        /**
         * Get number of news
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mNews.size();
        }
    }

    /******************************************************************************************
     * Holder
     ******************************************************************************************/

    /**
     * Holder to store news data
     */
    private class NewsHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mDate;
        private ImageView mNewsImage;

        /**
         * Constructor
         *
         * @param itemView
         */
        public NewsHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title_news);
            mDate = (TextView) itemView.findViewById(R.id.date_news);
            mNewsImage = (ImageView) itemView.findViewById(R.id.image_news);

            itemView.setOnClickListener(new View.OnClickListener() {
                /**Handle item clicks and lauch web browser with news link
                 *
                 * @param v
                 */
                @Override
                public void onClick(View v) {
                    News news = mNews.get(getAdapterPosition());

                    String url = news.getLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }

        public ImageView getNewsImage() {
            return mNewsImage;
        }

        public TextView getTitle() {
            return mTitle;
        }

        public void setTitle(TextView title) {
            this.mTitle = title;
        }

        public TextView getDate() {
            return mDate;
        }

    }
}
