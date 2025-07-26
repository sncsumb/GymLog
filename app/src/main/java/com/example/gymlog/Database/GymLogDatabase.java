package com.example.gymlog.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.gymlog.Database.entities.GymLog;

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

    public static final String gymLogTable = "gymLogTable";
}
