package com.kjy.care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.kjy.care.R;
import com.kjy.care.bean.AdviceInfo;

import java.util.List;

public class AdviceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;


    public void setList(List<AdviceInfo> list) {
        this.list = list;
    }

    private List<AdviceInfo> list;

    public AdviceAdapter(Context context){
        this.context = context;
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

            view = inflater.inflate(R.layout.adapter_advice, null);

            viewholder = new ViewHolder();


            viewholder.TextView_id=view.findViewById(R.id.TextView_id);
            viewholder.TextView_name=view.findViewById(R.id.TextView_name);
            viewholder.TextView_type=view.findViewById(R.id.TextView_type);
            viewholder.TextView_price=view.findViewById(R.id.TextView_price);
            viewholder.TextView_time=view.findViewById(R.id.TextView_time);


            view.setTag(viewholder);

            } else {
            viewholder = (ViewHolder) view.getTag();
                  }
                    AdviceInfo item = list.get(i);

        int black_333 = context.getResources().getColor(R.color.black);

        viewholder.TextView_id.setText(item.getId());viewholder.TextView_id.setTextColor(black_333);
        viewholder.TextView_name.setText(item.getName());viewholder.TextView_name.setTextColor(black_333);
        viewholder.TextView_type.setText(item.getType());viewholder.TextView_type.setTextColor(black_333);
        viewholder.TextView_price.setText(item.getPrice());viewholder.TextView_price.setTextColor(black_333);
        viewholder.TextView_time.setText(item.getTime());viewholder.TextView_time.setTextColor(black_333);




        return view;
    }



    class ViewHolder{
        TextView TextView_id;//编号
        TextView TextView_name;//名称
        TextView TextView_type;//类型
        TextView TextView_price;//价格
        TextView TextView_time;//时间

        View view;

    }
}
