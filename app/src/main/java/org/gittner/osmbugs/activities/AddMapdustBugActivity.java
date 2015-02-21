package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.osmdroid.util.GeoPoint;

@EActivity(R.layout.activity_add_mapdust_bug)
@OptionsMenu(R.menu.add_mapdust_bug)
public class AddMapdustBugActivity extends ActionBarActivity
{
    public static final String EXTRA_LATITUDE = "EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "EXTRA_LONGITUDE";

    @Extra(EXTRA_LATITUDE)
    double mLatitude;
    @Extra(EXTRA_LONGITUDE)
    double mLongitude;

    @ViewById(R.id.spnType)
    Spinner mSpnType;
    @ViewById(R.id.edttxtDescription)
    EditText mDescription;

    @OptionsMenuItem(R.id.action_done)
    MenuItem mMenuDone;

    private MaterialDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        /* Setup the Type Adapter */
        mSpnType = (Spinner) findViewById(R.id.spnType);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        typeAdapter.addAll(
                getString(R.string.wrong_turn),
                getString(R.string.bad_routing),
                getString(R.string.oneway_road),
                getString(R.string.blocked_street),
                getString(R.string.missing_street),
                getString(R.string.roundabout),
                getString(R.string.missing_speed_info),
                getString(R.string.other));

        mSpnType.setAdapter(typeAdapter);
        mSpnType.setSelection(7);

        typeAdapter.notifyDataSetChanged();

        mSaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.saving)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mMenuDone.setVisible(!mDescription.getText().toString().equals(""));

        return true;
    }


    @OptionsItem(R.id.action_done)
    void menuDoneClicked()
    {
        mSaveDialog.show();

        int type = MapdustBug.OTHER;
        switch (mSpnType.getSelectedItemPosition())
        {
            case 0:
                type = MapdustBug.WRONG_TURN;
                break;
            case 1:
                type = MapdustBug.BAD_ROUTING;
                break;
            case 2:
                type = MapdustBug.ONEWAY_ROAD;
                break;
            case 3:
                type = MapdustBug.BLOCKED_STREET;
                break;
            case 4:
                type = MapdustBug.MISSING_STREET;
                break;
            case 5:
                type = MapdustBug.ROUNDABOUT_ISSUE;
                break;
            case 6:
                type = MapdustBug.MISSING_SPEED_INFO;
                break;
            case 7:
                type = MapdustBug.OTHER;
                break;
        }

        addBug(
                new GeoPoint(mLatitude, mLongitude),
                mDescription.getText().toString(),
                type
        );
    }


    @Background
    void addBug(GeoPoint geoPoint, String description, int type)
    {
        boolean result = new MapdustApi().addBug(geoPoint, type, description);

        addBugDone(result);
    }


    @UiThread
    void addBugDone(boolean result)
    {
        mSaveDialog.dismiss();

        if (!result)
        {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
        else
        {
            setResult(RESULT_OK);
            finish();
        }
    }


    @AfterTextChange(R.id.edttxtDescription)
    void descriptionChanged()
    {
        invalidateOptionsMenu();
    }
}
