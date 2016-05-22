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
	//������ǩҳ���
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
						//���ݿ��ѯ
						final DBHelper helper = new DBHelper(TemperatureActivity.this);
						//��ѯ���ݣ���ȡ�α�
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
		
		
		
		//��ʼ���¶Ȳ��������
//		initTemperatureMeasureContext();
		
//		System.out.println("kokoko");
//		Intent intent = getIntent();
//		Bundle bundle = intent.getExtras();
//		temperature = bundle.getDouble("temper");
//		ViewTemperature(temperature);
		
		
		
		
		
		//������ť��ȡ�������ֵ�������¶ȼ���ʾ
//		startButton.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(temperatureText.getText().toString().equals("")||Float.valueOf(temperatureText.getText().toString())>30.0||Float.valueOf(temperatureText.getText().toString())<42.0){
//					Toast.makeText(TemperatureActivity.this, "������Ϸ����¶�ֵ", Toast.LENGTH_SHORT).show();
//					//���ݴ���handler
//					handler = new TemperatureHandler(TemperatureActivity.this);
//				}else {
//					//��ʼ��ˮ�����Ķ�̬�߶�
//					
//					//��ȡ�����ָ���߶�
//					customSphygmoanometerView.temperature = Float.valueOf(temperatureText.getText().toString());
//					
//					//����ģ���¶ȼ�
//					customSphygmoanometerView.invalidate();
//					customSphygmoanometerView.temperatureTimer.schedule(customSphygmoanometerView.temperatureTimerTask, 1000, 20);
//					
//					//������������
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
//			//����Ϣ��Я����obj��Map<> bluetoothMeasureData
//			Map<String, String> bluetoothMeasureData = (Map) msg.obj;
//			if (bluetoothMeasureData == null || bluetoothMeasureData.isEmpty())
//				return;
//			String errorInfo = bluetoothMeasureData.get("errorInfo");
////			Log.i("�������ĸ�ѹ����", bluetoothMeasureData.get("highBloodMeasure"));
//		}

//	}

//	private void initTemperatureMeasureContext() {
//		// TODO Auto-generated method stub
////		temperatureText = (TextView)findViewById(R.id.temperatText_F);
////		TextView startButton = (TextView)findViewById(R.id.startButton);
//		
//		//��ʼ������¶ȼ����
//		customSphygmomanometerLayout = (LinearLayout)findViewById(R.id.LeftLayout);
//		customSphygmoanometerView = new CustomSphygmomanometerView(this);
//		customSphygmomanometerLayout.addView(customSphygmoanometerView);
//	}

//}
