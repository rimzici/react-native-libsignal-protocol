
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.whispersystems.libsignal.DuplicateMessageException;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.libsignal.InvalidVersionException;
import org.whispersystems.libsignal.LegacyMessageException;
import org.whispersystems.libsignal.NoSessionException;
import org.whispersystems.libsignal.SessionBuilder;
import org.whispersystems.libsignal.SessionCipher;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.UntrustedIdentityException;
import org.whispersystems.libsignal.ecc.ECPublicKey;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;
import org.whispersystems.libsignal.protocol.SignalMessage;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;

import com.reactlibrary.storage.ProtocolStorage;

import android.util.Log;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Exception;
import java.math.BigInteger;
import java.util.List;


public class RNLibsignalProtocolModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private static final String RN_LIBSIGNAL_ERROR = "RN_LIBSIGNAL_ERROR";

  ProtocolStorage protocolStorage;

  public RNLibsignalProtocolModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;

    protocolStorage = new ProtocolStorage(reactContext);
  }

  @Override
  public String getName() {
    return "RNLibsignalProtocol";
  }

  @ReactMethod
  public void generateIdentityKeyPair(Promise promise) {
    try {
      IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
      String publicKey = Base64.encodeToString(identityKeyPair.getPublicKey().serialize(), Base64.DEFAULT);
      String privateKey = Base64.encodeToString(identityKeyPair.getPrivateKey().serialize(), Base64.DEFAULT);
      String serializedKP = Base64.encodeToString(identityKeyPair.serialize(), Base64.DEFAULT);
      WritableMap keyPairMap = Arguments.createMap();
      keyPairMap.putString("publicKey", publicKey);
      keyPairMap.putString("privateKey", privateKey);
      keyPairMap.putString("serializedKP", serializedKP);

      protocolStorage.setIdentityKeyPair(identityKeyPair);
      promise.resolve(keyPairMap);

    } catch (Exception e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    }
  }

  @ReactMethod
  public void generateRegistrationId(Promise promise) {
    try {
      int registrationId = KeyHelper.generateRegistrationId(false);
      protocolStorage.setLocalRegistrationId(registrationId);
      promise.resolve(registrationId);
    } catch (Exception e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    }
  }

  @ReactMethod
  public void generatePreKeys(int startId, int count, Promise promise) {
    try {
      List<PreKeyRecord> preKeys = KeyHelper.generatePreKeys(startId, count);

      WritableArray preKeyMapsArray = Arguments.createArray();
      for (PreKeyRecord key : preKeys) {
        String preKeyPublic = Base64.encodeToString(key.getKeyPair().getPublicKey().serialize(), Base64.DEFAULT);
        String preKeyPrivate = Base64.encodeToString(key.getKeyPair().getPrivateKey().serialize(), Base64.DEFAULT);
        int preKeyId = key.getId();
        String seriaizedPreKey = Base64.encodeToString(key.serialize(), Base64.DEFAULT);
        WritableMap preKeyMap = Arguments.createMap();
        preKeyMap.putString("preKeyPublic", preKeyPublic);
        preKeyMap.putString("preKeyPrivate", preKeyPrivate);
        preKeyMap.putInt("preKeyId", preKeyId);
        preKeyMap.putString("seriaizedPreKey", seriaizedPreKey);
        preKeyMapsArray.pushMap(preKeyMap);

        protocolStorage.storePreKey(preKeyId, key);
      }

      promise.resolve(preKeyMapsArray);
    } catch (Exception e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    }
  }

  @ReactMethod
  public void generateSignedPreKey(ReadableMap identityKeyPair, int signedKeyId, Promise promise) {
    try {
      byte[] serialized = Base64.decode(identityKeyPair.getString("serializedKP"), Base64.DEFAULT);

      IdentityKeyPair IKP = new IdentityKeyPair(serialized);
      SignedPreKeyRecord signedPreKey = KeyHelper.generateSignedPreKey(IKP, signedKeyId);
      String signedPreKeyPublic = Base64.encodeToString(signedPreKey.getKeyPair().getPublicKey().serialize(), Base64.DEFAULT);
      String signedPreKeyPrivate = Base64.encodeToString(signedPreKey.getKeyPair().getPrivateKey().serialize(), Base64.DEFAULT);
      String signedPreKeySignature = Base64.encodeToString(signedPreKey.getSignature(), Base64.DEFAULT);
      int signedPreKeyId = signedPreKey.getId();
      String seriaizedSignedPreKey = Base64.encodeToString(signedPreKey.serialize(), Base64.DEFAULT);
      
      WritableMap signedPreKeyMap = Arguments.createMap();
      signedPreKeyMap.putString("signedPreKeyPublic", signedPreKeyPublic);
      signedPreKeyMap.putString("signedPreKeyPrivate", signedPreKeyPrivate);
      signedPreKeyMap.putString("signedPreKeySignature", signedPreKeySignature);
      signedPreKeyMap.putInt("signedPreKeyId", signedPreKeyId);
      signedPreKeyMap.putString("seriaizedSignedPreKey", seriaizedSignedPreKey);
      
      protocolStorage.storeSignedPreKey(signedPreKeyId, signedPreKey);

      promise.resolve(signedPreKeyMap);
    } catch (Exception e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    }
  }

  @ReactMethod
  public void buildSession(String recipientId, int deviceId, ReadableMap retrievedPreKeyBundle, Promise promise) {
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(recipientId, deviceId);

    // Instantiate a SessionBuilder for a remote recipientId + deviceId tuple.
    SessionBuilder sessionBuilder = new SessionBuilder(protocolStorage, signalProtocolAddress);

    try {
      int preKeyId = retrievedPreKeyBundle.getInt("preKeyId");
      int registrationId = retrievedPreKeyBundle.getInt("registrationId");
      ECPublicKey preKey = new PreKeyRecord(Base64.decode(retrievedPreKeyBundle.getString("preKey"), Base64.DEFAULT)).getKeyPair().getPublicKey();
      int signedPreKeyId = retrievedPreKeyBundle.getInt("signedPreKeyId");
      SignedPreKeyRecord signedPreKey = new SignedPreKeyRecord(Base64.decode(retrievedPreKeyBundle.getString("seriaizedSignedPreKey"), Base64.DEFAULT));
      ECPublicKey signedPreKeyPublic = signedPreKey.getKeyPair().getPublicKey();
      byte[] signedPreKeySignature = signedPreKey.getSignature();
      IdentityKey identityKey = new IdentityKey(Base64.decode(retrievedPreKeyBundle.getString("identityKey"), Base64.DEFAULT), 0);

      PreKeyBundle preKeyBundle = new PreKeyBundle(
              registrationId,
              deviceId,
              preKeyId,
              preKey,
              signedPreKeyId,
              signedPreKeyPublic,
              signedPreKeySignature,
              identityKey
              );
      // Build a session with a PreKey retrieved from the server.
      sessionBuilder.process(preKeyBundle);
      promise.resolve(true);
    } catch (IOException e) {
      Log.d(ProtocolStorage.LOGTAG, "Encountered IOException for the recepient " + recipientId);
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    } catch (InvalidKeyException e) {
      Log.d(ProtocolStorage.LOGTAG, "Encountered InvalidKeyException for the recepient " + recipientId);
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    } catch (UntrustedIdentityException e) {
      Log.d(ProtocolStorage.LOGTAG, "Encountered UntrustedIdentityException for the recepient " + recipientId);
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
    }
  }

  @ReactMethod
  public void encrypt (String message, String recipientId, int deviceId, Promise promise) {
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(recipientId, deviceId);
    SessionCipher sessionCipher = new SessionCipher(protocolStorage, signalProtocolAddress);
    CiphertextMessage messageEncryped = null;
    try {
      messageEncryped = sessionCipher.encrypt(message.getBytes("UTF-8"));
      promise.resolve(Base64.encodeToString(messageEncryped.serialize(), Base64.DEFAULT));
    } catch (UntrustedIdentityException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void decrypt (String message, String recipientId, int deviceId, Promise promise) {
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(recipientId, deviceId);
    SessionCipher sessionCipher = new SessionCipher(protocolStorage, signalProtocolAddress);
    byte[] messageDecrypted = null;
    try {
      messageDecrypted = sessionCipher.decrypt(new PreKeySignalMessage(Base64.decode(message, Base64.DEFAULT)));
      promise.resolve(new String (messageDecrypted));
    } catch (UntrustedIdentityException e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
      e.printStackTrace();
    } catch (LegacyMessageException e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
      e.printStackTrace();
    } catch (InvalidMessageException e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
      e.printStackTrace();
    } catch (DuplicateMessageException e) {
      promise.reject(RN_LIBSIGNAL_ERROR, e.getMessage());
      e.printStackTrace();
    } catch (InvalidVersionException e) {
      e.printStackTrace();
    } catch (InvalidKeyIdException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
  }
}