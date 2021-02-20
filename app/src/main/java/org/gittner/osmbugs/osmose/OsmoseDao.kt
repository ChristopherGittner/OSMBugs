package org.gittner.osmbugs.osmose

import androidx.room.*
import io.reactivex.Observable

@Dao
interface OsmoseDao {
    @Query("SELECT * FROM OsmoseError")
    fun getAll(): Observable<List<OsmoseError>>

    @Insert
    fun insertAll(error: ArrayList<OsmoseError>)

    @Transaction
    suspend fun replaceAll(error: ArrayList<OsmoseError>) {
        clear()
        insertAll(error)
    }

    @Delete
    fun delete(error: OsmoseError)

    @Query("DELETE FROM OsmoseError")
    fun clear()
}