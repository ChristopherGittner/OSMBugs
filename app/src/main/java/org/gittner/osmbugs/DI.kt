package org.gittner.osmbugs

import androidx.room.Room
import org.gittner.osmbugs.keepright.KeeprightApi
import org.gittner.osmbugs.mapdust.MapdustApi
import org.gittner.osmbugs.osmnotes.OsmNotesApi
import org.gittner.osmbugs.osmose.OsmoseApi
import org.gittner.osmbugs.statics.Settings
import org.gittner.osmbugs.ui.ErrorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val DiModule = module {
    single { OsmNotesApi() }
    single { KeeprightApi() }
    single { MapdustApi() }
    single { OsmoseApi() }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "DB").build() }

    single { get<AppDatabase>().osmNoteDato() }
    single { get<AppDatabase>().keeprightDao() }
    single { get<AppDatabase>().mapdustDao() }
    single { get<AppDatabase>().osmoseDao() }

    viewModel { ErrorViewModel() }
}