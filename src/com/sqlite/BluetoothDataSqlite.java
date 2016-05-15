package com.sqlite;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BluetoothDataSqlite extends SQLiteOpenHelper{
	
	private static final String DB_NAME="Lumoback.db";
	
	private static final String DB_TABLE="Lumoback";
	
	private SQLiteDatabase db;
	private static final String TAG = "MyDataBase";
	
	//ʮ���ֶ�
	public static final String ID = "_id";
	public static final String startTime = "StartTime";
	public static final String endTime = "EndTime";
	public static final String heartRate = "HeartRate";
	public static final String temperature = "Temperature";
	public static final String frontAngle = "FrontAngle";
	public static final String behindAngle = "BehindAngle";
	public static final String leftAngle = "LeftAngle";
	public static final String rightAngle = "RightAngle";
	public static final String testResult = "TestResult";
	
	public BluetoothDataSqlite(Context context) {
		super(context, DB_NAME, null, 1);
		//�򿪻��½����ݿ���SQLiteDatabase����Ϊ�˶�ȡ��д������
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onCreate()");
		//�������sql���
		String sql = "CREATE TABLE " + DB_TABLE +"(" + ID +" INTEGER PRIMARY KEY AUTOINCREMENT," + startTime + " Date," + endTime +" Date," +heartRate +"INTEGER," + temperature +"FLOAT," 
		             + frontAngle +"FLOAT," +behindAngle + "FLOAT," + leftAngle + "FLOAT," + rightAngle + "FLOAT," +testResult+ "INTEGER)";
		db.execSQL(sql);
		
	}
	//�������ݿ�
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onUpgrade()");
		onCreate(db);
	}
	//�ر����ݿ�
	@Override
	public synchronized void close() {
		Log.i(TAG, "close()");
		db.close();
		super.close();
	}
	
	//��������
	public void insertData(String starttime,String endtime,int heartrate,float temperature,float frontangle,float behindangle,float leftangle,float rightangle,int testresult) {
		ContentValues values = new ContentValues();
		values.put(BluetoothDataSqlite.startTime,starttime);
		values.put(BluetoothDataSqlite.endTime,endtime);
		values.put(BluetoothDataSqlite.heartRate,heartrate);
		values.put(BluetoothDataSqlite.temperature, temperature);
		values.put(BluetoothDataSqlite.frontAngle, frontangle);
		values.put(BluetoothDataSqlite.behindAngle,behindangle);
		values.put(BluetoothDataSqlite.leftAngle,leftangle);
		values.put(BluetoothDataSqlite.rightAngle, rightangle);
		values.put(BluetoothDataSqlite.testResult,testresult);
		
		long row=db.insert(DB_TABLE, null, values);
		Log.i(TAG, "insertData row= "+row);
	}
}
