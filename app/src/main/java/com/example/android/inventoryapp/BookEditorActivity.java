package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER_ID = 1;
    private Uri mCurrentBookUri;
    private TextInputEditText mEditBookName;
    private TextInputEditText mEditPrice;
    private TextInputEditText mEditQuantity;
    private TextInputEditText mEditSupplierName;
    private TextInputEditText mEditSupplierPhone;
    private String mBookName;
    private String mPrice;
    private String mQuantity;
    private String mSupplierName;
    private String mSupplierPhone;
    private TextInputLayout mBookNameLayout;
    private TextInputLayout mPriceLayout;
    private TextInputLayout mQuantityLayout;
    private TextInputLayout mSupplierNameLayout;
    private TextInputLayout mSupplierPhoneLayout;
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
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add_book));
        } else {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_book:
                saveBook();
                return true;
            case android.R.id.home:
                if (!mBookChanged) {
                    finish();
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveBook() {
        mBookName = mEditBookName.getText().toString().trim();
        mPrice = mEditPrice.getText().toString().trim();
        mQuantity = mEditQuantity.getText().toString().trim();
        mSupplierName = mEditSupplierName.getText().toString().trim();
        mSupplierPhone = mEditSupplierPhone.getText().toString().trim();
        mBookNameLayout = findViewById(R.id.edit_book_name_layout);
        mPriceLayout = findViewById(R.id.edit_price_layout);
        mQuantityLayout = findViewById(R.id.edit_quantity_layout);
        mSupplierNameLayout = findViewById(R.id.edit_supplier_name_layout);
        mSupplierPhoneLayout = findViewById(R.id.edit_supplier_phone_layout);

        if (checkEmptyFields()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, mBookName);
        values.put(BookEntry.COLUMN_BOOK_PRICE, mPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, mSupplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, mSupplierPhone);

        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.save_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.save_book_success, Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.update_book_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_book_success, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private boolean checkEmptyFields() {

        boolean hasError = false;

        if (TextUtils.isEmpty(mBookName)) {
            mBookNameLayout.setError(getString(R.string.book_name_required));
            hasError = true;
        } else {
            mBookNameLayout.setError(null);
        }
        if (TextUtils.isEmpty(mPrice)) {
            mPriceLayout.setError(getString(R.string.price_required));
            hasError = true;
        } else {
            mPriceLayout.setError(null);
        }
        if (TextUtils.isEmpty(mQuantity)) {
            mQuantityLayout.setError(getString(R.string.quantity_required));
            hasError = true;
        } else {
            mQuantityLayout.setError(null);
        }
        if (TextUtils.isEmpty(mSupplierName)) {
            mSupplierNameLayout.setError(getString(R.string.supplier_name_required));
            hasError = true;
        } else {
            mSupplierNameLayout.setError(null);
        }
        if (TextUtils.isEmpty(mSupplierPhone)) {
            mSupplierPhoneLayout.setError(getString(R.string.supplier_phone_required));
            hasError = true;
        } else {
            mSupplierPhoneLayout.setError(null);
        }
        return hasError;
    }
}
