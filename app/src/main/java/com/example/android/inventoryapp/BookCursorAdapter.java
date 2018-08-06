package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract;

public class BookCursorAdapter extends CursorRecyclerViewAdapter<BookCursorAdapter.ViewHolder> {

    public BookCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @NonNull
    @Override
    public BookCursorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BookCursorAdapter.ViewHolder viewHolder, Cursor cursor) {

        int productNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        String productName = cursor.getString(productNameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        viewHolder.mProductName.setText(productName);
        viewHolder.mPrice.setText(price);
        viewHolder.mQuantity.setText(quantity);
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
