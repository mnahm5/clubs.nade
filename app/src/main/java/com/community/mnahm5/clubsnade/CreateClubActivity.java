package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateClubActivity extends AppCompatActivity {

    private Bitmap logoBitmap = null;
    private EditText etClubName;
    private EditText etClubDetails;
    private EditText etFees;
    private EditText etEmail;
    private ImageView ivLogo;
    private ParseObject club = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        setUp();
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
        Toast.makeText(getApplicationContext(), "Saving Data\nPlease Wait..", Toast.LENGTH_LONG).show();

        final String successMsg;
        if (club == null) {
            club = new ParseObject("Club");
            successMsg = "Club Created";
        }
        else {
            successMsg = "Create Updated";
        }

        club.put("name", etClubName.getText().toString());
        club.put("details", etClubDetails.getText().toString());
        club.put("fees", etFees.getText().toString());
        club.put("email", etEmail.getText().toString());

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
                    Toast.makeText(getApplicationContext(), successMsg, Toast.LENGTH_LONG).show();
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

    private void setUp() {
        etClubName = (EditText) findViewById(R.id.etClubName);
        etClubDetails = (EditText) findViewById(R.id.etClubDetails);
        etFees = (EditText) findViewById(R.id.etFees);
        etEmail = (EditText) findViewById(R.id.etEmail);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        final Button btCreateClub = (Button) findViewById(R.id.btCreateClub);

        final String clubId = getIntent().getStringExtra("clubId");
        if (clubId == null) {
            setTitle("Create Club");
        }
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
            query.whereEqualTo("objectId", clubId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        club = objects.get(0);
                        setTitle(club.get("name").toString());
                        etClubName.setText(club.get("name").toString());
                        etClubDetails.setText(club.get("details").toString());
                        etFees.setText(club.get("fees").toString());
                        etEmail.setText(club.get("email").toString());
                        btCreateClub.setText(R.string.update_club);
                        showClubLogo();
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No club found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void showClubLogo() {
        ParseFile file = (ParseFile) club.get("logo");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        logoBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ivLogo.setImageBitmap(logoBitmap);
                        ivLogo.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

}
