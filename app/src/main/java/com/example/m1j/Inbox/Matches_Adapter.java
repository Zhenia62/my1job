package com.example.m1j.Inbox;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.m1j.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Matches_Adapter extends RecyclerView.Adapter<Matches_Adapter.CustomViewHolder > implements Filterable {
    public Context context;
    ArrayList<Match_Get_Set> inbox_dataList = new ArrayList<>();
    ArrayList<Match_Get_Set> inbox_dataList_filter = new ArrayList<>();
    private Matches_Adapter.OnItemClickListener listener;
    private Matches_Adapter.OnLongItemClickListener longlistener;

    Integer today_day=0;

    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(Match_Get_Set item);
    }
    public interface OnLongItemClickListener{
        void onLongItemClick(Match_Get_Set item);
    }

    public Matches_Adapter(Context context, ArrayList<Match_Get_Set> user_dataList, Matches_Adapter.OnItemClickListener listener, Matches_Adapter.OnLongItemClickListener longlistener) {
        this.context = context;
        this.inbox_dataList=user_dataList;
        this.inbox_dataList_filter=user_dataList;
        this.listener = listener;
        this.longlistener=longlistener;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Matches_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matchs_layout,null);
        Matches_Adapter.CustomViewHolder viewHolder = new Matches_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return inbox_dataList_filter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username,last_message,date_created;
        ImageView user_image;
        RelativeLayout mainlayout;

        public CustomViewHolder(View view) {
            super(view);
            username=view.findViewById(R.id.username);
            user_image=view.findViewById(R.id.user_image);
        }

        public void bind(final Match_Get_Set item, final Matches_Adapter.OnItemClickListener listener, final  Matches_Adapter.OnLongItemClickListener longItemClickListener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });


        }

    }


    @Override
    public void onBindViewHolder(final Matches_Adapter.CustomViewHolder holder, final int i) {
        final Match_Get_Set item=inbox_dataList_filter.get(i);
        holder.username.setText(item.getUsername());

        if(!item.getPicture().equals("") && item.getPicture()!=null)
        Picasso.with(context)
                .load(item.getPicture())
                .resize(100,100)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.user_image);
        holder.bind(item,listener,longlistener);
   }



    // this method will cahnge the date to  "today", "yesterday" or date
    public String ChangeDate(String date){
        //current date in millisecond
        long currenttime= System.currentTimeMillis();

        //database date in millisecond
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        long databasedate = 0;
        Date d = null;
        try {
            d = f.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference=currenttime-databasedate;
        if(difference<86400000){
            int chatday= Integer.parseInt(date.substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            if(today_day==chatday)
                return sdf.format(d);
            else if((today_day-chatday)==1)
                return "Yesterday";
        }
        else if(difference<172800000){
            int chatday= Integer.parseInt(date.substring(0,2));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            if((today_day-chatday)==1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
        return sdf.format(d);
    }


    // that function will filter the result
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inbox_dataList_filter = inbox_dataList;
                } else {
                    ArrayList<Match_Get_Set> filteredList = new ArrayList<>();
                    for (Match_Get_Set row : inbox_dataList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    inbox_dataList_filter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inbox_dataList_filter;
                return filterResults;

            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inbox_dataList_filter = (ArrayList<Match_Get_Set>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}