package com.reactlibrary.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;

public class PreferenceManager {
    SharedPreferences pref;
    public PreferenceManager(Context context) {
        pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
    }

    public void setLocalRegistrationId(int id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("registrationId", id);
        editor.commit();
    }
    public int getLocalRegistrationId() {
        return pref.getInt("registrationId", -1);
    }
    public void setIdentityKeyPair(IdentityKeyPair identityKeyPair) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("identityKeyPair", Base64.encodeToString(identityKeyPair.serialize(), Base64.DEFAULT));
        editor.commit();
    }
    public IdentityKeyPair getIdentityKeyPair() {
        String identityKeyPairString = pref.getString("identityKeyPair", null);
        IdentityKeyPair identityKeyPair = null;
        try {
            identityKeyPair = new IdentityKeyPair(Base64.decode(identityKeyPairString, Base64.DEFAULT));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return identityKeyPair;
    }
}
