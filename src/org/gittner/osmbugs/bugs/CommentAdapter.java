package org.gittner.osmbugs.bugs;

import java.util.ArrayList;

import org.gittner.osmbugs.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<Comment> {
	
	Context context_ = null;
	int layoutResourceId_;
	ArrayList<Comment> data_ = null;

	public CommentAdapter(Context context, int layoutResourceId, ArrayList<Comment> data) {
		super(context, layoutResourceId, data);

		context_ = context;
		layoutResourceId_ = layoutResourceId;
		data_ = data;
	}
	
	@Override
	public int getCount(){
		return data_.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		LayoutInflater inflater = ((Activity)context_).getLayoutInflater();
		row = inflater.inflate(layoutResourceId_, parent, false);
		
		((TextView)row.findViewById(R.id.comment_text)).setText(data_.get(position).getText());
		
		return row;		
	}
}
