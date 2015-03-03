package com.kalypzo.cleansms.adapter;

import java.util.List;

import com.kalypzo.cleansms.SettingsActivity;
import com.kalypzo.cleansms.model.Contact;

import sms.kalypzo.cleansms.R;

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
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Contactadapter extends ArrayAdapter<Contact> {
	private Context my_context;
	public Bitmap Default_bitmap = null;

	// View lookup cache
	private static class ViewHolder {
		TextView name,num,id,body,date;
		ImageView img,read;
		RelativeLayout rl;
	}
	//public Typeface type;

	public Contactadapter(Context context, List<Contact> Contacts) {
		super(context, R.layout.contactdum, Contacts);
		my_context = context;
		Bitmap bitmap = BitmapFactory.decodeResource(my_context.getResources(),R.drawable.contact_blue);
		Default_bitmap = getRoundedBitmap(bitmap);
		//type = Typeface.createFromAsset(my_context.getAssets(),"fonts/Ubuntu-R.ttf"); 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Contact Contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.contactdum, null);
			 
			viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_photo);
			viewHolder.read = (ImageView) convertView.findViewById(R.id.iv_read);
			viewHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.num = (TextView) convertView.findViewById(R.id.tv_num);
		//	viewHolder.id = (TextView) convertView.findViewById(R.id.tv_id);
			viewHolder.body = (TextView) convertView.findViewById(R.id.tv_body);
			viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.rlayout);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		if(Contact.getRead()!=0)
			viewHolder.read.setVisibility(View.GONE);
		else
			viewHolder.read.setVisibility(View.VISIBLE);
		
		
		 if(Contact.getPhotoUri()!=null)
	       {
	    	   
	    	   Bitmap Fbitmap=null;
	    	   try
	    	   {
	    		   Bitmap bitmap=BitmapFactory.decodeByteArray(Contact.getPhotoUri(), 0,Contact.getPhotoUri().length);
	    		   Fbitmap=getRoundedBitmap(bitmap);
	    	   }catch(Exception e)
	    	   {	
	    	       viewHolder.img.setImageBitmap(Default_bitmap);
	    		   e.printStackTrace();
	    	   }
	    	   if(Fbitmap!=null)
	    	   viewHolder.img.setImageBitmap(Fbitmap);//setImageURI();
	    	   else
	    	   {
	    		  viewHolder.img.setImageBitmap(Default_bitmap);//if not given existing contact having photo null wont be displayed since photo uri is not null
	    	   }	  
	       }
	       else
	       {
	    	   viewHolder.img.setImageBitmap(Default_bitmap);
	       }
		 
		 
		viewHolder.name.setText(Contact.getName());
		//viewHolder.name.setTypeface(type);
		
		viewHolder.num.setText(Contact.getPhoneNumber());
		viewHolder.num.setVisibility(View.INVISIBLE);
		
		//viewHolder.id.setText(getid(Contact.getPhoneNumber()));
		//viewHolder.id.setVisibility(View.INVISIBLE);
		
		viewHolder.body.setText(Contact.getDate());//body
		viewHolder.date.setText(Contact.getDat());
		//viewHolder.body.setTypeface(type);
		
		// Return the completed view to render on screen
		/*
		Animation animationY=new TranslateAnimation(0,0,viewHolder.rl.getHeight()/4,0);
		animationY.setDuration(500);
		convertView.startAnimation(animationY);
		animationY=null;*/
		return convertView;
	}
	
	
	  public static Bitmap getRoundedBitmap(Bitmap bitmap){
	    	final Bitmap output=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
	    	final Canvas canvas=new Canvas(output);
	    	final int color=Color.RED;
	    	final Paint paint=new Paint();
	    	final Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
	    	final RectF rectF=new RectF(rect);
	    	paint.setAntiAlias(true);
	    	canvas.drawARGB(0, 0, 0, 0);
	    	paint.setColor(color);
	    	canvas.drawOval(rectF, paint);
	    	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    	canvas.drawBitmap(bitmap,rect,rect,paint);
	    	bitmap.recycle();
	    	return output;
	    }
	  public String getid(String address) {
			String res = null;
		
			Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
			Cursor cur1 = my_context.getContentResolver().query(ad, null, null,
					null, null);
			// cur1.moveToFirst();
			if (cur1.moveToFirst()) {

				while (cur1.moveToNext()) {
					if (address.equals(cur1.getString(cur1
							.getColumnIndexOrThrow("address")))) {
						res = cur1.getString(cur1.getColumnIndexOrThrow("_id"));
					}
					// else
					// {Log.d("test","Address not equal"+cur1.getString(cur1.getColumnIndexOrThrow("_id"))+" "+cur1.getString(cur1.getColumnIndexOrThrow("address")));}
				}
			} else {
				Log.d("test", "cursor error");
			}
			Log.d("test", "ID=" + res);
			cur1.close();
			return res;
		}
		
	    
}