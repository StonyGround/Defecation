package com.kjy.care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kjy.blue.blutooth.BLEDevice;
import com.kjy.care.R;
import com.kjy.care.activity.BaseApp;
import com.kjy.care.bean.DeviceInfo;
import com.kjy.care.util.StringUtil;

import java.util.List;

public class BindDeviceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;


    public void setList(List<DeviceInfo> list) {
        this.list = list;
    }

     private int  index=0;
    public void setIndex(int index){
        this.index = index;
        this.notifyDataSetChanged();
    }


    private List<DeviceInfo> list;

    public BindDeviceAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    boolean showSearch=true;


    public BindDeviceAdapter(Context context, boolean showSearch){
        this.context = context;
        this.showSearch=showSearch;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        int n=0;
        try{
            if(list!=null){
                n=list.size();
            }
        }catch(Exception e){}
        return n;
    }




    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewholder = null;
        if (view == null) {

            view = inflater.inflate(R.layout.adapter_bind_device, null);

            viewholder = new ViewHolder();


            viewholder.TextView_title=view.findViewById(R.id.TextView_title);
            viewholder.TextView_mac=view.findViewById(R.id.TextView_mac);
            viewholder.ImageView_search=view.findViewById(R.id.ImageView_search);

            viewholder.ImageView_connect=view.findViewById(R.id.ImageView_connect);

            view.setTag(viewholder);

            } else {
            viewholder = (ViewHolder) view.getTag();
                  }
        DeviceInfo item = list.get(i);
        String mac = item.getMac();
        if(!showSearch){
            viewholder.ImageView_search.setVisibility(View.GONE);
        }else{
            //绑定界面
            if(!StringUtil.isEmpty(mac)){
                BLEDevice device = BaseApp.getBLE().getDeviceByMac(mac);
                if(device!=null && device.isConnect()){
                    viewholder.ImageView_connect.setImageResource(R.mipmap.ble_connect);
                }else{
                    viewholder.ImageView_connect.setImageResource(R.mipmap.ble_disconnect);
                }

            }else{
                viewholder.ImageView_connect.setImageResource(R.mipmap.ble_disconnect);
            }

        }

        //



        viewholder.TextView_title.setText(item.getName());

        if(item.getRssi()!=999){
            mac =mac+"("+item.getRssi()+")";
        }

        viewholder.TextView_mac.setText(mac);

        int white = context.getResources().getColor(R.color.white);
        int blue_sky = context.getResources().getColor(R.color.blue_sky);



        viewholder.TextView_title.setTextColor(blue_sky);




        return view;
    }



    class ViewHolder{
        TextView TextView_title;//编号
        RelativeLayout RelativeLayout_bg;
        TextView TextView_mac;
        ImageView ImageView_search;

        ImageView ImageView_connect;


    }
}
