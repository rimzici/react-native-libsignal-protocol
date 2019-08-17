
#import "RNLibsignalProtocol.h"

@implementation RNLibsignalProtocol

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(generateIdentityKeyPair:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(generateRegistrationId:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(generatePreKeys:(nonnull NSNumber *)startId
keysCount:(nonnull NSNumber *)count
resolver:(RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(generateSignedPreKey:
(NSDictionary *)identityKeyPair
signedKeyId:(nonnull NSNumber *)signedKeyId 
resolver:(RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(buildSession:
recipientId:(NSString *)recipientId 
deviceId:(nonnull NSNumber *)deviceId 
retrievedPreKeyBundle:(NSDictionary *)retrievedPreKeyBundle 
resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(encrypt:
message:(NSString *)message 
recipientId:(NSString *)recipientId 
deviceId:(nonnull NSNumber *)deviceId 
resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

RCT_EXPORT_METHOD(decrypt:(NSString *)message 
recipientId:(NSString *)recipientId 
deviceId:(nonnull NSNumber *)deviceId 
resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *dict = @{@"publicKey":@"dummy"};
    if (dict) {
        resolve(dict);
    } else {
        NSError *error = nil;
        reject(@"no_events", @"There were no events", error);
    }
}

@end
  