
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNLibsignalProtocol, NSObject)
RCT_EXTERN_METHOD(generateIdentityKeyPair: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(testCall: (NSString *)value)
@end
  
