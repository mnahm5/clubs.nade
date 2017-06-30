package com.community.mnahm5.clubsnade;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String USER_ID = null;

    private String userId = null;
    private EditText etFullName;
    private EditText etStudentId;
    private EditText etEmail;
    private Button btEdit;
    private boolean editFlag = false;
    private ParseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String fragmentUserId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, fragmentUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etStudentId = (EditText) view.findViewById(R.id.etStudentId);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        btEdit = (Button) view.findViewById(R.id.btEdit);
        final Button btChangePassword = (Button) view.findViewById(R.id.btChangePassword);

        if (!ParseUser.getCurrentUser().getObjectId().equals(userId)) {
            btEdit.setVisibility(View.INVISIBLE);
            btChangePassword.setVisibility(View.INVISIBLE);
        }

        etFullName.setEnabled(false);
        etStudentId.setEnabled(false);
        etEmail.setEnabled(false);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    user = objects.get(0);
                    getActivity().setTitle(user.getUsername());
                    etFullName.setText(user.get("fullName").toString());
                    etStudentId.setText(user.get("studentId").toString());
                    etEmail.setText(user.getEmail());
                }
                else if (objects.size() == 0) {
                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editFlag) {
                    SaveChanges(view);
                }
                else {
                    EditProfile(view);
                }
            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword(view);
            }
        });

        return view;
    }

    public void EditProfile(View view) {
        etFullName.setEnabled(true);
        etStudentId.setEnabled(true);
        etEmail.setEnabled(true);
        editFlag = true;
        btEdit.setText(R.string.save_changes);
    }

    public void SaveChanges(View view) {
        user.put("fullName", etFullName.getText().toString());
        user.put("studentId", etStudentId.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_LONG).show();
                    etFullName.setEnabled(false);
                    etStudentId.setEnabled(false);
                    etEmail.setEnabled(false);
                    btEdit.setText(R.string.edit_profile);
                }
                else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void ChangePassword(View view) {

    }
}
