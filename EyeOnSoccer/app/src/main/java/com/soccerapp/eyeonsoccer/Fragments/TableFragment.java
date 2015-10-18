package com.soccerapp.eyeonsoccer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soccerapp.eyeonsoccer.R;


/**
 * Created by Asad on 17/10/2015.
 */
public class TableFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.table_fragment, container, false);
    }
}
