package com.community.mnahm5.clubsnade;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.text.format.DateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private ParseObject club;
    private ParseObject event;
    private Bitmap eventLogoBitmap = null;
    private Spinner spEventAccess;

    private Calendar startDate = null;
    private Calendar endDate = null;
    private boolean startTimePicking = false;
    private boolean endTimePicking = false;

    private EditText etEventName;
    private EditText etEventDetails;
    private EditText etLocation;
    private EditText etFees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        spEventAccess = (Spinner) findViewById(R.id.spEventAccess);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.access_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spEventAccess.setAdapter(adapter);

        setup();
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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (startTimePicking) {
            int hour, minute;
            if (startDate == null) {
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }
            else {
                hour = startDate.get(Calendar.HOUR_OF_DAY);
                minute = startDate.get(Calendar.MINUTE);
            }

            startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR, i);
            startDate.set(Calendar.MONTH, i1);
            startDate.set(Calendar.DAY_OF_MONTH, i2);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    CreateEventActivity.this,
                    CreateEventActivity.this,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(this)
            );
            timePickerDialog.show();
        }
        else if (endTimePicking) {
            int hour, minute;
            if (endDate == null && startDate == null) {
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }
            else if (endDate == null) {
                hour = startDate.get(Calendar.HOUR_OF_DAY);
                minute = startDate.get(Calendar.MINUTE);
            }
            else {
                hour = endDate.get(Calendar.HOUR_OF_DAY);
                minute = endDate.get(Calendar.MINUTE);
            }

            endDate = Calendar.getInstance();
            endDate.set(Calendar.YEAR, i);
            endDate.set(Calendar.MONTH, i1);
            endDate.set(Calendar.DAY_OF_MONTH, i2);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    CreateEventActivity.this,
                    CreateEventActivity.this,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(this)
            );
            timePickerDialog.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (startTimePicking) {
            startDate.set(Calendar.HOUR_OF_DAY, i);
            startDate.set(Calendar.MINUTE, i1);

            final Button btStartPickDate = (Button) findViewById(R.id.btStartPickDate);
            ShowDateOnButton(startDate, btStartPickDate, "Start");
            startTimePicking = false;
        }
        else if (endTimePicking) {
            endDate.set(Calendar.HOUR_OF_DAY, i);
            endDate.set(Calendar.MINUTE, i1);

            final Button btEndPickDate = (Button) findViewById(R.id.btEndPickDate);
            ShowDateOnButton(endDate, btEndPickDate, "End");
            endTimePicking = false;
        }
    }

    private void setup() {
        etEventName = (EditText) findViewById(R.id.etEventName);
        etEventDetails = (EditText) findViewById(R.id.etEventDetails);
        etLocation = (EditText) findViewById(R.id.etEventLocation);
        etFees = (EditText) findViewById(R.id.etEventFees);
        final String eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
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
        else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            query.whereEqualTo("objectId", eventId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        event = objects.get(0);
                        setTitle(event.get("name").toString() + " - Edit Details");
                        etEventName.setText(event.get("name").toString());
                        etEventDetails.setText(event.get("details").toString());
                        etLocation.setText(event.get("location").toString());
                        etFees.setText(event.get("fees").toString());

                        startDate = Calendar.getInstance();
                        startDate.setTime(event.getDate("startDate"));
                        final Button btStartPickDate = (Button) findViewById(R.id.btStartPickDate);
                        ShowDateOnButton(startDate, btStartPickDate, "Start");

                        endDate = Calendar.getInstance();
                        endDate.setTime(event.getDate("endDate"));
                        final Button btEndPickDate = (Button) findViewById(R.id.btEndPickDate);
                        ShowDateOnButton(endDate, btEndPickDate, "End");

                        final ImageView ivEventLogo = (ImageView) findViewById(R.id.ivEventLogo);
                        ParseFile file = (ParseFile) event.get("logo");
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        eventLogoBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ivEventLogo.setImageBitmap(eventLogoBitmap);
                                        ivEventLogo.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }

                        List<String> accessOptions = Arrays.asList((getResources().getStringArray(R.array.access_array)));
                        spEventAccess.setSelection(accessOptions.indexOf(event.get("access").toString()));

                        Button btCreateEvent = (Button) findViewById(R.id.btCreateEvent);
                        btCreateEvent.setText(R.string.edit_event);
                    }
                    else if (e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "No Event Found", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
        if (!spEventAccess.getSelectedItem().equals("None Selected") && startDate != null && endDate != null) {

            if (event == null) {
                event = new ParseObject("Event");
                event.put("clubId", club.getObjectId());
            }

            event.put("name", etEventName.getText().toString());
            event.put("details", etEventDetails.getText().toString());
            event.put("location", etLocation.getText().toString());
            event.put("fees", etFees.getText().toString());
            event.put("access", spEventAccess.getSelectedItem());


            if (eventLogoBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                eventLogoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("logo.png", byteArray);
                event.put("logo", file);
            }

            event.put("startDate", startDate.getTime());
            event.put("endDate", endDate.getTime());

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
        else if (startDate == null || endDate == null) {
            Toast.makeText(getApplicationContext(), "Start and End times need to picked", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Need select access", Toast.LENGTH_LONG).show();
        }
    }

    private void getPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void PickEndTime(View view) {
        int year, month, day;
        if (endDate == null && startDate == null) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        else if (endDate == null) {
            year = startDate.get(Calendar.YEAR);
            month = startDate.get(Calendar.MONTH);
            day = startDate.get(Calendar.DAY_OF_MONTH);
        }
        else {
            year = endDate.get(Calendar.YEAR);
            month = endDate.get(Calendar.MONTH);
            day = endDate.get(Calendar.DAY_OF_MONTH);
        }

        endTimePicking = true;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEventActivity.this,
                CreateEventActivity.this,
                year,
                month,
                day);
        datePickerDialog.show();
    }

    public void PickStartTime(View view) {
        int year, month, day;
        if (startDate == null) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            year = startDate.get(Calendar.YEAR);
            month = startDate.get(Calendar.MONTH);
            day = startDate.get(Calendar.DAY_OF_MONTH);
        }

        startTimePicking = true;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEventActivity.this,
                CreateEventActivity.this,
                year,
                month,
                day);
        datePickerDialog.show();
    }

    private void ShowDateOnButton(Calendar date, Button button, String type) {
        String btText = String.format(Locale.ENGLISH, "%s - %02d-%02d-%02d %02d:%02d",
                type,
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.YEAR),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE)
        );
        button.setText(btText);
    }
}
