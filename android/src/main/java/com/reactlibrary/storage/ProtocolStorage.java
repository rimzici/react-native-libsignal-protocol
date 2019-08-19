
package com.reactlibrary.storage;

import android.content.Context;
import android.util.Log;

import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;

import java.util.List;

public class ProtocolStorage implements SignalProtocolStore {

    DatabaseManager db;
    PreferenceManager pref;

    public static String LOGTAG = "PROTOCOL_STORAGE : ";

    public static final String PREKEY_TABLENAME = "prekeys";
	public static final String SIGNED_PREKEY_TABLENAME = "signed_prekeys";
	public static final String SESSION_TABLENAME = "sessions";
	public static final String IDENTITIES_TABLENAME = "identities";

    public static final String NAME = "name";// Bare JID
    public static final String DEVICE_ID = "device_id";// NAME and DEVICE_ID will be used to create SignalProtocolAddress

    public static final String IDENTITY_RECORD = "identity_record";
     public static final String SIGNAL_PROTOCOL_ADDRESS = "signal_protocol_address";

    public static final String SESSION_RECORD = "session_record";

    public static final String SIGNED_PREKEY_ID = "signed_prekey_id";
    public static final String SIGNED_PREKEY_RECORD = "signed_prekey_record";

    public static final String PREKEY_ID = "prekey_id";
    public static final String PREKEY_RECORD = "prekey_record";
    
    public ProtocolStorage (Context context) {
        db = DatabaseManager.getInstance(context);
        pref = new PreferenceManager(context);
    }

    /**
    * IdentityKeyStore
    */

    public void setLocalRegistrationId(int id) {
        pref.setLocalRegistrationId(id);
    }
    public void setIdentityKeyPair(IdentityKeyPair iKP) {
        pref.setIdentityKeyPair(iKP);
    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return pref.getIdentityKeyPair();
    }

    @Override
    public int getLocalRegistrationId() {
        return pref.getLocalRegistrationId();
    }

    @Override
    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        return db.saveIdentity(address, identityKey);
    }

    @Override
    public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
        return true;
    }

    @Override
    public IdentityKey getIdentity(SignalProtocolAddress address) {
        return db.getIdentity(address);
    }


    /**
    * PreKeyStore
    */

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        db.storePreKey(preKeyId, record);
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        return db.loadPreKey(preKeyId);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return db.containsPreKey(preKeyId);
    }

    @Override
    public void removePreKey(int preKeyId) {
        db.removePreKey(preKeyId);
    }


    /**
    * SessionStore
    */

    @Override
    public SessionRecord loadSession(SignalProtocolAddress address) {
        return db.loadSession(address);
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        return db.getSubDeviceSessions(name);
    }

    @Override
    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
        db.storeSession(address, record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress address) {
        return db.containsSession(address);
    }

    @Override
    public void deleteSession(SignalProtocolAddress address) {
        db.deleteSession(address);
    }

    @Override
    public void deleteAllSessions(String name) {
        db.deleteAllSessions(name);
    }


    /**
    * SignedPreKeyStore
    */


    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        db.storeSignedPreKey(signedPreKeyId, record);
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        return db.loadSignedPreKey(signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return db.loadSignedPreKeys();
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return db.containsSignedPreKey(signedPreKeyId);
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        db.removeSignedPreKey(signedPreKeyId);
    }

}