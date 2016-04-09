package com.example.user.navigationdrawer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by user on 2015/10/10.
 */
public class ServerUtilities {


    static final String TAG = "GCMDemo";
    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */



    static boolean register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");

        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId); // Fitst install this app to get the regId

        UserData userData = new UserData(context,"myFile");
        //  userData = new UserData(MainActivity.this,"myFile");  //new userData實例

            if(userData.getData("MyregId").equals(regId))
                System.out.println("userdata name=" + regId);
            else{
                userData.setData("MyregId", regId);
                System.out.println("userdata name=" + regId);
            }

        final String[] msg = new String[5];
        String name = userData.getData("MyName");
        String birth = userData.getData("MyBirth");
        String reg_Id = userData.getData("MyregId");
        String UUID = userData.getData("UUID");
        String account = userData.getData("MyAccount");


        msg[0] = reg_Id;
        msg[1] = name;
        msg[2] = birth;
        msg[3] = UUID;
        msg[4] = account;
                Thread postdataThread = new Thread(new ServerUtilities().new sendPostRunnable(msg));
        postdataThread.start();

        return false;
    }
    ////////////////////// post 程式碼資料最上端 往下看
    private String uriAPI = "http://192.168.1.10:7777";  //server IP:port
    // private String uriAPI = "http://192.168.1.2/httpPostTest/postTest_mysql.php";
    /** 「要更新版面」的訊息代碼 */
    protected static final int REFRESH_DATA = 0x00000001;
    /** 建立UI Thread使用的Handler，來接收其他Thread來的訊息 */
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null)
                        // 印出網路回傳的文字
                        //     Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                        break;
            }
        }
    };
    class sendPostRunnable implements Runnable
    {
        final String [] mssg = new String[5];

        public sendPostRunnable(String strTxt[])
        {
            System.arraycopy(strTxt, 0, mssg, 0, strTxt.length);
        }

        @Override
        public void run()
        {
            String result = sendPostDataToInternet(mssg); //post上去
            mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        }
    }

    private String sendPostDataToInternet(String strTxt[])
    {

		/* 建立HTTP Post連線 */

        HttpPost httpRequest = new HttpPost(uriAPI);
		/*
		 * Post運作傳送變數必須用NameValuePair[]陣列儲存
		 */



        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if(strTxt[3] == "Location") {
            params.add(new BasicNameValuePair("MyLongitude", strTxt[0]));//經度
            params.add(new BasicNameValuePair("MyLatitude", strTxt[1])); //緯度
            params.add(new BasicNameValuePair("UUID", strTxt[2]));
            System.out.println("Length fff" + strTxt.length);
        }
        else if (strTxt[0] == "1"){
            params.add(new BasicNameValuePair("Status", strTxt[0]));// 狀態
            params.add(new BasicNameValuePair("UUID", strTxt[1]));// 狀態
        }
        else {
            params.add(new BasicNameValuePair("MyregId", strTxt[0]));
            params.add(new BasicNameValuePair("MyName", strTxt[1]));
            params.add(new BasicNameValuePair("MyBirth", strTxt[2]));
            params.add(new BasicNameValuePair("UUID", strTxt[3]));
            params.add(new BasicNameValuePair("MyAccount", strTxt[4]));//欲增加以此類推
        }

        try
        {
			/* 發出HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 取得HTTP response */
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
			/* 若狀態碼為200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
				/* 取出回應字串 */
                String strResult = EntityUtils.toString(httpResponse
                        .getEntity());
                // 回傳回應字串
                return strResult;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
/////////////////////////////////// post資料底端

}

