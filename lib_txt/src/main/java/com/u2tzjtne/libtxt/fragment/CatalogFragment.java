package com.u2tzjtne.libtxt.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.u2tzjtne.libtxt.R;
import com.u2tzjtne.libtxt.adapter.CatalogueAdapter;
import com.u2tzjtne.libtxt.base.BaseFragment;
import com.u2tzjtne.libtxt.db.BookCatalogue;
import com.u2tzjtne.libtxt.util.PageFactory;

import java.util.ArrayList;

/**
 *
 * @author Administrator
 * @date 2016/8/31 0031
 */
public class CatalogFragment extends BaseFragment {
    public static final String ARGUMENT = "argument";

    private PageFactory pageFactory;
    private ArrayList<BookCatalogue> catalogueList = new ArrayList<>();

    private ListView lvCatalogue;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_catalog;
    }

    @Override
    protected void initData(View view) {
        pageFactory = PageFactory.getInstance();
        catalogueList.addAll(pageFactory.getDirectoryList());
        CatalogueAdapter catalogueAdapter = new CatalogueAdapter(getContext(), catalogueList);
        catalogueAdapter.setCharter(pageFactory.getCurrentCharter());
        lvCatalogue.setAdapter(catalogueAdapter);
        catalogueAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initView(View view) {
        lvCatalogue = view.findViewById(R.id.lv_catalogue);
    }

    @Override
    protected void initListener() {
        lvCatalogue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pageFactory.changeChapter(catalogueList.get(position).getBookCatalogueStartPos());
                getActivity().finish();
            }
        });
    }

    /**
     * 用于从Activity传递数据到Fragment
     *
     * @param bookpath
     * @return
     */
    public static CatalogFragment newInstance(String bookpath) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, bookpath);
        CatalogFragment catalogFragment = new CatalogFragment();
        catalogFragment.setArguments(bundle);
        return catalogFragment;
    }
}
