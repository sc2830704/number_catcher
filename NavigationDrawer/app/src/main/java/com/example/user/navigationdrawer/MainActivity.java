package com.example.user.navigationdrawer;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // private String uriAPI = "http://61.230.142.180:7777";  //  receive post data server IP
    // GCM google register account (GCM  sender ID)
    static final String SENDER_ID = "756678104108";

    final String TAG = "MainActivity";
    AsyncTask<Void, Void, Void> mRegisterTask;
    private NavigationDrawerFragment mNavigationDrawerFragment;


    private Handler myHandler;
    private PositionService myService;
    private CharSequence mTitle;
    private UserData userData;
    private String num = new String("null");
    private int time;
    private String temp,currentNumber;
    String UUID,lat,lng;

     private String[] positionMsg;
    Fragment objFragment = null;
    Fragment current = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = new UserData(MainActivity.this,"myFile");

        Log.d(TAG,"onCreate "+ temp);
        ininGCM();

        positionMsg = new String[4];

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        handler.post(timedTask);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    void ininGCM(){
        new GCMTask().execute();
        checkNotNull(SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch (position){
            case 0:

                mTitle = getString(R.string.title_section3);
                objFragment = new fragment_3();
                current = objFragment;
            break;
            case 1:
                mTitle = getString(R.string.title_section2);
                objFragment = new fragment_2();
                break;
            case 2:
                mTitle = getString(R.string.title_section1);
                objFragment = new fragment_1();
               break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction;
        // can back
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, objFragment);
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        Log.d("MainActivity", "FragmentTransaction Finish");
        //


    }
    @Override
    public void onNewIntent(Intent intent) {


        Log.d(TAG, "onNewIntent ");

        if (intent.getStringExtra("Refuse").equals("Yes")){
            temp="";
            unbindService(serviceConnection);
            Log.d(TAG, "unbindService");
            Log.d(TAG, "get in  onNewIntent " + intent.getStringExtra("Refuse"));
            myHandler.removeCallbacks(runnable);


            GCM_Stop();
        }
    }

    @Override
    public void onResume()
    {

        super.onResume();
        Log.d(TAG, "onResume");
        myHandler  = new Handler()
        {

            @Override
            public void handleMessage(Message msg)
            {
                //Toast.makeText(MainActivity.this, "" + myService.getLng() + " " + myService.getLat(), Toast.LENGTH_SHORT);

                positionMsg[0]=myService.getLng(); //經度
                positionMsg[1]=myService.getLat(); //緯度
                positionMsg[2]=userData.getData("UUID");
                positionMsg[3]="Location"; //Header
                Thread positionDataThread = new Thread(new ServerUtilities().new sendPostRunnable(positionMsg));
                positionDataThread.start();
            }

        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //You need to use getSupportFragmentManager() in your code, not getFragmentManager()
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        int stackCount = fragmentManager.getBackStackEntryCount();
        if (stackCount == 0) {
            this.finish();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
              mTitle = getString(R.string.title_section3);
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText (MainActivity.this, "onNew", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RequestCode:" + requestCode + "  ResultCode:" + resultCode);

        //Toast.makeText(MainActivity.this, "onActivityResult", Toast.LENGTH_SHORT);
        if(requestCode==1) {
            switch (resultCode) {

                    case 0:
                        if(data!=null) {
                            Bundle extras = data.getExtras();       //處理收到的資料，解析bundle
                            num = extras.getString("myNumber");
                            currentNumber = extras.getString("currentNumber");
                            saveNum(num,currentNumber);                         //資料儲存
                            upDateFragment();                      //更新fragment3頁面
                        }
                        Log.d(TAG, "initialPositionService");
                        initialPositionService();              //啟用service，使用定位功能
                        break;
                    default:

            }
        }
    }
    public void initialPositionService() {
        Log.d(TAG, "initialPositionService--2");
        Intent intent = new Intent(MainActivity.this,PositionService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private void saveNum(String num,String currentNumber)
    {
        int k = Integer.parseInt(num)-Integer.parseInt(currentNumber);
        userData.setData("waittingNumber", String.valueOf(k));
        userData.setData("MyNumber", num);
    }
    private void upDateFragment()
    {
        Fragment fragment3 = new fragment_3();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment3);
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commitAllowingStateLoss();
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service instanceof PositionService.ServiceBinder)
                myService = ((PositionService.ServiceBinder) service).getService();
            //Toast.makeText(MainActivity.this,myService.getProvider(),Toast.LENGTH_SHORT).show();
            // /handler開始處理Runnable任務
            myHandler.post(runnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    public void numberDrop()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GCM_Stop();

                if (userData.getData("MyNumber").equals("null"))
                    ;
                else {
                    userData.setData("MyNumber", "null");
                    upDateFragment();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();



    }

    // gcm beginning
    private class GCMTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {
            //  Log.d(TAG, "檢查裝置是否支援 GCM");
            // 檢查裝置是否支援 GCM
            GCMRegistrar.checkDevice(MainActivity.this);
            GCMRegistrar.checkManifest(MainActivity.this);
            final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);

            if (regId.equals(""))
            {
                //Log.d(TAG, "尚未註冊 Google GCM, 進行註冊");
                GCMRegistrar.register(MainActivity.this, SENDER_ID);

            }
            return null;
        }
    }
    private  void GCM_Stop(){
        String[] httpRequset= new String [2]; // number_table  status (if receive gcm)
        httpRequset[0]="1";
        httpRequset[1]=userData.getData("UUID");
        Thread positionDataThread = new Thread(new ServerUtilities().new sendPostRunnable(httpRequset));
        positionDataThread.start();
    }
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        // unregisterReceiver(mHandleMessageReceiver);
        //  GCMRegistrar.onDestroy(this);
        super.onDestroy();
        myHandler.removeCallbacks(runnable);

    }

    private void checkNotNull(Object reference, String name) {
        System.out.println("checkNotNull");
        if (reference == null) {
            System.out.println("reference == null");
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
    //gcm ending

    Handler handler = new Handler();
    Runnable timedTask = new Runnable(){
            @Override
        public void run() {

            time++;

            handler.postDelayed(timedTask, 3000);
            if(userData.getData("MyGCM").equals("true"))
            {
              //  numberDrop();
                userData.setData("MyGCM","false");
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message message =new Message();
            message.what = 1;
            myHandler.sendMessage(message);
            myHandler.postDelayed(this,3000);
        }
    };

}
