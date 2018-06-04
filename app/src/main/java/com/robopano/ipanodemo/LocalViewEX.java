package com.robopano.ipanodemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.robopano.ipanosdk.PanoViewPlugin;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;


/**
 * 自定义视图,显示全景
 * @author lijie
 *
 */
public class LocalViewEX extends CordovaActivity implements OnClickListener {
	private String loadUrl =null;
	private Button btn_save;
	private Button btn_cancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFullscreen();
		setContentView(R.layout.local_view_ex);
		super.init();
		initData();
		initEvent();
		
	}
	
	private void initEvent() {
		btn_save = (Button) findViewById(R.id.pp_btn_save);
		btn_cancel = (Button) findViewById(R.id.pp_btn_cancel);
		btn_save.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	protected CordovaWebView makeWebView() {

		SystemWebView webView = (SystemWebView) findViewById(R.id.cordovaWebView);

		return new CordovaWebViewImpl(new SystemWebViewEngine(webView));

	}

	@SuppressWarnings({ "deprecation", "ResourceType" })
	protected void createViews() {

		if (preferences.contains("BackgroundColor")) {
			int backgroundColor = preferences.getInteger("BackgroundColor",
					Color.BLACK);
			// Background of activity:
			appView.getView().setBackgroundColor(backgroundColor);
		}

		appView.getView().requestFocusFromTouch();
	}

	
	private void initData() {
		loadUrl = getIntent().getExtras().getString("group_url");
		PanoViewPlugin.path = loadUrl;
		loadUrl(launchUrl);
	}
	
	private void setResultFlag(boolean flag){
		Intent intent=new Intent();
		intent.putExtra("flag", flag);
		setResult(RESULT_OK,intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pp_btn_save:
			setResultFlag(true);
			break;
		case R.id.pp_btn_cancel:
			setResultFlag(false);
			break;

		default:
			break;
		}

		finish();
		
	}
	

public void setFullscreen() {    
     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//清除FLAG
     requestWindowFeature(Window.FEATURE_NO_TITLE);
     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
}


@Override
public boolean dispatchKeyEvent(KeyEvent event) {
	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
			&& event.getAction() == KeyEvent.ACTION_DOWN) {

		
		setResultFlag(false);
		
		finish();
		return true;
	}

	return super.dispatchKeyEvent(event);

}


@Override
public void onDestroy() {
	
	super.onDestroy();
	
	android.os.Process.killProcess(android.os.Process.myPid());
}

}
