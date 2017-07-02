package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChangeAdminsActivity extends AppCompatActivity {

    private ParseObject club = null;
    private ArrayList<String> lvData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_admins);

        getClub();
    }

    private void getClub() {
        String clubId = getIntent().getStringExtra("clubId");
        if (clubId != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
            query.whereEqualTo("objectId", clubId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        club = objects.get(0);
                        getAdminDetails();
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

    private void getAdminDetails() {
        if (club != null) {
            List<String> admins = club.getList("admins");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("objectId", admins);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseUser user: objects) {
                            lvData.add(String.format(Locale.ENGLISH, "%s (%s)", user.getUsername(), user.get("fullName").toString()));
                        }
                        for (int i = 0; i < 20; i++) {
                            lvData.add("Stuff");
                        }
                        ShowAdminList();
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No admins found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void ShowAdminList() {
        ListView lvAdmins = (ListView) findViewById(R.id.lvAdmins);
        lvAdmins.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                ChangeAdminsActivity.this,
                android.R.layout.simple_list_item_checked,
                lvData
        );
        lvAdmins.setAdapter(arrayAdapter);
        lvAdmins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Row checked", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Row unchecked", Toast.LENGTH_LONG).show();
                }
            }
        });

        ListView lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);
        List<String> searchResults = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            searchResults.add("Item");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ChangeAdminsActivity.this,
                android.R.layout.simple_list_item_1,
                searchResults
        );
        lvSearchResults.setAdapter(adapter);
    }
}
