package com.community.mnahm5.clubsnade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import static java.security.AccessController.getContext;

public class AddOrRemoveUserActivity extends AppCompatActivity {

    private ParseObject club = null;
    private ParseObject event = null;
    private String userType = null;

    private List<ParseUser> userList = null;

    private ListView lvSearchResults = null;

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
        final View v = getLayoutInflater().inflate(R.layout.dialog_add_users, null);
        lvSearchResults = (ListView) v.findViewById(R.id.lvSearchResults);

        final EditText etSearch= (EditText) v.findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FindUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FindUsers();

        builder.setView(v);
        final AlertDialog dialog = builder.create();

        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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

    private void FindUsers(String searchData) {
        if (club != null) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereNotContainedIn("objectId", club.getList(userType));

            if (searchData != null) {

            }

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        userList = objects;
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
                        lvSearchResults.setAdapter(simpleAdapter);

                        if (userListData.size() > 5) {
                            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lvSearchResults.getLayoutParams();
                            float dps = 350;
                            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                            lp.height = (int) (dps * scale + 0.5f);
                            lvSearchResults.setLayoutParams(lp);
                        }
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No users found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void FindUsers() {
        FindUsers(null);
    }
}
