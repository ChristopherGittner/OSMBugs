package org.gittner.osmbugs.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gittner.osmbugs.keepright.KeeprightApi
import org.gittner.osmbugs.keepright.KeeprightDao
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmnotes.OsmNoteDao
import org.gittner.osmbugs.osmnotes.OsmNotesApi
import org.gittner.osmbugs.osmose.OsmoseApi
import org.gittner.osmbugs.osmose.OsmoseDao
import org.gittner.osmbugs.osmose.OsmoseError
import org.gittner.osmbugs.statics.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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

    fun toggleOsmoseEnabled() {
        val new = !mOsmoseEnabled.value!!

        mSettings.Osmose.Enabled = new

        mOsmoseEnabled.value = new
    }

    fun getContentLoading(): LiveData<Boolean> {
        return mContentLoading
    }

    fun reloadErrors(mapCenter: IGeoPoint, boundingBox: BoundingBox) {
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
            } catch (err: Exception) {
                mError.value = err.message
            }

            try {
                if (mKeeprightEnabled.value!!) {
                    val errors = mKeeprightApi.download(
                        mapCenter,
                        mSettings.Keepright.ShowIgnored,
                        mSettings.Keepright.ShowTmpIgnored,
                        mSettings.isLanguageGerman()
                    )

                    mKeeprightDao.replaceAll(errors)
                }
            } catch (err: Exception) {
                mError.value = err.message
            }

            try {
                if (mOsmoseEnabled.value!!) {
                    val errors = mOsmoseApi.download(boundingBox)

                    mOsmoseDao.replaceAll(errors)
                }
            } catch (err: Exception) {
                mError.value = err.message
            }

            mContentLoading.value = false
        }
    }

    suspend fun updateOsmNote(osmNote: OsmNote, newComment: String, newState: OsmNote.STATE) {
        try {
            if (osmNote.State == OsmNote.STATE.OPEN && newState == OsmNote.STATE.CLOSED) {
                mOsmNotesApi.closeNote(osmNote.Id, newComment)
            } else if (osmNote.State == OsmNote.STATE.OPEN && newState == OsmNote.STATE.OPEN) {
                mOsmNotesApi.addComment(osmNote.Id, newComment)
            } else if (osmNote.State == OsmNote.STATE.CLOSED && newState == OsmNote.STATE.OPEN) {
                mOsmNotesApi.reopenNote(osmNote.Id, newComment)
            } else {
                throw RuntimeException("Closed Notes cannot be commented on")
            }
        } finally {
            // Try to reload this note and replace the old one.
            // If we can not load the new Node, we at least remove the old one
            try {
                mOsmNoteDao.insert(mOsmNotesApi.download(osmNote.Id))
            } catch (err: Exception) {
                mOsmNoteDao.delete(osmNote)
            }
        }
    }

    suspend fun updateKeeprightError(error: KeeprightError, newComment: String, newState: KeeprightError.STATE) {
        mKeeprightApi.comment(error.Schema, error.Id, newComment, newState)

        val newError = KeeprightError(error, newComment, newState)

        mKeeprightDao.insert(newError)
    }

    suspend fun addOsmNote(point: IGeoPoint, comment: String) {
        mOsmNoteDao.insert(mOsmNotesApi.addNote(point, comment))
    }
}