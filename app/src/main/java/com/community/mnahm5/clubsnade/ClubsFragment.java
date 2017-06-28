package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        ListView lvClubs = (ListView) view.findViewById(R.id.lvClubs);

        List<Map<String, String>> clubsData = new ArrayList<Map<String, String>>();

        for (int i = 0; i <=10; i++) {
            Map<String, String> clubData = new HashMap<String, String>();
            clubData.put("clubName", "Club Name");
            clubData.put("clubDetails", "Club Details");
            clubsData.add(clubData);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                clubsData,
                android.R.layout.simple_list_item_2,
                new String[] {"clubName", "clubDetails"},
                new int[] {android.R.id.text1, android.R.id.text2}
                );

        lvClubs.setAdapter(simpleAdapter);

        final Button btCreateClubs = (Button) view.findViewById(R.id.btCreateClub);
        btCreateClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateClub(view);
            }
        });

        return view;
    }

    public void CreateClub(View view) {
        Intent intent = new Intent(getContext(), CreateClubActivity.class);
        startActivity(intent);
    }
}
