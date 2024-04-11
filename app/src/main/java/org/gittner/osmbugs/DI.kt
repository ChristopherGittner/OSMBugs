package org.gittner.osmbugs

import androidx.room.Room
import androidx.room.migration.Migration
import org.gittner.osmbugs.keepright.KeeprightApi
import org.gittner.osmbugs.osmnotes.OsmNotesApi
import org.gittner.osmbugs.osmose.OsmoseApi
import org.gittner.osmbugs.ui.ErrorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val DiModule = module {
    single { OsmNotesApi() }
    single { KeeprightApi() }
    single { OsmoseApi() }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "DB").addMigrations(
        Migration(1, 2) {}
    ).build() }

    single { get<AppDatabase>().osmNoteDao() }
    single { get<AppDatabase>().keeprightDao() }
    single { get<AppDatabase>().osmoseDao() }

    viewModel { ErrorViewModel() }
}