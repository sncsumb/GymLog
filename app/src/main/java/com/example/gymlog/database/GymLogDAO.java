package com.example.gymlog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gymlog.database.entities.GymLog;

import java.util.List;

/**
 *
 *<br>
 * This is the data access object
 * <br>
 * It represents the queries that we will run in the database
 * <br>
 *  @author Serena Ngo
 *  @since 07/26/2025
 */

@Dao
public interface GymLogDAO {
    //we need queries, actions we can perform on our database

    //add records to our database
    @Insert(onConflict = OnConflictStrategy.REPLACE) //if we are inserting a record and insert another one with the same ID,
    // it will replace the existing thing in the database with the new one
    void insert(GymLog gymLog);

    @Query("SELECT * FROM " + GymLogDatabase.GYM_LOG_TABLE + " ORDER BY date DESC") //static reference to the name of the table that is defined in the database
    List<GymLog> getAllRecords();

//    @Query("SELECT * FROM " + GymLogDatabase.GYM_LOG_TABLE + " WHERE userId = :userId ORDER BY date DESC")
//    LiveData<List<GymLog>> getAllLogsByUserId(int userId);

    @Query("SELECT * FROM " + GymLogDatabase.GYM_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    List<GymLog> getRecordsByUserId(int loggedInUserId);
}
