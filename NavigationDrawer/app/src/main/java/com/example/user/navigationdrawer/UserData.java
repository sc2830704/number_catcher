package com.example.user.navigationdrawer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 2015/10/4.
 */
public class UserData {SharedPreferences sharePres;
    SharedPreferences.Editor myEditor;
    UserData (Context context,String fileName)
    {
        sharePres = context.getSharedPreferences(fileName,context.MODE_PRIVATE);
        myEditor= sharePres.edit();

    }
    protected void setData(String key,String value){
        myEditor.putString(key,value);
        myEditor.commit();  //�i�ϥ�apply(),  �t�O�b��commit() �|�����N���ʵ��G�g�J�ɮסAapply() �h�O�ק�O���餤���Ȧs��ơA�åH�D�P�B�覡�N���G�g�J�ɮפ��C
    }
    protected void setData(String key, int value)
    {
        myEditor.putInt(key, value);
        myEditor.commit();
    }
    protected String getData(String key)
    {
        return sharePres.getString(key,"null");
    }
    protected int getIntData(String key){ return sharePres.getInt(key,0);}

}
