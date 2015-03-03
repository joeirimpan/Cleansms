package com.kalypzo.cleansms;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sms.kalypzo.cleansms.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kalypzo.cleansms.adapter.Contactadapter;
import com.kalypzo.cleansms.model.Contact;
import com.kalypzo.cleansms.model.ConvItem;
import com.kalypzo.cleansms.sendsms.SMS;
import com.kalypzo.cleansms.sendsms.SMSreceiver;
import com.kalypzo.spambayes.SpamFilter;
import com.kalypzo.ui.fab.FloatingActionButton;

public class Prim extends Fragment implements OnItemClickListener{
	
	public ArrayList<ConvItem> msgList;
	public static FloatingActionButton floatingActionButton;
	//ImageButton btn;
	MyApp n = new MyApp();
	BounceListView ls;
	public Contactadapter adapter;
	Dialog dialog = null;
	public static ArrayList<String> dom = new ArrayList<String>();
	private ProgressDialog mProgressDialog;
	String[] stnum;
	static String thread_id;
	String[] stidnum;
	SMSreceiver s;
	SpamFilter filter;
	public static int k;
	public static int l;
	String id;

	public static Integer j;
	String[] numbers;
	public static int c;
	static private ArrayList<String> numb = new ArrayList<String>();
	static public ArrayList<String> send=new ArrayList<String>();
	
	public Prim(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootview = inflater.inflate(R.layout.primlist, container, false);
		ls = (BounceListView) rootview.findViewById(R.id.listPrim);
		//btn.setOnClickListener(this);
		
		DatabaseHandler db = new DatabaseHandler(getActivity());
		 DataBaseHelperThree d=new DataBaseHelperThree(getActivity());
		 Myfilter b= new Myfilter();
		 s=new SMSreceiver();
		 numb=s.getnumids();
		 String kola;
		 
		 if(numb.size()!=0)
		 {
		kola= numb.get(0);
		Log.d("the number is :",numb.get(0));
		 }
    	stnum=new String[numb.size()];
  	    // stnum=numb.toArray(stnum);
    	for(int x=0;x<numb.size();++x)
    	stnum[x]=numb.get(x);
   //	stnum=new String[numb.size()];
   	
   	
   	
   	stidnum=new String[numb.size()];
   
   	for(int j=0;j<numb.size();++j)
   	{  
   		stidnum[j]=getid(stnum[j]);
   		 Log.d("its :",stidnum[0]);
   	}
  	  
  	
  	  if(stnum.length!=0)
  	  {
  		 // Toast.makeText(getActivity(),stnum[0], Toast.LENGTH_LONG).show();
  	  }
		 
		 
	        /**
	         * CRUD Operations
	         * */
		// InboxLoaderFragment s=new InboxLoaderFragment();
	   //  LoadProjects t=new LoadProjects(); 
		 /** Creating a progress dialog window */
		  
	        mProgressDialog = new ProgressDialog(getActivity());
	        
	        /** Close the dialog window on pressing back button */
			mProgressDialog.setCancelable(true);
			
			/** Setting a horizontal style progress bar */
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			
	     registerForContextMenu(ls);
		 new LoadProjects().execute();
		 
		 floatingActionButton = (FloatingActionButton)rootview.findViewById(R.id.button_floating_action);
		 floatingActionButton.attachToListView(ls);
		 floatingActionButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent();
					i.setClass(getActivity(), SMS.class);
					startActivity(i);
				}
			});
		
		ls.setOnItemClickListener(this);
		return rootview;
	}
	
	//convert to byte array
	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	@SuppressLint("SimpleDateFormat")
	public ArrayList<ConvItem> getSMS() {

		ArrayList<ConvItem> ConvItems = new ArrayList<ConvItem>();
		Uri uriSMSURI = Uri
				.parse("content://mms-sms/conversations?simple=true");
		Cursor cur = getActivity().getContentResolver().query(uriSMSURI, null,
				null, null, "date desc");
		cur.moveToFirst();
		while (!cur.isAfterLast()) {
			ConvItem ConvItem = new ConvItem();
			String contactName = null;
			Long contactID = null;
			String address = null, body = null, res = null;
			Long date = null;
			Uri uriphotoid = null;
			int read;
			// to obtain address from canonical-addresses
			res = cur.getString(cur.getColumnIndex("recipient_ids"));
			if(res.split(" ").length==1)
			{
			address = getadd(res);
			body = cur.getString(cur.getColumnIndexOrThrow("snippet"));
			date = cur.getLong(cur.getColumnIndexOrThrow("date"));
			read = cur.getInt(cur.getColumnIndexOrThrow("read"));
			Date datenew = new Date(date);
			String formatted_date = new SimpleDateFormat("  " + "dd/MM/yyyy")
					.format(datenew);
			String thread_id = cur.getString(cur.getColumnIndexOrThrow("_id"));
			// retrieve contact name and contact id
			Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(address));
			Cursor cs = getActivity().getContentResolver().query(Nameuri,
					new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
					PhoneLookup.NUMBER + "='" + address + "'", null, null);
			if (cs.getCount() > 0) {
				while (cs.moveToNext()) {
					contactName = cs.getString(cs
							.getColumnIndex(PhoneLookup.DISPLAY_NAME));
					contactID = cs.getLong(cs.getColumnIndex(PhoneLookup._ID));
				}
			} else {
				contactID = null;
				contactName = address;
			}
			cs.close();
			if (contactID == null)
				uriphotoid = null;
			else
				uriphotoid = getPhotoUri(contactID);

			ConvItem.setDisplayName(contactName);
			ConvItem.setThreadId(thread_id);
			ConvItem.setAddress(address);
			ConvItem.setPhotoUri(uriphotoid);
			ConvItem.setDate(formatted_date);
			ConvItem.setBody(body);
			ConvItem.setRead(read);
			
			ConvItems.add(ConvItem);
			}
			cur.moveToNext();
		}
		cur.close();
		return ConvItems;

	}

	public String getadd(String res) {
		String address = null;
		Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
		Cursor curad = getActivity().getContentResolver().query(ad, null,
				"_id = " + res, null, null);
		curad.moveToFirst();
		address = curad.getString(curad.getColumnIndexOrThrow("address"));
		curad.close();
		return address;
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
		// cursor.close();
		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		return Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	private class LoadProjects extends AsyncTask<String, String, String> {

		private ProgressDialog Dialog;

		LoadProjects() {
			Dialog = new ProgressDialog(getActivity());
		}

		@Override
		protected void onPreExecute() {
			// showLoader("Loading...");

			super.onPreExecute();

			Dialog.setMessage("Loading");
			Dialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			DatabaseHandler db = new DatabaseHandler(getActivity());
			DataBaseHelperThree d = new DataBaseHelperThree(getActivity());

			Contact cont[] = new Contact[1000];
			Contact contact[] = new Contact[1000];
			l = 0;
			k = 0;
			// Create a new SpamFilter Object
			filter = new SpamFilter(getActivity());

			Myfilter b = new Myfilter();

			if (db.checkDBIsNull() && d.checkDBIsNull()) 
			{

				try {
					Log.d("Kalypzo :", "trainspam and traingood");
					// Train spam with a file of spam sms
					filter.trainSpam();
					// Train spam with a file of regular sms
					filter.trainGood();
					// We are finished adding words so finalize the results
					filter.finalizeTraining();
				 }catch (IOException e){
					e.printStackTrace();
				 }
				Log.d("Insert: ", "Inserting ..");

				msgList = getSMS();
				
				Log.d("Kalypzo :", "time");
				for (int i = 0; i < msgList.size(); i++) {
					ConvItem m = msgList.get(i);
					byte[] photo = null;
					if (b.contactExists(getActivity(), m.getAddress())) //primary
					{
						try {
							
							if(m.getPhotoUri()==null)
								photo=null;
							else
							{
								Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(m.getPhotoUri()));
								photo = getBytes(bitmap);
							}
							Log.d("read",Integer.toString(m.getRead()));
							cont[k] = new Contact(m.getDisplayName(),
									m.getAddress(), m.getBody(), photo,
									m.getRec(), m.getRead(), m.getDate());
							++k;
						} catch (FileNotFoundException e) {
							cont[k] = new Contact(m.getDisplayName(),
									m.getAddress(), m.getBody(), null,
									m.getRec(), m.getRead(), m.getDate());
							++k;
						}

					} else//analyze body if not in contacts
						
					{
						Log.d("kalypzoo", "Analyzing the body now..");
						Log.d("Number",m.getAddress());
						boolean spam=false;
						if(m.getBody()!=null)
						spam = filter.analyze(m.getBody()+"  "+m.getDisplayName());
						else
							Log.d("kalypzoo", "body is null");
						if (spam) {
							contact[l] = new Contact(m.getDisplayName(),
									m.getAddress(), m.getBody(), null,
									m.getRec(), m.getRead(), m.getDate());
							++l;
						} else {
							cont[k] = new Contact(m.getDisplayName(),
									m.getAddress(), m.getBody(), null,
									m.getRec(), m.getRead(), m.getDate());
							++k;
						}

					}
					

				}

				db.addContactOne(cont, k);
				d.addContactOne(contact, l);

			} 
			
			if(stnum.length!=0)//new message
			 {   
			 Contact contac;
				ArrayList<ConvItem> msgListTwo=getSMSOne();
				for(int i=0;i<msgListTwo.size();++i)
				{
				ConvItem c=msgListTwo.get(i);
			
				if(contactExists(getActivity(),c.getAddress()))
                  {
               	  
					
					
					//  try
					//  {
					  if(db.getContacttwo(c.getAddress()))
               	  contac=db.getContact(c.getAddress());
					  else
						  contac=new Contact(null,null,null,null,null,0,null); 
					  

					  
					 // }
					//  catch(IllegalArgumentException e)
					 
						// contac=new Contact(null,null,null,null,null); 
					  
               	  
               	   
               	  // if(contac.getPhoneNumber().equals(null)) {
					  if(!db.getContacttwo(c.getAddress()))
	               		{
	               		   try{
	  			        		Bitmap bitmap=BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(c.getPhotoUri()));
	  			        		byte[] photo=null;
	  			        		photo=getBytes(bitmap);
	  			        		db.addContact(new Contact(c.getDisplayName(),c.getAddress(),c.getBody(),photo,c.getRec(),c.getRead(),c.getDate()));
	  			        		
	  			        		}catch(FileNotFoundException e){
	  			        			db.addContact(new Contact(c.getDisplayName(),c.getAddress(),c.getBody(),null,c.getRec(),c.getRead(),c.getDate()));
	  			        			
	  			        		} 
               		   
	               	 }else
	               	   {   
	               		   Contact conta=contac;
	               		  
	               		   db.deleteContactone(contac.getPhoneNumber());
	               		   db.addContact(new Contact(conta.getName(),conta.getPhoneNumber(),c.getBody(),conta.getPhotoUri(),conta.getRec(),conta.getRead(),c.getDate()));
	               		  
	               	    }
               	  
                  }
				else if(db.getContacttwo(c.getAddress()))
				{
					contac=db.getContact(c.getAddress());
					Contact conta=contac;
            		   
					db.deleteContactone(contac.getPhoneNumber());
         		   db.addContact(new Contact(conta.getName(),conta.getPhoneNumber(),c.getBody(),conta.getPhotoUri(),conta.getRec(),conta.getRead(),c.getDate()));
					
					
				}
				else
				{
					boolean spam=false;
					try {
						Log.d("Kalypzo :", "trainspam and traingood");
						// Train spam with a file of spam sms
						filter.trainSpam();
						// Train spam with a file of regular sms
						filter.trainGood();
						// We are finished adding words so finalize the results
						filter.finalizeTraining();
					 }catch (IOException e){
						e.printStackTrace();
					 }
					
					Log.d("kalypzoo", "Analyzing the body now..");
					Log.d("Number",c.getAddress());
					
					if(c.getBody()!=null)
					spam = filter.analyze(c.getBody()+"  "+c.getDisplayName());
					else
						Log.d("kalypzoo", "body is null");
					if (spam) {
						
						d.addContact(new Contact(c.getDisplayName(),c.getAddress(),c.getBody(),null,c.getRec(),c.getRead(),c.getDate()));
					}

				}
				
            //      else
             //     {   
               	   
                      
               	  
               //	   Log.d("Insert: ", "Inserting ..");
               	 //  d.addContact(new Contact(c.getDisplayName(),c.getAddress(),c.getBody(),null,c.getRec(),c.getRead()));
                  //}
				}
				s.getnumids().clear();
			    
			 }
			

			//check for error here
			   if(send.size()!=0)
					   {
				          
				   String address=send.get(0);
				   id=getid(address);
				   ConvItem c=getSMSTwo();
				   Contact contac=db.getContact(address);
				   db.deleteContact(contac);
				   db.addContact(new Contact(c.getDisplayName(),
							c.getAddress(), c.getBody(),null,
							c.getRec(),c.getRead(),c.getDate()));
				   send.clear();
						   
					   }

			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// Reading all contacts
			DatabaseHandler db = new DatabaseHandler(getActivity());
			Log.d("Reading: ", "Reading all contacts..");
			List<Contact> contacts = db.getAllContacts();

			for (Contact cn : contacts) {
				String log = "Id: " + cn.getID() + " ,Name: " + cn.getName()
						+ " ,Phone: " + cn.getPhoneNumber() + " ,Uri: "
						+ cn.getPhotoUri()+" ,Read:"+cn.getRead();
				// Writing Contacts to log
				Log.d("Name: ", log);

			}
			adapter = new Contactadapter(getActivity(), contacts);
			ls.setAdapter(adapter);
			ls.setScrollingCacheEnabled(false);
			adapter.notifyDataSetChanged();
			// hideLoader();
			// progressDialog.dismiss();
			if (Dialog.isShowing()) {
				Dialog.dismiss();
			}

		}
	}

	public void showLoader(final String msg) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (dialog == null)
					dialog = new Dialog(getActivity(),
							R.style.Theme_Transparent);

				dialog.setContentView(R.layout.loading);
				dialog.setCancelable(true);
				dialog.show();
				// ImageView imgeView = (ImageView)
				// dialog.findViewById(R.id.imgeView);
				TextView tvLoading = (TextView) dialog
						.findViewById(R.id.tvLoading);
				if (msg.length() > 0)
					tvLoading.setText(msg);
				// imgeView.setBackgroundResource(R.anim.frame);
				// animationDrawable = (AnimationDrawable)
				// imgeView.getBackground();

			}
		});
	}

	private void runOnUiThread(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	protected void hideLoader() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		// AdapterView.AdapterContextMenuInfo cmi =
		// (AdapterView.AdapterContextMenuInfo) item.getMenuInfo ();

		if (item.getTitle() == "Delete") {
			DatabaseHandler db = new DatabaseHandler(getActivity());
			// Uri uriSMS = Uri.parse("content://mms-sms/conversations/");
			// Cursor cursor = getContentResolver().query(uriSMS, null, null,
			// null, "date desc");

			// cursor.moveToPosition((int) cmi.id);
			// // String thread_id2 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			// String thread_id3 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			Contact silinen = adapter.getItem(info.position);

			adapter.remove(silinen);
			adapter.notifyDataSetChanged();
			db.deleteContact(silinen);

			return super.onContextItemSelected(item);

		} else if (item.getTitle() == "Move Favorites") {
			//Toast.makeText(getActivity(), "moving to primary", Toast.LENGTH_LONG).show();
			
			
			// DatabaseHandler db = new DatabaseHandler(getActivity());
			// Uri uriSMS = Uri.parse("content://mms-sms/conversations/");
			// Cursor cursor = getContentResolver().query(uriSMS, null, null,
			// null, "date desc");

			// cursor.moveToPosition((int) cmi.id);
			// // String thread_id2 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			// String thread_id3 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			Contact silinen = adapter.getItem(info.position);
			dom.add(silinen.getPhoneNumber());
			
			

			return super.onContextItemSelected(item);

		} else if (item.getTitle() == "Move Promotion") {
			//Toast.makeText(getActivity(), "moving to social", Toast.LENGTH_LONG).show();
			DatabaseHandler db = new DatabaseHandler(getActivity());
			DataBaseHelperThree d=new DataBaseHelperThree(getActivity());
			// Uri uriSMS = Uri.parse("content://mms-sms/conversations/");
			// Cursor cursor = getContentResolver().query(uriSMS, null, null,
			// null, "date desc");

			// cursor.moveToPosition((int) cmi.id);
			// // String thread_id2 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			// String thread_id3 =
			// cursor.getString(cursor.getColumnIndex("thread_id"));
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			Contact silinen = adapter.getItem(info.position);
			Contact conta=silinen;

			adapter.remove(silinen);
			adapter.notifyDataSetChanged();
			db.deleteContact(silinen);
			 Log.d("Insert: ", "Inserting ..");
			 d.addContact(new Contact(conta.getName(),conta.getPhoneNumber(),conta.getDate(),conta.getPhotoUri(),conta.getRec(),conta.getRead(),conta.getDat()));
			 
			
			return super.onContextItemSelected(item);


		}
		//else if (item.getTitle() == "Move Social") {
			//Toast.makeText(getActivity(), "moving to promotion", Toast.LENGTH_LONG).show();
		//} 
		else {
			return false;
		}
		//return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
       
		menu.setHeaderTitle("Select The Action");
		menu.add(0, v.getId(), 0, "Delete");// groupId, itemId, order, title
		menu.add(0, v.getId(), 0, "Move Favorites");
		menu.add(0, v.getId(), 0, "Move Promotion");
		//menu.add(0, v.getId(), 0, "Move Social");
	}

	public ArrayList<String> getrecids() {
		return dom;
	}
	//invoked when new message is received
	@SuppressLint("SimpleDateFormat")
	public ArrayList<ConvItem> getSMSOne()

	{
		ArrayList<ConvItem> ConvItems = new ArrayList<ConvItem>();
		Uri uriSMSURI = Uri
				.parse("content://mms-sms/conversations?simple=true");
		for (int i = 0; i < stidnum.length; ++i) 
		{
			Cursor cur = getActivity().getContentResolver().query(uriSMSURI,
					null, "recipient_ids =" + stidnum[i], null, "date desc");
			cur.moveToFirst();
			

			while (!cur.isAfterLast()) {
				ConvItem ConvItem = new ConvItem();
				String contactName = null;
				Long contactID = null;
				String address = null, body = null, res = null;
				Long date = null;
				Uri uriphotoid = null;
				int read;
				// to obtain address from canonical-addresses
				res = cur.getString(cur.getColumnIndex("recipient_ids"));
				Log.d("XXXXnew messageXXXX",res);
				address = getadd(res);
				body = cur.getString(cur.getColumnIndexOrThrow("snippet"));
				date = cur.getLong(cur.getColumnIndexOrThrow("date"));
				read=cur.getInt(cur.getColumnIndexOrThrow("read"));
				Date datenew = new Date(date);
				String formatted_date = new SimpleDateFormat("  "
						+ "dd/MM/yyyy").format(datenew);
				String thread_id = cur.getString(cur
						.getColumnIndexOrThrow("_id"));
				// retrieve contact name and contact id
				Uri Nameuri = Uri.withAppendedPath(
						PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
				Cursor cs = getActivity().getContentResolver().query(
						Nameuri,
						new String[] { PhoneLookup.DISPLAY_NAME,
								PhoneLookup._ID },
						PhoneLookup.NUMBER + "='" + address + "'", null, null);
				if (cs.getCount() > 0) {
					while (cs.moveToNext()) {
						contactName = cs.getString(cs
								.getColumnIndex(PhoneLookup.DISPLAY_NAME));
						contactID = cs.getLong(cs
								.getColumnIndex(PhoneLookup._ID));
					}
				} else {
					contactID = null;
					contactName = address;
				}
				cs.close();
				if (contactID == null)
					uriphotoid = null;
				else
					uriphotoid = getPhotoUri(contactID);

				ConvItem.setDisplayName(contactName);
				ConvItem.setThreadId(thread_id);
				ConvItem.setAddress(address);
				ConvItem.setPhotoUri(uriphotoid);
				ConvItem.setDate(formatted_date);
				ConvItem.setBody(body);
				ConvItem.setRead(read);
				ConvItems.add(ConvItem);
				cur.moveToNext();
			}

			cur.close();
		}
		return ConvItems;

	}

	public boolean contactExists(Context context, String number) {
		// / number is the phone number
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID,
				PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,
				mPhoneNumberProjection, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return true;
			}
		} finally {
			if (cur != null)
				cur.close();
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		DatabaseHandler db = new DatabaseHandler(getActivity());
		
		
		
		final String number = ((TextView) view.findViewById(R.id.tv_num)).getText().toString();
		Log.d("Number", " : " + number);
		//final String threadid =((TextView) view.findViewById(R.id.tv_id)).getText().toString();
		//Contact contact = adapter.getItem(position);
		
		//String threadid =((TextView) view.findViewById(R.id.tv_id)).getText().toString();
		//Log.d("Thread_id", " : " + threadid);
		//if (getid(number) != null)
			//threadid = getid(number);
		//else
			//Log.d("test", "getid returning null");
		//Contact contact=db.getContact(number);
		//String threadid=contact.getRec();
		
		new Thread(new Runnable() {
		    public void run() {
		    	setreadstate(number);
		    }
		  }).start();
		
		Contact contac=db.getContact(number);
		db.updateContact(contac);
		Contact con=db.getContact(number);
		Log.d("read is", String.valueOf(con.getRead()));
		
		List<Contact> contacts = db.getAllContacts();
		
		// save index and top position
		int index = ls.getFirstVisiblePosition();
		View v = ls.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		
		adapter = new Contactadapter(getActivity(), contacts);
		ls.setAdapter(adapter);
		ls.setScrollingCacheEnabled(false);
		adapter.notifyDataSetChanged();
		
		// restore position
		ls.setSelectionFromTop(index, top);
		
		
		String name = getName(number);
		Intent myIntent = new Intent(getActivity().getApplicationContext(),
				ConversationLoaderPrimary.class);
		//myIntent.putExtra("threadid", threadid);
		myIntent.putExtra("address", number);
		myIntent.putExtra("name", name);
		startActivity(myIntent);
		getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);
}

	public void setreadstate(String address) {
		Uri ad = Uri.parse("content://sms/inbox");
		Cursor cur1 = getActivity().getContentResolver().query(ad, null,
				null, null, null);
		if (cur1.moveToFirst()) {
			while (cur1.moveToNext()) {
				if(address.equals(cur1.getString(cur1.getColumnIndexOrThrow("address"))))
				{
					Log.d("Kalypzoifaddressequal","Read column before:"
								+ cur1.getInt(cur1.getColumnIndex("read")));
					if (cur1.getInt(cur1.getColumnIndex("read")) == 0)
					{
						String thr = cur1.getString(cur1.getColumnIndex("thread_id"));
						Log.d("Kalypzo",
							"Read column inside if statement:"
									+ cur1.getInt(cur1.getColumnIndex("read")));
						ContentValues values = new ContentValues();
						values.put("read", 1);
						Uri con = Uri.parse("content://sms/inbox");
						getActivity().getContentResolver().update(con, values,"thread_id=" + thr, null);
						
						Cursor cur2 = getActivity().getContentResolver().query(con, null,
							"thread_id=" + thr, null, null);
						cur2.moveToFirst();
						while(cur2.moveToNext())
						Log.d("Kalypzo","Read column after updation:"
									+ cur2.getInt(cur2.getColumnIndex("read")));
					}
				}
			}
		} else {
			Log.d("test", "cur not working");
		}
		cur1.close();
	}
	
	public String getName(String address)// For title of conversation class
	{
		Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(address));
		Cursor cs = getActivity().getContentResolver().query(Nameuri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				PhoneLookup.NUMBER + "='" + address + "'", null, null);
		String contactName = null;
		if (cs.getCount() > 0) {
			while (cs.moveToNext()) {
				contactName = cs.getString(cs
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} else {
			contactName = address;
		}
		cs.close();
		return contactName;
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
	
	public ArrayList<String> getSend()
	{
		return send;
	}
	
	@SuppressLint("SimpleDateFormat")
	public ConvItem getSMSTwo()

	{      	ConvItem ConvItem = new ConvItem();
		//ArrayList<ConvItem> ConvItems = new ArrayList<ConvItem>();
		Uri uriSMSURI = Uri
				.parse("content://mms-sms/conversations?simple=true");
		
			Cursor cur = getActivity().getContentResolver().query(uriSMSURI,
					null, "recipient_ids =" + id, null, "date desc");
			cur.moveToFirst();

			while (!cur.isAfterLast()) {
				
				String contactName = null;
				Long contactID = null;
				String address = null, body = null, res = null;
				Long date = null;
				Uri uriphotoid = null;
				int read;
				// to obtain address from canonical-addresses
				res = cur.getString(cur.getColumnIndex("recipient_ids"));
				address = getadd(res);
				body = cur.getString(cur.getColumnIndexOrThrow("snippet"));
				date = cur.getLong(cur.getColumnIndexOrThrow("date"));
				read=cur.getInt(cur.getColumnIndexOrThrow("read"));
				Date datenew = new Date(date);
				String formatted_date = new SimpleDateFormat("  "
						+ "dd/MM/yyyy").format(datenew);
				String thread_id = cur.getString(cur
						.getColumnIndexOrThrow("_id"));
				
				// retrieve contact name and contact id
				Uri Nameuri = Uri.withAppendedPath(
						PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
				Cursor cs = getActivity().getContentResolver().query(
						Nameuri,
						new String[] { PhoneLookup.DISPLAY_NAME,
								PhoneLookup._ID },
						PhoneLookup.NUMBER + "='" + address + "'", null, null);
				if (cs.getCount() > 0) {
					while (cs.moveToNext()) {
						contactName = cs.getString(cs
								.getColumnIndex(PhoneLookup.DISPLAY_NAME));
						contactID = cs.getLong(cs
								.getColumnIndex(PhoneLookup._ID));
					}
				} else {
					contactID = null;
					contactName = address;
				}
				cs.close();
				if (contactID == null)
					uriphotoid = null;
				else
					uriphotoid = getPhotoUri(contactID);

				ConvItem.setDisplayName(contactName);
				ConvItem.setThreadId(thread_id);
				ConvItem.setAddress(address);
				ConvItem.setPhotoUri(uriphotoid);
				ConvItem.setDate(formatted_date);
				ConvItem.setBody(body);
				ConvItem.setRead(read);
				
//				ConvItems.add(ConvItem);
				cur.moveToNext();  //CHECK THIS
			}

			cur.close();
		
		return ConvItem;

	}

	@Override
	public void onResume() {
		
		// TODO Auto-generated method stub
		super.onResume();
		DatabaseHandler db = new DatabaseHandler(getActivity());
		//Toast.makeText(getActivity(),"on resume",Toast.LENGTH_LONG).show();
		//check for error here
		   if(send.size()!=0)
				   {
			          
					   String address=send.get(0);
					   id=getid(address);
					   ConvItem c=getSMSTwo();
					   Contact contac=db.getContact(address);
					   Contact conta=contac;
               		 
					   db.deleteContactone(contac.getPhoneNumber());
               		   db.addContact(new Contact(conta.getName(),conta.getPhoneNumber(),c.getBody(),conta.getPhotoUri(),conta.getRec(),conta.getRead(),conta.getDat()));
					   send.clear();
					   
				   }
		   
		   
		List<Contact> contacts = db.getAllContacts();

		// save index and top position
		int index = ls.getFirstVisiblePosition();
		View v = ls.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		
		adapter = new Contactadapter(getActivity(), contacts);
		ls.setAdapter(adapter);
		ls.setScrollingCacheEnabled(false);
		adapter.notifyDataSetChanged();

		// restore position
		ls.setSelectionFromTop(index, top);
	}
}
