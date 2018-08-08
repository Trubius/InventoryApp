package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Custom CursorRecyclerViewAdapter: https://stackoverflow.com/questions/26517855/using-the-recyclerview-with-a-database
 */

public abstract class CursorRecyclerViewAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public CursorRecyclerViewAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null){
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor !=null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    public abstract void onBindViewHolder(ViewHolder viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!mDataValid){
            throw new IllegalStateException("This should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Couldn't move to position" + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
