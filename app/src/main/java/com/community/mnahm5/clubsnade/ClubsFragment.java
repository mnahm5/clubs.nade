package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClubsFragment extends Fragment {

    private static final String STATE = null;

    private String state = null;

    public ClubsFragment() {
        // Required empty public constructor
    }

    public static ClubsFragment newInstance(String fragmentState) {
        ClubsFragment fragment = new ClubsFragment();
        Bundle args = new Bundle();
        args.putString(STATE, fragmentState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getString(STATE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);
        if (container != null) {
            container.removeAllViews();
        }

        Toast.makeText(getContext(), "Getting Data\nPlease Wait..", Toast.LENGTH_LONG).show();

        final ListView lvClubs = (ListView) view.findViewById(R.id.lvClubs);
        final List<Map<String, String>> clubsData = new ArrayList<Map<String, String>>();
        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                clubsData,
                android.R.layout.simple_list_item_2,
                new String[] {"clubName", "clubDetails"},
                new int[] {android.R.id.text1, android.R.id.text2}
                );

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
        query.whereExists("name");
        query.addAscendingOrder("name");
        if (state.equals("Admin") || state.equals("Member")) {
            ArrayList<String> userIds = new ArrayList<String>();
            userIds.add(ParseUser.getCurrentUser().getObjectId());
            query.whereContainedIn("admins", userIds);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    for (ParseObject club: objects) {
                        Map<String, String> clubData = new HashMap<String, String>();
                        clubData.put("clubName", club.get("name").toString());
                        if (club.get("details").toString().length() > 30) {
                            clubData.put("clubDetails", club.get("details").toString().substring(0, 30).concat("..."));
                        }
                        else {
                            clubData.put("clubDetails", club.get("details").toString());
                        }
                        clubData.put("id", club.getObjectId());
                        clubsData.add(clubData);
                    }
                    lvClubs.setAdapter(simpleAdapter);
                }
                else if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button btCreateClubs = (Button) view.findViewById(R.id.btCreateClub);
        btCreateClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateClub(view);
            }
        });

        lvClubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedClubId = clubsData.get(i).get("id");
                Fragment fragment = null;
                fragment = ClubDetailsFragment.newInstance(selectedClubId);
                replaceFragment(fragment);
            }
        });

        return view;
    }

    public void CreateClub(View view) {
        Intent intent = new Intent(getContext(), CreateClubActivity.class);
        startActivity(intent);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
