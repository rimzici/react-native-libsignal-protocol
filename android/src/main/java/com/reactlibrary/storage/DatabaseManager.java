package com.reactlibrary.storage;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "protocolstore";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseManager instance = null;

    private static String LOGTAG = "PROTOCOL_STORAGE : ";

    private static String CREATE_IDENTITIES_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.IDENTITIES_TABLENAME + "("
            + ProtocolStorage.NAME + " TEXT,  "
            + ProtocolStorage.DEVICE_ID + " TEXT, "
            + ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + " TEXT, "
            + ProtocolStorage.IDENTITY_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.DEVICE_ID + " , " + ProtocolStorage.NAME + ") ON CONFLICT REPLACE );";

    private static String CREATE_SESSIONS_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.SESSION_TABLENAME + "("
            + ProtocolStorage.NAME + " TEXT,  "
            + ProtocolStorage.DEVICE_ID + " TEXT, "
            + ProtocolStorage.SESSION_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.DEVICE_ID + " , " + ProtocolStorage.NAME + ") ON CONFLICT REPLACE );";

    private static String CREATE_SIGNED_PREKEYS_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.SIGNED_PREKEY_TABLENAME + "("
            + ProtocolStorage.NAME + " TEXT,  "
            + ProtocolStorage.DEVICE_ID + " TEXT, "
            + ProtocolStorage.SIGNED_PREKEY_ID + " TEXT, "
            + ProtocolStorage.SIGNED_PREKEY_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.DEVICE_ID + " , " + ProtocolStorage.NAME + ") ON CONFLICT REPLACE );";

    private static String CREATE_PREKEYS_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.PREKEY_TABLENAME + "("
            + ProtocolStorage.NAME + " TEXT,  "
            + ProtocolStorage.DEVICE_ID + " TEXT, "
            + ProtocolStorage.PREKEY_ID + " TEXT, "
            + ProtocolStorage.PREKEY_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.DEVICE_ID + " , " + ProtocolStorage.NAME + ") ON CONFLICT REPLACE );";

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.rawQuery("PRAGMA secure_delete=ON", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SESSIONS_STATEMENT);
        db.execSQL(CREATE_PREKEYS_STATEMENT);
        db.execSQL(CREATE_SIGNED_PREKEYS_STATEMENT);
        db.execSQL(CREATE_IDENTITIES_STATEMENT);
    }

    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProtocolStorage.NAME, address.getName());
        values.put(ProtocolStorage.DEVICE_ID, address.getDeviceId());
        values.put(ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS, address.toString());
        values.put(ProtocolStorage.IDENTITY_RECORD, Base64.encodeToString(identityKey.serialize(), Base64.DEFAULT));
        String where = ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + "=?";
        String[] whereArgs = {address.toString()};
        int rows = db.update(ProtocolStorage.IDENTITIES_TABLENAME, values, where, whereArgs);
        long flag = -1;
        if (rows == 0) {
            flag = db.insert(ProtocolStorage.IDENTITIES_TABLENAME, null, values);
        }
        db.close();
        return flag != -1;
    }

    public IdentityKey getIdentity(SignalProtocolAddress address) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.IDENTITIES_TABLENAME + " where " + ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + " =?";
        String[] whereArgs = {address.toString()};
        Cursor cursor = db.rawQuery(SQL, whereArgs);
        String identity = "";

        if (cursor.moveToFirst()) {
            do {
                identity = cursor.getString(3);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        IdentityKey identityKey = null;
        try {
            byte[] identityBytes = Base64.decode(identity, Base64.DEFAULT);
            identityKey = new IdentityKey(identityBytes, 0);
        } catch (InvalidKeyException e) {
            Log.d(LOGTAG, "Encountered invalid IdentityKey in database for address " + address.toString());
        }
        return identityKey;
    }

}