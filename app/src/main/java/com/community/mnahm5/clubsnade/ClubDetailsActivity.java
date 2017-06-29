package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
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
        final TextView tvEmail = (TextView) findViewById(R.id.tvEmail);

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
                    tvEmail.setText(club.getString("email"));
                    showClubLogo();
                    checkMembership();
                }
                else if (e != null) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showClubLogo() {
        final ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);

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

    private void checkMembership() {
        final Button btJoin = (Button) findViewById(R.id.btJoin);

        List<String> admins = club.getList("admins");
        for (String userId: admins) {
            if (userId.compareTo(ParseUser.getCurrentUser().getObjectId()) == 0) {
                btJoin.setVisibility(View.INVISIBLE);
            }
        }
    }
}
