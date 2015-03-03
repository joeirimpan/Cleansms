package com.kalypzo.cleansms;

import sms.kalypzo.cleansms.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingsActivity extends ActionBarActivity implements OnItemClickListener {
	public Context context;
	ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_layout);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//theme it
		String colorpref=getPref("color",this);
		if(colorpref.equals("blue"))
		{
			brandGlowEffect(this,"#00CFEE");
			getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		}
		else if(colorpref.equals("another"))
		{
			brandGlowEffect(this,"#FFAABB");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));
		}
		
		context = this;
		listview = (ListView) findViewById(R.id.lvsettings);
		String[] values = new String[] { "Select default view","Color Theme" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.left_in,R.anim.right_out);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	public static void putPref(String key, String value, Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getPref(String key, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getString(key, null);
	}

	/**
	 * Overscroll hack
	 * @param context-context of the corresponding fragment or activity
	 * @param color-hex color code
	 */
	static void brandGlowEffect(Context context,String color) {
		  int brandColor = Color.parseColor(color);
	      //glow
	      int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
	      Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
	      androidGlow.setColorFilter(brandColor,PorterDuff.Mode.SRC_IN);
	      //edge
	      int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
	      Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
	      androidEdge.setColorFilter(brandColor,PorterDuff.Mode.SRC_IN);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			CharSequence items[] = new CharSequence[] { "Primary", "Inbox" };
			// change selection
			String def = getPref("view", context);
			int selection = 0;
			if (def.equals("primary"))
				selection = 0;
			else
				selection = 1;
			adb.setSingleChoiceItems(items, selection, new OnClickListener() {

				@Override
				public void onClick(DialogInterface d, int n) {
					if (n == 0) {
						putPref("view", "primary", getApplicationContext());
						// Toast.makeText(context,getPref("view",context),Toast.LENGTH_SHORT).show();
					} else {
						putPref("view", "inbox", getApplicationContext());
						// Toast.makeText(context,getPref("view",context),Toast.LENGTH_SHORT).show();
					}
					((Dialog) d).dismiss();
				}
			});
			adb.setNegativeButton("Cancel", null);
			adb.setTitle("Which one?");
			adb.show();
		}
		else if(position==1)
		{
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			CharSequence items[] = new CharSequence[] { "Default Blue", "Creamish Pink" };
			// change selection
			String def = getPref("color", context);
			int selection = 0;
			if (def.equals("blue"))
				selection = 0;
			else
				selection = 1;
			adb.setSingleChoiceItems(items, selection, new OnClickListener() {

				@Override
				public void onClick(DialogInterface d, int n) {
					if (n == 0) {
						putPref("color", "blue", getApplicationContext());
						brandGlowEffect(getApplicationContext(),"#00CFEE");
						getSupportActionBar().setBackgroundDrawable(
								new ColorDrawable(Color.parseColor("#00CFEE")));
						// Toast.makeText(context,getPref("view",context),Toast.LENGTH_SHORT).show();
					} else {
						putPref("color", "another", getApplicationContext());
						brandGlowEffect(getApplicationContext(),"#00CFEE");
						getSupportActionBar().setBackgroundDrawable(
								new ColorDrawable(Color.parseColor("#FFAABB")));
						// Toast.makeText(context,getPref("view",context),Toast.LENGTH_SHORT).show();
					}
					((Dialog) d).dismiss();
				}
			});
			adb.setNegativeButton("Cancel", null);
			adb.setTitle("Color");
			adb.show();
		}
	}
	

}