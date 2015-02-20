package org.gittner.osmbugs.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gittner.osmbugs.R;

public class OsmoseFixView extends LinearLayout
{
    private TextView mAdd;
    private TextView mModify;
    private TextView mDelete;


    public OsmoseFixView(Context context)
    {
        super(context);
        init();
    }


    private void init()
    {
        inflate(getContext(), R.layout.osmose_fix, this);

        mAdd = (TextView) findViewById(R.id.txtvFixAdd);
        mModify = (TextView) findViewById(R.id.txtvFixModify);
        mDelete = (TextView) findViewById(R.id.txtvFixDelete);
        mAdd.setVisibility(View.GONE);
        mModify.setVisibility(View.GONE);
        mDelete.setVisibility(View.GONE);
    }


    public OsmoseFixView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    public OsmoseFixView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void set(OsmoseFix fix)
    {
        if (!fix.getAdd().isEmpty())
        {
            String textAdd = "";
            for (OsmKeyValuePair tag : fix.getAdd())
            {
                textAdd += "+ " + tag.toString() + "\n";
            }
            textAdd = textAdd.trim();
            mAdd.setText(textAdd);
            mAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            mAdd.setVisibility(View.GONE);
        }
        if (!fix.getModify().isEmpty())
        {
            String textModify = "";
            for (OsmKeyValuePair tag : fix.getModify())
            {
                textModify += "~ " + tag.toString() + "\n";
            }
            textModify = textModify.trim();
            mModify.setText(textModify);
            mModify.setVisibility(View.VISIBLE);
        }
        else
        {
            mModify.setVisibility(View.GONE);
        }
        if (!fix.getDelete().isEmpty())
        {
            String textDelete = "";
            for (OsmKeyValuePair tag : fix.getDelete())
            {
                textDelete += "- " + tag.toString() + "\n";
            }
            textDelete = textDelete.trim();
            mDelete.setText(textDelete);
            mDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            mDelete.setVisibility(View.GONE);
        }
    }
}
