package com.kalypzo.cleansms;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sms.kalypzo.cleansms.R;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.kalypzo.cleansms.adapter.Contactadapter;
import com.kalypzo.cleansms.model.Contact;
import com.kalypzo.cleansms.model.ConvItem;
import com.kalypzo.cleansms.sendsms.SMS;
import com.kalypzo.ui.fab.FloatingActionButton;

public class Promo extends Fragment  implements OnItemClickListener{

		//ImageButton btn;
		MyApp n=new MyApp();
		BounceListView ls;
		Context context;
		public Contactadapter adapter;
		public Promo(){}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootview = inflater.inflate(R.layout.primlist, container, false);
			ls = (BounceListView) rootview.findViewById(R.id.listPrim);
			//btn = (ImageButton) rootview.findViewById(R.id.message);
			//btn.setOnClickListener(this);
			registerForContextMenu(ls);
			
			FloatingActionButton floatingActionButton = (FloatingActionButton)rootview.findViewById(R.id.button_floating_action);
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
			
			
			 DataBaseHelperThree db = new DataBaseHelperThree(getActivity());
			 Myfilter b= new Myfilter();
		        /**
		         * CRUD Operations
		         * */
			 
		       
		     
			 
		 
		        // Reading all contacts
		        Log.d("Reading: ", "Reading all contacts..");
		        List<Contact> contacts = db.getAllContacts();       
		 
		        for (Contact cn : contacts) {
		            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber()+" ,Uri: "+cn.getPhotoUri();
		                // Writing Contacts to log
		        Log.d("Name: ", log);
			
		        }
		        adapter = new Contactadapter(getActivity(), contacts);
				ls.setAdapter(adapter);
				ls.setOnItemClickListener(this);
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
			Cursor cur = getActivity().getContentResolver().query(uriSMSURI, null, null, null,
					"date desc");
			cur.moveToFirst();
			while (!cur.isAfterLast()) {

				ConvItem ConvItem = new ConvItem();

				String address = null, body = null, dname = null,res=null,id_dummy=null;
				Long date=null;
				Uri uriphotoid=null;
				//to obtain address from canonical-addresses
				res = cur.getString(cur.getColumnIndex("recipient_ids"));
				Uri ad =Uri.parse("content://mms-sms/canonical-addresses/");
				Cursor curad=getActivity().getContentResolver().query(ad, null,null, null, null);
				curad.moveToFirst();
				while(!curad.isAfterLast())
				{
					id_dummy=curad.getString(curad.getColumnIndexOrThrow("_id"));
					if(id_dummy.equals(res))
					address=curad.getString(curad.getColumnIndexOrThrow("address"));
					curad.moveToNext();
				}
				curad.close();
				
				body = cur.getString(cur.getColumnIndexOrThrow("snippet"));
				date = cur.getLong(cur.getColumnIndexOrThrow("date"));
				
				Date datenew=new Date(date);
				String formatted_date=new SimpleDateFormat("  "+"dd/MM/yyyy").format(datenew);
				//person =cur.getLong(cur.getColumnIndexOrThrow("person"));
				String thread_id = cur.getString(cur.getColumnIndexOrThrow("_id"));

				Long ContactID = fetchContactIdFromPhoneNumber(address);
				uriphotoid = getPhotoUri(ContactID);
				
				dname = getcontactname(address);
				
				ConvItem.setDisplayName(dname);
				ConvItem.setThreadId(thread_id);
				ConvItem.setAddress(address);
				ConvItem.setPhotoUri(uriphotoid);
				ConvItem.setDate(formatted_date);
				ConvItem.setBody(body);
				// ConvItem.setType(type);
				ConvItems.add(ConvItem);
				cur.moveToNext();
			}
			cur.close();
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
			public boolean onContextItemSelected(MenuItem item)
			{
				if (item.getTitle() == "Delete") 
				{
					DataBaseHelperThree d=new DataBaseHelperThree(getActivity());//promo
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
							.getMenuInfo();
					Contact silinen = adapter.getItem(info.position);
					adapter.remove(silinen);
					adapter.notifyDataSetChanged();
					d.deleteContact(silinen);
					return super.onContextItemSelected(item);

				} 
				else if (item.getTitle() == "Move Favorites") 
				{
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
							.getMenuInfo();
					Contact silinen = adapter.getItem(info.position);
					Prim.dom.add(silinen.getPhoneNumber());
					return super.onContextItemSelected(item);

				}
				else if (item.getTitle() == "Move Primary") 
				{
					DatabaseHandler db = new DatabaseHandler(getActivity());//primary
					DataBaseHelperThree d=new DataBaseHelperThree(getActivity());//promo
					AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					Contact silinen = adapter.getItem(info.position);
					Contact conta=silinen;
					adapter.remove(silinen);
					adapter.notifyDataSetChanged();
					d.deleteContact(silinen);
					db.addContact(new Contact(conta.getName(),conta.getPhoneNumber(),conta.getDate(),conta.getPhotoUri(),conta.getRec(),conta.getRead(),conta.getDat()));
					return super.onContextItemSelected(item);
				}
				else 
				{
					return false;
				}
			}

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				super.onCreateContextMenu(menu, v, menuInfo);
		        menu.setHeaderTitle("Select The Action");
				menu.add(0, v.getId(), 0, "Delete");// groupId, itemId, order, title
				menu.add(0, v.getId(), 0, "Move Favorites");
				menu.add(0, v.getId(), 0, "Move Primary");
			}
			
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				// TODO Auto-generated method stub
				DataBaseHelperThree db = new DataBaseHelperThree(getActivity());
				String number = ((TextView) view.findViewById(R.id.tv_num)).getText()
						.toString();
				Log.d("Number", " : " + number);
				String threadid = null;
				if (getid(number) != null)
					threadid = getid(number);
				else
					Log.d("test", "getid returning null");
				
				setreadstate(number);
				Contact contac=db.getContact(number);
				db.updateContact(contac);
				Contact con=db.getContact(number);
				Log.d("read is", String.valueOf(con.getRead()));
				
				List<Contact> contacts = db.getAllContacts();

				adapter = new Contactadapter(getActivity(), contacts);
				ls.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				String name = getName(number);
				Intent myIntent = new Intent(getActivity().getApplicationContext(),
						ConversationLoaderPrimary.class);
				myIntent.putExtra("threadid", threadid);
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

			public String getName(String address)//For title of conversation class
			{
				Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));
				Cursor cs = getActivity().getContentResolver().query(Nameuri,
						new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
						PhoneLookup.NUMBER + "='" + address + "'", null, null);
				String contactName = null;
				if (cs.getCount() > 0) 
				{
					while (cs.moveToNext()) 
					{
						contactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
					}
				}
				else 
				{
					contactName = address;
				}
				cs.close();
				return contactName;
			}
			
			public String getid(String address)
			{
				String res = null;
				Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
				Cursor cur1 = getActivity().getContentResolver().query(ad, null,null, null, null);
				//cur1.moveToFirst();
				if(cur1.moveToFirst())
				{
					
					while(cur1.moveToNext())
					{
						if(address.equals(cur1.getString(cur1.getColumnIndexOrThrow("address"))))
						{
							res = cur1.getString(cur1.getColumnIndexOrThrow("_id"));}
						//else
						//{Log.d("test","Address not equal"+cur1.getString(cur1.getColumnIndexOrThrow("_id"))+" "+cur1.getString(cur1.getColumnIndexOrThrow("address")));}
						}
				}
				else
				{Log.d("test","cur not working");}	
				Log.d("test","ID="+res);
				cur1.close();
				return res;
			}
			
			
		
	}


