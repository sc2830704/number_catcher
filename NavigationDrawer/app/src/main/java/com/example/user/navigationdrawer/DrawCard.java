package com.example.user.navigationdrawer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 2015/10/4.
 */
public class DrawCard extends Activity{


    NfcAdapter nfcAdapter ;
    PendingIntent nfcPendingIntent;
    IntentFilter[] ndefExchangeFilter;
    TextView text;
    UserData userData;
    String name, id,send_data;
    byte[] sendBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawcard_activity);

        text = (TextView)findViewById(R.id.log);
        getData();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(NfcFunctionCheck())
            nfcAdapter.setNdefPushMessage(setUserMessage(), DrawCard.this);

        //設定pendingIntent ,用來當NfcAdapter設定enableForegroundDispatch(this,PendingIntent,IntentFilter[],null)方法時的物件
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //設定exchangeFilter,用來當NfcAdapter設定enableForegroundDispatch(this,PendingIntent,IntentFilter[],null)方法時的物件
        setNdefExchangeFilters();
    }

    //** check NFC function is ok , if not return false*/
    private boolean NfcFunctionCheck(){
        if (nfcAdapter==null) {
            toast("Do not have NFC!");
            finish();
            return false;
        }else if( !nfcAdapter.isEnabled() ) {
            toast("NFC is not Enabled!");
            Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(setnfc);
            return false;
        }else if(!nfcAdapter.isNdefPushEnabled()){
            toast("NFC Beam is not Enabled");
            Intent setnfc = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
            startActivity(setnfc);
            return false;
        }
        else
            return true;
    }
    private void getData()
    {
        userData = new UserData(this,"myFile");
        id=userData.getData("UUID");
        name=userData.getData("MyName");
        send_data=id+";"+name;
    }
    private void setNdefExchangeFilters()
    {
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

        ndefExchangeFilter = new IntentFilter [] {ndefDetected};
    }




    @Override
    public void onResume()
    {
        super.onResume();
        if(NfcFunctionCheck())
            nfcAdapter.setNdefPushMessage(setUserMessage(), DrawCard.this);
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, ndefExchangeFilter, null);

    }

    @Override
    public void onPause()
    {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();

    }
    @Override
    public void onNewIntent(Intent intent)
    {
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
        {
            //取得從前景掃描的NDEFMessage
            NdefMessage msg[] = getNdefMessage(intent);
            //取的NDEFMessage字串
            String myNumber = getMsgString(msg[0],0);
            String currentNumber = getMsgString(msg[0],1);
            numberDelivery(myNumber,currentNumber);

        }
    }
    private void numberDelivery(String myNumber,String currentNumber)
    {

        Intent intent =new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("myNumber",myNumber);
        bundle.putString("currentNumber",currentNumber);
        intent.putExtras(bundle);
        setResult(0, intent); //requestCode需跟A.class的一樣
        finish();

    }
    //**NdefMessage格式轉換成String */
    private String getMsgString(NdefMessage msg,int recordCount){
        String payloadAsString = "";
        byte payload[]=msg.getRecords()[recordCount].getPayload();

        for (int i=1+payload[0];i<payload.length;i++)
            payloadAsString=payloadAsString+(char)payload[i];
        return payloadAsString;
    }


    private NdefMessage[] getNdefMessage(Intent intent) {
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsg != null) {
                msgs = new NdefMessage[rawMsg.length];
                for (int i = 0; i < rawMsg.length; i++) {
                    msgs[i] = (NdefMessage) rawMsg[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }

        }
        return msgs;
    }
    public NdefMessage setUserMessage()
    {

        try{
            sendBytes = send_data.getBytes("BIG5");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        NdefRecord idRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte [] {},sendBytes);
        //NdefRecord nameRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte [] {}, nameBytes);
        return new NdefMessage(new NdefRecord [] {idRecord});
    }

    private void toast(String msg){
        Toast.makeText(DrawCard.this,msg,Toast.LENGTH_SHORT).show();
    }
}
