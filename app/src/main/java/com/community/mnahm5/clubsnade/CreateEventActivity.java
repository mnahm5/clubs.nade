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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    private ParseObject club;
    private Bitmap eventLogoBitmap = null;
    private Spinner spEventAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setup();

        spEventAccess = (Spinner) findViewById(R.id.spEventAccess);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.access_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spEventAccess.setAdapter(adapter);
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
                eventLogoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ImageView ivEventLogo = (ImageView) findViewById(R.id.ivEventLogo);
                ivEventLogo.setImageBitmap(eventLogoBitmap);
                ivEventLogo.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void setup() {
        final String clubId = getIntent().getStringExtra("clubId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
        query.whereEqualTo("objectId", clubId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    club = objects.get(0);
                    setTitle(club.getString("name") + " - Create Event");
                }
                else if (e != null) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error\nNo Club Found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void ChooseEventLogo(View view) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            getPhotos();
        }
    }

    public void CreateEvent(View view) {
        if (!spEventAccess.getSelectedItem().equals("None Selected")) {
            final EditText etEventName = (EditText) findViewById(R.id.etEventName);
            final EditText etEventDetails = (EditText) findViewById(R.id.etEventDetails);
            final EditText etLocation = (EditText) findViewById(R.id.etEventLocation);
            final EditText etFees = (EditText) findViewById(R.id.etEventFees);

            ParseObject event = new ParseObject("Event");
            event.put("name", etEventName.getText().toString());
            event.put("details", etEventDetails.getText().toString());
            event.put("location", etLocation.getText().toString());
            event.put("fees", etFees.getText().toString());
            event.put("access", spEventAccess.getSelectedItem());
            event.put("clubId", club.getObjectId());

            if (eventLogoBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                eventLogoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("logo.png", byteArray);
                event.put("logo", file);
            }

            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Event Saved", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Need select access", Toast.LENGTH_LONG).show();
        }
    }

    private void getPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
}
