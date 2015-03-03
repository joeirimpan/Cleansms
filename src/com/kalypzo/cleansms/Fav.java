package com.kalypzo.cleansms;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sms.kalypzo.cleansms.R;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.kalypzo.cleansms.adapter.CustomListViewAdapter;
import com.kalypzo.cleansms.model.ConvItem;
import com.kalypzo.cleansms.sendsms.SMS;
import com.kalypzo.ui.fab.FloatingActionButton;

public class Fav extends Fragment implements	OnClickListener{
	static String thread_id;
	BounceListView ls;
	Context context;
	static private ArrayList<String> dom=new ArrayList<String>();	
	String[] num;
	String[] numOne;
	CustomListViewAdapter adapter;
	//   I8mageButton btn;
	
	
	public Fav(){}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootview = inflater.inflate(R.layout.primlist, container, false);
		ls = (BounceListView) rootview.findViewById(R.id.listPrim);
	//	btn = (ImageButton) rootview.findViewById(R.id.message);
	//	btn.setOnClickListener(this);
		
		
		FloatingActionButton floatingActionButton = (FloatingActionButton)rootview.findViewById(R.id.button_floating_action);
		floatingActionButton.attachToListView(ls);
		
		 DatabaseHandler db = new DatabaseHandler(getActivity());
		 DataBaseHelperThree d=new DataBaseHelperThree(getActivity());
		 DataBaseHelper dbc = new DataBaseHelper(getActivity());
	        /**
	         * CRUD Operations
	         * */
		 
	       //i have added the code for adding into promotions(in the else)statement of if loop.I have done dat the delay come only when we touch "tabs" n not when we move to promotions 
		 Prim m=new Prim();
		  //dom=m.getrecids();
	     dom=dbc.getAllContacts();
		  num=new String[dom.size()];
		  num=dom.toArray(num);
		  numOne=dom.toArray(num);
		  for(int i=0;i<dom.size();++i)
			  numOne[i]=getid(num[i]);
	  
	  
	  //Toast.makeText(getActivity(),"num.length==0",Toast.LENGTH_LONG).show();
	  
	  if(num.length!=0) {
		   Log.d("first",num[0]);
	       ArrayList<ConvItem> msgList = getSMS();
	  
	    CustomListViewAdapter adapter = new CustomListViewAdapter(getActivity(), msgList);
		ls.setAdapter(adapter);
	  }	
		return rootview;
	}

	
	 public static byte[] getBytes(Bitmap bitmap) {
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        bitmap.compress(CompressFormat.PNG, 0, stream);
	        return stream.toByteArray();
	    }
	@SuppressLint("SimpleDateFormat")
	public ArrayList<ConvItem> getSMS()

	{
		ArrayList<ConvItem> ConvItems = new ArrayList<ConvItem>();
		Uri uriSMSURI = Uri.parse("content://mms-sms/conversations?simple=true");
		for(int i=0;i<dom.size();++i)
		{
		Cursor cur =getActivity().getContentResolver().query(uriSMSURI, null,"recipient_ids =" + numOne[i] ,null,
				"date desc");
		cur.moveToFirst();
		while (!cur.isAfterLast()) {

			ConvItem ConvItem = new ConvItem();

			String address = null, body = null, dname = null,res=null;
			Long date=null;
			Uri uriphotoid=null;
			
			//to obtain address from canonical-addresses
			res = cur.getString(cur.getColumnIndex("recipient_ids"));
			Uri ad =Uri.parse("content://mms-sms/canonical-addresses/");
			Cursor curad=getActivity().getContentResolver().query(ad, null,"_id = " + res, null, null);
			curad.moveToFirst();
			address=curad.getString(curad.getColumnIndexOrThrow("address"));
			
			curad.close();
			
			body = cur.getString(cur.getColumnIndexOrThrow("snippet"));
			date = cur.getLong(cur.getColumnIndexOrThrow("date"));
			
			Date datenew=new Date(date);
			String formatted_date=new SimpleDateFormat("  "+"dd/MM/yyyy").format(datenew);
			thread_id = cur.getString(cur.getColumnIndexOrThrow("_id"));

			Long ContactID = fetchContactIdFromPhoneNumber(address);
			uriphotoid = getPhotoUri(ContactID);
			
			dname = getcontactname(address);
			
			ConvItem.setDisplayName(dname);
			ConvItem.setThreadId(thread_id);
			ConvItem.setAddress(address);
			ConvItem.setPhotoUri(uriphotoid);
			ConvItem.setDate(formatted_date);
			ConvItem.setBody(body);
		    ConvItems.add(ConvItem);
			cur.moveToNext();
		}
		cur.close();
		}
		return ConvItems;

		
	}
	
public String getcontactname(String phoneNumber) {
		
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = getActivity().getContentResolver().query(uri,
				new String[] {PhoneLookup.DISPLAY_NAME},
				null, null, null);

		String contactname = null;

		if (cursor.moveToFirst()) {
			//do {
				contactname = cursor.getString((cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME)));

			//} while (cursor.moveToNext());
		}
		cursor.close();
		return contactname;
	}

//fetching contact id for contact photouri retrieval
	public Long fetchContactIdFromPhoneNumber(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = getActivity().getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);

		Long contactId = null;

		if (cursor.moveToFirst()) {
		//	do {
				contactId = cursor.getLong((cursor
						.getColumnIndex(PhoneLookup._ID)));

		//	} while (cursor.moveToNext());
		}
		cursor.close();
		return contactId;
	}
	
	// fetch photouri
		public Uri getPhotoUri(Long contactId) {
			ContentResolver contentResolver = getActivity().getContentResolver();

			try {
				Cursor cursor = contentResolver
						.query(ContactsContract.Data.CONTENT_URI,
								null,
								ContactsContract.Data.CONTACT_ID
										+ "="
										+ contactId
										+ " AND "

										+ ContactsContract.Data.MIMETYPE
										+ "='"
										+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
										+ "'", null, null);

				if (cursor != null) {
					if (!cursor.moveToFirst()) {
						return null; // no photo
					}
				} else {
					return null; // error in cursor process
				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			//cursor.close();
			Uri person = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, contactId);
			return Uri.withAppendedPath(person,
					ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		}
		
		@Override
		public void onClick(View v) {
			//switch (v.getId()) {
			//case R.id.message:
				Intent i = new Intent();
				i.setClass(getActivity(), SMS.class);
				startActivity(i);
			//	break;
			//}
			
			}
		
		public String getid(String address) {
			String res = null;
		
			Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
			Cursor cur1 = getActivity().getContentResolver().query(ad, null, null,
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
