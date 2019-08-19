package com.reactlibrary.storage;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "protocolstore";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseManager instance = null;

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
            + ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + " TEXT, "
            + ProtocolStorage.SESSION_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.DEVICE_ID + " , " + ProtocolStorage.NAME + ") ON CONFLICT REPLACE );";

    private static String CREATE_SIGNED_PREKEYS_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.SIGNED_PREKEY_TABLENAME + "("
            + ProtocolStorage.SIGNED_PREKEY_ID + " TEXT, "
            + ProtocolStorage.SIGNED_PREKEY_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.SIGNED_PREKEY_ID + ") ON CONFLICT REPLACE );";

    private static String CREATE_PREKEYS_STATEMENT = "CREATE TABLE "
            + ProtocolStorage.PREKEY_TABLENAME + "("
            + ProtocolStorage.PREKEY_ID + " TEXT, "
            + ProtocolStorage.PREKEY_RECORD + " TEXT, "
            + "UNIQUE("+ ProtocolStorage.PREKEY_ID + ") ON CONFLICT REPLACE );";

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
            Log.d(ProtocolStorage.LOGTAG, "Encountered invalid IdentityKey in database for address " + address.toString());
        }
        return identityKey;
    }

    public boolean storePreKey(int preKeyId, PreKeyRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProtocolStorage.PREKEY_ID, preKeyId);
        values.put(ProtocolStorage.PREKEY_RECORD, Base64.encodeToString(record.serialize(), Base64.DEFAULT));
        String where = ProtocolStorage.PREKEY_ID + "=?";
        String[] whereArgs = {String.valueOf(preKeyId)};
        int rows = db.update(ProtocolStorage.PREKEY_TABLENAME, values, where, whereArgs);
        long flag = -1;
        if (rows == 0) {
            flag = db.insert(ProtocolStorage.PREKEY_TABLENAME, null, values);
        }
        db.close();
        return flag != -1;
    }

    public PreKeyRecord loadPreKey (int preKeyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.PREKEY_TABLENAME + " where " + ProtocolStorage.PREKEY_ID + " =?";
        String[] whereArgs = {String.valueOf(preKeyId)};
        Cursor cursor = db.rawQuery(SQL, whereArgs);
        String prekeyRec = "";
        PreKeyRecord preKeyRecord = null;

        if (cursor.moveToFirst()) {
            do {
                prekeyRec = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        try {
            preKeyRecord = new PreKeyRecord(Base64.decode(prekeyRec, Base64.DEFAULT));
        } catch (IOException e) {
            Log.d(ProtocolStorage.LOGTAG, "Encountered IOException for id " + String.valueOf(preKeyId));
        }
        return preKeyRecord;
    }

    public int removePreKey (int preKeyId) {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] args = {Integer.toString(preKeyId)};
            return db.delete(ProtocolStorage.PREKEY_TABLENAME,
                    ProtocolStorage.PREKEY_ID + "=?",
                    args);
    }

    public boolean containsPreKey (int preKeyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ProtocolStorage.PREKEY_RECORD};
        String[] selectionArgs = {String.valueOf(preKeyId)};
        Cursor cursor = db.query(ProtocolStorage.PREKEY_TABLENAME,
                columns,
                ProtocolStorage.PREKEY_ID + "=?",
                selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }


    public boolean storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProtocolStorage.SIGNED_PREKEY_ID, signedPreKeyId);
        values.put(ProtocolStorage.SIGNED_PREKEY_RECORD, Base64.encodeToString(record.serialize(), Base64.DEFAULT));
        String where = ProtocolStorage.SIGNED_PREKEY_ID + "=?";
        String[] whereArgs = {String.valueOf(signedPreKeyId)};
        int rows = db.update(ProtocolStorage.SIGNED_PREKEY_TABLENAME, values, where, whereArgs);
        long flag = -1;
        if (rows == 0) {
            flag = db.insert(ProtocolStorage.SIGNED_PREKEY_TABLENAME, null, values);
        }
        db.close();
        return flag != -1;
    }

    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.SIGNED_PREKEY_TABLENAME + " where " + ProtocolStorage.SIGNED_PREKEY_ID + " =?";
        String[] whereArgs = {String.valueOf(signedPreKeyId)};
        Cursor cursor = db.rawQuery(SQL, whereArgs);
        String sPrekeyRec = "";
        SignedPreKeyRecord sPreKeyRecord = null;

        if (cursor.moveToFirst()) {
            do {
                sPrekeyRec = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        try {
            sPreKeyRecord = new SignedPreKeyRecord(Base64.decode(sPrekeyRec, Base64.DEFAULT));
        } catch (IOException e) {
            Log.d(ProtocolStorage.LOGTAG, "Encountered IOException for id " + String.valueOf(signedPreKeyId));
        }
        return sPreKeyRecord;
    }

    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.SIGNED_PREKEY_TABLENAME;
        Cursor cursor = db.rawQuery(SQL, null);
        List<SignedPreKeyRecord> list = new ArrayList();

        if (cursor.moveToFirst()) {
            do {
                try {
                    list.add(new SignedPreKeyRecord(Base64.decode(cursor.getString(1), Base64.DEFAULT)));
                } catch (IOException e) {
                    Log.d(ProtocolStorage.LOGTAG, "Encountered IOException");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean containsSignedPreKey(int signedPreKeyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ProtocolStorage.SIGNED_PREKEY_RECORD};
        String[] selectionArgs = {String.valueOf(signedPreKeyId)};
        Cursor cursor = db.query(ProtocolStorage.SIGNED_PREKEY_TABLENAME,
                columns,
                ProtocolStorage.SIGNED_PREKEY_ID + "=?",
                selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }

    public int removeSignedPreKey(int signedPreKeyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {Integer.toString(signedPreKeyId)};
        return db.delete(ProtocolStorage.SIGNED_PREKEY_TABLENAME,
                ProtocolStorage.SIGNED_PREKEY_ID + "=?",
                args);
    }


    public boolean storeSession(SignalProtocolAddress address, SessionRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProtocolStorage.NAME, address.getName());
        values.put(ProtocolStorage.DEVICE_ID, address.getDeviceId());
        values.put(ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS, address.toString());
        values.put(ProtocolStorage.SESSION_RECORD, Base64.encodeToString(record.serialize(), Base64.DEFAULT));
        String where = ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + "=?";
        String[] whereArgs = {address.toString()};
        int rows = db.update(ProtocolStorage.SESSION_TABLENAME, values, where, whereArgs);
        long flag = -1;
        if (rows == 0) {
            flag = db.insert(ProtocolStorage.SESSION_TABLENAME, null, values);
        }
        db.close();
        return flag != -1;
    }

    public SessionRecord loadSession(SignalProtocolAddress address) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.SESSION_TABLENAME + " where " + ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + " =?";
        String[] whereArgs = {address.toString()};
        Cursor cursor = db.rawQuery(SQL, whereArgs);
        String record = "";

        if (cursor.moveToFirst()) {
            do {
                record = cursor.getString(3);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        SessionRecord sessionRecord = null;
        try {
            sessionRecord = new SessionRecord(Base64.decode(record, Base64.DEFAULT));
        } catch (IOException e) {
            Log.d(ProtocolStorage.LOGTAG, "Encountered IOException in database for address " + address.toString());
        }
        return sessionRecord;
    }


    public List<Integer> getSubDeviceSessions(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String SQL = "select * from " + ProtocolStorage.SESSION_TABLENAME + " where " + ProtocolStorage.NAME + " =?";
        String[] whereArgs = {name};
        Cursor cursor = db.rawQuery(SQL, whereArgs);
        List<Integer> list = new ArrayList();

        if (cursor.moveToFirst()) {
            do {
                list.add(Integer.parseInt(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean containsSession(SignalProtocolAddress address) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ProtocolStorage.SESSION_RECORD};
        String[] selectionArgs = {String.valueOf(address.toString())};
        Cursor cursor = db.query(ProtocolStorage.SESSION_TABLENAME,
                columns,
                ProtocolStorage.SIGNAL_PROTOCOL_ADDRESS + "=?",
                selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }


    public int deleteSession(SignalProtocolAddress address) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {address.toString()};
        return db.delete(ProtocolStorage.SIGNED_PREKEY_TABLENAME,
                ProtocolStorage.SIGNED_PREKEY_ID + "=?",
                args);
    }


    public void deleteAllSessions(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {name};
        db.delete(ProtocolStorage.SIGNED_PREKEY_TABLENAME,
                ProtocolStorage.NAME + " = ?",
                args);
    }

}