package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.FirebaseApp;

public class RegisterPage extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        FirebaseApp.initializeApp(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Check if the email, username, and password fields are not empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegisterPage.this, "בבקשה מלא את כל הפרטים", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterPage.this, "סיסמאות לא תואמות", Toast.LENGTH_SHORT).show();
            return;
        }

        // user account with Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterPage.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        // Set the display name (username) for the user
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(profileUpdates);

                        Toast.makeText(RegisterPage.this, "ההרשמה הצליחה!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Registration failed
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            // User with this email already exists
                            Toast.makeText(RegisterPage.this, "משתמש כבר קיים במערכת", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthException) {
                            // Other Firebase Authentication errors
                            Toast.makeText(RegisterPage.this, "ההרשמה נכשלה " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Other errors
                            Toast.makeText(RegisterPage.this, "ההרשמה נכשלה", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
