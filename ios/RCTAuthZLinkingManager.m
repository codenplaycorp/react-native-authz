#import "RCTAuthZLinkingManager.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "RCTUtils.h"

NSString *const RCTAuthZNotification = @"AuthZRedirectNotification";

@implementation RCTAuthZLinkingManager

+ (BOOL)application:(UIApplication *)application openURL:(NSURL *)URL sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    NSDictionary<NSString *, id> *payload = @{@"url": URL.absoluteString};

    [[NSNotificationCenter defaultCenter] postNotificationName:RCTAuthZNotification object:self userInfo:payload];
    return YES;
}

+ (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray *))restorationHandler
{
    if ([userActivity.activityType isEqualToString:NSUserActivityTypeBrowsingWeb]) {
        NSDictionary *payload = @{@"url": userActivity.webpageURL.absoluteString};

        [[NSNotificationCenter defaultCenter] postNotificationName:RCTAuthZNotification object:self userInfo:payload];
    }
    return YES;
}

@end