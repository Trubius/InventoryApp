package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER_ID = 1;
    private Uri mCurrentBookUri;
    private TextView mBookName;
    private TextView mPrice;
    private TextView mQuantity;
    private TextView mSupplierName;
    private TextView mSupplierPhone;
    private ImageButton mDecreaseQuantity;
    private ImageButton mIncreaseQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);

        mBookName = findViewById(R.id.detail_book_name_value);
        mPrice = findViewById(R.id.detail_price_value);
        mQuantity = findViewById(R.id.detail_quantity_value);
        mSupplierName = findViewById(R.id.detail_supplier_name_value);
        mSupplierPhone = findViewById(R.id.detail_supplier_phone_value);
        mDecreaseQuantity = findViewById(R.id.button_quantity_decrease);
        mIncreaseQuantity = findViewById(R.id.button_quantity_increase);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_book:
                Intent intent = new Intent(BookDetailsActivity.this, BookEditorActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;
            case R.id.delete_book:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            int bookIdColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            final long bookId = cursor.getLong(bookIdColumnIndex);
            String bookName = cursor.getString(bookNameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            mBookName.setText(bookName);
            mPrice.setText(Utils.formatPrice(price));
            if (quantity == 0) {
                mQuantity.setText(R.string.out_of_stock);
                mDecreaseQuantity.setEnabled(false);
            } else {
                mQuantity.setText(String.valueOf(quantity));
                mDecreaseQuantity.setEnabled(true);
            }
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(supplierPhone);
            mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newQuantity = quantity - 1;
                    if (newQuantity < 0) {
                        newQuantity = 0;
                    }
                    mCurrentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, newQuantity);

                    getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            });
            mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newQuantity = quantity + 1;
                    mCurrentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, newQuantity);

                    getContentResolver().update(mCurrentBookUri, values, null, null);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_book_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_book_success, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
