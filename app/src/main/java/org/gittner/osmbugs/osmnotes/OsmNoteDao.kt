package org.gittner.osmbugs.osmnotes

import androidx.room.*
import io.reactivex.Observable

@Dao
interface OsmNoteDao {
    @Query("SELECT * FROM OsmNote")
    fun getAll() : Observable<List<OsmNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(error : OsmNote)

    @Insert
    suspend fun insertAll(error: ArrayList<OsmNote>)

    @Transaction
    suspend fun replaceAll(error: ArrayList<OsmNote>) {
        clear()
        insertAll(error)
    }

    @Delete
    suspend fun delete(error: OsmNote)

    @Query("DELETE FROM OsmNote")
    suspend fun clear()
}