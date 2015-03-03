package com.kalypzo.cleansms;

import java.util.ArrayList;

import sms.kalypzo.cleansms.R;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import com.kalypzo.cleansms.adapter.CustomCursorAdapter;
import com.kalypzo.cleansms.model.ConvItem;
import com.kalypzo.cleansms.sendsms.SMS;
import com.kalypzo.ui.fab.FloatingActionButton;

public class InboxLoaderFragment extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener{

	public static FloatingActionButton floatingActionButton;
	public ArrayList<ConvItem> msgList = new ArrayList<ConvItem>();
	private static final int LOADER_ID = 1;// identify which loader
	LoaderManager lm;
	CustomCursorAdapter mAdapter;
	BounceListView lv;
	
	//ImageButton btn;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	public InboxLoaderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootview = inflater.inflate(R.layout.inboxloaderfragment,
				container, false);

		lv = (BounceListView) rootview.findViewById(R.id.list);
		setHasOptionsMenu(true); 
		mAdapter = new CustomCursorAdapter(getActivity(), null, 0);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
		
		floatingActionButton = (FloatingActionButton)rootview.findViewById(R.id.button_floating_action);
		floatingActionButton.attachToListView(lv);
		floatingActionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(getActivity(), SMS.class);
				startActivity(i);
			}
		});
		
		
		/* ListView listView =lv;
	        // Create a ListView-specific touch listener. ListViews are given special treatment because
	        // by default they handle touches for their list items... i.e. they're in charge of drawing
	        // the pressed state (the list selector), handling list item clicks, etc.
	        SwipeDismissListViewTouchListener touchListener =
	                new SwipeDismissListViewTouchListener(
	                        listView,
	                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
	                            @Override
	                            public boolean canDismiss(int position) {
	                                return true;
	                            }

	                            @Override
	                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
	                                for (final int position : reverseSortedPositions) {
	                                   // mAdapter.remove(mAdapter.getItem(position));
	                                	
	                                	AlertDialog.Builder adb=new AlertDialog.Builder(getActivity()); 
	                                    adb.setTitle("Delete Conversation?");
	                                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                                        public void onClick(DialogInterface dialog, int id) {
	                                           
	                                        	
	                                        }
	                                    });
	                                    adb.setNegativeButton("Cancel", null); 
	                                    adb.show();
	                                	
	                                	
	                                	listView.invalidateViews();
	                                }
	                                mAdapter.notifyDataSetChanged();
	                            }
	                        });
	        listView.setOnTouchListener(touchListener);
	        // Setting this scroll listener is required to ensure that during ListView scrolling,
	        // we don't look for swipes.
	        listView.setOnScrollListener(touchListener.makeScrollListener());
		*/
		mCallbacks = this;
		lm = getLoaderManager();
		// Initiating the loader
		lm.initLoader(LOADER_ID, null, mCallbacks);

		return rootview;
	}

	@Override 
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.search, menu); 
	    //actionbar restrictions for seearchview
	    
	   // SearchView searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
	   // searchView.setOnQueryTextListener(queryListener);
	    super.onCreateOptionsMenu(menu,inflater);
	}
	
	
	
	private String grid_currentQuery = null; // holds the current query...

	final private OnQueryTextListener queryListener = new OnQueryTextListener() {       

	    @Override
	    public boolean onQueryTextChange(String newText) {
	        if (TextUtils.isEmpty(newText)) {
	           // getActivity().getActionBar().setSubtitle("List");               
	            grid_currentQuery = null;
	        } else {
	           // getActivity().getActionBar().setSubtitle("List - Searching for: " + newText);
	            grid_currentQuery = newText;

	        }   
	        getLoaderManager().restartLoader(0, null,mCallbacks); 
	        return false;
	    }

	    @Override
	    public boolean onQueryTextSubmit(String query) {            
	      //  Toast.makeText(getActivity(), "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
	        return false;
	    }
	};
	
	
	
	
	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0,
			Bundle arg1) {

		final String SMS_ALL = "content://sms/";
        Uri uri = Uri.parse(SMS_ALL);
        String[] projection = new String[]{"_id", "thread_id", "address", "person", "body", "date", "type"};
		
		String[] grid_columns = new String[] {"body"};
		String grid_whereClause = "body LIKE ?";
		
		Uri baseUri = Uri.parse("content://mms-sms/conversations?simple=true");

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		if (!TextUtils.isEmpty(grid_currentQuery)) {            
	        //return new CursorLoader(getActivity(), baseUri,grid_columns, grid_whereClause, new String[] { grid_currentQuery + "%" },"date desc");
			return new CursorLoader(getActivity(),uri,grid_columns, grid_whereClause, new String[] { grid_currentQuery + "%" }, "date desc");
		} 
		
		return new CursorLoader(getActivity(), baseUri, null, null, null,
				"date desc");
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0,
			Cursor arg1) {
		switch (arg0.getId()) {
		case LOADER_ID:
			// The asynchronous load is complete and the data
			// is now available for use. Only now can we associate
			// the queried Cursor with the SimpleCursorAdapter.
			mAdapter.swapCursor(arg1);
			break;
		}
		// The listview now displays the queried data

	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {

		mAdapter.swapCursor(null);
	}

	
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		//Uri uriSMS =Uri.parse("content://mms-sms/conversations?simple=true");
		//Cursor cursor = getActivity().getContentResolver().query(uriSMS,null, null, null,"date desc");
		 
		// Gets the Cursor object currently bound to the ListView
        final Cursor cursor = mAdapter.getCursor();

        // Moves to the Cursor row corresponding to the ListView item that was clicked
        cursor.moveToPosition(arg2);
		
		
        //	Cursor cursor = CustomCursorAdapter.getCur();// correct cursor for the
														// position
		// arg3 is the position
		//cursor.moveToPosition((arg2));
		
		String thread_id2 = cursor.getString(cursor.getColumnIndex("_id"));
		Log.d("Recepient Id",thread_id2);
		final String number = ((TextView) arg1.findViewById(R.id.tv_num)).getText().toString();
		//String address_2 = CustomCursorAdapter.getadd(thread_id2);
		//String address_2 = getadd(thread_id2);
		//Log.d("Address",address_2);
		//String name = CustomCursorAdapter.getName(address_2);
		//String name = getName(address_2);
		new Thread(new Runnable() {
			    public void run() {
			    	setreadstate(number);
			    }
			  }).start();
		
		// Uri uriSMS =
		// Uri.parse("content://mms-sms/conversations?simple=true");
		// Cursor cur = getActivity().getContentResolver().query(uriSMS,
		// null,"_id = " + thread_id2, null, "date asc");
		// Log.d("Kalypzo","Read column is before:"+cur.getInt(cur.getColumnIndex("read")));

		/*
		 * Uri uriSMS =
		 * Uri.parse("content://mms-sms/conversations?simple=true"); Cursor cur
		 * = getActivity().getContentResolver().query(uriSMS, null,"_id = " +
		 * thread_id2, null, "date asc");
		 * Log.d("Kalypzo","Read column is before:"
		 * +cur.getInt(cur.getColumnIndex("read"))); //Long read =
		 * cursor.getLong(cursor.getColumnIndex("read")); if(cur.moveToFirst())
		 * { while(cur.moveToNext()) { if
		 * (cur.getInt(cur.getColumnIndex("read")) == 0) { ContentValues values
		 * = new ContentValues(); values.put("read",1);
		 * getActivity().getContentResolver
		 * ().update(Uri.parse("content://mms-sms/conversations?simple=true"
		 * ),values, "_id="+thread_id2, null);
		 * Log.d("Kalypzo","Read column is now:"
		 * +cur.getInt(cur.getColumnIndex("read"))); } } } cur.close();
		 */

		Intent myIntent = new Intent(getActivity().getApplicationContext(),
				ConversationLoader.class);
		myIntent.putExtra("thread_id2", thread_id2);
		myIntent.putExtra("address_2", number);
		//myIntent.putExtra("name", name);
		startActivity(myIntent);
		getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);

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
			Cursor cs;
			cs = getActivity().getContentResolver().query(Nameuri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				PhoneLookup.NUMBER + "='" + address + "'", null, null);
		
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
		}
		return contactName;
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
							"Read column before:"
									+ cur1.getInt(cur1.getColumnIndex("read")));
						ContentValues values = new ContentValues();
						values.put("read", 1);
						Uri con = Uri.parse("content://sms/inbox");
						getActivity().getContentResolver().update(con, values,"thread_id=" + thr, null);
						/*
						Cursor cur2 = getActivity().getContentResolver().query(con, null,
							"thread_id=" + thr, null, null);
						cur2.moveToFirst();
						while(cur2.moveToNext())
						Log.d("Kalypzo","Read column after updation:"
									+ cur2.getInt(cur2.getColumnIndex("read")));*/
					}
				}
			}
		} else {
			Log.d("test", "cur not working");
		}
		cur1.close();
	}
	public String getadd(String res) 
	{
		String address = null;
		Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
		Cursor curad = getActivity().getContentResolver().query(ad, null,
				"_id = " + res, null, null);
		curad.moveToFirst();
		address = curad.getString(curad.getColumnIndexOrThrow("address"));
		curad.close();
		return address;
	}

	public String getid(String address) {
		String res = null;
		Uri ad = Uri.parse("content://mms-sms/canonical-addresses/");
		Cursor cur1 = getActivity().getContentResolver().query(ad, null, null,
				null, null);
		
		if (cur1.moveToFirst()) {

			while (cur1.moveToNext()) {
				if (address.equals(cur1.getString(cur1
						.getColumnIndexOrThrow("address")))) {
					res = cur1.getString(cur1.getColumnIndexOrThrow("_id"));
				}
				
			}
		} else {
			Log.d("test", "cursor error");
		}
		Log.d("test", "ID=" + res);
		cur1.close();
		return res;
	}


}// /main activity
