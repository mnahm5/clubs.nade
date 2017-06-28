package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ClubDetailsActivity extends AppCompatActivity {

    private ParseObject club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_details);

        Intent intent = getIntent();
        final String clubId = intent.getStringExtra("clubId");

        final TextView tvClubDetails = (TextView) findViewById(R.id.tvClubDetails);
        final TextView tvFees = (TextView) findViewById(R.id.tvFees);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
        query.whereEqualTo("objectId", clubId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() == 1 && e == null) {
                    club = objects.get(0);
                    setTitle(club.getString("name"));
                    tvClubDetails.setText(club.getString("details"));
                    String fees = tvFees.getText().toString() + " ";
                    tvFees.setText(fees.concat(club.getString("fees")));
                }
                else if (e != null) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
