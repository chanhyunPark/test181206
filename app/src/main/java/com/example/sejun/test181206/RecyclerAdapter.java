package com.example.sejun.test181206;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>
{

    ArrayList<RecyclerItem> mitem;

    public RecyclerAdapter(ArrayList<RecyclerItem> mitem)
    {
        this.mitem = mitem;
    }

    //새로운 뷰 홀더
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_view, parent,false ));
    }


    //ViewHolder라는 패턴의 강제화


    //View의 내용을 해당 포지션의 데이터로 바꾼다
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position)
    {
        holder.mNickTv.setText(mitem.get(position).getNick());
        holder.mCommentTv.setText(mitem.get(position).getComment());
        holder.mDateTv.setText(mitem.get(position).getDate());
    }

    @Override
    public int getItemCount()
    {
        return mitem.size();
    }

    // 커스텀 뷰홀더
    // item layout 에 존재하는 위젯들을 바인딩합니다.
    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mNickTv;
        private TextView mCommentTv;
        private TextView mDateTv;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            mNickTv = (TextView) itemView.findViewById(R.id.itemNicTv);
            mCommentTv = (TextView) itemView.findViewById(R.id.itemCommentTv);
            mDateTv = (TextView) itemView.findViewById(R.id.itemDateTv);
        }
    }
}
