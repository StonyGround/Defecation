package com.kjy.care.adapter;

import android.content.Context;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kjy.care.R;
import com.kjy.care.bean.DeviceInfo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> implements  View.OnClickListener {
    private Context context;
    private LayoutInflater inflater;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        int position = (int)view.getTag();
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (onItemClickListener!=null){
            onItemClickListener.onItemClick(view,position);



        }
    }

    public void setList(List<DeviceInfo> list) {
        this.list = list;
    }

    public List<DeviceInfo> getList() {
        return list;
    }

    private int  index=0;
    public void setIndex(int index){
        this.index = index;
        this.notifyDataSetChanged();
    }


    private List<DeviceInfo> list;

    public DeviceAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_device, parent,false);
        view.setOnClickListener(this);

        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder viewholder = holder;

        DeviceInfo item = list.get(position);

        holder.itemView.setTag(position);



        viewholder.TextView_title.setText(item.getName());

        int orange_2 = context.getResources().getColor(R.color.orange_2);
        int black_gray = context.getResources().getColor(R.color.black_gray);

        int transparent = context.getResources().getColor(R.color.transparent);



        if(position==index) {


            viewholder.TextView_title.setTextColor(orange_2);
            viewholder.TextView_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            viewholder.View_line.setBackgroundColor(orange_2);
            viewholder.View_line.setVisibility(View.VISIBLE);
        }else {

            viewholder.TextView_title.setTextColor(black_gray);
            viewholder.TextView_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            viewholder.View_line.setBackgroundColor(transparent);
            viewholder.View_line.setVisibility(View.GONE);
        }


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        int n=0;
        try{
            if(list!=null){
                n=list.size();
            }
        }catch(Exception e){}
        return n;
    }





    class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextView_title;//编号
        RelativeLayout RelativeLayout_bg;
        View View_line;


        public ViewHolder(View itemView) {
            super(itemView);

            TextView_title=itemView.findViewById(R.id.TextView_title);
            RelativeLayout_bg=itemView.findViewById(R.id.RelativeLayout_bg);
            View_line=itemView.findViewById(R.id.View_line);
        }
    }
}
