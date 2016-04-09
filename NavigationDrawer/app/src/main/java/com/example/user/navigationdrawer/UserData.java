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
        myEditor.commit();  //可使用apply(),  差別在於commit() 會直接將異動結果寫入檔案，apply() 則是修改記憶體中的暫存資料，並以非同步方式將結果寫入檔案中。
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
