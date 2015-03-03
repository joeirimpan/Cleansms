package com.kalypzo.cleansms;

import com.kalypzo.cleansms.adapter.CustomCursorBubbleAdapter;

import sms.kalypzo.cleansms.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ListView;

public class ConversationLoaderPrimary extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnClickListener {

	static String address;
	static String passedvar;
	static String name;
	Context c = this;
	EditText msg;
	ImageButton btn;

	private static final int LOADER_ID = 3;// identify which loader
	LoaderManager lm;
	CustomCursorBubbleAdapter adapter;
	ListView lv;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	Prim s=new Prim();

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//prevent keyboard auto open
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		String colorpref=SettingsActivity.getPref("color",this);
		if(colorpref.equals("blue"))
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		else if(colorpref.equals("another"))
		getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));

		passedvar = getIntent().getStringExtra("threadid");
		address = getIntent().getStringExtra("address");
		name = getIntent().getStringExtra("name");
		setTitle(name);

		// toasting the thread idS
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
				"address = " + "'"+address+"'", null, "date asc");
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

				Toast.makeText(this, "Message sent successfully",
						Toast.LENGTH_LONG).show();
				s.getSend().add(address);
				
			} else {
				Toast.makeText(this, "Message field is blank",
						Toast.LENGTH_LONG).show();
			}
			break;
		}

	}

}// /main activity
