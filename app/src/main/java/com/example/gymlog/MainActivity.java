package com.example.gymlog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.database.GymLogRepository;
import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.database.entities.User;
import com.example.gymlog.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER_ID = "MAIN_ACTIVITY_USER_ID ";
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

        loginUser();
        invalidateOptionsMenu(); //make sure user is logged in

        if(loggedInUserId == -1){
            Intent intent = LoginActivity.loginIntentFactory((getApplicationContext()));
            startActivity(intent);
        }

        //get instance of repository, access to database
        repository = GymLogRepository.getRepository(getApplication());

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

    private void loginUser() {
        //TODO: make login method functional
        user = new User("potato", "password");
        loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //turn xml file into something workable
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    /**
     * Reference to menu items
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        //get username from user parameter
        item.setTitle(user.getUsername());
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
        //TODO: finish logout method
        startActivity(LoginActivity.loginIntentFactory((getApplicationContext())));
    }

    //take login information
    //package private - can be package private because all of our activities are in the same package
    //static because we never instantiate main activity (android does that for us)
    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class); //use intent to start main activity
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
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
        ArrayList<GymLog> allLogs = repository.getAllLogs();
        if(allLogs.isEmpty()) {
            binding.logDisplayTextView.setText(R.string.nothing_to_shower_time_to_hit_the_gym);
        }
        StringBuilder sb = new StringBuilder();
        for(GymLog log : allLogs) {
            sb.append(log);
            System.out.println(log);
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
