package com.example.gymlog.database;

import android.app.Application;
import android.util.Log;

import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.MainActivity;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GymLogRepository {

    private GymLogDAO gymLogDAO;
    private ArrayList<GymLog> allLogs;

    private static GymLogRepository repository;

    private GymLogRepository(Application application) {
        GymLogDatabase db = GymLogDatabase.getDatabase(application);
        this.gymLogDAO = db.gymLogDAO();
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
            return future.get(); //pull informaton out of future object
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.i(MainActivity.TAG, "Problem when getting all GymLogs in the repository");
        }
        return null;
    }

    public void insertGymLog(GymLog gymLog) {
        GymLogDatabase.databaseWriteExecutor.execute(() -> {
            gymLogDAO.insert(gymLog);
        });
    }
}
