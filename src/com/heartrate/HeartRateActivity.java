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
	
	
	//定时
	private Timer timer01= new Timer();
	private TimerTask task01;
	//曲线
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
	static int REQUEST_ENABLE_BT = 1; //蓝牙开启标志位
	BluetoothSocket socket = null;  //用于数据传输的socket
	public ConnectedThread thread = null; //连接蓝牙设备线程
	
	
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
//        添加按钮监听器   开启蓝牙 开启连接通信线程
		Button start01 = (Button)findViewById(R.id.start);
		start01.setVisibility(View.GONE);
		start01.setOnClickListener(new StartButtonListener());
		//开始绘图按钮
//		Button end01 = (Button)findViewById(R.id.end);
//		end01.setOnClickListener(new EndButtonListener());
		
		
		/*一个listview用来显示搜索到的蓝牙设备*/
        ListView arraylistview=(ListView)findViewById(R.id.arraylistview);
        ListChooseListener L01= new ListChooseListener();
        adtDevices=new ArrayAdapter<String>(this,R.layout.array_item,listDevices);      
        arraylistview.setAdapter(adtDevices);
        arraylistview.setOnItemClickListener(L01);
        
        // Register the BroadcastReceiver  
       	IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果  
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intent); // Don't forget to unregister during onDestroy
        
        initVariable();
		initView();
		
		new Thread(new ProgressRunable()).start();
		
		//曲线
		context = getApplicationContext();
		
		//获取main界面上的布局
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		
		//这个类用来放置曲线上的所有点，根据这些点画出曲线
		series = new XYSeries(title);
		
		//创建一个数据集的实例，用来创建图表
		mDataset = new XYMultipleSeriesDataset();
		
		//将点集添加到这个数据集当中
		mDataset.addSeries(series);
		
		//以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
				int color = Color.GREEN;
				PointStyle style = PointStyle.CIRCLE;
				renderer = buildRenderer(color, style, true);

				//设置好图表的样式
				setChartSettings(renderer, "X", "Y", 0, 300, 4, 16, Color.WHITE, Color.WHITE);

				//生成图表
				chart = ChartFactory.getLineChartView(context, mDataset, renderer);

				//将图表添加到布局中去
				layout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));


				/*	       thread = new Thread(){
			    	   public void arrayList(int u) { 
			    		   ArrayList arrayList = new ArrayList();
			    		   arrayList.add(HardwareControler.readADC());   			
			   		}
			       };*/
				//这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
				handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						//        		刷新图表
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
		//程序结束关闭Timer
		timer.cancel();
		timer01.cancel();
		super.onDestroy();
	};
	
	
        
        /*选择蓝牙设备并进行连接*/
    	class ListChooseListener implements OnItemClickListener{

    		final Button start01=(Button)findViewById(R.id.start);
    		
    		
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			String str = listDevices.get(position);
    			String[] values = str.split("\\|");//分割字符
    			String address=values[1];
    			Log.e("address",values[1]);
    			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);	
    			Method m;			//建立连接
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
    				//socket = device.createRfcommSocketToServiceRecord(uuid); //建立连接（该方法不能用)
    				mBluetoothAdapter.cancelDiscovery();  
    				//取消搜索蓝牙设备
    				socket.connect(); 
    				gx=3;
    				System.out.println(gx);
//    				setTitle("连接成功");
    				Toast.makeText(HeartRateActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
    				start01.setVisibility(0);
    				
    			} catch (IOException e) {
    				e.printStackTrace();
    				gx=1;
//    				setTitle("连接失败");//目前连接若失败会导致程序出现ANR
    			}
    			thread = new ConnectedThread(socket);  //开启通信的线程
    			thread.start();
    			progress01 = thread.progress;
    			System.out.println("progress01"+progress01);
    		}
    	}
    	
		
    	
    	/*广播接收器类用来监听蓝牙的广播*/
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
    				adtDevices.notifyDataSetChanged();//动态更新listview
    			}
    			
    			
    		}
    		
    	}
    	
    	/*蓝牙启动按钮*/
    	class MyButtonListener implements OnClickListener{

    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			//如果没有打开蓝牙，此时打开蓝牙
    			if (!mBluetoothAdapter.isEnabled()) {
    	            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	            startActivity(enableBtIntent);
    	        }

    			 mBluetoothAdapter.startDiscovery();
    			 System.out.println("开始搜索蓝牙");
    			
    		}
        	
        }
    	
    	/*绘图启动按钮*/
    	class StartButtonListener implements OnClickListener {
    		final Button start01=(Button)findViewById(R.id.start);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gx==3){
				start01.setVisibility(View.GONE);
				}
//				startTime = System.currentTimeMillis();
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
//				Date date = new Date(startTime);
//				System.out.println(formatter.format(date));
				//新开启一个线程，用于计算时间
				
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
    	 * 该类只实现了数据的接收，蓝牙数据的发送自行实现
    	 * 
    	 * */
//    	private class ConnectedThread extends Thread {
//    		
//    		private final BluetoothSocket mmSocket;
//            private final InputStream mmInStream;
//            private final OutputStream mmOutStream;
//            //构造函数
//            public ConnectedThread(BluetoothSocket socket) {
//                mmSocket = socket;
//                InputStream tmpIn = null;
//                OutputStream tmpOut = null;
//         
//                // Get the input and output streams, using temp objects because
//                // member streams are final
//                try {
//                    tmpIn = socket.getInputStream(); //获取输入流
//                    tmpOut = socket.getOutputStream();  //获取输出流
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
//                    	 bytes = mmInStream.read(buffer); //bytes数组返回值，为buffer数组的长度
//                         // Send the obtained bytes to the UI activity
//                    	 String str = new String(buffer);
//                    	 System.out.println("接受到的数据："+str);
////                    	 flat = byteToInt(buffer);   //用一个函数实现类型转化，从byte到int
//                         handler.obtainMessage(READ, bytes, -1, str)
//                                 .sendToTarget();     //压入消息队列
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
    	
	
	//曲线
//	@Override
//	public void onDestroy() {
//		//程序结束关闭Timer
//		timer.cancel();
//		super.onDestroy();
//	};
	
	protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		//设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
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
		//有关对图表的渲染可参看api文档
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
				  Toast.makeText(HeartRateActivity.this, "请连接蓝牙!", Toast.LENGTH_SHORT).show();
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
	   
	 //移除数据集中旧的点集
	 		mDataset.removeSeries(series);

	 		//判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
	 		int length = series.getItemCount();
	 		int bz=0;
	 		//		addX = length;
	 		if (length > 300) {
	 			length = 300;
	 			bz=1;
	 		}
	 		addX = length;
	 		//将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
	 		for (int i = 0; i < length; i++) {
	 			xv[i] = (int) series.getX(i) -bz;
	 			yv[i] = (int) series.getY(i);
	 		}

	 		//点集先清空，为了做成新的点集而准备
	 		series.clear();
	 		mDataset.addSeries(series);
	 		//将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
	 		//这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
	 		series.add(addX, addY);
	 		for (int k = 0; k < length; k++) {
	 			series.add(xv[k], yv[k]);
	 		}


	 		//在数据集中添加新的点集
	 		//		mDataset.addSeries(series);

	 		//视图更新，没有这一步，曲线不会呈现动态
	 		//如果在非UI主线程中，需要调用postInvalidate()，具体参考api
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
