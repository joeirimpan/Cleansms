package com.kalypzo.cleansms.sendsms;

import java.io.IOException;
import java.util.ArrayList;

import com.kalypzo.cleansms.MainActivity;
import com.kalypzo.spambayes.SpamFilter;

import sms.kalypzo.cleansms.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;
public class SMSreceiver extends BroadcastReceiver {
    //private final String DEBUG_TAG = getClass().getSimpleName().toString();
    private static final String ACTION_SMS_DELIVER = "android.provider.Telephony.SMS_DELIVER";
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    
    private Context mContext;
    private Intent mIntent;
    static public ArrayList<String> numb=new ArrayList<String>();
    SpamFilter filter;

    // Retrieve SMS
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;

        String action = intent.getAction();

        if(action.equals(ACTION_SMS_RECEIVED)||action.equals(ACTION_SMS_DELIVER)){

            String address=null,body=null, str = "";
          

            SmsMessage[] msgs = getMessagesFromIntent(mIntent);
            if (msgs != null) {
                for (int i = 0; i < msgs.length; i++) {
                    address = msgs[i].getOriginatingAddress();
                    body = msgs[i].getMessageBody();
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";
                    numb.add(address);
                }
            }   
            
            	ContentValues values = new ContentValues();
            	values.put("address",address);
            	values.put("body",body);
            	context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
          
            
            // ---send a broadcast intent to update the SMS received in the
            // activity---
          //  Intent broadcastIntent = new Intent();
            //broadcastIntent.setAction("SMS_RECEIVED_ACTION");
           // broadcastIntent.putExtra("sms", str);
            //context.sendBroadcast(broadcastIntent);
            
           int test =check_primary(address,body);
           if(test==1)					//if message is primary
           showNotification(mContext,str,address,body);
            
        }

    }
    
    private int check_primary(String address,String body) 
    {
    	filter = new SpamFilter(mContext);
    	if (contactExists(mContext,address)) 
		{
			return 1;
		} 
    	else
		{
    		try {
			filter.trainSpam();
			filter.trainGood();
			filter.finalizeTraining();
			 }catch (IOException e){
				e.printStackTrace();
			 }
    		boolean spam=false;
			if(body!=null)
			spam = filter.analyze(body+"  "+getName(address));
			else
				Log.d("kalypzoo", "body is null");
			if (spam) {
				return 0;
			} else {
				return 1;
			}

		}
		
		
		
	}

	public ArrayList<String> getnumids()
	{
		return numb;
	}

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    //Push Notif
    protected void showNotification(Context mContext,String str,String address,String body) {
    	//Display notification...
    	 NotificationManager notifManager = (NotificationManager) mContext
                 .getSystemService(Context.NOTIFICATION_SERVICE);

         Notification notif = new Notification(
                 R.drawable.logo, str/*smsSummary*/,
                 System.currentTimeMillis());
         notif.defaults |= Notification.DEFAULT_SOUND;
         notif.defaults |= Notification.DEFAULT_VIBRATE;
         notif.defaults |= Notification.DEFAULT_LIGHTS;
         String name = getName(address);
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

         // ...but we still need to provide and intent; an empty one will
         // suffice. Alter for your own app's requirement.

         Intent notificationIntent = new Intent(mContext,MainActivity.class);
         PendingIntent pi = PendingIntent.getActivity(mContext, 0,notificationIntent, 0);
         notif.setLatestEventInfo(mContext, "Cleansms :"+name,body, pi);
         notifManager.notify(0, notif);
    }

    public boolean contactExists(Context context, String number) {
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
		PhoneLookup.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
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




    public String getName(String address)
	{
		Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(address));
		Cursor cs =  mContext.getContentResolver().query(Nameuri,
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

}