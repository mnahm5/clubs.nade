package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class EventDetailsFragment extends Fragment {

    private static final String EVENT_ID = "eventId";

    private String eventId = null;
    private ParseObject event = null;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(String fragmentClubId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, fragmentClubId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        getEventDetails(view);

        return view;
    }

    private void getEventDetails(final View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", eventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    event = objects.get(0);

                    getActivity().setTitle(event.get("name").toString());

                    final ImageView ivEventLogo = (ImageView) view.findViewById(R.id.ivEventLogo);
                    final TextView tvEventDetails = (TextView) view.findViewById(R.id.tvEventDetails);
                    final TextView tvEventFees = (TextView) view.findViewById(R.id.tvEventFees);
                    final TextView tvEventAccess = (TextView) view.findViewById(R.id.tvEventAccess);

                    tvEventDetails.setText(event.get("details").toString());
                    String fees = String.format(Locale.ENGLISH, "%s%s", tvEventFees.getText(), event.get("fees").toString());
                    tvEventFees.setText(fees);
                    String access = String.format(Locale.ENGLISH, "%s %s", tvEventAccess.getText(), event.get("access").toString());
                    tvEventAccess.setText(access);
                }
                else if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "No event found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
