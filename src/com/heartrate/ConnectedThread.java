package com.heartrate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

/*
 * ����ֻʵ�������ݵĽ��գ��������ݵķ�������ʵ��
 * 
 * */
public class ConnectedThread extends Thread {
	int READ = 1;  //���ڴ�������Сѧ���е�ʶ����
	
	final BluetoothSocket mmSocket;
    final InputStream mmInStream;
    final OutputStream mmOutStream;
    //���캯��
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream(); //��ȡ������
            tmpOut = socket.getOutputStream();  //��ȡ�����
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
            	 bytes = mmInStream.read(buffer); //bytes���鷵��ֵ��Ϊbuffer����ĳ���
                 // Send the obtained bytes to the UI activity
            	 String str = new String(buffer);
            	 System.out.println("���ܵ������ݣ�"+str);
//            	 flat = byteToInt(buffer);   //��һ������ʵ������ת������byte��int
//                 handler.obtainMessage(READ, bytes, -1, str)
//                         .sendToTarget();     //ѹ����Ϣ����
                 
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