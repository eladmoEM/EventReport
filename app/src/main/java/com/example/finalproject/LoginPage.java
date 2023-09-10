package com.example.finalproject;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    private FirebaseAuth firebaseAuth;
    public static final String SHARED_PREF_NAME = "mypref";
    public static final String KEY_NAME = "username";
    public static final String KEY_USER_ID = "user_id";

    private EventDatabaseHelper databaseHelper;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseHelper = new EventDatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        usernameEditText = findViewById(R.id.user_name);
        passwordEditText = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.login_btn);
        registerButton = findViewById(R.id.register_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (validateInput(username, password)) {
                    loginUser(username, password);
                } else {
                    Toast.makeText(LoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Firebase Authentication successful
                            String userId = user.getUid();
                            SharedPrefManager.getInstance(getApplicationContext()).saveLoggedInUserId(userId);
                            saveUserDataToSharedPreferences(email, userId);
                            Toast.makeText(LoginPage.this, "Login successful (Firebase)", Toast.LENGTH_SHORT).show();
                            navigateToHomePage();
                        } else {
                            Toast.makeText(LoginPage.this, "Failed to get user information", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Firebase Authentication failed, try local database
                        if (databaseHelper.authenticateUser(email, password)) {
                            // Local database authentication successful
                            String userId = databaseHelper.getUserId(email);
                            saveUserDataToSharedPreferences(email, userId);
                            Toast.makeText(LoginPage.this, "Login successful (Local)", Toast.LENGTH_SHORT).show();
                            navigateToHomePage();
                        } else {
                            Toast.makeText(LoginPage.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }

        private void saveUserDataToSharedPreferences(String username, String userId) {
            // Save the username and user ID in shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, username);
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        }

    private void navigateToHomePage() {
        String userId = SharedPrefManager.getInstance(getApplicationContext()).getLoggedInUserId();
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }


    private boolean validateInput(String username, String password) {
            return !username.isEmpty() && !password.isEmpty();
        }
    }
