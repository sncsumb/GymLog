package com.example.gymlog.database.typeConverters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;

/**
 *
 *<br>
 * This is the Date Type Converter
 * <br>
 * It will convert the type of Date into a useable type
 * We need this because Room only supports 3 types, and Date is not one of them
 * <br>
 *  @author Serena Ngo
 *  @since 07/26/2025
 */

public class LocalDateTypeConverter {
    @TypeConverter
    public long convertDateToLong(LocalDateTime date) {
        ZonedDateTime zdt = ZonedDateTime.of(date, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    @TypeConverter
    public LocalDateTime convertLongToDate(Long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant,ZoneId.systemDefault());
    }
}
