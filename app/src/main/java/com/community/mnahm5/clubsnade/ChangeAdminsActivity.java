package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChangeAdminsActivity extends AppCompatActivity {

    private String clubId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_admins);

        clubId = getIntent().getStringExtra("clubId");
        if (clubId != null) {
            setTitle(clubId);
        }
    }
}
