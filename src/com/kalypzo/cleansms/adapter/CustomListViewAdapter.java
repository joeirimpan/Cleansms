package com.kalypzo.cleansms.adapter;

import java.util.ArrayList;

import sms.kalypzo.cleansms.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kalypzo.cleansms.model.ConvItem;
  
public class CustomListViewAdapter extends ArrayAdapter<ConvItem>
{  
	private Context my_context;
	ArrayList<ConvItem> ConvItems;
	public Bitmap Default_bitmap = null;
	private static class ViewHolder {
        TextView name,body,date;
        ImageView img;
        RelativeLayout rl;
    }
	
	 public CustomListViewAdapter(Context context, ArrayList<ConvItem> ConvItems) {
		 super(context, R.layout.convitem, ConvItems);
		// this.ConvItems=ConvItems;
	     my_context=context;
	     Bitmap bitmap = BitmapFactory.decodeResource(my_context.getResources(),R.drawable.contact_blue);
	     Default_bitmap = getRoundedBitmap(bitmap);
	    }
  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        // TODO Auto-generated method stub  
    	ConvItem ConvItem = getItem(position);
    	ViewHolder viewHolder;
    	if(convertView==null){
        	 viewHolder = new ViewHolder();
             LayoutInflater inflater = LayoutInflater.from(my_context);
             convertView = inflater.inflate(R.layout.convitem, null);
             viewHolder.img = (ImageView) convertView.findViewById(R.id.iv_photo);
             viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
             viewHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
             viewHolder.body = (TextView) convertView.findViewById(R.id.tv_body);
 			 viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.relativelayout);
             convertView.setTag(viewHolder);
           } else {
               viewHolder = (ViewHolder) convertView.getTag();
           }
        
        // Populate the data into the template view using the data object
        if(ConvItem.getPhotoUri()!=null)
        {
     	   
     	   Bitmap Fbitmap=null;
     	   try
     	   {
     	   Bitmap bitmap=BitmapFactory.decodeStream(my_context.getContentResolver().openInputStream(ConvItem.getPhotoUri()));
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
     		   viewHolder. img.setImageBitmap(Default_bitmap);//if not given existing contact having photo null wont be displayed since photo uri is not null
     	   }	  
        }
        else
        {
     	   viewHolder.img.setImageBitmap(Default_bitmap);
        }
        
        if(ConvItem.getDisplayName()==null)
        	viewHolder.name.setText(ConvItem.getAddress());//sms contact name not in the contacts so only no. is being displayed
        else
        	viewHolder.name.setText(ConvItem.getDisplayName());
        viewHolder.date.setText(ConvItem.getDate());
        viewHolder.body.setText(ConvItem.getBody());//setting the body of the message
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

	
}