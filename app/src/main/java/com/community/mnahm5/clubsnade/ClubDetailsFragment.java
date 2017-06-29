package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ClubDetailsFragment extends Fragment {
    private static final String CLUB_ID = null;

    private String clubId;

    public ClubDetailsFragment() {
        // Required empty public constructor
    }

    public static ClubDetailsFragment newInstance(String fragmentClubId) {
        ClubDetailsFragment fragment = new ClubDetailsFragment();
        Bundle args = new Bundle();
        args.putString(CLUB_ID, fragmentClubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clubId = getArguments().getString(CLUB_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_club_details, container, false);
    }
}
