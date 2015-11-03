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
 * Created by Asad on 17/10/2015.
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
    //private Spinner mMonthsSpinner;
    private ArrayAdapter mMonthsSpinnerAdapter;
    //private TextView mNoDataMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBroadcastReceiver();
        setupImageViewer();
        setRetainInstance(true);
    }

    private void setupImageViewer() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }

    private String getActionBarTitle() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mNewsView = inflater.inflate(R.layout.news_fragment, container, false);
        //mNoDataMessage = (TextView) mNewsView.findViewById(R.id.no_fixture_data);
        mTeamsSpinner = (Spinner) mNewsView.findViewById(R.id.teams_spinner_news);

        //Setup recycler view click detection mechanism
        setupClickDetection();

        setTeamsSpinner(savedInstanceState);
        mNewsList = (RecyclerView) (mNewsView.findViewById(R.id.news_list));
        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_NEWS)
                && ((String) savedInstanceState.getString(Global.KEY_ACTION_BAR_TITLE))
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

    private void setupClickDetection() {
        //Detect single tap on screen
        final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
        //setupOnItemTouchListener(gestureDetector);
    }

    /*private void setupOnItemTouchListener(final GestureDetector gestureDetector) {
        mNewsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View fixture = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (fixture != null && gestureDetector.onTouchEvent(motionEvent)) {


                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });
    }*/

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
                mNews.clear();
                fetchData();
                mNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mNews.isEmpty()) outState.putSerializable(KEY_NEWS, mNews);
        String title = getActionBarTitle();
        outState.putString(Global.KEY_ACTION_BAR_TITLE, title);
        outState.putInt(Constants.KEY_TEAMS_SPINNER, mTeamsSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    /********************************************************************************
     * AsyncTask
     ********************************************************************************/

    private class NewsDataAsync extends AsyncTask<Object, Void, Void> {

        private final String SOURCE_ATTRIBUTE = "src";
        private ProgressBar mProgressBar;

        private View mView;
        private Bitmap mImagebitmap;

        public NewsDataAsync(View view) {
            this.mView = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) (mView.findViewById(R.id.progress_bar_news));
            mProgressBar.setIndeterminate(true);
            mTeamsSpinner.setVisibility(Spinner.GONE);
            mNewsList.setVisibility(RecyclerView.GONE);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        private String getTeamNewsLink(String team) {
            while (true) {
                if (!Global.teamList.isEmpty()) break;
            }
            String t = team;
            String homeLink = Global.teamList.get(Global.teamNameList.indexOf(team)).getHomeLink();
            //homeLink.replace("index", "rss");
            return homeLink.replace("index", "rss");
        }

        private String getLeagueCode(String currentActionBarTitle) {
            return Constants.TEAM_CODES[Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle)];
        }

        private String getLeagueNewsLink(String leagueName) {

            return String.format(Constants.LEAGUE_NEWS_WEBLINK,
                    leagueName.toLowerCase().replaceAll(" ", "-").trim(),
                    getLeagueCode(getActionBarTitle()));
        }

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

        private void getRssData(String newsLink, List<News> newsObjList) {
            try {
                Document doc = Jsoup.connect(newsLink).userAgent(Constants.USER_AGENT).get();
                Log.d("", "");
                Document doc1 = Jsoup.parse(doc.html(), "", Parser.xmlParser());
                Elements items = doc1.select("item");

                for (int index = 0; index < items.size(); index++) {
                    String title = items.get(index).select("title").first().text();
                    String link = items.get(index).select("link").first().text();
                    String date = items.get(index).select("pubDate").first().text();
                    String imageLink = null;
                    try {
                        imageLink = items.get(index).select("enclosure").first().attr("url");
                    } catch (NullPointerException e) {
                        // Some news might not have image
                    }
                    newsObjList.add(new News(title, imageLink, date, link));
                }
                Log.d("clear", doc.html());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getNewsLink(Document newsFeedsPage, String teamName, String leagueName, boolean isLeague) {
            if (isLeague) {
                //Get league link
                Elements leagues = newsFeedsPage.getElementsByClass("responsive").get(2).children();
                for (Element league : leagues) {
                    String f = leagueName.toLowerCase().trim();
                    String l = league.child(0).text().trim().toLowerCase();
                    if ((league.child(0).text().trim().toLowerCase()).contains(leagueName.toLowerCase().trim()))
                        return league.child(0).attr("href");
                }
            } else {
                //get team link
                Elements teamsSortedByLeague = newsFeedsPage.getElementsByClass("responsive").get(3).children();
                for (Element league : teamsSortedByLeague) {
                    String y = league.child(0).text().toLowerCase();
                    String z = leagueName.toLowerCase().trim();
                    if (!(league.child(0).text().toLowerCase()).contains(leagueName.toLowerCase().trim()))
                        continue;

                    for (Element team : league.child(1).children()) {

                        if ((team.child(0).text().trim().toLowerCase()).contains(teamName.toLowerCase().trim()))
                            return team.child(0).attr("href");

                    }
                    for (Element team : league.child(1).children()) {
                        try {
                            if ((team.child(0).text().trim().toLowerCase().replaceAll("-", " ")).contains(teamName.substring(0, teamName.lastIndexOf(" ")).toLowerCase().trim()))
                                return team.child(0).attr("href");

                            if ((team.child(0).text().trim().toLowerCase().replaceAll("-", " ")).contains(teamName.substring(teamName.lastIndexOf(" ")).toLowerCase().trim()))
                                return team.child(0).attr("href");

                        } catch (IndexOutOfBoundsException e) {
                            Log.e("test", "out of bound while matching team");
                        }
                    }
                }
            }
            return null;
        }


        private Document getNewsFeedPage() {
            Document doc = null;
            try {
                doc = Jsoup.connect(Constants.LEAGUE_NEWS_WEBLINK).userAgent(Constants.USER_AGENT).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        private void getNewsData(List<News> newsList, Elements rows) {

            int numberOfRows = rows.size();
            //String fixtureDate = "";

            for (int index = 0; index < numberOfRows; index++) {

                /**********HTML parsing to get data************/
                Element row = rows.get(index);

                Log.d("test", "Parsing news");
                if (fixtureList(row))  // Can be fixture row or date row
                {
                    Elements fixtures = row.children(); //Get all fixture rows
                    int s = fixtures.size();
                    for (Element fixture : fixtures) {
                        /**************************Html parsing********************************/

                        //String time = fixture.getElementsByClass("matches__date").get(0).text();
                        //String month = fixtureDate.substring(fixtureDate.lastIndexOf(" "), fixtureDate.length()).trim();
                        //String dateTime = getYear(month) + " " + fixtureDate + ", " + time;

                        //Convert datetime to local time
                        //dateTime = Global.getLocalTime(dateTime);
                        //String localMonth = dateTime.split(" ")[3].replaceAll(",", "").trim();
                        //if (!((String) mMonthsSpinner.getSelectedItem()).trim().equalsIgnoreCase
                        //      (localMonth.trim())) {
                        //continue;
                        //}

                        String firstTeam = fixture.getElementsByClass("swap-text__target").get(0).text();
                        String secondTeam = fixture.getElementsByClass("swap-text__target").get(1).text();
                        String newsLink = fixture.getElementsByClass("matches__link").get(0).attr("href");

                        /***************************HTML parsing*******************************/

                        Log.d("", "");

                        //Object to store fixture data
                        // News newsObj = new Fixture(firstTeam, secondTeam, newsLink, dateTime);

                        //newsList.add(newsObj);
                    }

                } else {
                    //fDate = row.text();
                    continue;
                }

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
            mTeamsSpinnerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(ProgressBar.GONE);
            mNewsList.setVisibility(RecyclerView.VISIBLE);
            mTeamsSpinner.setVisibility(Spinner.VISIBLE);
        }

    }

    private class ImageLoadAsyncTask extends AsyncTask<Object, Void, Void > {
        private NewsHolder mHolder;
        private Bitmap mImageBitmap;
        private View mNewsRow;
        private ImageView mImage;
        private ProgressBar mProgressBar;

        public ImageLoadAsyncTask(View newsRow) {
            this.mNewsRow = newsRow;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImage = (ImageView)mNewsRow.findViewById(R.id.image_news);
            mImage.setVisibility(ImageView.GONE);
            mProgressBar = (ProgressBar)mNewsRow.findViewById(R.id.image_progress_bar);
            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHolder.getNewsImage().setImageBitmap(mImageBitmap);

            mProgressBar.setVisibility(ProgressBar.GONE);
            mImage.setVisibility(ImageView.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... params) {
            mHolder = (NewsHolder)params[0];
            int position  = (int)params[1];

            try {

                mImageBitmap = ImageLoader.getInstance().loadImageSync(mNews.get(position).getImageLink());

            } catch (NullPointerException e) {
                Log.d("test", "No image found at " + position);
            }
            return null;
        }
    }
    /***********************************************************************************
     * Adapter
     ***********************************************************************************/

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<News> mNews;
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private View mNewsRow;
        private NewsHolder mNewsHolder;

        public NewsAdapter(Context context, List<News> news) {
            this.mNews = news;
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mNewsRow = mLayoutInflater.inflate(R.layout.news_row, parent, false);
            mNewsHolder = new NewsHolder(mNewsRow);
            return mNewsHolder;
        }

        @Override
        public void onBindViewHolder(NewsHolder holder, int position) {
            holder.getTitle().setText(mNews.get(position).getTitle());
            holder.getDate().setText(mNews.get(position).getDate());
            if (mNews.get(position).getImageLink() != null) {
                new ImageLoadAsyncTask(mNewsRow).execute(holder, position);

            } else holder.getNewsImage().setImageResource(R.mipmap.ic_launcher);
        }

        @Override
        public int getItemCount() {
            return mNews.size();
        }
    }

    /******************************************************************************************
     * Holder
     ******************************************************************************************/

    private class NewsHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mDate;
        private ImageView mNewsImage;

        public NewsHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title_news);
            mDate = (TextView) itemView.findViewById(R.id.date_news);
            mNewsImage = (ImageView) itemView.findViewById(R.id.image_news);

            itemView.setOnClickListener(new View.OnClickListener() {
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

        public void setNewsImage(ImageView mNewsImage) {
            this.mNewsImage = mNewsImage;
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

        public void setDate(TextView date) {
            this.mDate = mDate;
        }
    }
}
