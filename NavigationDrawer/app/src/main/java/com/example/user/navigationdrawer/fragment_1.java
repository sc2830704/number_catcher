package com.example.user.navigationdrawer;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by user on 2015/9/29.
 */
public class fragment_1 extends Fragment {
    View rootView;
    UserData userData;
    String name,account,birth;
    TextView account_textview,birth_textview,name_textview;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        userData = new UserData(activity,"myFile");
        name = userData.getData("MyName");
        birth = userData.getData("MyBirth");
        account = userData.getData(("MyAccount"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_1,container,false);

        name_textview = (TextView)rootView.findViewById(R.id.name);
        account_textview = (TextView)rootView.findViewById(R.id.account);
        birth_textview = (TextView)rootView.findViewById(R.id.birthday);

        name_textview.setText("姓名:"+name);
        account_textview.setText("信箱:"+account);
        birth_textview.setText("生日:"+birth);

        return  rootView;
    }



}
