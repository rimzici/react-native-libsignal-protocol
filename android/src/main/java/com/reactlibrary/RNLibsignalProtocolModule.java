
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.util.KeyHelper;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import android.util.Log;
import android.util.Base64;

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
  public void generateIdentityKeyPair(Callback successCallback) {
    IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
    String publicKey = Base64.encodeToString(identityKeyPair.getPublicKey().serialize(), Base64.NO_WRAP);
    String privateKey = Base64.encodeToString(identityKeyPair.getPrivateKey().serialize(), Base64.NO_WRAP);
    WritableMap keyPairMap = Arguments.createMap();
    keyPairMap.putString("publicKey", publicKey);
    keyPairMap.putString("privateKey", privateKey);
    successCallback.invoke(keyPairMap);
  }
}