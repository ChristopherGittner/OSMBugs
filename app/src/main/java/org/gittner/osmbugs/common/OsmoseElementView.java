package org.gittner.osmbugs.common;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gittner.osmbugs.R;

public class OsmoseElementView extends LinearLayout
{
    private TextView mTitle;
    private TextView mTags;


    public OsmoseElementView(Context context)
    {
        super(context);
        init();
    }


    private void init()
    {
        inflate(getContext(), R.layout.osmose_element, this);

        mTitle = (TextView) findViewById(R.id.txtvTitle);
        mTags = (TextView) findViewById(R.id.txtvTags);
        mTitle.setVisibility(View.GONE);
        mTags.setVisibility(View.GONE);
    }


    public OsmoseElementView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    public OsmoseElementView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void set(OsmoseElement element)
    {
        mTitle.setText(element.toString());
        Linkify.addLinks(mTitle, Linkify.WEB_URLS);
        mTitle.setText(Html.fromHtml(mTitle.getText().toString()));
        mTitle.setVisibility(View.VISIBLE);

        for (OsmoseFix fix : element.getFixes())
        {
            OsmoseFixView fixView = new OsmoseFixView(getContext());
            fixView.set(fix);
            LinearLayout layoutFixes = (LinearLayout) findViewById(R.id.layoutFixes);
            layoutFixes.addView(fixView);
        }

        mTags = (TextView) findViewById(R.id.txtvTags);

        if (!element.getTags().isEmpty())
        {
            String textTags = "";
            for (OsmKeyValuePair tag : element.getTags())
            {
                textTags += tag.toString() + "\n";
            }
            textTags = textTags.trim();
            mTags.setText(textTags);
            mTags.setVisibility(View.VISIBLE);
        }
        else
        {
            mTags.setVisibility(View.GONE);
        }
    }
}
