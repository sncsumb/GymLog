package com.example.gymlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.gymlog.database.GymLogRepository;
import com.example.gymlog.database.entities.User;
import com.example.gymlog.databinding.ActivityLoginBinding;

/**
 *
 *<br>
 * This is the LoginActivity class
 * <br>
 * This class gives functionality for users to log in
 * <br>
 *  @author Serena Ngo
 *  @since 07/27/2025
 */

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GymLogRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymLogRepository.getRepository(getApplication());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });
    }

    /**
     * verifyUser observes LiveData object
     * Store userID and sharedPreference
     */
    private void verifyUser() {

        String username = binding.userNameLoginEditText.getText().toString();


        if (username.isEmpty()) {
            toastMaker("username should not be blank");
            return;
        }
        LiveData<User> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, user -> {
            if (user != null) { //grab user password if user exists (not null)
                String password = binding.passwordLoginEditText.getText().toString(); //grab password
                if (password.equals(user.getPassword())) { //if password match, start activity
                    //reset the intent
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));

                } else {
                    toastMaker("Invalid password!");
                    binding.passwordLoginEditText.setSelection(0);
                }
            } else {
                toastMaker(String.format("%s is not a valid username.", username));
                binding.userNameLoginEditText.setSelection(0);
            }
        });
    }

    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}