package com.person;

import com.example.lumoback20160318.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class PersonView extends Activity{
	private TextView textview0001;
	private EditText edittext0001;
	private TextView textview0002;
	private EditText edittext0002;
	private TextView textview0003;
	private EditText edittext0003;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_layout);
		
		textview0001 = (TextView)findViewById(R.id.textview0001);
		edittext0001 = (EditText)findViewById(R.id.edittext0001);
		
		textview0002 = (TextView)findViewById(R.id.textview0002);
		edittext0002 = (EditText)findViewById(R.id.edittext0002);
		
		textview0003 = (TextView)findViewById(R.id.textview0003);
		edittext0003 = (EditText)findViewById(R.id.edittext0003);
	}

}
