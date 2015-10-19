package com.soccerapp.eyeonsoccer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soccerapp.eyeonsoccer.Classes.Constants;
import com.soccerapp.eyeonsoccer.Classes.Model.League;
import com.soccerapp.eyeonsoccer.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Asad on 18/10/2015.
 */
public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.LeagueHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private LeagueHolder mLeagueHolder;
    private View mLeagueRow;
    private List<League> mLeagues = Collections.emptyList(); //to avoid null pointer exception

    public LeagueAdapter(Context context, List<League> leagues) {
        mLayoutInflater = LayoutInflater.from(context);
        mLeagues = leagues;
        mContext = context;
    }

    @Override
    public LeagueHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mLeagueRow = mLayoutInflater.inflate(R.layout.league_row, viewGroup, false);
        mLeagueHolder = new LeagueHolder(mLeagueRow);
        return mLeagueHolder;
    }

    @Override
    public void onBindViewHolder(LeagueHolder leagueHolder, int index) {
        leagueHolder.leagueName.setText(mLeagues.get(index).getName());
        leagueHolder.leagueLogo.setImageResource(mLeagues.get(index).getLogoId());
    }

    @Override
    public int getItemCount() {
        return mLeagues.size();
    }

    class LeagueHolder extends RecyclerView.ViewHolder {
        ImageView leagueLogo;
        TextView leagueName;

        public LeagueHolder(View itemView) {
            super(itemView);

            leagueLogo = (ImageView) itemView.findViewById(R.id.league_logo);
            leagueName = (TextView) itemView.findViewById(R.id.league_name);
        }
    }
}
