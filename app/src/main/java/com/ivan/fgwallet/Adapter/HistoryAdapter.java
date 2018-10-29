package com.ivan.fgwallet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.TransactionDetailActivity;
import com.ivan.fgwallet.model.History;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static android.R.color.holo_green_light;
import static android.R.color.holo_red_light;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private Collection<History> histories;

    public HistoryAdapter(Context context, Collection<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    class ViewHolder {
        TextView tvReceivedSent, tvDate, tvBTC, tvMemo;
    }

    @Override
    public int getCount() {
        return histories == null ? 0 : histories.size();
    }

    @Override
    public History getItem(int position) {
        return (History) histories.toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.list_item_history, parent, false);
            holder.tvReceivedSent = (TextView) view.findViewById(R.id.tv_reveiced_sent);
            holder.tvDate = (TextView) view.findViewById(R.id.tv_day);
            holder.tvBTC = (TextView) view.findViewById(R.id.tv_btc);
            holder.tvMemo = (TextView) view.findViewById(R.id.tv_memo);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = dateFormat.parse(getItem(position).getTime().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvReceivedSent.setText(upCaseFirst(getItem(position).getType()));
        holder.tvDate.setText(dateFormat.format(date));
        holder.tvBTC.setText(getItem(position).getAmount().toString() + " BTC");
        if (getItem(position).getMemo() == null || getItem(position).getMemo().equals("")) {
            holder.tvMemo.setVisibility(View.GONE);
        } else {
            holder.tvMemo.setVisibility(View.VISIBLE);
            holder.tvMemo.setText( "  - " + getItem(position).getMemo());
        }

        if (upCaseFirst(getItem(position).getType()).equals("Received")) {
            holder.tvReceivedSent.setTextColor(context.getResources().getColor(holo_green_light));
            holder.tvReceivedSent.setText(context.getResources().getString(R.string.received));

            holder.tvDate.setTextColor(context.getResources().getColor(holo_green_light));
            holder.tvBTC.setTextColor(context.getResources().getColor(holo_green_light));
        } else {
            holder.tvReceivedSent.setTextColor(context.getResources().getColor(holo_red_light));
            holder.tvReceivedSent.setText(context.getResources().getString(R.string.sent));

            holder.tvDate.setTextColor(context.getResources().getColor(holo_red_light));
            holder.tvBTC.setTextColor(context.getResources().getColor(holo_red_light));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransactionDetailActivity.class);
                intent.putExtra("HISTORY_ITEM", getItem(position));
                context.startActivity(intent);
            }
        });
        return view;
    }

    private String upCaseFirst(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
