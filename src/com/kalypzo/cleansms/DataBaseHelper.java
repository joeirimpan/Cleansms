package com.kalypzo.cleansms;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.kalypzo.cleansms.model.ConvItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DataBaseHelper extends SQLiteOpenHelper {

	// public static final String SMS_URI =
	// “/data/data/org.secure.sms/databases/”;
	public static final String db_name = "favorites";
	private static final String TABLE_MESS = "Mymessage";
	private static final String KEY_ID = "id";
	private static final String KEY_REC = "recipientid";

	

	public static final int version = 1;
	Context context;

	public DataBaseHelper(Context context) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MESS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_REC + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	public boolean checkDataBase(String db) {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = "data/data/" + context.getPackageName()
					+ "/databases/" + db;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does’t exist yet.

		} catch (Exception e) {

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion >= newVersion)
			return;

		if (oldVersion == 1) {
			Log.d("New Version", "Datas can be upgraded");
		}

		Log.d("SampleData", "onUpgrade:" + newVersion);
	}

	@SuppressLint("SimpleDateFormat")
	public ArrayList<ConvItem> getAllMessages() {

		ArrayList<ConvItem> messageList = new ArrayList<ConvItem>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MESS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ConvItem contact = new ConvItem();
				Date datenew = new Date(Integer.parseInt(cursor.getString(0)));
				String fdate = new SimpleDateFormat(" " + "dd/MM/yyyy")
						.format(datenew);
				contact.setDate(fdate);
				contact.setBody(cursor.getString(1));
				contact.setAddress(cursor.getString(2));
				// Adding contact to list
				messageList.add(contact);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		// return contact list*/
		return messageList;
	}

	void addContact(String rec) {
		SQLiteDatabase db = this.getWritableDatabase();
		InsertHelper ih = new InsertHelper(db, TABLE_MESS);
	/*	ContentValues values = new ContentValues();
		values.put(KEY_NAME, contact.getName()); // Contact Name
		values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
		values.put(KEY_DATE, contact.getDate());
        values.put(KEY_PHOTO,contact.getPhotoUri());
        values.put(KEY_REC,contact.getRec());
		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
		*/
		
        final int yColumn=ih.getColumnIndex(KEY_REC);
        
        try {
            
                // ... Create the data for this row (not shown) ...
 
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
 
                
                ih.bind(yColumn,rec);
               
                
 
                // Insert the row into the database.
                ih.execute();
            
        }
        finally {
            ih.close();  // See comment below from Stefan Anca
        }
        db.close();
	}

	
	@SuppressLint("SimpleDateFormat")
	public ArrayList<String> getAllContacts() {
		
		ArrayList<String> contactList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MESS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToLast()) {
			do {
				String recid=cursor.getString(1);
				
				contactList.add(recid);
			} while (cursor.moveToPrevious());
		}
          cursor.close();
          db.close();
		// return contact list
		return contactList;
	}
	
public boolean checkDBIsNull() {
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MESS + "", null); 
		
		if (cur != null) { 
			cur.moveToFirst(); 
			//System.out.println("record : " + cur.getInt(0)); 
			if (cur.getInt(0) == 0) {
				//System.out.println("Table is Null"); 
			cur.close(); 
			return true; } 
			cur.close(); }
		else { 
			//System.out.println("Cursor is Null");
			return true;
		}
		//System.out.println("Table Not Null");
		return false;
	}

//Deleting single contact
	public void deleteContact() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MESS, null,
				null);
		
		db.close();
	}


}
