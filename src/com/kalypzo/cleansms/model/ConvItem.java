package com.kalypzo.cleansms.model;

import android.net.Uri;
//pojo class
public class ConvItem {

	private String address = null;
	private String displayName = null;
	private String threadId = null;
	private String body = null;
	private String date=null;
    private String rec=null;
	Uri photoUri = null;
	private int red;
	
	public ConvItem()
	{
		
	}

	ConvItem(String dt,String num,String bdy,String re)
	{
		this.address=num;
		this.date=dt;
		this.body=bdy;
		this.rec=re;
	}
	
	public void setRead(int read)
	{
		this.red=read;
	}
	
	public int getRead()
	{
		return red;
	}
	
	public Uri getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getRec()
	{
		return rec;
	}
	
	public void setRec(String re)
	{
		this.rec=re;
	}
}
