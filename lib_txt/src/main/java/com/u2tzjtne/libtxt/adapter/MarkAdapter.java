package com.u2tzjtne.libtxt.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.u2tzjtne.libtxt.Config;
import com.u2tzjtne.libtxt.R;
import com.u2tzjtne.libtxt.db.BookMarks;
import com.u2tzjtne.libtxt.util.PageFactory;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/1/3.
 */
public class MarkAdapter extends BaseAdapter {
    private Context mContext;
    private List<BookMarks> list ;
    private Typeface typeface;
    private PageFactory pageFactory;

    public MarkAdapter(Context context, List<BookMarks> list) {
        mContext = context;
        this.list = list;
        pageFactory = PageFactory.getInstance();
        typeface = Config.getInstance().getTypeface();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_bookmark,null);
            viewHolder.textMark = convertView.findViewById(R.id.text_mark);
            viewHolder.progress = convertView.findViewById(R.id.progress1);
            viewHolder.markTime = convertView.findViewById(R.id.mark_time);
            viewHolder.textMark.setTypeface(typeface);
            viewHolder.progress.setTypeface(typeface);
            viewHolder.markTime.setTypeface(typeface);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textMark.setText(list.get(position).getText());
        long begin = list.get(position).getBegin();
        float fPercent = (float) (begin * 1.0 / pageFactory.getBookLen());
        DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(fPercent * 100) + "%";
        viewHolder.progress.setText(strPercent);
        viewHolder.markTime.setText(list.get(position).getTime().substring(0, 16));
        return convertView;
    }

    static class ViewHolder {
        TextView textMark, progress, markTime;
    }
}
