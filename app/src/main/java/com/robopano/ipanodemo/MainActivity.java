package com.robopano.ipanodemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.robopano.ipanosdk.Listener.CalcListener;
import com.robopano.ipanosdk.Listener.LoginListener;
import com.robopano.ipanosdk.Listener.UploadListener;
import com.robopano.ipanosdk.bean.ProductionInfo;
import com.robopano.ipanosdk.bean.SingleInfo;
import com.robopano.ipanosdk.manager.Ipano3Manager;
import com.robopano.ipanosdk.manager.ScanManager;
import com.robopano.ipanosdk.manager.UploadManager;
import com.robopano.ipanosdk.utils.PanoUrlUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button      mBtnCapture;
    private ScanManager sm;
    private String      file_path;
    public  String   target = null;
    public  String   name   = null;
    public  String   dpi    = null;
    public  String   token  = "";
    //    private String[] names  = new String[]{"次卧1", "次卧2", "次卧3", "书房1", "书房2",
//            "书房3", "阳台1", "阳台2", "阳台3"};
    private String[] names  = new String[]{"cc1", "cc2", "cc3", "zz1", "zz2",
            "zz3", "aa1", "aa2", "aa3"};

    private ProgressDialog mProgressDialog;
    private Button         mBtnCalcBasemap;
    private Button         mBtnLogin;
    private Button         mBtnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCapture = (Button) findViewById(R.id.btn_capture);
        mBtnCalcBasemap = (Button) findViewById(R.id.btn_calc_basemap);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);

        mBtnCapture.setOnClickListener(this);
        mBtnCalcBasemap.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnUpload.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setCancelable(false);

        file_path = getFilesDir() + File.separator + "pano";
        sm = new ScanManager(this);

        //sm.setWifiOpenTimeout(8); 开启wifi超时时间,默认8秒
        sm.setWifiConnectTimeout(18);//连接wifi超时时间,默认8秒,如要自定义,建议不低于2秒
        sm.setScanListener(new ScanManager.ScanListener() {

            @Override
            public void handle_start() {
                mProgressDialog.show();
                Log.d(TAG, "----- handle_start");
            }

            @Override
            public void handle_scan_finish(float v) {
                Log.d(TAG, "----- scan_finish");
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "拍摄成功", Toast.LENGTH_SHORT).show();
                go2PanoViewPage();
//				go2PanoViewEXPage();
            }

            @Override
            public void handle_scan_init() {
                mProgressDialog.setMessage("handle_scan_init");
                Log.d(TAG, "----- handle_scan_init");
            }

            @Override
            public void handle_stitch_ok() {
                mProgressDialog.setMessage("handle_stitch_ok");
                Log.d(TAG, "----- stitch_ok");
            }

            @Override
            public void init_start() {
                Log.d(TAG, "--- initmap_start");
                mProgressDialog.setMessage("第一次需要初始化，请耐心等待");
            }

            @Override
            public void init_ok() {

                Log.d(TAG, "--- initmap_ok");
            }

            @Override
            public void handle_start_ok() {
                mProgressDialog.setMessage("handle_start_ok");
                Log.d(TAG, "---- start_ok");
            }

            @Override
            public void handle_scan_ok() {
                mProgressDialog.setMessage("handle_scan_ok");
                Log.d(TAG, "---- scan_ok");
            }

            @Override
            public void handleFailed(int errorCode) {
                if (mProgressDialog != null) {

                    mProgressDialog.dismiss();
                }
                String msg = null;
                switch (errorCode) {
                    case 0:
                        msg = "连接失败";
                        break;
                    case 1:
                        msg = "扫描失败";
                        break;
                    case 2:
                        msg = "渲染失败";
                        break;
                    case 3:
                        msg = "取图失败";
                        break;
                    case 4:
                        msg = "处理失败";
                        break;
                    default:
                        msg = "连接失败";
                        break;
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handle_filter_wifilist(List<ScanResult> list) {
                //过滤后的设备wifi列表,可自定义页面进行展示
                /*showWifilistDialog(list);*/

                // 如果设备只有一个则自动连接,否则显示列表进行选择
                if (list.size() > 1) {
                    showWifilistDialog(list);
                } else {
                    //autoSetPassword为true,则不用显示密码输入框,后台可直接连接
                    sm.onDeviceWifiSelect(list.get(0).SSID, true);
                }
            }

            @Override
            public void handle_wifi_open(int code) {
                switch (code) {
                    case 0:
                        //开启wifi
                        showWifiopenDialog(true);
                        break;
                    case 1:
                        //开启wifi完成
                        showWifiopenDialog(false);
                        break;
                    case 2:
                        //开启wifi超时
                        showWifiopenDialog(false);
                        Toast.makeText(getApplicationContext(), "扫描超时,请重试", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //连接设备wifi
                        showWifiConnectDialog(true);
                        Toast.makeText(getApplicationContext(), "连接设备wifi", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        //连接设备wifi完成
                        showWifiConnectDialog(false);
                        Toast.makeText(getApplicationContext(), "连接设备wifi完成", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        //连接设备wifi超时
                        showWifiConnectDialog(false);
                        Toast.makeText(getApplicationContext(), "连接超时,请手动连接", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void handle_wifi_error(int code) {
                switch (code) {
                    case 0:
                        //开启wifi失败
                        Toast.makeText(getApplicationContext(), "开启wifi失败,请允许相关权限", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //wifi扫描列表为空或设备列表为空
                        Toast.makeText(getApplicationContext(), "未扫描到设备", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //wifi密码错误或wifi连接失败
                        Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //定位权限
                        Toast.makeText(getApplicationContext(), "扫描失败,请开启定位权限", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void handle_wifi_connectSuccess(String name) {
                //wifi连接成功
                Toast.makeText(getApplicationContext(), "已连接上" + name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handle_wifi_password_set(String wifiName) {
                //-----当设置onDeviceWifiSelect()的autoSetPassword参数为true时,可忽略此处。

                //显示wifi密码输入页面,可自定义
                showWifiPasswordDialog(wifiName);
            }
        });
    }

    private AlertDialog dialog_wifiopen = null;

    //开启wifi到获取扫描信息有延时,等待页面,可自定义
    private void showWifiopenDialog(boolean flag) {
        if (flag) {
            AlertDialog ad = new AlertDialog.Builder(this).show();
            ad.setCanceledOnTouchOutside(false);
            ad.setContentView(R.layout.wifi_open);
            dialog_wifiopen = ad;
        } else {
            if (dialog_wifiopen != null && dialog_wifiopen.isShowing()) {
                dialog_wifiopen.dismiss();
            }
        }
    }

    private AlertDialog dialog_wificonnect = null;

    //开启wifi到获取扫描信息有延时,等待页面,可自定义
    private void showWifiConnectDialog(boolean flag) {
        if (flag) {
            AlertDialog ad = new AlertDialog.Builder(this).show();
            ad.setCanceledOnTouchOutside(false);
            ad.setContentView(R.layout.wifi_connect);
            dialog_wificonnect = ad;
        } else {
            if (dialog_wificonnect != null && dialog_wificonnect.isShowing()) {
                dialog_wificonnect.dismiss();
            }
        }
    }

    //显示wifi密码输入页面,可自定义
    private void showWifiPasswordDialog(final String wifiName) {
        final Dialog dg = new Dialog(this);
        dg.setCanceledOnTouchOutside(false);
        View view = View.inflate(this, R.layout.wifi_config_dialog, null);
        dg.setTitle("连接" + wifiName);
        final EditText pswEdit = (EditText) view.findViewById(R.id.wifiDialogPsw);
        ((Button) view.findViewById(R.id.wifiDialogCancel)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dg.dismiss();
            }
        });
        ((Button) view.findViewById(R.id.wifiDialogCertain)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = pswEdit.getText().toString().trim();
                //********** wifi密码输入,后台自动连接 *********
                sm.setWifiPassword(wifiName, password);
                dg.dismiss();
            }
        });

        dg.show();
        dg.setContentView(view);
    }

    //显示过滤后设备wifi列表的页面, 可自定义
    private void showWifilistDialog(List<ScanResult> list) {
        final List<String> wifi_names = new ArrayList<String>();
        for (ScanResult sr : list) {
            wifi_names.add(sr.SSID);
        }

        String[] items = new String[wifi_names.size()];
        items = wifi_names.toArray(items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择设备");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //  ********** TODO : 设备wifi选择*********
               /* sm.onDeviceWifiSelect(wifi_names.get(which));*/

                //autoSetPassword为true,则不用显示密码输入框,后台可直接连接
                sm.onDeviceWifiSelect(wifi_names.get(which), true);
            }
        });

        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.show();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd" + "HHmmss");
        return sdf.format(new Date());

    }

    // 本地预览
    private void go2PanoViewPage() {
        SharedPreferences sp = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("target", target);
        edit.putString("name", name);
        String targetS = sp.getString("target", "");
        String nameS = sp.getString("name", "");
        Intent intent = new Intent(this, LocalView.class);
        Map<String, String> map = new TreeMap<String, String>();
        map.put(target, name);
        Log.d(TAG, "--- target = " + target + "---name=" + name);
        if (!TextUtils.isEmpty(targetS) && !TextUtils.isEmpty(nameS)) {
            map.put(targetS, nameS);
            Log.d(TAG, "--- targetS = " + targetS + "---nameS=" + nameS);
        }
//		intent.putExtra("group_url",
//				PanoUrlUtils.getPanoSingleUrl(target, name,dpi));
        intent.putExtra("group_url",
//                PanoUrlUtils.getPanoGroupUrl(map, dpi));
                PanoUrlUtils.getPanoSingleUrl(target, name, dpi));
        this.startActivityForResult(intent, 1);
        edit.commit();
    }

    // 本地预览编辑模式
    private void go2PanoViewEXPage() {
        Intent intent = new Intent(this, LocalViewEX.class);
        intent.putExtra("group_url",
                PanoUrlUtils.getPanoSingleUrl(target, name, dpi));
        this.startActivityForResult(intent, 2);
    }

    public void setButtonEnable() {
        mBtnCapture.setClickable(true);
        mBtnCapture.setBackgroundColor(Color.parseColor("#aaaa0000"));
        mBtnCapture.setText("scan");
    }

    public void setButtonUnable() {
        mBtnCapture.setClickable(false);
        mBtnCapture.setBackgroundColor(Color.parseColor("#999999"));
    }

    private void initDeviceParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println("density:" + dm.density + "----dpi:" + dm.densityDpi
                + "----width:" + dm.widthPixels + "----height:"
                + dm.heightPixels);

        //获取dpi,本地预览传入字符串参数中
        dpi = String.valueOf(dm.densityDpi);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
            /*setButtonUnable();*/

                name = names[(int) (Math.random() * names.length)];
//                target = file_path + File.separator + getCurrentTime() + File.separator + name;
                target = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
                String targetPano2 = target + "IpanoTwo";
                File file = new File(targetPano2);
                if (!file.exists()) {
                    file.mkdirs();
                }
                sm.scan(target, name);
                break;
            case R.id.btn_calc_basemap:

                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("calc BaseMap..");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Ipano3Manager ipano3Manager = new Ipano3Manager(MainActivity.this);
                ipano3Manager.init();
                ipano3Manager.setCalcListener(new CalcListener() {
                    @Override
                    public void OnError(String errorInfo) {
                        //初始化错误
                        if (progressDialog != null) {

                            progressDialog.dismiss();
                        }
                        Log.d(TAG, "--- calcbasemap error = " + errorInfo);
                    }

                    @Override
                    public void OnComplete() {
                        if (progressDialog != null) {

                            progressDialog.dismiss();
                        }
                        //初始化完成
                        Log.d(TAG, "--- calcbasemap success = ");
                        Toast.makeText(MainActivity.this, "初始化完成", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_login:
                Log.d(TAG, "--- btn_login");
                // 设置username 和 password
                String username = "B500";
                String password = "321";
                login(username, password);
                break;
            case R.id.btn_upload:
                Log.d(TAG, "--- btn_upload");
                upload();
                break;
            default:
                break;
        }
    }

    private void upload() {
        UploadManager uploadManager = new UploadManager(MainActivity.this);
        uploadManager.setUploadListener(new UploadListener() {
            @Override
            public void OnError(String errorInfo) {

                Log.d(TAG, "--- errorInfo = " + errorInfo);
            }

            @Override
            public void OnProgress(int index, int progress) {
                Log.d(TAG, "--- int = " + index + " progress = " + progress);
            }

            @Override
            public void OnSuccess(String info) {

                Log.d(TAG, "--- OnSuccess = " + info);
                Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });

        ProductionInfo info = new ProductionInfo();

        info.setProvince("广东省");
        info.setCity("深圳市");
        info.setArea("南山区");
        //商圈
        info.setDistrict("广东省");
        //小区
        info.setVillage("钓鱼台");
        //房屋面积
        info.setMianji("55");
        //楼号
        info.setHouseNumber("6");
        //房号
        info.setRoomNumber("4");
        //几室几厅几卫
        info.setRoom("1");
        info.setHall("2");
        info.setToilet("3");
        info.setPrice("2500000");
        info.setIntroduce("房源描述");
        //设备wifi名称
        info.setMacAddress("IPANO2T-e0b94df478fd");
        //照片拍摄的时间戳
        String timestamp = "20171220114050";
        info.setGroup_id(timestamp);
        //场景数量
        int group_count = 2;
        info.setGroup_count(group_count);

        List<SingleInfo> uploadList = new ArrayList<SingleInfo>();
        for (int i = 0; i < group_count; i++) {
            SingleInfo singleInfo = new SingleInfo();
            // 场景图片的路径
            singleInfo.setImageDir(target);
            // location 该场景名称
            singleInfo.setLocation("厕所"+i);
            singleInfo.setGroup_id(timestamp);
            // 场景 pano2.jpg 的地址
            singleInfo.setOnetotwo_imageUrl(target+"/pano2.jpg");
            // 设备标识 固定
            singleInfo.setDevice("2t");
            uploadList.add(singleInfo);
        }
        uploadManager.upload(token, info, uploadList);
    }

    private void login(String username, String password) {
        UploadManager uploadManager = new UploadManager(MainActivity.this);
        uploadManager.setLoginListener(new LoginListener() {
            @Override
            public void OnError(String errorInfo) {
                Log.d(TAG, "--- login error = " + errorInfo);
            }

            @Override
            public void OnSuccess(String token) {

                Log.d(TAG, "--- token = " + token);
                Toast.makeText(MainActivity.this, ("token = " + token), Toast.LENGTH_SHORT).show();
                MainActivity.this.token = token;
            }
        });
        uploadManager.login(username, password);
    }
}
