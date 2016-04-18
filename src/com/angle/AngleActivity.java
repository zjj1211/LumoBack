package com.angle;

import com.example.lumoback20160318.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AngleActivity extends Activity{
	private TextView textview001;
	private EditText edittext001;
	private TextView textview002;
	private EditText edittext002;
	private TextView textview003;
	private EditText edittext003;
	private TextView textview004;
	private EditText edittext004;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.angle_layout);
		
		textview001 = (TextView)findViewById(R.id.textview001);
		edittext001 = (EditText)findViewById(R.id.edittext001);
		
		textview002 = (TextView)findViewById(R.id.textview002);
		edittext002 = (EditText)findViewById(R.id.edittext002);
		
		textview003 = (TextView)findViewById(R.id.textview003);
		edittext003 = (EditText)findViewById(R.id.edittext003);
		
		textview004 = (TextView)findViewById(R.id.textview004);
		edittext004 = (EditText)findViewById(R.id.edittext004);
	}

}
