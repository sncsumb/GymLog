package com.example.gymlog.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gymlog.Database.entities.GymLog;

import java.util.ArrayList;
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

    @Query("Select * from " + GymLogDatabase.GYM_LOG_TABLE) //static reference to the name of the table that is defined in the database
    ArrayList<GymLog> getAllRecords();
}
