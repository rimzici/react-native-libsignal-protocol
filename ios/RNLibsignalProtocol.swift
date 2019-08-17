//
//  RNLibsignalProtocol.swift
//  RNLibsignalProtocol
//
//  Created by Rimnesh Fernandez on 17/08/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

import Foundation
import SignalProtocol

@objc(RNLibsignalProtocol)
class RNLibsignalProtocol: NSObject {

    @objc func generateIdentityKeyPair(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        if let data = try? SignalCrypto.generateIdentityKeyPair() {
            print("TEST data", data);
            // print("TEST data.base64EncodedString", data.base64EncodedString());
            // resolve(data.base64EncodedString());
        } else {
            reject("RNLibsignal Error", "Cannot generate key pair", nil);
        }
    }

    @objc func testCall(_ value: String) {
        print("TEST value", value);
        // var data = Data(base64Encoded: value)!
        // print("TEST value data", data);
        // print("TEST value.base64EncodedString", data.base64EncodedString());
    }
}
