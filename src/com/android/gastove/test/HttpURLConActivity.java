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
	public static String URL = "http://192.168.1.109:8080/FirstServletService/"; // IP��ַ���Ϊ���Լ���IP  
	  
    public static String URL_Register = URL + "RegisterServlet";  
    public static String URL_Login = URL + "LoginServlet";  
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_http_urlcon);  
  
        tvContent = (TextView) findViewById(R.id.tv_content); // ����ҳ���Ͼ�һ���򵥵�TextView������չʾ��ȡ����������  
        requestUsingHttpURLConnection();  
    }  
    /** 
     * ��Ϣ���� 
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
        // ����ͨ�����ڵ��͵ĺ�ʱ�������������߳̽�����������  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                HttpURLConnection connection = null;  
                try {  
                    URL url = new URL("https://www.baidu.com"); // ����һ��URL,ע�⡪������ðٶ���ҳʵ�飬��ʹ��https  
                    connection = (HttpURLConnection) url.openConnection(); // �򿪸�URL����  
                    connection.setRequestMethod("GET"); // �������󷽷�����POST��GET��������������GET����˵��POST��ʱ������POST  
                    connection.setConnectTimeout(8000); // �������ӽ����ĳ�ʱʱ��  
                    connection.setReadTimeout(8000); // �������籨���շ���ʱʱ��  
                    InputStream in = connection.getInputStream();  // ͨ�����ӵ���������ȡ�·����ģ�Ȼ�����Java��������  
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
//                    tvContent.setText(response.toString()); // ����  
                } catch (MalformedURLException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
}
