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
import com.kjy.care.bean.RecordList;
import com.kjy.care.util.DateUtil;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> implements  View.OnClickListener {
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

    public void setList(List<RecordList> list) {
        this.list = list;
    }

    public List<RecordList> getList() {
        return list;
    }

    private int  index=0;
    public void setIndex(int index){
        this.index = index;
        this.notifyDataSetChanged();
    }


    private List<RecordList> list;

    public StringAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_string, parent,false);
        view.setOnClickListener(this);

        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder viewholder = holder;

        RecordList item = list.get(position);

        holder.itemView.setTag(position);

        viewholder.TextView_content.setText(item.t_time.substring(11)+" "+item.t_content);




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
        TextView TextView_content;//编号



        public ViewHolder(View itemView) {
            super(itemView);

            TextView_content=itemView.findViewById(R.id.TextView_content);

        }
    }
}
