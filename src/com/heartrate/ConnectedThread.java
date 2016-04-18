package com.heartrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

/*
 * 该类只实现了数据的接收，蓝牙数据的发送自行实现
 * 
 * */
public class ConnectedThread extends Thread {
	int READ = 1;  //用于传输数据小学队列的识别字
	
	final BluetoothSocket mmSocket;
    final InputStream mmInStream;
    final OutputStream mmOutStream;
    //构造函数
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream(); //获取输入流
            tmpOut = socket.getOutputStream();  //获取输出流
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    public void run() {
    	byte[] buffer = new byte[32];  // buffer store for the stream
        int bytes; // bytes returned from read()   
        // Keep listening to the InputStream until an exception occurs
        while (true) {        	
            try {                	
                // Read from the InputStream            
            	 bytes = mmInStream.read(buffer); //bytes数组返回值，为buffer数组的长度
                 // Send the obtained bytes to the UI activity
            	 String str = new String(buffer);
            	 System.out.println("接受到的数据："+str);
//            	 flat = byteToInt(buffer);   //用一个函数实现类型转化，从byte到int
//                 handler.obtainMessage(READ, bytes, -1, str)
//                         .sendToTarget();     //压入消息队列
                 
            } catch (Exception e) {
            	System.out.print("read error");
                break;
                
            }
        }
    }

	private int byteToInt(byte[] b) {
		// TODO Auto-generated method stub
		return (((int)b[0])+((int)b[1])*256);
	}    
}