package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registration");
        redirect();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void redirect() {
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    public void Register(View view) {
        final EditText etFullName = (EditText) findViewById(R.id.etFullName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);

        if (etPassword.getText().toString().equals(etPassword2.getText().toString())) {
            ParseUser user = new ParseUser();
            user.setUsername(etUsername.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setEmail(etEmail.getText().toString());
            user.put("fullName", etFullName.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "User Account Created", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
        }
    }

    public void LoginLink(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
