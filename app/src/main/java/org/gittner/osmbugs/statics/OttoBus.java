package org.gittner.osmbugs.statics;

import com.squareup.otto.Bus;

public class OttoBus extends Bus
{
    private static OttoBus instance = new OttoBus();


    public static OttoBus getInstance()
    {
        return instance;
    }


    private void OttoBus()
    {

    }
}
