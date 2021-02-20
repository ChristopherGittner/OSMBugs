package org.gittner.osmbugs.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.keepright.KeeprightApi
import org.gittner.osmbugs.keepright.KeeprightDao
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.mapdust.MapdustApi
import org.gittner.osmbugs.mapdust.MapdustDao
import org.gittner.osmbugs.mapdust.MapdustError
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmnotes.OsmNoteDao
import org.gittner.osmbugs.osmnotes.OsmNotesApi
import org.gittner.osmbugs.osmose.OsmoseApi
import org.gittner.osmbugs.osmose.OsmoseDao
import org.gittner.osmbugs.osmose.OsmoseError
import org.gittner.osmbugs.statics.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.BoundingBox

@SuppressLint("CheckResult")
class ErrorViewModel : ViewModel(), KoinComponent {
    private val mSettings = Settings.getInstance()

    private val mError = MutableLiveData<String>()

    private val mOsmNotesApi: OsmNotesApi by inject()
    private val mOsmNoteDao: OsmNoteDao by inject()
    private val mOsmNotes = MutableLiveData<ArrayList<OsmNote>>(ArrayList())
    private val mOsmNotesEnabled = MutableLiveData(mSettings.OsmNotes.Enabled)

    private val mKeeprightApi: KeeprightApi by inject()
    private val mKeeprightDao: KeeprightDao by inject()
    private val mKeeprightErrors = MutableLiveData<ArrayList<KeeprightError>>(ArrayList())
    private val mKeeprightEnabled = MutableLiveData(mSettings.Keepright.Enabled)

    private val mMapdustApi: MapdustApi by inject()
    private val mMapdustDao: MapdustDao by inject()
    private val mMapdustErrors = MutableLiveData<ArrayList<MapdustError>>(ArrayList())
    private val mMapdustEnabled = MutableLiveData(mSettings.Mapdust.Enabled)

    private val mOsmoseApi: OsmoseApi by inject()
    private val mOsmoseDao: OsmoseDao by inject()
    private val mOsmoseErrors = MutableLiveData<ArrayList<OsmoseError>>(ArrayList())
    private val mOsmoseEnabled = MutableLiveData(mSettings.Osmose.Enabled)

    private val mContentLoading = MutableLiveData(false)

    init {
        mOsmNoteDao.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mOsmNotes.value!!.clear()
                mOsmNotes.value!!.addAll(it)
                mOsmNotes.value = mOsmNotes.value
            }

        mKeeprightDao.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mKeeprightErrors.value!!.clear()
                mKeeprightErrors.value!!.addAll(it)
                mKeeprightErrors.value = mKeeprightErrors.value
            }

        mMapdustDao.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mMapdustErrors.value!!.clear()
                mMapdustErrors.value!!.addAll(it)
                mMapdustErrors.value = mMapdustErrors.value
            }

        mOsmoseDao.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mOsmoseErrors.value!!.clear()
                mOsmoseErrors.value!!.addAll(it)
                mOsmoseErrors.value = mOsmoseErrors.value
            }
    }

    fun getError(): LiveData<String> {
        return mError
    }

    fun getOsmNotes(): LiveData<ArrayList<OsmNote>> {
        return mOsmNotes
    }

    fun getOsmNotesEnabled(): LiveData<Boolean> {
        return mOsmNotesEnabled
    }

    fun setOsmNotesEnabled(enabled: Boolean) {
        mOsmNotesEnabled.value = enabled
    }

    fun getKeeprightErrors(): LiveData<ArrayList<KeeprightError>> {
        return mKeeprightErrors
    }

    fun getKeeprightEnabled(): LiveData<Boolean> {
        return mKeeprightEnabled
    }

    fun setKeeprightEnabled(enabled: Boolean) {
        mKeeprightEnabled.value = enabled
    }

    fun getMapdustErrors(): LiveData<ArrayList<MapdustError>> {
        return mMapdustErrors
    }

    fun getMapdustEnabled(): LiveData<Boolean> {
        return mMapdustEnabled
    }

    fun setMapdustEnabled(enabled: Boolean) {
        mMapdustEnabled.value = enabled
    }

    fun getOsmoseErrors(): LiveData<ArrayList<OsmoseError>> {
        return mOsmoseErrors
    }

    fun getOsmoseEnabled(): LiveData<Boolean> {
        return mOsmoseEnabled
    }

    fun setOsmoseEnabled(enabled: Boolean) {
        mOsmoseEnabled.value = enabled
    }

    fun toggleOsmNotesEnabled() {
        val new = !mOsmNotesEnabled.value!!

        mSettings.OsmNotes.Enabled = new

        mOsmNotesEnabled.value = new
    }

    fun toggleKeeprightEnabled() {
        val new = !mKeeprightEnabled.value!!

        mSettings.Keepright.Enabled = new

        mKeeprightEnabled.value = new
    }

    fun toggleMapdustEnabled() {
        val new = !mMapdustEnabled.value!!

        mSettings.Mapdust.Enabled = new

        mMapdustEnabled.value = new
    }

    fun toggleOsmoseEnabled() {
        val new = !mOsmoseEnabled.value!!

        mSettings.Osmose.Enabled = new

        mOsmoseEnabled.value = new
    }

    fun getContentLoading(): LiveData<Boolean> {
        return mContentLoading
    }

    fun onMapMoved(mapCenter: IGeoPoint, boundingBox: BoundingBox) {
        if (mContentLoading.value!!) {
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            mContentLoading.value = true

            try {
                if (mOsmNotesEnabled.value!!) {
                    val errors = mOsmNotesApi.download(
                        boundingBox,
                        mSettings.OsmNotes.ShowClosed,
                        mSettings.OsmNotes.ErrorLimit
                    )

                    mOsmNoteDao.replaceAll(errors)
                }

                if (mKeeprightEnabled.value!!) {
                    val errors = mKeeprightApi.download(
                        mapCenter,
                        mSettings.Keepright.ShowIgnored,
                        mSettings.Keepright.ShowTmpIgnored,
                        mSettings.isLanguageGerman()
                    )

                    mKeeprightDao.replaceAll(errors)
                }

                if (mMapdustEnabled.value!!) {
                    val errors = mMapdustApi.download(boundingBox)

                    mMapdustDao.replaceAll(errors)
                }

                if (mOsmoseEnabled.value!!) {
                    val errors = mOsmoseApi.download(boundingBox)

                    mOsmoseDao.replaceAll(errors)
                }
            } catch (err: Exception) {
                mError.value = err.message
            } finally {
                mContentLoading.value = false
            }
        }
    }

    suspend fun updateOsmNote(osmNote: OsmNote, newComment: String, newState: OsmNote.STATE) {
        if (osmNote.State == OsmNote.STATE.OPEN && newState == OsmNote.STATE.CLOSED) {
            mOsmNotesApi.closeNote(osmNote.Id, newComment)
        } else if (osmNote.State == OsmNote.STATE.OPEN && newState == OsmNote.STATE.OPEN) {
            mOsmNotesApi.addComment(osmNote.Id, newComment)
        } else if (osmNote.State == OsmNote.STATE.CLOSED && newState == OsmNote.STATE.OPEN) {
            mOsmNotesApi.reopenNote(osmNote.Id, newComment)
        } else {
            throw RuntimeException("Closed Notes cannot be commented on")
        }

        // Try to reload this note and replace the old one.
        // If we can not load the new Node, we at least remove the old one
        try {
            mOsmNoteDao.insert(mOsmNotesApi.download(osmNote.Id))
        } catch (err: Exception) {
            mOsmNoteDao.delete(osmNote)

            throw err
        }
    }

    suspend fun updateKeeprightError(error: KeeprightError, newComment: String, newState: KeeprightError.STATE) {
        mKeeprightApi.comment(error.Schema, error.Id, newComment, newState)

        val newError = KeeprightError(error, newComment, newState)

        mKeeprightDao.insert(newError)
    }

    suspend fun updateMapdustError(error: MapdustError, newComment: String, newState: MapdustError.STATE) {
        if (newState != error.State) {
            mMapdustApi.changeErrorStatus(error.Id, newComment, newState)

            error.State = newState
        } else {
            mMapdustApi.commentError(error.Id, newComment)
        }

        // Add the New Comment to the Error
        error.Comments.add(MapdustError.MapdustComment(newComment, mSettings.Mapdust.Username))

        mMapdustDao.insert(error)
    }

    suspend fun addOsmNote(point: IGeoPoint, comment: String) {
        mOsmNoteDao.insert(mOsmNotesApi.addNote(point, comment))
    }

    suspend fun addMapdustError(point: IGeoPoint, type: MapdustError.ERROR_TYPE, description: String) {
        val newId = mMapdustApi.addBug(point, type, description)

        val newError = mMapdustApi.download(newId)

        mMapdustDao.insert(newError)
    }
}