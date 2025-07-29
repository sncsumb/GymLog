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
            if (user != null) {
                String password = binding.passwordLoginEditText.getText().toString();
                if (password.equals(user.getPassword())) {
                    //get reference to sharedPreferences object (preferences that are shared system wide)
                    SharedPreferences sharedPreferences = getApplicationContext()
                            .getSharedPreferences(MainActivity.SHARED_PREFERENCE_USERID_KEY,//used shared references key to get list of all preferences associated with application
                            Context.MODE_PRIVATE); //and do not share this information outside of this application (mode_private)
                    //Editor - inner class to shared preferences to edit the settings in our shared preference (preserves encapsulation)
                    SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
                    sharedPrefEditor.putInt(MainActivity.SHARED_PREFERENCE_USERID_KEY, user.getId()); //store userID with key
                    sharedPrefEditor.apply(); //save changes
                    //reset the intent
                    System.out.println("before loginActivity " + user.getId());
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));
                    System.out.println("after loginActivity " + user.getId());

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