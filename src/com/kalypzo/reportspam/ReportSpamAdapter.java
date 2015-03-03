package com.kalypzo.reportspam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sms.kalypzo.cleansms.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReportSpamAdapter extends CursorAdapter {

	private static class ViewHolder {
		TextView name, num, body, date;
		RelativeLayout rl;
		int position;
	}

	private static Context my_context;
	public static Cursor cur;
	public Bitmap Default_bitmap = null;

	public ReportSpamAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		my_context = context;
	}

	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.reportspam_items, null);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.tv_name);
		holder.date = (TextView) view.findViewById(R.id.tv_date);
		holder.num = (TextView) view.findViewById(R.id.tv_num);
		holder.body = (TextView) view.findViewById(R.id.tv_body);
		holder.rl = (RelativeLayout) view.findViewById(R.id.relativelayout);
		view.setTag(holder);
		return view;
	}

	public void bindView(final View view, Context context, final Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.position = cursor.getPosition();
		String address = null;
		String contactName = null;
		Long contactID = null;
		String res = cursor.getString(cursor.getColumnIndex("thread_id"));
		if (res.split(" ").length == 1) {
			int read = cursor.getInt(cursor.getColumnIndex("read"));
			address = cursor.getString(cursor.getColumnIndex("address"));// address
			holder.num.setText(address);
			holder.num.setVisibility(View.INVISIBLE);

			Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(address));
			Cursor cs = my_context.getContentResolver().query(Nameuri,
					new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
					PhoneLookup.NUMBER + "='" + address + "'", null, null);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					contactName = cs.getString(cs
							.getColumnIndex(PhoneLookup.DISPLAY_NAME));
					contactID = cs.getLong(cs.getColumnIndex(PhoneLookup._ID));
				}
			} else {
				contactID = null;
				contactName = address;
			}
			cs.close();

			holder.name.setText(contactName);
			// holder.name.setTypeface(type);
			// date
			long milliSeconds = cursor.getLong(cursor
					.getColumnIndexOrThrow("date"));
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			String finalDateString = formatter.format(calendar.getTime());
			holder.date.setText(finalDateString);
			// holder.date.setTypeface(type);

			String body1 = cursor.getString(cursor
					.getColumnIndexOrThrow("body"));
			holder.body.setText(body1);
			// holder.body.setTypeface(type);
		}// if close

		/*
		 * Animation animationY=new
		 * TranslateAnimation(0,0,holder.rl.getHeight()/4,0);
		 * animationY.setDuration(500); view.startAnimation(animationY);
		 * animationY=null;
		 */
		Log.d("Kalypzo", "Recycle row");
	}
}
