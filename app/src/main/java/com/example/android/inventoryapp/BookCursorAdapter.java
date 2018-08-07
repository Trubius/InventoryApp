package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorRecyclerViewAdapter<BookCursorAdapter.ViewHolder> {

    private Context mContext;

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @NonNull
    @Override
    public BookCursorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BookCursorAdapter.ViewHolder viewHolder, Cursor cursor) {

        int productIdColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        String productName = cursor.getString(productNameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        final long productId = cursor.getLong(productIdColumnIndex);

        viewHolder.mProductName.setText(productName);
        viewHolder.mPrice.setText(String.valueOf(price));
        viewHolder.mQuantity.setText(String.valueOf(quantity));
        viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), BookDetailsActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, productId);
                intent.setData(currentBookUri);
                mContext.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mItemView;
        private TextView mProductName;
        private TextView mPrice;
        private TextView mQuantity;
        private Button mSaleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mProductName = itemView.findViewById(R.id.product_name);
            mPrice = itemView.findViewById(R.id.price_value);
            mQuantity = itemView.findViewById(R.id.quantity_value);
            mSaleButton = itemView.findViewById(R.id.sale_button);
        }
    }
}
