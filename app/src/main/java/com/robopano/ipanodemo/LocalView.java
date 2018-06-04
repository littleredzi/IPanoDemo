package com.robopano.ipanodemo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.robopano.ipanosdk.PanoViewPlugin;

import org.apache.cordova.CordovaActivity;

/**
 * 全屏预览
 * @author lijie
 *
 */
public class LocalView extends CordovaActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreen();
        String  loadUrl=null;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            loadUrl = bundle.getString("group_url",null);
        }

        PanoViewPlugin.path=loadUrl;
        loadUrl(launchUrl);
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


            setResult(RESULT_OK);

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
