package com.example.gymlog.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.MainActivity;
import com.example.gymlog.database.entities.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 *<br>
 * This is the GymLog repository
 * <br>
 * This class allows use to use DAO to access our database
 * <br>
 *  @author Serena Ngo
 *  @since 07/25/2025
 */
public class GymLogRepository {

    private final GymLogDAO gymLogDAO;
    private final UserDAO userDAO;
    private ArrayList<GymLog> allLogs;

    private static GymLogRepository repository;

    private GymLogRepository(Application application) {
        GymLogDatabase db = GymLogDatabase.getDatabase(application);
        this.gymLogDAO = db.gymLogDAO();
        this.userDAO = db.userDAO();
        this.allLogs = (ArrayList<GymLog>) this.gymLogDAO.getAllRecords();
    }

    /**
     * The getRepository() is a singleton
     * It allows us to only ever have one instance of the GymLog repository
     * and can only be called in this class since GymLogRepository is private
     * @param application
     */
    public static GymLogRepository getRepository(Application application) {
        if(repository != null) {
            return repository;
        }
        Future<GymLogRepository> future = GymLogDatabase.databaseWriteExecutor.submit(
                new Callable<GymLogRepository>() {
                    @Override
                    public GymLogRepository call() throws Exception {
                        return new GymLogRepository(application);
                    }
                }
        );
        try{
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(MainActivity.TAG, "Problem getting GymLogRepository, thread error.");
        }
        return null;
    }

    /**
     * Method that allows us to use the getAllRecords method from gymLogDAO to use on a thread
     * @return
     */
    public ArrayList<GymLog> getAllLogs() {
        //Future gets a reference, something that will be fulfilled in the future and let a thread do its operation
        //when it comes back we can process it
        //get a value sometime in the future
        //offload processing to a thread and wait for that thread to return
        //future object is accessing gymlog database
        //submit task to ExecutorService
        Future<ArrayList<GymLog>> future = GymLogDatabase.databaseWriteExecutor.submit(
                //submit instance of the class that implements Callable (interface)
                new Callable<ArrayList<GymLog>>() {
                    @Override
                    public ArrayList<GymLog> call() throws Exception {
                        return (ArrayList<GymLog>) gymLogDAO.getAllRecords();
                    }
                }
        );
        try {
            return future.get(); //pull information out of future object
        } catch (InterruptedException | ExecutionException e) {
            Log.i(MainActivity.TAG, "Problem when getting all GymLogs in the repository");
        }
        return null;
    }

    //may need to have wait feature for future
    public void insertGymLog(GymLog gymLog) {
        GymLogDatabase.databaseWriteExecutor.execute(() -> {
            gymLogDAO.insert(gymLog);
        });
    }
    public void insertUser(User...user) {
        GymLogDatabase.databaseWriteExecutor.execute(() -> {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username); //return LiveData
    }

    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId); //return LiveData
    }

    public LiveData<List<GymLog>> getAllLogsByUserIdLiveData(int loggedInUserId) {
        return gymLogDAO.getRecordsByUserIdLiveData(loggedInUserId);
    }

    /**
     * getAllLogsByUserId gets Future as a reference for something that will be fulfilled later
     * This let's us offload processing to a thread and wait for the thread to return
     * The future object will access the GymLog database and submit the task to ExecutorService
     * @param loggedInUserId
     * @return
     */
    @Deprecated
    public ArrayList<GymLog> getAllLogsByUserId(int loggedInUserId) {

        Future<ArrayList<GymLog>> future = GymLogDatabase.databaseWriteExecutor.submit(
                //submit instance of the class that implements Callable (interface)
                new Callable<ArrayList<GymLog>>() {
                    @Override
                    public ArrayList<GymLog> call() throws Exception {
                        return (ArrayList<GymLog>) gymLogDAO.getRecordsByUserId(loggedInUserId);
                    }
                }
        );
        try {
            return future.get(); //pull information out of future object
        } catch (InterruptedException | ExecutionException e) {
            Log.i(MainActivity.TAG, "Problem when getting all GymLogs in the repository");
        }
        return null;
    }
}
