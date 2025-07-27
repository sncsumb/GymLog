package com.example.gymlog.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymlog.Database.entities.GymLog;
import com.example.gymlog.MainActivity;

import java.lang.reflect.Executable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *<br>
 * This represents the GymLog database
 * <br>
 * It represents the actual database that our information is stored to
 * <br>
 *  @author Serena Ngo
 *  @since 07/26/2025
 */

@Database(entities = {GymLog.class}, version = 1, exportSchema = false)
public abstract class GymLogDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "GymLog_database";
    public static final String GYM_LOG_TABLE = "gymLogTable";

    private static volatile GymLogDatabase INSTANCE; //don't want multiple things to access our database at any time
    private static final int NUMBER_OF_THREADS = 4; //don't want to run database queries on the main database thread

    /**
     * Create a service that will suplly threads for us to do database operations on
     * Create all of them at startup, put them in a pool, and pull something out of the pool
     * The database will only have a max of 4 threads
     */
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static GymLogDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //lock into single thread
            //if null, synchronize gymLog class to make sure nothing else is referencing the class
            synchronized (GymLogDatabase.class) {
                //make sure instance is still null (nothing else referenced to it during synchronization)
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    GymLogDatabase.class, DATABASE_NAME
                                    )
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i(MainActivity.TAG,"DATABASE CREATED!");
            //TODO: add databaseWriteExecutor.execute() -> {...} //use to insert default records into database
        }
    };

    public abstract GymLogDAO gymLogDAO();

}
