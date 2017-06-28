package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateClubActivity extends AppCompatActivity {

    private Bitmap logoBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        setTitle("Create Club");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhotos();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                logoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
                ivLogo.setImageBitmap(logoBitmap);
                ivLogo.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void ChooseImage(View view) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            getPhotos();
        }
    }

    public void CreateClub(View view) {
        final EditText etClubName = (EditText) findViewById(R.id.etClubName);
        final EditText etClubDetails = (EditText) findViewById(R.id.etClubDetails);
        final EditText etFees = (EditText) findViewById(R.id.etFees);
        final ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);

        ParseObject club = new ParseObject("Club");
        club.put("name", etClubName.getText().toString());
        club.put("details", etClubDetails.getText().toString());
        club.put("fees", etFees.getText().toString());

        if (logoBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ParseFile file = new ParseFile("logo.png", byteArray);
            club.put("logo", file);
        }

        ArrayList<String> admins = new ArrayList<>();
        admins.add(ParseUser.getCurrentUser().getObjectId());
        club.put("admins", admins);

        club.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Club Created", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
}
