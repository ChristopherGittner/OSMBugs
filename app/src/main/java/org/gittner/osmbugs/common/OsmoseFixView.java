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

        mAdd = findViewById(R.id.txtvFixAdd);
        mModify = findViewById(R.id.txtvFixModify);
        mDelete = findViewById(R.id.txtvFixDelete);
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
            StringBuilder textAdd = new StringBuilder();
            for (OsmKeyValuePair tag : fix.getAdd())
            {
                textAdd.append("+ ").append(tag.toString()).append("\n");
            }
            textAdd = new StringBuilder(textAdd.toString().trim());
            mAdd.setText(textAdd.toString());
            mAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            mAdd.setVisibility(View.GONE);
        }

        if (!fix.getModify().isEmpty())
        {
            StringBuilder textModify = new StringBuilder();
            for (OsmKeyValuePair tag : fix.getModify())
            {
                textModify.append("~ ").append(tag.toString()).append("\n");
            }
            textModify = new StringBuilder(textModify.toString().trim());
            mModify.setText(textModify.toString());
            mModify.setVisibility(View.VISIBLE);
        }
        else
        {
            mModify.setVisibility(View.GONE);
        }

        if (!fix.getDelete().isEmpty())
        {
            StringBuilder textDelete = new StringBuilder();
            for (OsmKeyValuePair tag : fix.getDelete())
            {
                textDelete.append("- ").append(tag.toString()).append("\n");
            }
            textDelete = new StringBuilder(textDelete.toString().trim());
            mDelete.setText(textDelete.toString());
            mDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            mDelete.setVisibility(View.GONE);
        }
    }
}
