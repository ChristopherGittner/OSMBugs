package org.gittner.osmbugs.keepright

import androidx.room.*
import io.reactivex.Observable

@Dao
interface KeeprightDao {
    @Query("SELECT * FROM KeeprightError")
    fun getAll() : Observable<List<KeeprightError>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(error : KeeprightError)

    @Insert
    suspend fun insertAll(error: ArrayList<KeeprightError>)

    @Delete
    suspend fun delete(error: KeeprightError)

    @Transaction
    suspend fun replaceAll(error: ArrayList<KeeprightError>) {
        clear()
        insertAll(error)
    }

    @Query("DELETE FROM KeeprightError")
    suspend fun clear()
}