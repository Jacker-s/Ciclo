package com.ciclo21.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DailyRecord>>

    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    suspend fun getAllRecordsAsList(): List<DailyRecord>

    @Query("SELECT * FROM daily_records WHERE date = :date")
    fun getRecordByDate(date: LocalDate): Flow<DailyRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DailyRecord)

    @Query("DELETE FROM daily_records WHERE date = :date")
    suspend fun deleteRecord(date: LocalDate)

    @Query("DELETE FROM daily_records")
    suspend fun clearAll()
}
