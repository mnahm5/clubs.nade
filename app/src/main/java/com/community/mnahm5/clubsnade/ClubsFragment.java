package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ClubsFragment extends Fragment {

    public ClubsFragment() {
        // Required empty public constructor
    }

    public static ClubsFragment newInstance() {
        return new ClubsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clubs, container, false);
    }
}
