package com.kalypzo.cleansms.adapter;

import sms.kalypzo.cleansms.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.kalypzo.cleansms.SettingsActivity;
import com.kalypzo.cleansms.model.NavDrawerItem;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	public Bitmap Default_bitmap;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.contact_blue);
		Default_bitmap = getRoundedBitmap(bitmap);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
         
		RelativeLayout rl= (RelativeLayout) convertView.findViewById(R.id.rel);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        ImageView profile_icon = (ImageView) convertView.findViewById(R.id.profile_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView profile_name = (TextView) convertView.findViewById(R.id.profile_name);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
        String profile_uri=null,username=null;
        Cursor c = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null); 
		if(c.moveToFirst())
		{
			profile_uri=c.getString(c.getColumnIndex("photo_uri"));
			username=c.getString(c.getColumnIndex("display_name"));
		}
		c.close();
		Uri photouri=null;
		Bitmap roundprofile_photo = null;
		
		if(profile_uri!=null&&position==0)
		{	
			photouri = Uri.parse(profile_uri);
		
			Bitmap bitmap=null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photouri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if(bitmap!=null)
				roundprofile_photo=getRoundedBitmap(bitmap); 
			
			
			
			//height of rl
			//final float scale = context.getResources().getDisplayMetrics().density;
			//int pixels=(int)(70*scale+0.5f);
			//rl.getLayoutParams().height=pixels;
			rl.setBackgroundResource(R.drawable.user_bg);
			//rl.setBackgroundColor(Color.parseColor("#00CFEE"));
        	profile_icon.setImageBitmap(roundprofile_photo);
        	profile_name.setText(navDrawerItems.get(position).getTitle());
		}
		else if(profile_uri==null&&position==0&&username!=null)
        {
        	rl.setBackgroundResource(R.drawable.user_bg);
			//rl.setBackgroundColor(Color.parseColor("#00CFEE"));
        	profile_icon.setImageBitmap(Default_bitmap);
        	profile_name.setText(navDrawerItems.get(position).getTitle());
        }
        else
        {
        	String colorpref=SettingsActivity.getPref("color",context);
    		if(colorpref.equals("blue"))
    		{
    			rl.setBackgroundResource(R.drawable.list_selector);
    		}
    		else if(colorpref.equals("another"))
    		{
    			rl.setBackgroundResource(R.drawable.list_selector1);
    		}
        	imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
        	txtTitle.setText(navDrawerItems.get(position).getTitle());
        }
        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility()){
        	txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
        	// hide the counter view
        	txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
	}
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
	 
}
