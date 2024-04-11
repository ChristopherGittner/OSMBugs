package org.gittner.osmbugs

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.gittner.osmbugs.keepright.KeeprightDao
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmnotes.OsmNoteDao
import org.gittner.osmbugs.osmose.OsmoseDao
import org.gittner.osmbugs.osmose.OsmoseError

@Database(entities = [OsmNote::class, KeeprightError::class, OsmoseError::class], version = 2)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun osmNoteDao(): OsmNoteDao
    abstract fun keeprightDao(): KeeprightDao
    abstract fun osmoseDao(): OsmoseDao
}