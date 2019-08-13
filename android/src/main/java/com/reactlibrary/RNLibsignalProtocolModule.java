
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;

import android.util.Log;
import android.util.Base64;

import java.lang.Exception;
import java.util.List;


public class RNLibsignalProtocolModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNLibsignalProtocolModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNLibsignalProtocol";
  }

  @ReactMethod
  public void generateIdentityKeyPair(Callback successCallback, Callback errorCallback) {
    try {
      IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
      String publicKey = Base64.encodeToString(identityKeyPair.getPublicKey().serialize(), Base64.DEFAULT);
      String privateKey = Base64.encodeToString(identityKeyPair.getPrivateKey().serialize(), Base64.DEFAULT);
      String serializedKP = Base64.encodeToString(identityKeyPair.serialize(), Base64.DEFAULT);
      WritableMap keyPairMap = Arguments.createMap();
      keyPairMap.putString("publicKey", publicKey);
      keyPairMap.putString("privateKey", privateKey);
      keyPairMap.putString("serializedKP", serializedKP);

      successCallback.invoke(keyPairMap);
    } catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  @ReactMethod
  public void generateRegistrationId(Callback successCallback, Callback errorCallback) {
    try {
      int registrationId = KeyHelper.generateRegistrationId(false);
      successCallback.invoke(registrationId);
    } catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  @ReactMethod
  public void generatePreKeys(int startId, int count, Callback successCallback, Callback errorCallback) {
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
      }

      successCallback.invoke(preKeyMapsArray);
    } catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  @ReactMethod
  public void generateSignedPreKey(ReadableMap identityKeyPair, int signedKeyId, Callback successCallback, Callback errorCallback) {
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

      successCallback.invoke(signedPreKeyMap);
    } catch (Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }
}