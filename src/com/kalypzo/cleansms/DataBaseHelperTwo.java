package com.kalypzo.cleansms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DataBaseHelperTwo extends SQLiteOpenHelper {

	// public static final String SMS_URI =
	// “/data/data/org.secure.sms/databases/”;
	public static final String db_name = "sms.db";

	public static final int version = 1;
	Context context;

	public DataBaseHelperTwo(Context context) {
		super(context, db_name, null, version);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table datatable(address varchar(10),date varchar(10),body varchar(10))");
		Toast.makeText(context, "database created", Toast.LENGTH_LONG).show();
		Log.i("dbcreate", "Database has created");
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

	/*
	 * public ArrayList<Messages> getAllMessages() { ArrayList<Messages>
	 * messageList = new ArrayList<Messages>(); // Select All Query String
	 * selectQuery = "SELECT  * FROM " + TABLE_MESS;
	 * 
	 * SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor =
	 * db.rawQuery(selectQuery, null);
	 * 
	 * // looping through all rows and adding to list if (cursor.moveToFirst())
	 * { do { Messages contact = new Messages(); Date datenew=new
	 * Date(Integer.parseInt(cursor.getString(0))); String fdate=new
	 * SimpleDateFormat(" "+"dd/MM/yyyy").format(datenew);
	 * contact.setDate(fdate); contact.setBody(cursor.getString(1));
	 * contact.setPhoneNumber(cursor.getString(2)); // Adding contact to list
	 * messageList.add(contact); } while (cursor.moveToNext()); }
	 * 
	 * // return contact list return messageList; }
	 */

}
