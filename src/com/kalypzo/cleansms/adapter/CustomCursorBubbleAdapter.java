package com.kalypzo.cleansms.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sms.kalypzo.cleansms.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CustomCursorBubbleAdapter extends CursorAdapter {
	private static class ViewHolder {
		TextView body, date;

	}
	private static Context my_context;
	//public Typeface typef;
	public CustomCursorBubbleAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		my_context = context;
		//typef = Typeface.createFromAsset(my_context.getAssets(),"fonts/Ubuntu-R.ttf"); 

	}

	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View view = LayoutInflater.from(context)
				.inflate(R.layout.sms_row, null);
		ViewHolder holder = new ViewHolder();

		holder.body = (TextView) view.findViewById(R.id.message_text);
		holder.date = (TextView) view.findViewById(R.id.date);

		view.setTag(holder);
		Log.d("Kalypzo", "New row");
		return view;
	}

	@SuppressLint("SimpleDateFormat")
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) view.getTag();

		String body1 = cursor.getString(cursor.getColumnIndexOrThrow("body"));
		Long type = cursor.getLong(cursor.getColumnIndexOrThrow("type"));

		long milliSeconds = cursor
				.getLong(cursor.getColumnIndexOrThrow("date"));
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		String finalDateString = formatter.format(calendar.getTime());

		LayoutParams lp = (LayoutParams) holder.body.getLayoutParams();

		// If not mine then it is from sender to show orange background and
		// align to left
		if (type == 1) {
			holder.body.setBackgroundResource(R.drawable.incomin);
			holder.body.setTextColor(Color.parseColor("#FFFFFF"));
			lp.gravity = Gravity.LEFT;
		}
		// Check whether message is mine to show green background and align to
		// right
		else {
			holder.body.setBackgroundResource(R.drawable.outgoing);
			holder.body.setTextColor(Color.parseColor("#176E9A"));
			lp.gravity = Gravity.RIGHT;
		}
		holder.body.setLayoutParams(lp);
		// viewHolder.body.setTextColor(R.color.textColor);
		holder.date.setLayoutParams(lp);
		holder.date.setText(finalDateString);
	//	holder.date.setTypeface(typef);
		holder.body.setText(body1);
	//	holder.body.setTypeface(typef);// setting the body of the
									// message
		Log.d("Kalypzo", "Recycle row");

	}

}
