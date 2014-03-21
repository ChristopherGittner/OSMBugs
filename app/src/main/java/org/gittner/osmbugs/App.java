package org.gittner.osmbugs;

import android.app.Application;
import android.content.Context;

/**
 * Created by christopher on 3/20/14.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    private static Context mContext;
}
