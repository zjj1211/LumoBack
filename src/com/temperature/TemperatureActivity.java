package com.temperature;

import java.util.Map;

import com.example.lumoback20160318.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TemperatureActivity extends Activity{
	//������ǩҳ���
	private EditText temperatureText;
	private Button startButton;
	private LinearLayout customSphygmomanometerLayout;
	private CustomSphygmomanometerView customSphygmoanometerView;
	private float temperature = 0;
	
	private Handler handler;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature_layout);
		
		//��ʼ���¶Ȳ��������
		initTemperatureMeasureContext();
		
		//������ť��ȡ�������ֵ�������¶ȼ���ʾ
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(temperatureText.getText().toString().equals("")||Float.valueOf(temperatureText.getText().toString())>30.0||Float.valueOf(temperatureText.getText().toString())<42.0){
					Toast.makeText(TemperatureActivity.this, "������Ϸ����¶�ֵ", Toast.LENGTH_SHORT).show();
					//���ݴ���handler
					handler = new TemperatureHandler(TemperatureActivity.this);
				}else {
					//��ʼ��ˮ�����Ķ�̬�߶�
					
					//��ȡ�����ָ���߶�
					customSphygmoanometerView.temperature = Float.valueOf(temperatureText.getText().toString());
					
					//����ģ���¶ȼ�
					customSphygmoanometerView.invalidate();
					customSphygmoanometerView.temperatureTimer.schedule(customSphygmoanometerView.temperatureTimerTask, 1000, 20);
					
					//������������
					startButton.setClickable(false);
				}
			}
			
		});
		
	}
	
	public class TemperatureHandler extends Handler {
		
		private Context context;
		
		public TemperatureHandler(Context context){
			this.context = context;	
		}
//		public void handleMessage(Message msg) {
//			//����Ϣ��Я����obj��Map<> bluetoothMeasureData
//			Map<String, String> bluetoothMeasureData = (Map) msg.obj;
//			if (bluetoothMeasureData == null || bluetoothMeasureData.isEmpty())
//				return;
//			String errorInfo = bluetoothMeasureData.get("errorInfo");
////			Log.i("�������ĸ�ѹ����", bluetoothMeasureData.get("highBloodMeasure"));
//		}

	}

	private void initTemperatureMeasureContext() {
		// TODO Auto-generated method stub
		temperatureText = (EditText)findViewById(R.id.temperatText_F);
		startButton = (Button)findViewById(R.id.startButton);
		
		//��ʼ������¶ȼ����
		customSphygmomanometerLayout = (LinearLayout)findViewById(R.id.LeftLayout);
		customSphygmoanometerView = new CustomSphygmomanometerView(this);
		customSphygmomanometerLayout.addView(customSphygmoanometerView);
	}

}
