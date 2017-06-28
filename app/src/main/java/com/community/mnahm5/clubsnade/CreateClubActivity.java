package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CreateClubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        setTitle("Create Club");
    }

    public void ChooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivity(intent);


        Toast.makeText(getApplicationContext(), "Pick your club logo", Toast.LENGTH_LONG).show();
    }

    public void CreateClub(View view) {
        Toast.makeText(getApplicationContext(), "Club successfully created", Toast.LENGTH_LONG).show();
    }
}
