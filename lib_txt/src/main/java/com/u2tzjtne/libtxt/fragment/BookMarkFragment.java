package com.u2tzjtne.libtxt.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.u2tzjtne.libtxt.R;
import com.u2tzjtne.libtxt.adapter.MarkAdapter;
import com.u2tzjtne.libtxt.base.BaseFragment;
import com.u2tzjtne.libtxt.db.BookMarks;
import com.u2tzjtne.libtxt.util.PageFactory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 * @date 2016/8/31 0031
 */
public class BookMarkFragment extends BaseFragment {
    public static final String ARGUMENT = "argument";


    private ListView lvBookmark;

    private String bookPath;
    private List<BookMarks> bookMarksList;
    private MarkAdapter markAdapter;
    private PageFactory pageFactory;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_bookmark;
    }

    @Override
    protected void initData(View view) {
        pageFactory = PageFactory.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bookPath = bundle.getString(ARGUMENT);
        }
        bookMarksList = new ArrayList<>();
        bookMarksList = DataSupport.where("bookpath = ?", bookPath).find(BookMarks.class);
        markAdapter = new MarkAdapter(getActivity(), bookMarksList);
        lvBookmark.setAdapter(markAdapter);
    }

    @Override
    protected void initView(View view) {
        lvBookmark = view.findViewById(R.id.lv_bookmark);
    }

    @Override
    protected void initListener() {
        lvBookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pageFactory.changeChapter(bookMarksList.get(position).getBegin());
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        lvBookmark.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否删除书签？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.delete(BookMarks.class, bookMarksList.get(position).getId());
                                bookMarksList.clear();
                                bookMarksList.addAll(DataSupport.where("bookpath = ?", bookPath).find(BookMarks.class));
                                markAdapter.notifyDataSetChanged();
                            }
                        }).setCancelable(true).show();
                return false;
            }
        });
    }

    /**
     * 用于从Activity传递数据到Fragment
     *
     * @param bookpath
     * @return
     */
    public static BookMarkFragment newInstance(String bookpath) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, bookpath);
        BookMarkFragment bookMarkFragment = new BookMarkFragment();
        bookMarkFragment.setArguments(bundle);
        return bookMarkFragment;
    }

}
