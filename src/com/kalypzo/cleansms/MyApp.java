package com.kalypzo.cleansms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApp extends Application{

	
	SharedPreferences mPrefs; 
	
	@Override public void onCreate() {
		super.onCreate(); 
		Context mContext = this.getApplicationContext();
		//0 = mode private. only this app can read these preferences 
		mPrefs = mContext.getSharedPreferences("myAppPrefs", 0);
		// the rest of your app initialization code goes here 
		} 
	public boolean getFirstRun() {
		return mPrefs.getBoolean("firstRun", true); 
		} 
	public void setRunned() { 
		SharedPreferences.Editor edit = mPrefs.edit(); 
		edit.putBoolean("firstRun", false);
		edit.commit();
	}
}
