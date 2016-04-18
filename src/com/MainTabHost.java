package com;

import com.angle.AngleActivity;
import com.example.lumoback20160318.R;
import com.heartrate.HeartRateActivity;
import com.person.PersonView;
import com.temperature.TemperatureActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainTabHost extends TabActivity{
	private TabHost tabHost;
	private RadioGroup radioGroup;
	
	//Tab选项卡的图标数组
	private int[] tabIconViewArray = {R.drawable.xinlv,R.drawable.wendu,R.drawable.jiaodu,R.drawable.person};
	
	//Tab选项卡文字数组
	private String[] tabNameTextArray = {"心率","温度","角度","个人"};
	
	//Tab选项卡中的内容数组
	private Class[] tabContentClassArray = {HeartRateActivity.class,TemperatureActivity.class,AngleActivity.class,PersonView.class};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_host);
		
		//为每一个选项卡设置按钮、图标、文字、内容
		tabHost = getTabHost();
		for(int i =0; i<tabContentClassArray.length;i++) {
			TabSpec tabSpec = tabHost.newTabSpec(tabNameTextArray[i]).setIndicator(tabNameTextArray[i]).setContent(getTabItemIntent(i));
			tabHost.addTab(tabSpec);
		}
		initData();
	}
	
	
	//对选项卡上的每一个按钮进行监听，实现模块的切换
	private void initData() {
		// TODO Auto-generated method stub
		radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.RadioButton0:tabHost.setCurrentTabByTag(tabNameTextArray[0]);
				break;
				case R.id.RadioButton1:tabHost.setCurrentTabByTag(tabNameTextArray[1]);
				break;
				case R.id.RadioButton2:tabHost.setCurrentTabByTag(tabNameTextArray[2]);
				break;
				case R.id.RadioButton3:tabHost.setCurrentTabByTag(tabNameTextArray[3]);
				break;
				}
			}
		});
		((RadioButton) radioGroup.getChildAt(0)).toggle();
	}
	
	//给Tab选项卡设置Activity内容
	private Intent getTabItemIntent(int index){
		Intent intent = new Intent(this,tabContentClassArray[index]);
		return intent;
	}
}
