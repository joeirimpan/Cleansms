package com.kalypzo.cleansms.sendsms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kalypzo.cleansms.SettingsActivity;

import sms.kalypzo.cleansms.R;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class SMS extends ActionBarActivity {
	public static StringBuilder numbers;
	ImageButton btnSendSMS;
	EditText txtPhoneNo;
	EditText txtMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		// getActionBar().setDisplayShowHomeEnabled(false); // remove app icon
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		String colorpref=SettingsActivity.getPref("color",this);
		if(colorpref.equals("blue"))
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00CFEE")));
		else if(colorpref.equals("another"))
		getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#FFAABB")));

		btnSendSMS = (ImageButton) findViewById(R.id.btnSendSMS);
		txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
		txtMessage = (EditText) findViewById(R.id.txtMessage);

		btnSendSMS.setOnClickListener(getNewSendSmsListener());

		String[] proj = { BaseColumns._ID, Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.TYPE, };
		Cursor peopleCursor = this.getContentResolver().query(
				Phone.CONTENT_URI, proj, null, null, null);
		ContactListAdapter contactadapter = new ContactListAdapter(this,
				peopleCursor);
		MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView) findViewById(R.id.txtPhoneNo);
		textView.setThreshold(1);
		textView.setAdapter(contactadapter);
		textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
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

	public static class ContactListAdapter extends CursorAdapter implements
			Filterable {
		public Context con;

		public ContactListAdapter(Context context, Cursor c) {
			super(context, c);
			con = context;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final TextView view = (TextView) inflater.inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int typeInt = cursor.getInt(3); // Phone.TYPE
			CharSequence type = Phone.getTypeLabel(con.getResources(), typeInt,
					null);
			((TextView) view).setSingleLine(false);
			((TextView) view).setText(cursor.getString(1) + "\n"
					+ cursor.getString(2) + " " + type);
		}

		@Override
		public String convertToString(Cursor cursor) {
			return (cursor.getString(1) + "(" + cursor.getString(2) + ")");
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}

			ContentResolver cr = con.getContentResolver();
			Uri uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
					constraint.toString());
			String[] proj = { BaseColumns._ID, Phone.DISPLAY_NAME,
					Phone.NUMBER, Phone.TYPE, };
			return cr.query(uri, proj, null, null, null);
		}
	}

	private OnClickListener getNewSendSmsListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				String phoneNo = txtPhoneNo.getText().toString();
				//get the address inside the parenthesis
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(phoneNo);
				String ph = null;
				String message = txtMessage.getText().toString();
				/**
				 * sending sms to multiple contacts
				 */
				while (m.find()) 
				{
					ph = m.group(1).replaceAll("-", "");
					ph.replaceAll(" ", "");
					ph.replace("+91", "");
					if (pollIsValid(ph.toString(), message)) 
					{
						sendSMS(ph.toString(), message);
						Toast.makeText(getBaseContext(), "Message is sending",
								Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(getBaseContext(),
								"Please enter both phone number and message.",
								Toast.LENGTH_SHORT).show();
					Log.d("sms", ph);
				}
			
			}
		};
	}

	private boolean pollIsValid(String phoneNo, String message) {
		return phoneNo.length() > 0 && message.length() > 0;
	}

	private void sendSMS(String phoneNumber, String message) {
		this.registerReceiver(new SmsSentReciever(), new IntentFilter(
				Constants.SENT));
		this.registerReceiver(new SmsDeliveredReciever(), new IntentFilter(
				Constants.DELIVERED));

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				Constants.SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(Constants.DELIVERED), 0);

		SmsManager.getDefault().sendTextMessage(phoneNumber, null, message,
				sentPI, deliveredPI);
	}

}
