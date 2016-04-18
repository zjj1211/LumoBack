package com;

import com.example.lumoback20160318.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class WelcomeActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.welcome, null);
		setContentView(view);
		
		//渐变显示启动屏
		AlphaAnimation aa= new AlphaAnimation(0.2f,1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
		});
	}
	//跳转
	private void redirectTo() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,MainTabHost.class);
		startActivity(intent);
		finish();
	}

}
