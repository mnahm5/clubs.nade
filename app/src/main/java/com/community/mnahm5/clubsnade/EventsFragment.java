package com.community.mnahm5.clubsnade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsFragment extends Fragment {

    private static final String STATE = null;

    private String state = null;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String fragmentState) {
        EventsFragment fragment = new EventsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        if (container != null) {
            container.removeAllViews();
        }

        final Button btCreateEvent = (Button) view.findViewById(R.id.btCreateEvent);
        if (state != null && state.equals("Home")) {
            btCreateEvent.setVisibility(View.INVISIBLE);
        }

        final ListView lvEvents = (ListView) view.findViewById(R.id.lvEvents);
        final List<Map<String, String>> eventsData = new ArrayList<Map<String, String>>();

        for (int i = 0; i < 10; i++) {
            Map<String, String> eventData = new HashMap<String, String>();
            eventData.put("name", "Event Name");
            eventData.put("clubName", "Club Name");
            eventsData.add(eventData);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                eventsData,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "clubName"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        lvEvents.setAdapter(simpleAdapter);

        return view;
    }
}
