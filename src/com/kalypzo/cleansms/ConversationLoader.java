package com.kalypzo.cleansms;

import com.kalypzo.cleansms.adapter.CustomCursorBubbleAdapter;

import sms.kalypzo.cleansms.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ListView;

public class ConversationLoader extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnClickListener {

	static String address;
	static String passedvar;
	public String nametest;
	static String name=null;
	public static Context c;
	EditText msg;
	ImageButton btn;

	private static final int LOADER_ID = 2;// identify which loader
	LoaderManager lm;
	CustomCursorBubbleAdapter adapter;
	ListView lv;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//prevent keyboard auto open
		getSupportActionBar().setDisplayShowHomeEnabled(false); //remove app icon
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		c=this;
		
		String colorpref=SettingsActivity.getPref("color",this);
		if(colorpref.equals("blue"))
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		else if(colorpref.equals("another"))
		getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));
		
		passedvar = getIntent().getStringExtra("thread_id2");
		address = getIntent().getStringExtra("address_2");
		//name = getIntent().getStringExtra("name");
		Log.d("Address",address);
		nametest=getName(address);
		setTitle(nametest);
		
		//setTitle(name);

		// toasting the thread id
		//Toast t = Toast.makeText(c, passedvar, Toast.LENGTH_LONG);
		//t.show();
		
		msg = (EditText) findViewById(R.id.newmessagecontent);
		
		btn = (ImageButton) findViewById(R.id.sendmessage);
		// setting up the list and adapter
		ListView list = (ListView) findViewById(R.id.ConvListView);
		

		adapter = new CustomCursorBubbleAdapter(this, null, 0);
		list.setAdapter(adapter);
		btn.setOnClickListener(this);

		mCallbacks = this;
		lm = getSupportLoaderManager();
		// Initiating the loader
		lm.initLoader(LOADER_ID, null, mCallbacks);

	}
	public String getName(String address)//For title of conversation class
	{
		String contactName = null;
		Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));
		if(Nameuri==null)
		{
			contactName = address; 
		}
		else
		{
			Cursor cs= this.getContentResolver().query(Nameuri,
				new String[] { PhoneLookup.DISPLAY_NAME},
				PhoneLookup.NUMBER + "='" + address + "'", null, null);
		
			if (cs.getCount() > 0) 
			{
				while (cs.moveToNext()) 
				//cs.moveToFirst();
				{
					contactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				}
			}
			else 
			{
				contactName = address;
			}
			cs.close();
		}
		return contactName;
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.conversationmenu, menu);
		return true;
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
			// ProjectsActivity is my 'home' activity
			onBackPressed();
			return true;
		case R.id.call:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + address));
			startActivity(callIntent);
			return true;
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0,
			Bundle arg1) {

		Uri baseUri = Uri.parse("content://sms/");

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.

		return new CursorLoader(this, baseUri, null,
				"thread_id = " + passedvar, null, "date asc");
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0,
			Cursor arg1) {
		switch (arg0.getId()) {
		case LOADER_ID:
			// The asynchronous load is complete and the data
			// is now available for use. Only now can we associate
			// the queried Cursor with the SimpleCursorAdapter.
			adapter.swapCursor(arg1);
			break;
		}
		// The listview now displays the queried data

	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {

		adapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendmessage:

			// Initialize SmsManager Object
			SmsManager smsManager = SmsManager.getDefault();
			String msgpoll = msg.getText().toString();
			if (msgpoll.length() > 0) {
				
				// Send Message using method of SmsManager object
				smsManager.sendTextMessage(address, null, msg.getText()
						.toString(), null, null);
				
				//set bubble here then sync from database here
				
				Toast.makeText(this, "Message sent successfully",
						Toast.LENGTH_LONG).show();
				msg.setText("");
				msg.clearComposingText();
			} else {
				Toast.makeText(this, "Message field is blank",
						Toast.LENGTH_LONG).show();
			}
			break;
		}

	}
}// /main activity
