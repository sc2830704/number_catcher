package com.example.user.navigationdrawer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 2015/9/29.
 */
public class fragment_2 extends Fragment {
    View rootView;
    ImageView postview;
    // ImageView mapview;
    UserData userData;
    private WebView webView;
    private Button buttonLoadNumber;

    private TextView totalNumber,waitNumber;
    private NotificationManager notificationManager; //手機功能通知物件
    final String[] msg = new String[5];

    ImageButton mapbutton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_2,container,false);
        postview = (ImageView)rootView.findViewById(R.id.postofficeView);
        mapbutton = (ImageButton)rootView.findViewById(R.id.mapButton);

        postview.setImageResource(R.drawable.postoffice);
        mapbutton.setImageResource(R.drawable.ic_map);

        webView = (WebView) rootView.findViewById(R.id.webView);
        totalNumber = (TextView) rootView.findViewById(R.id.totalNumber);
        waitNumber = (TextView) rootView.findViewById(R.id.waitNumber);
        buttonLoadNumber = (Button) rootView.findViewById(R.id.load_button);

        Toast.makeText(getActivity(), getString(R.string.app_name) + "????", Toast.LENGTH_SHORT).show();
        initWebViewSettings();

        webView.loadUrl("http://192.168.1.10:1337");

        buttonLoadNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "get load", Toast.LENGTH_SHORT).show();
                webView.loadUrl("http://192.168.1.10:1337");
                webView.loadUrl("javascript:setAndroidtotalNumber()");
            }
        });





        return  rootView;
    }


    private void initWebViewSettings() {

        webView.addJavascriptInterface(this, "Android");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAppCacheEnabled(true);
        //webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLightTouchEnabled(true);
        webView.setWebViewClient(myWebViewClient);
        webView.setWebChromeClient(myChromeClient);
    }

    private WebViewClient myWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(getActivity(), "載入頁面", Toast.LENGTH_SHORT).show();
            // txtUrlAddress.setText(url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Toast.
            //    setProgressBarIndeterminateVisibility(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //
            //   setProgressBarIndeterminateVisibility(false);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //

            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    private WebChromeClient myChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            //
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            //
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
        }

        @Override
        public boolean onJsTimeout() {
            return true;
        }
    };


    private void tremor() {
        Notification notification = new Notification();
        notification.vibrate = new long[]{0, 100, 200, 300};
        notificationManager.notify(0, notification);
    }

    private AlertDialog getAlertDialog(String title, String message) {
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //設定Dialog的標題
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                Toast.makeText(getActivity(), "您按下OK按鈕", Toast.LENGTH_SHORT).show();
            }
        });
        //設定Negative按鈕資料
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                Toast.makeText(getActivity(), "您按下Cancel按鈕", Toast.LENGTH_SHORT).show();
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }


    @JavascriptInterface
    public void setAndroidtotalNumber_calledWebJava(String input) {
        final String msg = input;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                totalNumber.setText(getString(R.string.totalNumber) +" : " + msg);
                webView.loadUrl("http://192.168.1.10:1337");
            }
        });
    }

    @JavascriptInterface
    public void setAndroidwaitNumber_calledWebJava(String input) {
        final String msg = input;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
               waitNumber.setText(getString(R.string.waitNumber) +" : "+ msg);

            }
        });
    }



}
