package com.temperature;

import java.util.Map;
import com.temperature.PullToRefreshLayout.OnRefreshListener;

import com.example.lumoback20160318.R;
import com.heartrate.*;
import com.lumobacksqlite.DBHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TemperatureActivity extends Activity{
	//声明标签页组件
	private TextView temperatureText;
	private LinearLayout customSphygmomanometerLayout;
	private CustomSphygmomanometerView customSphygmoanometerView;
	private double temperature;
	
	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature_layout);
		temperatureText = (TextView)findViewById(R.id.content_view);
		((PullToRefreshLayout)findViewById(R.id.refresh_view)).
		setOnRefreshListener(new MyListener());	
		}
	
	class MyListener implements OnRefreshListener {

		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			// TODO Auto-generated method stub
			new Thread() {
				public void run() {
					try{
						//数据库查询
						final DBHelper helper = new DBHelper(TemperatureActivity.this);
						//查询数据，获取游标
						final Cursor c = helper.querytemp();
						System.out.println("Cursor c = "+c);
						c.moveToFirst();
						
						Thread.sleep(2000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(c!=null){
									temperatureText.setText(c.getString(0));
									System.out.println("c(0) = "+ c.getString(0));
								}
								c.close();
								pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
							} 
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
			
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			// TODO Auto-generated method stub
			
		}}
		
		
		
		//初始化温度测量的组件
//		initTemperatureMeasureContext();
		
//		System.out.println("kokoko");
//		Intent intent = getIntent();
//		Bundle bundle = intent.getExtras();
//		temperature = bundle.getDouble("temper");
//		ViewTemperature(temperature);
		
		
		
		
		
		//启动按钮获取输入的数值，启动温度计显示
//		startButton.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(temperatureText.getText().toString().equals("")||Float.valueOf(temperatureText.getText().toString())>30.0||Float.valueOf(temperatureText.getText().toString())<42.0){
//					Toast.makeText(TemperatureActivity.this, "请输入合法的温度值", Toast.LENGTH_SHORT).show();
//					//数据处理handler
//					handler = new TemperatureHandler(TemperatureActivity.this);
//				}else {
//					//初始化水银柱的动态高度
//					
//					//获取输入的指定高度
//					customSphygmoanometerView.temperature = Float.valueOf(temperatureText.getText().toString());
//					
//					//启动模拟温度计
//					customSphygmoanometerView.invalidate();
//					customSphygmoanometerView.temperatureTimer.schedule(customSphygmoanometerView.temperatureTimerTask, 1000, 20);
//					
//					//不能连续启动
//					startButton.setClickable(false);
//				}
//			}
//			
//		});
//		
		}
		
		
//	@Override
//	public void onResume() {
//		super.onResume();
//		HeartRateActivity HRA = new HeartRateActivity();
//		temperature=HRA.temper;
//		System.out.println("temperature == "+ temperature);
//		ViewTemperature(temperature);
//	};
	
//	public void ViewTemperature(Double temperature) {
//		String text = String.valueOf(temperature);
//		temperatureText.setText(text);
//	}
	
//	public class TemperatureHandler extends Handler {
//		
//		private Context context;
//		
//		public TemperatureHandler(Context context){
//			this.context = context;	
//		}
//		public void handleMessage(Message msg) {
//			//把消息上携带的obj给Map<> bluetoothMeasureData
//			Map<String, String> bluetoothMeasureData = (Map) msg.obj;
//			if (bluetoothMeasureData == null || bluetoothMeasureData.isEmpty())
//				return;
//			String errorInfo = bluetoothMeasureData.get("errorInfo");
////			Log.i("测量到的高压数据", bluetoothMeasureData.get("highBloodMeasure"));
//		}

//	}

//	private void initTemperatureMeasureContext() {
//		// TODO Auto-generated method stub
////		temperatureText = (TextView)findViewById(R.id.temperatText_F);
////		TextView startButton = (TextView)findViewById(R.id.startButton);
//		
//		//初始化左侧温度计组件
//		customSphygmomanometerLayout = (LinearLayout)findViewById(R.id.LeftLayout);
//		customSphygmoanometerView = new CustomSphygmomanometerView(this);
//		customSphygmomanometerLayout.addView(customSphygmoanometerView);
//	}

//}
