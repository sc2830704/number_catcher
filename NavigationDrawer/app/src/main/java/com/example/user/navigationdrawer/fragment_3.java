package com.example.user.navigationdrawer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by user on 2015/9/29.
 */
public class fragment_3 extends Fragment {
    View rootView;

    Button btnDraw;
    TextView textview3,textview4;
    UserData userData;
    Button button;
    String waittingNumber;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        userData = new UserData(activity,"myFile");

        waittingNumber = userData.getData("waittingNumber");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragment3", "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_3,container,false);

        textview4=(TextView)rootView.findViewById(R.id.textView4);

        setNumberView(rootView);

        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        btnDraw = (Button)rootView.findViewById(R.id.draw);
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DrawCard.class);
                getActivity().startActivityForResult(intent, 1);
            }
        });
        button = (Button)rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).numberDrop();
            }
        });

    }
    public void setNumberView(View rootView)
    {
        textview3=(TextView)rootView.findViewById(R.id.textView3);
        String num = new String(userData.getData("MyNumber"));
        textview4.setText("等待時間:\n約"+waittingNumber+"分鐘");
        if (!num.equals("null")){
            rootView.findViewById(R.id.draw).setVisibility(View.INVISIBLE);
            textview3.setText("YourNumber:\n"+num);
        }
        else {
            rootView.findViewById(R.id.draw).setVisibility(View.VISIBLE);
            textview3.setText("Didn't get any Number");
        }
    }

}
