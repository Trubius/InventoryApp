package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER_ID = 1;
    private Uri mCurrentBookUri;
    private EditText mEditBookName;
    private EditText mEditPrice;
    private EditText mEditQuantity;
    private EditText mEditSupplierName;
    private EditText mEditSupplierPhone;
    private boolean mBookChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null){
            setTitle(getString(R.string.add_book));
        }else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);
        }

        mEditBookName = findViewById(R.id.edit_book_name_value);
        mEditPrice = findViewById(R.id.edit_price_value);
        mEditQuantity = findViewById(R.id.edit_quantity_value);
        mEditSupplierName = findViewById(R.id.edit_supplier_name_value);
        mEditSupplierPhone = findViewById(R.id.edit_supplier_phone_value);

        mEditBookName.setOnTouchListener(mTouchListener);
        mEditPrice.setOnTouchListener(mTouchListener);
        mEditQuantity.setOnTouchListener(mTouchListener);
        mEditSupplierName.setOnTouchListener(mTouchListener);
        mEditSupplierPhone.setOnTouchListener(mTouchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            String bookName = cursor.getString(bookNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mEditBookName.setText(bookName);
            mEditPrice.setText(String.valueOf(price));
            mEditQuantity.setText(String.valueOf(quantity));
            mEditSupplierName.setText(supplierName);
            mEditSupplierPhone.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditBookName.setText("");
        mEditPrice.setText(String.valueOf(""));
        mEditQuantity.setText(String.valueOf(""));
        mEditSupplierName.setText("");
        mEditSupplierPhone.setText("");
    }
}
