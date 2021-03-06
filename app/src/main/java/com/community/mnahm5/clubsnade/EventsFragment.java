package com.community.mnahm5.clubsnade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsFragment extends Fragment {

    private static final String STATE = "state";
    private static final String CLUB_ID = "clubId";

    private String state = null;
    private String clubId = null;
    private HashMap<String, ParseObject> clubs = null;
    private List<ParseObject> events = null;
    private ListView lvEvents;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String fragmentState, String fragmentClubId) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(STATE, fragmentState);
        args.putString(CLUB_ID, fragmentClubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            state = getArguments().getString(STATE);
            clubId = getArguments().getString(CLUB_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        if (container != null) {
            container.removeAllViews();
        }

        lvEvents = (ListView) view.findViewById(R.id.lvEvents);
        getClubs();

        return view;
    }

    private void getClubs() {
        ParseQuery<ParseObject> query = null;

        if (state != null && state.equals("Home")) {
            List<String> userIds = new ArrayList<String>();
            userIds.add(ParseUser.getCurrentUser().getObjectId());
            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Club");
            query1.whereContainedIn("admins", userIds);
            queries.add(query1);

            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Club");
            query2.whereContainedIn("clubMembers", userIds);
            queries.add(query2);

            query = ParseQuery.or(queries);
        }
        else if (state != null && state.equals("Club")) {
            query = ParseQuery.getQuery("Club");
            query.whereEqualTo("objectId", clubId);
        }

        if (query != null) {
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        clubs = new HashMap<String, ParseObject>();
                        for (ParseObject club: objects) {
                            clubs.put(club.getObjectId(), club);
                        }
                        getEvents();
                    }
                    else if (e != null) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "No Club Found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void getEvents() {
        if (clubs != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");

            if (state != null && !state.equals("All")) {
                List<String> clubIds = new ArrayList<String>(clubs.keySet());
                query.whereContainedIn("clubId", clubIds);
            }

            if (state != null && state.equals("Club")) {
                getActivity().setTitle(clubs.get(clubId).get("name").toString() + " - Events");
            }

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        events = objects;
                        ShowListView();
                    }
                    else if (e != null) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "No Event Found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void ShowListView() {
        if (events != null && clubs != null) {
            final List<Map<String, String>> eventsData = new ArrayList<Map<String, String>>();

            for (ParseObject event: events) {
                if (!(event.get("access").toString().equals("Admins Only") &&
                    !clubs.get(event.get("clubId").toString()).getList("admins").contains(ParseUser.getCurrentUser().getObjectId()))) {
                    Map<String, String> eventData = new HashMap<String, String>();
                    eventData.put("name", event.get("name").toString());
                    eventData.put("clubName", clubs.get(event.get("clubId").toString()).get("name").toString());
                    eventsData.add(eventData);
                }
            }

            final SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getContext(),
                    eventsData,
                    android.R.layout.simple_list_item_2,
                    new String[] {"name", "clubName"},
                    new int[] {android.R.id.text1, android.R.id.text2}
            );
            lvEvents.setAdapter(simpleAdapter);

            lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String eventId = events.get(i).getObjectId();
                    Fragment fragment = null;
                    fragment = EventDetailsFragment.newInstance(eventId);
                    replaceFragment(fragment);
                }
            });
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
