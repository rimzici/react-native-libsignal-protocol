
package com.reactlibrary.storage;

import android.content.Context;

import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.util.List;

// public class ProtocolStorage implements SignalProtocolStore {
public class ProtocolStorage implements IdentityKeyStore {

    DatabaseManager db;

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
    }

    /**
    * IdentityKeyStore
    */

    public int regId;
    public IdentityKeyPair identityKP;
    public void setLocalRegistrationId(int id) {
        // Save in shared preference.
        regId = id;
    }
    public void setIdentityKeyPair(IdentityKeyPair iKP) {
        // Save in shared preference.
        identityKP = iKP;
    }
    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        // Get from shared preference.
        return identityKP;
    }
    @Override
    public int getLocalRegistrationId() {
        // Get from shared preference.
        return regId;
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
//    @Override
//    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
//
//    }
//
//    @Override
//    public void storePreKey(int preKeyId, PreKeyRecord record) {
//
//    }
//
//    @Override
//    public boolean containsPreKey(int preKeyId) {
//
//    }
//
//    @Override
//    public void removePreKey(int preKeyId) {
//
//    }
//
//    /**
//    * SessionStore
//    */
//    @Override
//    public SessionRecord loadSession(SignalProtocolAddress address) {
//
//    }
//
//    @Override
//    public List<Integer> getSubDeviceSessions(String name) {
//
//    }
//
//    @Override
//    public void storeSession(SignalProtocolAddress address, SessionRecord record) {
//
//    }
//
//    @Override
//    public boolean containsSession(SignalProtocolAddress address) {
//
//    }
//
//    @Override
//    public void deleteSession(SignalProtocolAddress address) {
//
//    }
//
//    @Override
//    public void deleteAllSessions(String name) {
//
//    }
//
//    /**
//    * SignedPreKeyStore
//    */
//    @Override
//    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
//
//    }
//
//    @Override
//    public List<SignedPreKeyRecord> loadSignedPreKeys() {
//
//    }
//
//    @Override
//    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
//
//    }
//
//    @Override
//    public boolean containsSignedPreKey(int signedPreKeyId) {
//
//    }
//
//    @Override
//    public void removeSignedPreKey(int signedPreKeyId) {
//
//    }

}