package com.example.user.navigationdrawer;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by User on 2015/10/21.
 * Service for position
 */

public class PositionService extends Service implements LocationListener{
    private LocationManager locationManger;
    private String provider;
    private IBinder myBinder = new ServiceBinder();
    private double lat,lng;

    class ServiceBinder extends Binder
    {
        PositionService getService()
        {
            return PositionService.this;
        }
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        locationManger = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(isLocatedEnabled(locationManger)) ;
        if(hasProvider())
            getLocation();


    }
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId)
    {
        super.onStartCommand(intent,flags,startId);

        return startId;
    }
    private boolean isLocatedEnabled(LocationManager locationManger){

        if(!locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            new AlertDialog.Builder(this)
                    .setTitle("尚未開啟GPS")
                    .setMessage("是否要設定定為服務?")
                    .setCancelable(false)
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }

        return true;
    }
    private boolean hasProvider()
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //provider = locationManger.getBestProvider(criteria,true);
        provider=LocationManager.NETWORK_PROVIDER;
        if(provider!=null)
            return true;
        return false;
    }
    private void getLocation()
    {
        Location location = locationManger.getLastKnownLocation(provider);
        updateLocation(location);
        locationManger.addGpsStatusListener(gpsListener);
        locationManger.requestLocationUpdates(provider,3000,0,this);
    }
    private void updateLocation(Location location) {

        //lat = location.getLatitude();
        //lng = location.getLongitude();
        /*
        Message message = new Message();
        message.what=1;
        bundle.putDouble("Lat",location.getLatitude());
        bundle.putDouble("Lng",location.getLongitude());
        message.setData(bundle);
        MainActivity.myHandler.sendMessage(message);*/

    }

    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lng = location.getLongitude();
        updateLocation(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public String getLat() {
        return String.valueOf(lat);
    }
    public String getLng() {
        return String.valueOf(lng);
    }

    GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d("PositionService", "GPS_EVENT_STARTED");
                    //Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d("PositionService", "GPS_EVENT_STOPPED");
                    //Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d("PositionService", "GPS_EVENT_FIRST_FIX");
                    //Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.d("PositionService", "GPS_EVENT_SATELLITE_STATUS");
                    break;
            }
        }
    };
    public String getProvider() {
        return provider;
    }
    @Override
    public void onDestroy() {
        Log.d("PositionService","onDestory");
        super.onDestroy();
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.d("PositionService","onOnbind");
        return super.onUnbind(intent);
    }

}
