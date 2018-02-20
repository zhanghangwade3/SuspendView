package com.jaycee.suspendview;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button mButtonShow;
	private Button mButtonHide;
	private static Intent service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mButtonShow = (Button) findViewById(R.id.button_show);
		mButtonHide = (Button) findViewById(R.id.button_hide);
		
		mButtonShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				service = new Intent(MainActivity.this, SuspendViewService.class);
				startService(service);
			}
		});
		
		mButtonHide.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService(service);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
