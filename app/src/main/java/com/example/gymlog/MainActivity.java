package com.example.gymlog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.gymlog.database.GymLogRepository;
import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.database.entities.User;
import com.example.gymlog.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER_ID = "MAIN_ACTIVITY_USER_ID";
    static final String SHARED_PREFERENCE_USERID_KEY = "SHARED_PREFERENCE_USERID_KEY";
    static final String SHARED_PREFERENCE_USERID_VALUE = "SHARED_PREFERENCE_USERID_VALUE";
    private static final String SAVED_INSTANCE_STATE_USERID_KEY = "SAVED_INSTANCE_STATE_USERID_KEY";
    private static final int LOGGED_OUT = -1;
    ActivityMainBinding binding;
    private GymLogRepository repository;
    public static final String TAG = "SN_GYMLOG";
    String mExercise = "";
    double mWeight = 0.0;
    int mReps = 0;

    //TODO: add login information
    int loggedInUserId = -1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get instance of repository, access to database, retrieve information from the db
        repository = GymLogRepository.getRepository(getApplication());
        loginUser(savedInstanceState); //user needs to log in

        if(loggedInUserId == -1){
            Intent intent = LoginActivity.loginIntentFactory((getApplicationContext()));
            startActivity(intent);
        }

        //scrollable content
        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());
        updateDisplay(); //update display with new information after reopening app
        //adds data into database and displays on application
        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay(); //get new information
                insertGymlogRecord();
                updateDisplay();

            }
        });

        //display data on application in realtime
        binding.exerciseInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDisplay();
            }
        });

    }

    /**
     * Login functionality; allows users to log into their GymLog account
     *
     *
     */
    private void loginUser(Bundle savedInstanceState) {
        //check for shared preference for logged in user
        //shared preference is a key value pair that is the mid level of persistence
        //somewhere in between intent and database call
        //system level variable
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_USERID_KEY,
                Context.MODE_PRIVATE); //get shared preferences associated with the application only in this device
        if (sharedPreferences.contains(SHARED_PREFERENCE_USERID_VALUE)) { //if UserID is in sharedPreferences
            //get sharedPreference for userID, used LOGGED_OUT value if it does not exist
            loggedInUserId = sharedPreferences.getInt(SHARED_PREFERENCE_USERID_VALUE, LOGGED_OUT);
            System.out.println("in loginUser 1" + user);

            //if the user is logged out, if they are a shared preference, get from database
        } //allows users to still stay logged in even after closing the application
//        check intent for logged in user
        if (loggedInUserId == LOGGED_OUT && savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)) {
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
            System.out.println("in loginUser 2" + user);
        } //if
        if (loggedInUserId == LOGGED_OUT) { //if user is still logged out, pull it from intent
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID,LOGGED_OUT);
            System.out.println("in loginUser 3" + user);
        }
        if (loggedInUserId == LOGGED_OUT) {
            System.out.println("in loginUser 4" + user);
            return;
        }

        System.out.println("in loginUser 5" + user);

        LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
        System.out.println("in loginUser 6" + user);

        userObserver.observe(this, user -> {
            System.out.println("in loginUser 7" + user);

            this.user = user;
            if(this.user != null) {
                invalidateOptionsMenu();
            } else {
                logout();
            }
        });

//        loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, -1);

//        if(loggedInUserId == LOGGED_OUT) {
//            return;
//        } else {
//            LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
//            userObserver.observe(this, user -> {
//                if (user != null) {
//                    this.user = user;
//                    invalidateOptionsMenu(); //make sure user is logged in
//                }
//            });
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_USERID_KEY, loggedInUserId);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_USERID_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(MainActivity.SHARED_PREFERENCE_USERID_KEY, loggedInUserId);
        sharedPrefEditor.apply();
    }

    /**
     * onCreateOptionsMenu adds a logout menu to allow users to logout of their account
     * Users can also cancel out of it
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //turn xml file into something workable
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    /**
     * Reference to menu items for logout
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem); //pull from logout_menu xml
        item.setVisible(true);
        if(user == null) {
            return false;
        }

        //get username from user parameter and display it
        item.setTitle(user.getUsername()); //logout menu title - this is currently null

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                Toast.makeText(MainActivity.this, "LOGOUT TO BE IMPLEMENTED", Toast.LENGTH_SHORT).show();
                showLogoutDialog();
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When user logs out, invalidate all their information and takes them to the login screen
     */
    private void showLogoutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this); //get context of menu and pass it
        //instantiate memory for alert dialog (these are rendered using a singleton)
        //ensure that there is only one alert dialogue at a time. If there is more than one, do not render on top of other
        final AlertDialog alertDialog = alertBuilder.create();

        alertBuilder.setMessage("Logout?");

        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                alertDialog.dismiss();
            }
        });

        //show alerts
        alertBuilder.create().show();
    }

    private void logout() {
        //set sharedPreference value to -1
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_USERID_KEY,Context.MODE_PRIVATE);
        //get editor
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(SHARED_PREFERENCE_USERID_KEY, LOGGED_OUT);
        sharedPrefEditor.apply();
        //reset the intent
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT)
;        //go to the login activity
        startActivity(LoginActivity.loginIntentFactory((getApplicationContext())));
    }

    //take login information
    //package private - can be package private because all of our activities are in the same package
    //static because we never instantiate main activity (android does that for us)
    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class); //use intent to start main activity
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId); //sets key MAIN_ACTIVITY_USER_ID to value userID
        return intent;
    }

    private void insertGymlogRecord() {
        if(mExercise.isEmpty()) {
            return;
        }

        GymLog log = new GymLog(mExercise,mWeight,mReps,loggedInUserId);
        repository.insertGymLog(log);
    }

    //Update information for entered data
    private void updateDisplay() {
        ArrayList<GymLog> allLogs = repository.getAllLogsByUserId(loggedInUserId);
//        allLogs.observe(this, List<GymLog> -> {
//
//        });
        if(allLogs.isEmpty()) {
            binding.logDisplayTextView.setText(R.string.nothing_to_show_time_to_hit_the_gym);
        }
        StringBuilder sb = new StringBuilder();
        for(GymLog log : allLogs) {
            sb.append(log);
        }
        binding.logDisplayTextView.setText(sb.toString());
    }

    private void getInformationFromDisplay() {
        mExercise = binding.exerciseInputEditText.getText().toString();
        try {
            mWeight = Double.parseDouble(binding.weightInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("TAG", "Error reading value from Weight edit text");
        }

        try {
            mReps = Integer.parseInt(binding.repInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("TAG", "Error reading value from Weight edit text");
        }
    }
}
