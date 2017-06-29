package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ClubDetailsFragment extends Fragment {
    private static final String CLUB_ID = null;

    private String clubId;
    private ParseObject club;

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
        final View view = inflater.inflate(R.layout.fragment_club_details, container, false);

        final TextView tvClubDetails = (TextView) view.findViewById(R.id.tvClubDetails);
        final TextView tvFees = (TextView) view.findViewById(R.id.tvFees);
        final TextView tvEmail = (TextView) view.findViewById(R.id.tvEmail);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
        query.whereEqualTo("objectId", clubId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() == 1 && e == null) {
                    club = objects.get(0);
                    getActivity().setTitle(club.getString("name"));
                    tvClubDetails.setText(club.getString("details"));
                    String fees = tvFees.getText().toString() + " ";
                    tvFees.setText(fees.concat(club.getString("fees")));
                    tvEmail.setText(club.getString("email"));
                    showClubLogo(view);
                    checkMembership(view);
                }
                else if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void showClubLogo(View view) {
        final ImageView ivLogo = (ImageView) view.findViewById(R.id.ivLogo);

        ParseFile file = (ParseFile) club.get("logo");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ivLogo.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }

    private void checkMembership(View view) {
        final Button btJoin = (Button) view.findViewById(R.id.btJoin);

        List<String> admins = club.getList("admins");
        for (String userId: admins) {
            if (userId.compareTo(ParseUser.getCurrentUser().getObjectId()) == 0) {
                btJoin.setVisibility(View.INVISIBLE);
            }
        }
    }
}