package com.kalypzo.cleansms;

import sms.kalypzo.cleansms.R;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class AboutUsFragment extends Fragment{

	TextView t1,t2;
	ImageView i; 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootview=inflater.inflate(R.layout.aboutusfragment,container,false);
    	
    	t1 = (TextView) rootview.findViewById(R.id.about_ust1);
    	t1.setText("\tThis app is designed and created by:");
    	
    	//t.setText("Website \n\twww.kalypzo.in\n\n\n"+"Email Us\n\t\tyadu@kalypzo.in\n\t\tjoe@kalypzo.in\n\t\tshyam@kalypzo.in\n");
    	i = (ImageView) rootview.findViewById(R.id.kalypzo_logo);
    	i.setImageResource(R.drawable.kalypzo);
    	//i.setBackgroundColor(Color.rgb(100, 100, 50));
    	t2 = (TextView) rootview.findViewById(R.id.about_ust2);
    	t2.setText("\nStartup Village\nKinfra High Tech Park\nKalamassery\nKochi-683503\n\nwww.kalypzo.in\n\nFor more details and queries:\ncontact@kalypzo.in\n+919567619749");
    	
    	return rootview;
    }
  
	
}
