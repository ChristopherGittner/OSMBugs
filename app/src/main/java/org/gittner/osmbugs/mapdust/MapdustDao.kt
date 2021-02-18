package org.gittner.osmbugs.mapdust

import androidx.room.*
import io.reactivex.Observable

@Dao
interface MapdustDao {
    @Query("SELECT * FROM MapdustError")
    fun getAll() : Observable<List<MapdustError>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(error : MapdustError)

    @Insert
    suspend fun insertAll(error: ArrayList<MapdustError>)

    @Transaction
    suspend fun replaceAll(error: ArrayList<MapdustError>) {
        clear()
        insertAll(error)
    }

    @Delete
    suspend fun delete(error: MapdustError)

    @Query("DELETE FROM MapdustError")
    suspend fun clear()
}