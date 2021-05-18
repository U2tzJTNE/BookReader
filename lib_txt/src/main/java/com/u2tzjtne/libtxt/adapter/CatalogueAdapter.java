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
import com.u2tzjtne.libtxt.db.BookCatalogue;

import java.util.List;

/**
 * @author u2tzjtne
 */
public class CatalogueAdapter extends BaseAdapter {
    private Context mContext;
    private List<BookCatalogue> bookCatalogueList;
    private Typeface typeface;
    private int currentCharter = 0;

    public CatalogueAdapter(Context context, List<BookCatalogue> bookCatalogueList) {
        mContext = context;
        this.bookCatalogueList = bookCatalogueList;
        Config config = Config.getInstance();
        typeface = config.getTypeface();
    }

    @Override
    public int getCount() {
        return bookCatalogueList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookCatalogueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCharter(int charter){
        currentCharter = charter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder= new ViewHolder();
            convertView = inflater.inflate(R.layout.cataloguelistview_item,null);
            viewHolder.tvCatalogue = (TextView)convertView.findViewById(R.id.catalogue_tv);
            viewHolder.tvCatalogue.setTypeface(typeface);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (currentCharter == position){
            viewHolder.tvCatalogue.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }else{
            viewHolder.tvCatalogue.setTextColor(mContext.getResources().getColor(R.color.read_textColor));
        }
        viewHolder.tvCatalogue.setText(bookCatalogueList.get(position).getBookCatalogue());
        return convertView;
    }

    static class ViewHolder {
        TextView tvCatalogue;
    }
}
