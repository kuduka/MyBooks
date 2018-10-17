package com.soc.uoc.pqtm.mybooks;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soc.uoc.pqtm.mybooks.helper.ImageDownloaderTask;
import com.soc.uoc.pqtm.mybooks.model.BookContent;

import java.text.SimpleDateFormat;


public class BookDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "id";
    private BookContent.BookItem mItem;


    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            int position = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            mItem = BookContent.getBooks().get(position);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);
        SimpleDateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");

        if (mItem != null) {
            new ImageDownloaderTask((ImageView) rootView.findViewById(R.id.detail_imgurl)).execute(mItem.getUrl_image());
            ((TextView) rootView.findViewById(R.id.detail_author)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.detail_desc)).setText(mItem.getDescription());
            ((TextView) rootView.findViewById(R.id.detail_dpublish)).setText(dformat.format(mItem.getPublication_date()));
        }
        return rootView;
    }
}
