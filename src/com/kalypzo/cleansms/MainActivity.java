package com.kalypzo.cleansms;

import com.kalypzo.cleansms.adapter.NavDrawerListAdapter;
import com.kalypzo.cleansms.model.NavDrawerItem;
import com.kalypzo.reportspam.ReportSpam;

import sms.kalypzo.cleansms.R;

import java.util.ArrayList;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	static int dec = 0;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private ArrayList<String> navMenuTitles = new ArrayList<String>();;
	private TypedArray navMenuIcons;
	public String unread = null;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//actionbar
		// hide feature
		setContentView(R.layout.activity_main);

		// getActionBar().setDisplayShowHomeEnabled(false); //remove app icon
		// getActionBar().setDisplayShowTitleEnabled(false); //remove app title
		String colorpref=SettingsActivity.getPref("color",this);
		if(colorpref.equals("blue"))
		{
			SettingsActivity.brandGlowEffect(this,"#00CFEE");
			getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		}
		else if(colorpref.equals("another"))
		{
			SettingsActivity.brandGlowEffect(this,"#FFAABB");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));
		}

		mTitle = mDrawerTitle = getTitle();

		// profile name
		String profile_name = null;
		Cursor c = getApplication().getContentResolver().query(
				ContactsContract.Profile.CONTENT_URI, null, null, null, null);
		if (c.moveToFirst()) {
			profile_name = c.getString(c.getColumnIndex("display_name"));
		}
		c.close();

		// load slide menu items
		// navMenuTitles =
		// getResources().getStringArray(R.array.nav_drawer_items);

		if (profile_name != null)
			navMenuTitles.add(profile_name);
		else
			dec = 1;

		navMenuTitles.add("Primary");
		navMenuTitles.add("Promotions");
		navMenuTitles.add("Favorites");
		navMenuTitles.add("Inbox");
		navMenuTitles.add("Report Spam");
		navMenuTitles.add("Rate Us");
		navMenuTitles.add("About Us");
		
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array

		// user-profile
		if (profile_name != null)
			navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(0),
					navMenuIcons.getResourceId(0, -1)));

		// Primary
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(1 - dec),
				navMenuIcons.getResourceId(0, -1)));
		// Promotions
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(2 - dec),
				navMenuIcons.getResourceId(1, -1)));
		// Favorites
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(3 - dec),
				navMenuIcons.getResourceId(2, -1)));
		unread = getunreadcount();
		// Inbox
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(4 - dec),
				navMenuIcons.getResourceId(3, -1)));

		// We will add a counter here
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[6],
		// navMenuIcons.getResourceId(5, -1), true,unread));

		// Customization
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
		// .getResourceId(2, -1)));

		// Report Spam
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(5 - dec),
						navMenuIcons.getResourceId(6, -1)));
		// Rate Us
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(6 - dec),
						navMenuIcons.getResourceId(5, -1)));
		// About Us
		navDrawerItems.add(new NavDrawerItem(navMenuTitles.get(7 - dec),
				navMenuIcons.getResourceId(4, -1)));
	
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {

				getSupportActionBar().setTitle(mTitle);
				//Prim.floatingActionButton.show();
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {

				getSupportActionBar().setTitle(mDrawerTitle);
				//Prim.floatingActionButton.hide();
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			String disp=SettingsActivity.getPref("view",this);
			if(disp.equals("inbox"))
				displayView(4 - dec);//inbox
			else
				displayView(1 - dec);//primary
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		 menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */

	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		if (dec == 1)// user profile empty
		{
			switch (position) {
			case 0:

				fragment = new Prim();
				
				break;
			case 1:

				fragment = new Promo();
				break;

			// case 2:

			// fragment = new ColorPickerActivity();
			// break;
			case 2:

				fragment = new Fav();
				break;
			case 3:

				fragment = new InboxLoaderFragment();
				break;
			case 4:
				fragment = new ReportSpam();
				break;
			// Intent i = new Intent();
			// i.setClass(this,ReportSpam.class);
			// startActivity(i);
			case 5:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=sms.kalypzo.cleansms")));
				break;
			case 6:

				fragment = new AboutUsFragment();
				break;

			

			default:
				break;
			}
		} else {
			switch (position) {
			case 1:

				fragment = new Prim();
				break;
			case 2:

				fragment = new Promo();
				break;

			// case 2:

			// fragment = new ColorPickerActivity();
			// break;
			case 3:

				fragment = new Fav();
				break;
			case 4:

				fragment = new InboxLoaderFragment();
				break;
			case 5:
				fragment = new ReportSpam();
				break;
			case 6:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=sms.kalypzo.cleansms")));
				break;
			case 7:

				fragment = new AboutUsFragment();
				break;
			default:
				break;
			}
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			
			
			setTitle(navMenuTitles.get(position));
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public String getunreadcount() {
		int count = 0;
		Uri ad = Uri.parse("content://mms-sms/conversations?simple=true");
		Cursor cur1 = this.getContentResolver().query(ad, null, null, null,
				null);
		if (cur1.moveToFirst()) {
			while (cur1.moveToNext()) {
				if (cur1.getInt(cur1.getColumnIndexOrThrow("read")) == 0) {
					++count;
				}
			}
		} else {
			Log.d("test", "cur not working");
		}
		cur1.close();
		return String.valueOf(count);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		String colorpref=SettingsActivity.getPref("color",this);
		if(colorpref.equals("blue"))
		{
			SettingsActivity.brandGlowEffect(this,"#00CFEE");
			getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		}
		else if(colorpref.equals("another"))
		{
			SettingsActivity.brandGlowEffect(this,"#FFAABB");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));
		}
		adapter.notifyDataSetChanged();
		super.onPostResume();
	}

}
