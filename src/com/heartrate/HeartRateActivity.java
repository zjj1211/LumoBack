package com.heartrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.lumoback20160318.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class HeartRateActivity extends Activity {
	
	private TasksCompletedView mTasksView;
	
	private int heartRate;
	private int mCurrentProgress;
	
	
	//��ʱ
	private Timer timer01= new Timer();
	private TimerTask task01;
	//����
	private Timer timer = new Timer();
	private TimerTask task;
	private static int j;
	
	private static int flag = 1;
	private static int gx=0;
	private Handler handler;
	private Handler handler01;
	private String title = "pulse";
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private Context context;
	private int addX =-1;
	double addY;
	int[] xv= new int[300];
	int[] yv= new int[300];
	int[] hua = new int[]{9,10,11,12,13,14,13,12,11,10,9,8,7,6,7,8,9,10,11,10,10};
	private static final AtomicBoolean processing = new AtomicBoolean(false);
	private static int averageIndex =0;
	private static final int averageArraySize = 4;
	private static final int[] averageArray = new int[averageArraySize];
	public static int progress01=87;
	
	
	public static enum TYPE {
		GREEN, RED
	};
	
	private static TYPE currentType = TYPE.GREEN;
	
	public static TYPE getCurrent() {
		return currentType;
	}
	
	private static int beatsIndex = 0;
	private static final int beatsArraySize = 3;
	private static final int[] beatsArray = new int[beatsArraySize];
	private static double beats = 0;
	private static long startTime = 0;
	
	final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	static int REQUEST_ENABLE_BT = 1; //����������־λ
	BluetoothSocket socket = null;  //�������ݴ����socket
	public ConnectedThread thread = null; //���������豸�߳�
	
	
	private List<String> listDevices = new ArrayList<String>();
	private ArrayAdapter<String> adtDevices;
	BlueBroadcastReceiver mReceiver = new BlueBroadcastReceiver();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Button bluetooth =(Button)findViewById(R.id.button); 
		bluetooth.setOnClickListener(new MyButtonListener());    
//        ��Ӱ�ť������   �������� ��������ͨ���߳�
		Button start01 = (Button)findViewById(R.id.start);
		start01.setVisibility(View.GONE);
		start01.setOnClickListener(new StartButtonListener());
		//��ʼ��ͼ��ť
//		Button end01 = (Button)findViewById(R.id.end);
//		end01.setOnClickListener(new EndButtonListener());
		
		
		/*һ��listview������ʾ�������������豸*/
        ListView arraylistview=(ListView)findViewById(R.id.arraylistview);
        ListChooseListener L01= new ListChooseListener();
        adtDevices=new ArrayAdapter<String>(this,R.layout.array_item,listDevices);      
        arraylistview.setAdapter(adtDevices);
        arraylistview.setOnItemClickListener(L01);
        
        // Register the BroadcastReceiver  
       	IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������  
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intent); // Don't forget to unregister during onDestroy
        
        initVariable();
		initView();
		
		new Thread(new ProgressRunable()).start();
		
		//����
		context = getApplicationContext();
		
		//��ȡmain�����ϵĲ���
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		
		//������������������ϵ����е㣬������Щ�㻭������
		series = new XYSeries(title);
		
		//����һ�����ݼ���ʵ������������ͼ��
		mDataset = new XYMultipleSeriesDataset();
		
		//���㼯��ӵ�������ݼ�����
		mDataset.addSeries(series);
		
		//���¶������ߵ���ʽ�����Եȵȵ����ã�renderer�൱��һ��������ͼ������Ⱦ�ľ��
				int color = Color.GREEN;
				PointStyle style = PointStyle.CIRCLE;
				renderer = buildRenderer(color, style, true);

				//���ú�ͼ�����ʽ
				setChartSettings(renderer, "X", "Y", 0, 300, 4, 16, Color.WHITE, Color.WHITE);

				//����ͼ��
				chart = ChartFactory.getLineChartView(context, mDataset, renderer);

				//��ͼ����ӵ�������ȥ
				layout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));


				/*	       thread = new Thread(){
			    	   public void arrayList(int u) { 
			    		   ArrayList arrayList = new ArrayList();
			    		   arrayList.add(HardwareControler.readADC());   			
			   		}
			       };*/
				//�����Handlerʵ������������Timerʵ������ɶ�ʱ����ͼ��Ĺ���
				handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						//        		ˢ��ͼ��
						updateChart();
						super.handleMessage(msg);
					}
				};

				task = new TimerTask() {
					@Override
					public void run() {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				};

				timer.schedule(task, 1,20); 	
        
	}
	@Override
	public void onDestroy() {
		//��������ر�Timer
		timer.cancel();
		timer01.cancel();
		super.onDestroy();
	};
	
	
        
        /*ѡ�������豸����������*/
    	class ListChooseListener implements OnItemClickListener{

    		final Button start01=(Button)findViewById(R.id.start);
    		
    		
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			String str = listDevices.get(position);
    			String[] values = str.split("\\|");//�ָ��ַ�
    			String address=values[1];
    			Log.e("address",values[1]);
    			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);	
    			Method m;			//��������
    			try {
    				m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
    				socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
    			} catch (SecurityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (NoSuchMethodException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (InvocationTargetException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			try {
    				//socket = device.createRfcommSocketToServiceRecord(uuid); //�������ӣ��÷���������)
    				mBluetoothAdapter.cancelDiscovery();  
    				//ȡ�����������豸
    				socket.connect(); 
    				gx=3;
    				System.out.println(gx);
//    				setTitle("���ӳɹ�");
    				Toast.makeText(HeartRateActivity.this, "���ӳɹ�", Toast.LENGTH_SHORT).show();
    				start01.setVisibility(0);
    				
    			} catch (IOException e) {
    				e.printStackTrace();
    				gx=1;
//    				setTitle("����ʧ��");//Ŀǰ������ʧ�ܻᵼ�³������ANR
    			}
    			thread = new ConnectedThread(socket);  //����ͨ�ŵ��߳�
    			thread.start();
    			progress01 = thread.progress;
    			System.out.println("progress01"+progress01);
    		}
    	}
    	
		
    	
    	/*�㲥���������������������Ĺ㲥*/
    	class BlueBroadcastReceiver extends BroadcastReceiver{

    		@Override
    		public void onReceive (Context context, Intent intent) {
    			String action=intent.getAction();
    			// When discovery finds a device
    			if(action.equals(BluetoothDevice.ACTION_FOUND)){
    				// Get the BluetoothDevice object from the Intent 
    				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    				// Add the name and address to an array adapter to show in a ListView
    				String str=(device.getName() + "|" + device.getAddress());
    				listDevices.add(str);
    				adtDevices.notifyDataSetChanged();//��̬����listview
    			}
    			
    			
    		}
    		
    	}
    	
    	/*����������ť*/
    	class MyButtonListener implements OnClickListener{

    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			//���û�д���������ʱ������
    			if (!mBluetoothAdapter.isEnabled()) {
    	            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	            startActivity(enableBtIntent);
    	        }

    			 mBluetoothAdapter.startDiscovery();
    			 System.out.println("��ʼ��������");
    			
    		}
        	
        }
    	
    	/*��ͼ������ť*/
    	class StartButtonListener implements OnClickListener {
    		final Button start01=(Button)findViewById(R.id.start);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gx==3){
				start01.setVisibility(View.GONE);
				}
//				startTime = System.currentTimeMillis();
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy��-MM��dd��-HHʱmm��ss��");
//				Date date = new Date(startTime);
//				System.out.println(formatter.format(date));
				//�¿���һ���̣߳����ڼ���ʱ��
				
				final Handler handler01 = new Handler(){
					@Override
					public void handleMessage(Message msg01) {
					// TODO Auto-generated method stub
					
					if(msg01.what==2){
						flag=0;
						System.out.println("flag="+flag);
					}
					super.handleMessage(msg01);
					}
				};
				
				task01 = new TimerTask() {  
				    @Override 
				    public void run() {  
				        // TODO Auto-generated method stub  
				        Message message = new Message();  
				        message.what = 2;  
						handler01.sendMessage(message);  
				    }  
				};
			timer01.schedule(task01, 1000,1000);
			
			}
    	 }
    	
    	
//    	class EndButtonListener implements OnClickListener {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				long endTime = System.currentTimeMillis();
//				System.out.println(endTime);
//				double totalTimeInSecs = (endTime - startTime) / 1000d;
//				System.out.println(totalTimeInSecs);
//				if(totalTimeInSecs >=1){
//					flag=0;
//					System.out.println(flag);
//				}
//			}
    		
//    	}
        
    	/*
    	 * ����ֻʵ�������ݵĽ��գ��������ݵķ�������ʵ��
    	 * 
    	 * */
//    	private class ConnectedThread extends Thread {
//    		
//    		private final BluetoothSocket mmSocket;
//            private final InputStream mmInStream;
//            private final OutputStream mmOutStream;
//            //���캯��
//            public ConnectedThread(BluetoothSocket socket) {
//                mmSocket = socket;
//                InputStream tmpIn = null;
//                OutputStream tmpOut = null;
//         
//                // Get the input and output streams, using temp objects because
//                // member streams are final
//                try {
//                    tmpIn = socket.getInputStream(); //��ȡ������
//                    tmpOut = socket.getOutputStream();  //��ȡ�����
//                } catch (IOException e) { }
//         
//                mmInStream = tmpIn;
//                mmOutStream = tmpOut;
//            }
//         
//            public void run() {
//            	byte[] buffer = new byte[32];  // buffer store for the stream
//                int bytes; // bytes returned from read()   
//                // Keep listening to the InputStream until an exception occurs
//                while (true) {        	
//                    try {                	
//                        // Read from the InputStream            
//                    	 bytes = mmInStream.read(buffer); //bytes���鷵��ֵ��Ϊbuffer����ĳ���
//                         // Send the obtained bytes to the UI activity
//                    	 String str = new String(buffer);
//                    	 System.out.println("���ܵ������ݣ�"+str);
////                    	 flat = byteToInt(buffer);   //��һ������ʵ������ת������byte��int
//                         handler.obtainMessage(READ, bytes, -1, str)
//                                 .sendToTarget();     //ѹ����Ϣ����
//                         
//                    } catch (Exception e) {
//                    	System.out.print("read error");
//                        break;
//                        
//                    }
//                }
//            }
//
//			private int byteToInt(byte[] b) {
//				// TODO Auto-generated method stub
//				return (((int)b[0])+((int)b[1])*256);
//			}    
//    	}
    	
	
	//����
//	@Override
//	public void onDestroy() {
//		//��������ر�Timer
//		timer.cancel();
//		super.onDestroy();
//	};
	
	protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		//����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.RED);
//		r.setPointStyle(null);
//		r.setFillPoints(fill);
		r.setLineWidth(1);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
			double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		//�йض�ͼ�����Ⱦ�ɲο�api�ĵ�
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GREEN);
		renderer.setXLabels(20);
		renderer.setYLabels(10);
		renderer.setXTitle("Time");
		renderer.setYTitle("mmHg");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 3 );
		renderer.setShowLegend(false);
	}
	
	private void updateChart() {
		if(flag==1)
			addY=10;
		else{
			flag=1;
			if(gx<2){
			   if(hua[20]>1){
				  Toast.makeText(HeartRateActivity.this, "����������!", Toast.LENGTH_SHORT).show();
				  hua[20]=0;
			   }
			   hua[20]++;
			   return;
		}else
			hua[20]=10;
			j=0;
			
	   }
	   if(j<20){
		   addY=hua[j];
		   j++;
	   }
	   
	 //�Ƴ����ݼ��оɵĵ㼯
	 		mDataset.removeSeries(series);

	 		//�жϵ�ǰ�㼯�е����ж��ٵ㣬��Ϊ��Ļ�ܹ�ֻ������100�������Ե���������100ʱ��������Զ��100
	 		int length = series.getItemCount();
	 		int bz=0;
	 		//		addX = length;
	 		if (length > 300) {
	 			length = 300;
	 			bz=1;
	 		}
	 		addX = length;
	 		//���ɵĵ㼯��x��y����ֵȡ��������backup�У����ҽ�x��ֵ��1�������������ƽ�Ƶ�Ч��
	 		for (int i = 0; i < length; i++) {
	 			xv[i] = (int) series.getX(i) -bz;
	 			yv[i] = (int) series.getY(i);
	 		}

	 		//�㼯����գ�Ϊ�������µĵ㼯��׼��
	 		series.clear();
	 		mDataset.addSeries(series);
	 		//���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
	 		//�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�
	 		series.add(addX, addY);
	 		for (int k = 0; k < length; k++) {
	 			series.add(xv[k], yv[k]);
	 		}


	 		//�����ݼ�������µĵ㼯
	 		//		mDataset.addSeries(series);

	 		//��ͼ���£�û����һ�������߲�����ֶ�̬
	 		//����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api
	 		chart.invalidate();
	}
	
	
	private void initVariable() {
		heartRate = progress01;
		System.out.println("heartRate=="+heartRate);
		mCurrentProgress=0;
	}
	
	private void initView() {
		mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
	}
	
	class ProgressRunable implements Runnable {

		@Override
		public void run() {
			while (mCurrentProgress < heartRate) {
				mCurrentProgress += 1;
				mTasksView.setProgress(mCurrentProgress);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}


}
