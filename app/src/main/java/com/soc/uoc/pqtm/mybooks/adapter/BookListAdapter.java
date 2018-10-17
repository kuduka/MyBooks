package com.soc.uoc.pqtm.mybooks.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.soc.uoc.pqtm.mybooks.BookDetailActivity;
import com.soc.uoc.pqtm.mybooks.BookDetailFragment;
import com.soc.uoc.pqtm.mybooks.BookListActivity;
import com.soc.uoc.pqtm.mybooks.R;

import android.view.ViewGroup;
import android.widget.TextView;

import com.soc.uoc.pqtm.mybooks.model.BookContent;
import java.util.List;


public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private final BookListActivity mContext;
    private boolean mTwoPane = false;
    private List<BookContent.BookItem> mValues;

    public void setItems(List<BookContent.BookItem> items) {
        this.mValues = items;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BookContent.BookItem item = (BookContent.BookItem) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(BookDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
                BookDetailFragment fragment = new BookDetailFragment();
                fragment.setArguments(arguments);
                mContext.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.book_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra(BookDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
                mContext.startActivity(intent);
            }
        }
    };

    public BookListAdapter(BookListActivity mContext, List<BookContent.BookItem> items, boolean mTwoPane) {
        this.mContext = mContext;
        this.mValues = items;
        this.mTwoPane = mTwoPane;
    }

    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_list_content_even, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_list_content_odd, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemCount(){
        return this.mValues != null ? this.mValues.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(BookListAdapter.ViewHolder holder, final int position) {

        holder.mTitleView.setText(String.valueOf(mValues.get(position).getTitle()));
        holder.mAuthorView.setText(mValues.get(position).getAuthor());
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleView, mAuthorView;

        public ViewHolder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mAuthorView = view.findViewById(R.id.detail_author);
        }
    }

}
