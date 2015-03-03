package com.kalypzo.cleansms.model;


public class Contact {
	

	//private variables
	int _id;
	String _date;
	String _name;
	String _phone_number;
	byte[] photoUri;
    private String rec=null;
    int _red;
    String _dat;
	
	// Empty constructor
	public Contact(){
		
	}
	// constructor
	public Contact(int id, String name, String _phone_number,String date,String dat){
		this._id = id;
		this._name = name;
		this._phone_number = _phone_number;
		this._date=date;
		this._dat=dat;
	}
	
	// constructor
	public Contact(String name, String _phone_number,String date,byte[] photo,String re,int red,String dat){
		this._name = name;
		this._phone_number = _phone_number;
		this._date=date;
		this.photoUri=photo;
		this.rec=re;
		this._red=red;
		this._dat=dat;
	}
	public String getDat()
	{
		return this._dat;
	}
	
	public void setDat(String dat)
	{
		this._dat=dat;
	}
	
	public String getDate()
	{
		return this._date;
	}
	
	public int getRead()
	{
		return this._red;
	}
	public void setRead(int read)
	{
		this._red=read;
	}
	
	public String setDate(String date)
	{
		return this._date=date;
	}
	// getting ID
	public int getID(){
		return this._id;
	}
	
	// setting id
	public void setID(int id){
		this._id = id;
	}
	
	// getting name
	public String getName(){
		return this._name;
	}
	
	// setting name
	public void setName(String name){
		this._name = name;
	}
	
	// getting phone number
	public String getPhoneNumber(){
		return this._phone_number;
	}
	
	// setting phone number
	public void setPhoneNumber(String phone_number){
		this._phone_number = phone_number;
	}
	
	public byte[] getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(byte[] photoUri) {
		this.photoUri = photoUri;
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
