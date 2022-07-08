package com.kjy.care.activity.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.kjy.care.R;
import com.kjy.care.activity.UserActivity;
import com.kjy.care.util.ButtonChangeUtil;
import com.kjy.care.util.SPUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ControlUserFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "ControlUser";

    public ControlUserFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }



    ImageView ImageView_water_press,   ImageView_water_temper,   ImageView_water_air,    ImageView_massage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        ImageView_water_press =view.findViewById(R.id.ImageView_water_press);
        ImageView_water_temper=view.findViewById(R.id.ImageView_water_temper);
        ImageView_water_air=view.findViewById(R.id.ImageView_water_air);
        ImageView_massage=view.findViewById(R.id.ImageView_massage);
        LinearLayout LinearLayout_water_press=view.findViewById(R.id.LinearLayout_water_press);
        LinearLayout_water_press.setOnClickListener(this);
        LinearLayout LinearLayout_warm_air=view.findViewById(R.id.LinearLayout_warm_air);
        LinearLayout_warm_air.setOnClickListener(this);
        LinearLayout LinearLayout_water_temper=view.findViewById(R.id.LinearLayout_water_temper);
        LinearLayout_water_temper.setOnClickListener(this);
        LinearLayout LinearLayout_massage=view.findViewById(R.id.LinearLayout_massage);
        LinearLayout_massage.setOnClickListener(this);



        TextView TextView_more=view.findViewById(R.id.TextView_more);
        TextView_more.setOnClickListener(this);


        loadNowLevIco();
        return view;
    }










    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){


            case R.id.LinearLayout_water_press:
                ButtonChangeUtil.changeWaterPress(ImageView_water_press,getActivity(),mysql);
                //通知到
                break;
            case R.id.LinearLayout_warm_air:
                ButtonChangeUtil.changeWarmAir(ImageView_water_air,getActivity(),mysql);
                break;
            case R.id.LinearLayout_water_temper:
                ButtonChangeUtil.changeWaterTemper(ImageView_water_temper,getActivity(),mysql);
                break;
            case R.id.LinearLayout_massage:
                ButtonChangeUtil.changeMassage(ImageView_massage,getActivity(),mysql);
                break;
            case R.id.TextView_more:

                UserActivity.luncher();
                break;


        }
    }




    /**
     * 获取当前状态，如果  MainActivity 有操作过
     */
    private void loadNowLevIco(){


        String WATER_PRESS_LEV= SPUtil.get(getActivity(),SPUtil.WATER_PRESS,"0");//水压 低
        String WATER_TEMPER_LEV=SPUtil.get(getActivity(),SPUtil.WATER_TEMPER,"0");//水温  关
        String WARM_AIR_LEV=SPUtil.get(getActivity(),SPUtil.WARM_AIR,"0");//暖风
       // String TIMER_DRY_LEV=SPUtil.get(this,SPUtil.TIMER_DRY,"0");//定时干燥
        String MASSAGE_LEV=SPUtil.get(getActivity(),SPUtil.MASSAGE,"0");//按摩



        ButtonChangeUtil.changeWaterPressIco(ImageView_water_press,getActivity(),Integer.parseInt(WATER_PRESS_LEV));
        ButtonChangeUtil.changeWaterTemperIco(ImageView_water_temper,getActivity(),Integer.parseInt(WATER_TEMPER_LEV) );
        ButtonChangeUtil.changeWarmAirIco(ImageView_water_air,getActivity(),Integer.parseInt(WARM_AIR_LEV));
        ButtonChangeUtil.changeMassageIco(ImageView_massage,getActivity(),Integer.parseInt(MASSAGE_LEV));




    }
}