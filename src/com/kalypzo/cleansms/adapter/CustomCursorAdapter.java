package com.kalypzo.cleansms.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.kalypzo.cleansms.SettingsActivity;

import sms.kalypzo.cleansms.R;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//use ORM
public class CustomCursorAdapter extends CursorAdapter 
{
	
	private static class ViewHolder 
	{
		TextView name,num,body,date;
		ImageView img,read;
		RelativeLayout rl;
		int position;
	}
	private static Context my_context;
	public static Cursor cur;
	public Bitmap Default_bitmap = null;
	//public Typeface type;
	
	
	public CustomCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		my_context = context;
		Bitmap bitmap = BitmapFactory.decodeResource(my_context.getResources(),R.drawable.contact_blue);
		Default_bitmap = getRoundedBitmap(bitmap);
		//type = Typeface.createFromAsset(my_context.getAssets(),"fonts/Ubuntu-R.ttf"); 
	}

	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		View view = LayoutInflater.from(context).inflate(R.layout.convitem,null);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.tv_name);
		holder.date = (TextView) view.findViewById(R.id.tv_date);
		holder.num = (TextView) view.findViewById(R.id.tv_num);
		holder.body = (TextView) view.findViewById(R.id.tv_body);
		holder.img = (ImageView) view.findViewById(R.id.iv_photo);
		holder.read = (ImageView) view.findViewById(R.id.iv_read);
		holder.rl = (RelativeLayout) view.findViewById(R.id.relativelayout);
		view.setTag(holder);
		Log.d("Kalypzo", "New row");
		return view;
	}
	
	public void bindView(final View view, Context context, final Cursor cursor) 
	{
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.position=cursor.getPosition();
		
		String address = null;
		String contactName = null;
		Long contactID = null;
		// to obtain address from canonical-addresses
		String res = cursor.getString(cursor.getColumnIndex("recipient_ids"));
		if(res.split(" ").length==1)
		{
			int read = cursor.getInt(cursor.getColumnIndex("read"));
			if (read != 0)
				holder.read.setVisibility(View.GONE);
			else
				holder.read.setVisibility(View.VISIBLE);
		address = getadd(res);// address
		final String adco = address;
		holder.num.setText(address);
		holder.num.setVisibility(View.INVISIBLE);
		
		setCur(cursor);
		
		Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));
		Cursor cs = my_context.getContentResolver().query(Nameuri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				PhoneLookup.NUMBER + "='" + address + "'", null, null);
		if (cs.getCount() > 0) 
		{
			while (cs.moveToNext()) 
			{
				contactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				contactID = cs.getLong(cs.getColumnIndex(PhoneLookup._ID));
			}
		} 
		else 
		{
			contactID = null;
			contactName = address;
		}
		cs.close();
		final Long cid = contactID;//copy of contact id
		if (contactID == null)
			holder.img.setImageBitmap(Default_bitmap);
		else
		{
			//optimizing listview loading
			holder.img.setImageBitmap(Default_bitmap);
			// Using an AsyncTask to load the slow images in a background thread
			new AsyncTask<ViewHolder, Void, Bitmap>() {
				private ViewHolder holder;

			    @Override
			    protected Bitmap doInBackground(ViewHolder... params) {
			        holder = params[0];
			        return retrieveContactPhoto(cid);
			    }

			    @Override
			    protected void onPostExecute(Bitmap result) {
			        super.onPostExecute(result);
			        if (holder.position==cursor.getPosition()&&holder.num.getText().toString()==adco) {
			            // If this item hasn't been recycled already, hide the
			            // progress and set and show the image
			           // holder.progress.setVisibility(View.GONE);
			        	holder.img.setVisibility(View.GONE);
			            holder.img.setVisibility(View.VISIBLE);
			            holder.img.setImageBitmap(result);
			        }
			    }
			}.execute(holder);
			
			//retrieveandsetContactPhoto(contactID, holder);
		}
		holder.name.setText(contactName);
		//holder.name.setTypeface(type);
		//date
		long milliSeconds = cursor
				.getLong(cursor.getColumnIndexOrThrow("date"));
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		String finalDateString = formatter.format(calendar.getTime());
		holder.date.setText(finalDateString);
		//holder.date.setTypeface(type);
		
		String body1 = cursor.getString(cursor.getColumnIndexOrThrow("snippet"));
			holder.body.setText(body1);
			//holder.body.setTypeface(type);
		}//if close
		
		/*
		Animation animationY=new TranslateAnimation(0,0,holder.rl.getHeight()/4,0);
		animationY.setDuration(500);
		view.startAnimation(animationY);
		animationY=null;*/
		Log.d("Kalypzo", "Recycle row");
	}
	
	public static Cursor getCur() {
		return cur;
	}

	public void setCur(Cursor cur) {
		CustomCursorAdapter.cur = cur;
	}
	
	
	private Bitmap retrieveContactPhoto(Long contactID) 
	{
		Bitmap photo = null;
		try {
			InputStream inputStream = ContactsContract.Contacts
					.openContactPhotoInputStream(my_context
							.getContentResolver(), ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactID));
			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);
				if (photo != null) {
					photo = getRoundedBitmap(photo);
					//holder.img.setImageBitmap(photo);
				}

			}
			else if (inputStream == null) {
				photo=Default_bitmap;
				//holder.img.setImageBitmap(Default_bitmap);
			}
			if (photo == null) {
				photo=Default_bitmap;
				//holder.img.setImageBitmap(Default_bitmap);
			}
			if (inputStream != null)
				inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("Kalypzo", "Exception in inputstream");
		}
		return photo;

	}
	/*
	private void retrieveandsetContactPhoto(Long contactID, ViewHolder holder) 
	{
		Bitmap photo = null;
		try {
			InputStream inputStream = ContactsContract.Contacts
					.openContactPhotoInputStream(my_context
							.getContentResolver(), ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactID));
			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);
				if (photo != null) {
					photo = getRoundedBitmap(photo);
					holder.img.setImageBitmap(photo);
				}

			}
			else if (inputStream == null) {
				holder.img.setImageBitmap(Default_bitmap);
			}
			if (photo == null) {
				holder.img.setImageBitmap(Default_bitmap);
			}
			if (inputStream != null)
				inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("Kalypzo", "Exception in inputstream");
		}

	}*/

	public static Bitmap getRoundedBitmap(Bitmap bitmap) 
	{
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);
		final int color = Color.RED;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		return output;
	}

	public static String getadd(String res) 
	{
		String address = null;
		Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
		Cursor curad = my_context.getContentResolver().query(ad, null,
				"_id = " + res, null, null);
		curad.moveToFirst();
		address = curad.getString(curad.getColumnIndexOrThrow("address"));
		curad.close();
		return address;
	}

}
