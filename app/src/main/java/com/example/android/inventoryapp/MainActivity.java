package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER_ID = 1;
    private BookCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEditorActivity.class);
                startActivity(intent);
            }
        });

        EmptyRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
        emptyView.setText(R.string.get_started);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBookCursorAdapter = new BookCursorAdapter(this, null);
        recyclerView.setAdapter(mBookCursorAdapter);

        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
    }

    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, getString(R.string.lord_of_the_rings));
        values.put(BookEntry.COLUMN_BOOK_PRICE, 20);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 1);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.amazon));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, getString(R.string.phone_number));

        getContentResolver().insert(BookEntry.CONTENT_URI, values);

        Toast.makeText(this, getString(R.string.book_saved), Toast.LENGTH_SHORT).show();
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, R.string.delete_all_books_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_all_books_success, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllBooks();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_books:
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
                BookEntry.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookCursorAdapter.swapCursor(null);
    }
}