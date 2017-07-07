package com.community.mnahm5.clubsnade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.UserDictionary;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class AddOrRemoveUserActivity extends AppCompatActivity {

    private ParseObject club = null;
    private ParseObject event = null;
    private String userType = null;

    private List<ParseUser> newUsers = null;
    private List<ParseUser> removedUsers = null;

    private List<String> userList = null;
    private SimpleAdapter lvUserListAdapter = null;
    private List<Map<String, String>> userListData = null;

    private List<ParseUser> dialogUserList = null;
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
        final Button btGo = (Button) v.findViewById(R.id.btGo);

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindUsers(etSearch.getText().toString());
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

        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddUserToMainList(dialogUserList.get(i));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void SaveChanges(View view) {
        Toast.makeText(getApplicationContext(), "Saving Changes Please Wait", Toast.LENGTH_SHORT).show();
        if (club != null) {
            club.put(userType, userList);
            club.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Admins Changed", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void ShowCurrentUserList() {
        if (club != null) {
            List<String> userIds = club.getList(userType);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("objectId", userIds);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        final ListView lvUserList = (ListView) findViewById(R.id.lvUserList);
                        userListData = new ArrayList<Map<String, String>>();
                        userList = new ArrayList<String>();

                        for (ParseUser user: objects) {
                            Map<String, String> userData = new HashMap<String, String>();
                            userData.put("username", user.getUsername());
                            userData.put("fullName", user.get("fullName").toString());
                            userListData.add(userData);
                            userList.add(user.getObjectId());
                        }

                        lvUserListAdapter = new SimpleAdapter(
                                AddOrRemoveUserActivity.this,
                                userListData,
                                android.R.layout.simple_list_item_2,
                                new String[] {"username", "fullName"},
                                new int[] {android.R.id.text1, android.R.id.text2}
                        );
                        lvUserList.setAdapter(lvUserListAdapter);

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
            ParseQuery<ParseUser> query;
            if (searchData != null) {
                List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();

                ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                query1.whereContains("username", searchData);
                queries.add(query1);

                ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                query2.whereContains("fullName", searchData);
                queries.add(query2);

                ParseQuery<ParseUser> query3 = ParseUser.getQuery();
                query3.whereContains("fullName", searchData.toLowerCase());
                queries.add(query3);

                ParseQuery<ParseUser> query4 = ParseUser.getQuery();
                query4.whereContains("fullName", searchData.toUpperCase());
                queries.add(query4);

                ParseQuery<ParseUser> query5 = ParseUser.getQuery();
                query5.whereContains("fullName", CapitalizeWords(searchData));
                queries.add(query5);

                query = ParseQuery.or(queries);
            }
            else {
                query = ParseUser.getQuery();
            }
            query.whereNotContainedIn("objectId", userList);
            query.setLimit(15);

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        dialogUserList = objects;
                        final List<Map<String, String>> userListData = new ArrayList<Map<String, String>>();

                        for (ParseUser user: dialogUserList) {
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

    private String CapitalizeWords(String word) {
        String[] strArray = word.split(" ");
        String result = "";
        for (int i = 0; i < strArray.length; i++) {
            String cap = strArray[i].substring(0, 1).toUpperCase() + strArray[i].substring(1);
            result += cap;
            if (i != (strArray.length - 1)) {
                result += " ";
            }
        }
        return result;
    }

    private void AddUserToMainList(ParseUser user) {
        if (newUsers == null) {
            newUsers = new ArrayList<ParseUser>();
        }
        newUsers.add(user);
        userList.add(user.getObjectId());
        Map<String, String> userData = new HashMap<String, String>();
        userData.put("username", user.getUsername());
        userData.put("fullName", user.get("fullName").toString());
        userListData.add(userData);
        lvUserListAdapter.notifyDataSetChanged();
    }
}
