package com.android.gastove.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.gastove.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class HttpURLConActivity extends Activity {
	private TextView tvContent;  
	public static String URL = "http://192.168.1.109:8080/FirstServletService/"; // IP地址请改为你自己的IP  
	  
    public static String URL_Register = URL + "RegisterServlet";  
    public static String URL_Login = URL + "LoginServlet";  
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_http_urlcon);  
  
        tvContent = (TextView) findViewById(R.id.tv_content); // 这里页面上就一个简单的TextView，用于展示获取到报文内容  
        requestUsingHttpURLConnection();  
    }  
    /** 
     * 消息处理 
     */  
    private Handler handler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            if(msg.what == 1){  
                tvContent.setText("11"+msg.obj.toString());  
            }  
        }  
    };  
      

    private void requestUsingHttpURLConnection() {  
        // 网络通信属于典型的耗时操作，开启新线程进行网络请求  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                HttpURLConnection connection = null;  
                try {  
                    URL url = new URL("https://www.baidu.com"); // 声明一个URL,注意——如果用百度首页实验，请使用https  
                    connection = (HttpURLConnection) url.openConnection(); // 打开该URL连接  
                    connection.setRequestMethod("GET"); // 设置请求方法，“POST或GET”，我们这里用GET，在说到POST的时候再用POST  
                    connection.setConnectTimeout(8000); // 设置连接建立的超时时间  
                    connection.setReadTimeout(8000); // 设置网络报文收发超时时间  
                    InputStream in = connection.getInputStream();  // 通过连接的输入流获取下发报文，然后就是Java的流处理  
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
                    StringBuilder response = new StringBuilder();  
                    String line;  
                    while ((line = reader.readLine()) != null){  
                        response.append(line);  
                    }  
                    Message msg = new Message();  
                    msg.what = 1;  
                    msg.obj = response.toString();  
//                    Log.e("WangJ", response.toString());  
                    handler.sendMessage(msg);  
//                    tvContent.setText(response.toString()); // 地雷  
                } catch (MalformedURLException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
}
