package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class AddOrRemoveUserActivity extends AppCompatActivity {

    private ParseObject club = null;
    private ParseObject event = null;
    private String userType = null;

    private List<ParseObject> userList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_remove_user);

        Intent intent = getIntent();
        final String clubId = intent.getStringExtra("clubId");
        if (clubId != null) {
            userType = intent.getStringExtra("userType");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
            query.whereEqualTo("objectId", clubId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        club = objects.get(0);
                        setTitle(String.format(
                                "%s - %s",
                                club.get("name"),
                                userType.substring(0, 1).toUpperCase() + userType.substring(1)
                        ));
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Club Found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
