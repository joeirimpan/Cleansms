package com.kalypzo.cleansms;
import sms.kalypzo.cleansms.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
 
public class SplashScreen extends Activity {
 Editor editor;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    public Context context;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context=this;
      final  SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",true);
        //SettingsActivity.putPref("versioncode",String.valueOf(getVersion()),getApplicationContext());
      //  Log.d("version",SettingsActivity.getPref("versioncode",getApplicationContext()));
        if(firstRun)//||String.valueOf(getVersion())!=SettingsActivity.getPref("versioncode",getApplicationContext()))
        {
        	
        	new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            	@Override
            	public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            		editor = settings.edit();
            		editor.putBoolean("firstRun", false);
            		editor.commit();
            		//SettingsActivity.putPref("versioncode",String.valueOf(getVersion()),getApplicationContext());
            		SettingsActivity.putPref("view","primary",getApplicationContext());
            		SettingsActivity.putPref("color","blue",getApplicationContext());
                	Intent i = new Intent(SplashScreen.this, MainActivity.class);
                	startActivity(i);
                // close this activity
                finish();
            	}
        		}, SPLASH_TIME_OUT);
        }//if close
        else
        {
        	Intent i = new Intent(SplashScreen.this, MainActivity.class);
        	startActivity(i);
        	finish();
        }
     }//oncreate close
    public int getVersion() {
        int v = 0;
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // Huh? Really?
        }
        return v;
    }
}
 
