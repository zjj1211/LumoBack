package com.lumobacksqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	//���ݿ�����
	private static final String DB_NAME= "Lumoback.db";
	//����
	private static final String DB_TABLE = "Lumoback";
	//����SQLite����
	private SQLiteDatabase db;
	private static final String DATABASE_CREATE = "create table Lumoback(_id integer primary key autoincrement,"+"starttime time,"+"endtime time,"+"heartrate integer,"+"temperature double,"
													+"frontangle double,"+"rightangle double,"+"deviationangle double,"+"testresult integer)";
	
	public DBHelper(Context mcontext) {
		super(mcontext,DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db=db;
		db.execSQL(DATABASE_CREATE);
	}
	//����
	public void insert(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(DB_TABLE, null, values);
		db.close();
	}
	//��ѯ
	public Cursor querytemp() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(DB_TABLE, new String[]{"temperature"}, "_id=(select max(_id) from Lumoback)", null, null, null, null);
		return c;
	}
	//ɾ��
	public void delecte(int id)
	{
		if(db==null)
			db=getWritableDatabase();
		db.delete(DB_TABLE, "_id=?",new String[] {String.valueOf(id)});
	}
	//�ر����ݿ�
	public void close() {
		if(db!=null)
			db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	

}
