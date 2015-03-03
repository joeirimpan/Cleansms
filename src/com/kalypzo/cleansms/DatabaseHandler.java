package com.kalypzo.cleansms;

import java.util.ArrayList;
import java.util.List;

import com.kalypzo.cleansms.model.Contact;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Contacts table name
	private static final String TABLE_CONTACTS = "contacts";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_PH_NO = "phone_number";
	private static final String KEY_DATE="date";
	private static final String KEY_PHOTO="photouri";
    private static final String KEY_REC="recipientid";
    private static final String KEY_READ="read";
    private static final String KEY_DAT="dat";
    
    
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_PH_NO + " TEXT," + KEY_DATE + " TEXT," + KEY_PHOTO + " BLOB," + KEY_REC + " TEXT," + KEY_READ + " INTEGER," + KEY_DAT + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		InsertHelper ih = new InsertHelper(db, TABLE_CONTACTS);
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
		final int greekColumn = ih.getColumnIndex(KEY_NAME);
        final int ionicColumn = ih.getColumnIndex(KEY_PH_NO);
        final int romanColumn = ih.getColumnIndex(KEY_DATE);
        final int sColumn=ih.getColumnIndex(KEY_PHOTO);
        final int yColumn=ih.getColumnIndex(KEY_REC);
        final int rColumn=ih.getColumnIndex(KEY_READ);
        final int zColumn=ih.getColumnIndex(KEY_DAT);
        try {
            
                // ... Create the data for this row (not shown) ...
 
                // Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
 
                // Add the data for each column
                ih.bind(greekColumn,contact.getName());
                ih.bind(ionicColumn,contact.getPhoneNumber());
                
                ih.bind(romanColumn, contact.getDate());
                ih.bind(sColumn,contact.getPhotoUri());
                ih.bind(yColumn,contact.getRec());
                ih.bind(rColumn,contact.getRead());
                ih.bind(zColumn,contact.getDat() );
                
 
                // Insert the row into the database.
                ih.execute();
            
        }
        finally {
            ih.close();  // See comment below from Stefan Anca
        }
        db.close();
	}

	// Getting single contact
	Contact getContact(String num) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
				KEY_NAME, KEY_PH_NO, KEY_DATE, KEY_PHOTO, KEY_REC, KEY_READ, KEY_DAT}, KEY_PH_NO + "=?",
				new String[] { num }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Contact contact = new Contact(cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getBlob(4), cursor.getString(5),cursor.getInt(6),cursor.getString(7));
		// return contact
		return contact;
	}
	
	// Getting All Contacts
	@SuppressLint("SimpleDateFormat")
	public List<Contact> getAllContacts() {
		List<Contact> contactList = new ArrayList<Contact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToLast()) {
			do {
				Contact contact = new Contact();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				
				contact.setName(cursor.getString(1));
				contact.setPhoneNumber(cursor.getString(2));
				//Date datenew=new Date(Integer.parseInt(cursor.getString(3)));
                //String fdate=new SimpleDateFormat(" "+"dd/MM/yyyy").format(datenew);
                contact.setDate(cursor.getString(3));//body
              
                contact.setPhotoUri(cursor.getBlob(4));
                contact.setRec(cursor.getString(5));
                contact.setRead(cursor.getInt(6));
                contact.setDat(cursor.getString(7));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToPrevious());
		}
          cursor.close();
          db.close();
		// return contact list
		return contactList;
	}

	// Updating single contact
	public int updateContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, contact.getName());
		values.put(KEY_PH_NO, contact.getPhoneNumber());
		values.put(KEY_DATE, contact.getDate());
		values.put(KEY_PHOTO, contact.getPhotoUri());
		values.put(KEY_REC, contact.getRec());
		values.put(KEY_READ, 1);
		values.put(KEY_DAT, contact.getDat());
		
		Log.d("machans", String.valueOf(values.get(KEY_READ)));

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?",
				new String[] { contact.getPhoneNumber() });
	}

	// Deleting single contact
	public void deleteContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.getID()) });
		db.close();
	}


	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	public boolean checkDBIsNull() {
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CONTACTS + "", null); 
		
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
	
	@SuppressWarnings({ "deprecation" })
	// Adding new contact
		void addContactOne(Contact[] contact,int size) {
		   
		   Log.d("god :", "help");
			
			SQLiteDatabase db = this.getWritableDatabase();
			InsertHelper ih = new InsertHelper(db, TABLE_CONTACTS);
			
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
			
			
			
				
			
			final int greekColumn = ih.getColumnIndex(KEY_NAME);
	        final int ionicColumn = ih.getColumnIndex(KEY_PH_NO);
	        final int romanColumn = ih.getColumnIndex(KEY_DATE);
	        final int sColumn=ih.getColumnIndex(KEY_PHOTO);
	        final int yColumn=ih.getColumnIndex(KEY_REC);
	        final int rColumn=ih.getColumnIndex(KEY_READ);
	        final int zColumn=ih.getColumnIndex(KEY_DAT);
	        try {
	        	
	        	db.beginTransaction();
	        	for(int i=size-1;i>=0;--i)
	        	{
	                // ... Create the data for this row (not shown) ...
	 
	                // Get the InsertHelper ready to insert a single row
	                ih.prepareForInsert();
	 
	                // Add the data for each column
	                ih.bind(greekColumn,contact[i].getName());
	                ih.bind(ionicColumn,contact[i].getPhoneNumber());
	                
	                ih.bind(romanColumn, contact[i].getDate());
	                ih.bind(sColumn,contact[i].getPhotoUri());
	                ih.bind(yColumn,contact[i].getRec());
	                ih.bind(rColumn,contact[i].getRead());
	                ih.bind(zColumn,contact[i].getDat());
	 
	                // Insert the row into the database.
	                ih.execute();
	        	}
	        	db.setTransactionSuccessful();
	            
	        }
	        finally {
	            ih.close();  // See comment below from Stefan Anca
	            db.endTransaction();
	        }
			
	        db.close();

		}
	
	// Getting single contact
		Contact getContactone(String num) {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
					KEY_NAME, KEY_PH_NO, KEY_DATE, KEY_PHOTO, KEY_REC, KEY_READ, KEY_DAT}, KEY_PH_NO + "=?",
					new String[] { num }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();

			Contact contact = new Contact(cursor.getString(1),
					cursor.getString(2), cursor.getString(3), cursor.getBlob(4), cursor.getString(5),cursor.getInt(6),cursor.getString(7));
			// return contact
			return contact;
		}
		
		// Deleting single contact
				public void deleteContactone(String num) {
					Log.d("ividem","workingtwo");
					SQLiteDatabase db = this.getWritableDatabase();
					db.delete(TABLE_CONTACTS, KEY_PH_NO + " = ?",
							new String[] { num });
					db.close();
				}
				
				// Getting single contact
				Boolean getContacttwo(String num) {
					SQLiteDatabase db = this.getReadableDatabase();

					Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
							KEY_NAME, KEY_PH_NO, KEY_DATE, KEY_PHOTO, KEY_REC, KEY_READ, KEY_DAT}, KEY_PH_NO + "=?",
							new String[] { num }, null, null, null, null);
					
					if(cursor.moveToFirst())
					return true;
					else
				   return false;

								
				}
		
}

