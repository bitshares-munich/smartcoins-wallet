package de.bitshares_munich.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by nelson on 12/13/16.
 */
public class SCWallSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "scwall.db";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String SQL_CREATE_ASSETS_TABLE = "CREATE TABLE IF NOT EXISTS " + SCWallDatabaseContract.Assets.TABLE_NAME + " (" +
            SCWallDatabaseContract.Assets.COLUMN_ID + " TEXT PRIMARY KEY, " +
            SCWallDatabaseContract.Assets.COLUMN_SYMBOL + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Assets.COLUMN_PRECISION + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.Assets.COLUMN_ISSUER + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Assets.COLUMN_DESCRIPTION + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Assets.COLUMN_MAX_SUPPLY + TYPE_INTEGER + ")";
    private static final String SQL_CREATE_TRANSFERS_TABLE = "CREATE TABLE IF NOT EXISTS " + SCWallDatabaseContract.Transfers.TABLE_NAME + " (" +
            SCWallDatabaseContract.Transfers.COLUMN_ID + " TEXT PRIMARY KEY, " +
            SCWallDatabaseContract.Transfers.COLUMN_TIMESTAMP + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_FEE_AMOUNT + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_FEE_ASSET_ID + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_FROM + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_TO + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_TRANSFER_AMOUNT + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_TRANSFER_ASSET_ID + TYPE_TEXT + " DEFAULT '', " +
            SCWallDatabaseContract.Transfers.COLUMN_MEMO_MESSAGE + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_MEMO_FROM + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_MEMO_TO + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_BLOCK_NUM + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_EQUIVALENT_VALUE_ASSET_ID + TYPE_TEXT + ", " +
            SCWallDatabaseContract.Transfers.COLUMN_EQUIVALENT_VALUE + TYPE_INTEGER + ", " +
            "FOREIGN KEY (" + SCWallDatabaseContract.Transfers.COLUMN_FEE_ASSET_ID + ") REFERENCES " +
            SCWallDatabaseContract.Assets.TABLE_NAME + "(" + SCWallDatabaseContract.Assets.COLUMN_ID + "), " +
            "FOREIGN KEY (" + SCWallDatabaseContract.Transfers.COLUMN_TRANSFER_ASSET_ID + ") REFERENCES " +
            SCWallDatabaseContract.Assets.TABLE_NAME + "(" + SCWallDatabaseContract.Assets.COLUMN_ID + "), " +
            "FOREIGN KEY (" + SCWallDatabaseContract.Transfers.COLUMN_EQUIVALENT_VALUE_ASSET_ID + ") REFERENCES " +
            SCWallDatabaseContract.Assets.TABLE_NAME + "(" + SCWallDatabaseContract.Assets.COLUMN_ID + "))";
    private static final String SQL_CREATE_USER_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS " + SCWallDatabaseContract.UserAccounts.TABLE_NAME + "(" +
            SCWallDatabaseContract.UserAccounts.COLUMN_ID + " TEXT PRIMARY KEY, " +
            SCWallDatabaseContract.UserAccounts.COLUMN_NAME + TYPE_TEXT + ")";
    private static final String SQL_CREATE_ACCOUNT_KEYS_TABLE = "CREATE TABLE IF NOT EXISTS " + SCWallDatabaseContract.AccountKeys.TABLE_NAME + "( " +
            SCWallDatabaseContract.BaseTable.COLUMN_CREATION_DATE + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.AccountKeys.COLUMN_BRAINKEY + TYPE_TEXT + ", " +
            SCWallDatabaseContract.AccountKeys.COLUMN_SEQUENCE_NUMBER + TYPE_INTEGER + ", " +
            SCWallDatabaseContract.AccountKeys.COLUMN_WIF + TYPE_TEXT + " UNIQUE)";
    private final String TAG = this.getClass().getName();


    public SCWallSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        Log.d(TAG, SQL_CREATE_ASSETS_TABLE);
        Log.d(TAG, SQL_CREATE_TRANSFERS_TABLE);
        Log.d(TAG, SQL_CREATE_USER_ACCOUNTS_TABLE);
        Log.d(TAG, SQL_CREATE_ACCOUNT_KEYS_TABLE);

        db.execSQL(SQL_CREATE_ASSETS_TABLE);
        db.execSQL(SQL_CREATE_TRANSFERS_TABLE);
        db.execSQL(SQL_CREATE_USER_ACCOUNTS_TABLE);
        db.execSQL(SQL_CREATE_ACCOUNT_KEYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL(SQL_CREATE_ACCOUNT_KEYS_TABLE);
    }
}
