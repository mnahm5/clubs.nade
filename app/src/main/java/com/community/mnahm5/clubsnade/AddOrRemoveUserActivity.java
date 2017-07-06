package com.community.mnahm5.clubsnade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrRemoveUserActivity extends AppCompatActivity {

    private ParseObject club = null;
    private ParseObject event = null;
    private String userType = null;

    private List<ParseUser> userList = null;

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
                        ShowCurrentUserList();
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

    public void AddUsers(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddOrRemoveUserActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_add_users, null);
        final EditText etSearch = (EditText) v.findViewById(R.id.etSearch);

        final List<Map<String, String>> userListData = new ArrayList<Map<String, String>>();

        for (int i = 0; i < 20; i++) {
            Map<String, String> userData = new HashMap<String, String>();
            userData.put("username", "Username");
            userData.put("fullName", "Full Name");
            userListData.add(userData);
        }

        final ListView lvSearchResults = (ListView) v.findViewById(R.id.lvSearchResults);
        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                AddOrRemoveUserActivity.this,
                userListData,
                android.R.layout.simple_list_item_2,
                new String[] {"username", "fullName"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        lvSearchResults.setAdapter(simpleAdapter);

        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void SaveChanges(View view) {

    }

    private void ShowCurrentUserList() {
        if (club != null) {
            List<String> userIds = club.getList("admins");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("objectId", userIds);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        userList = objects;
                        final ListView lvUserList = (ListView) findViewById(R.id.lvUserList);
                        final List<Map<String, String>> userListData = new ArrayList<Map<String, String>>();

                        for (ParseUser user: userList) {
                            Map<String, String> userData = new HashMap<String, String>();
                            userData.put("username", user.getUsername());
                            userData.put("fullName", user.get("fullName").toString());
                            userListData.add(userData);
                        }

                        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                                AddOrRemoveUserActivity.this,
                                userListData,
                                android.R.layout.simple_list_item_2,
                                new String[] {"username", "fullName"},
                                new int[] {android.R.id.text1, android.R.id.text2}
                        );
                        lvUserList.setAdapter(simpleAdapter);

                        if (userListData.size() > 6) {
                            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lvUserList.getLayoutParams();
                            lp.height = 1000;
                            lvUserList.setLayoutParams(lp);
                        }
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
