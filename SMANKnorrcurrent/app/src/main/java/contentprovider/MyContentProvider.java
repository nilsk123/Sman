package contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.surfa.smanknorrcurrent.DatabaseHelper;

/**
 * Created by surfa on 8-10-2015.
 */
public class MyContentProvider extends ContentProvider{

    private DatabaseHelper database;

    private static final String AUTHORITY = "com.example.surfa.smanknorrcurrent.contentprovider";

    @Override
    public boolean onCreate() {

        database = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
