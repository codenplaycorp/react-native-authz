#import "RNAuthz.h"
#import "RCTUtils.h"
#import "RCTLog.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"

@implementation RNAuthz

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (void) doCloseAfterLoginOnNotification:(NSNotification *) notification
{
    NSLog(@"[SafariView] SafariView dismissed.");

    [self.safariView dismissViewControllerAnimated: true completion: nil];
    [self.bridge.eventDispatcher sendAppEventWithName:@"InAppBrowserTabOnDismiss" body:notification.userInfo];
}

-(void) safariViewControllerDidFinish:(nonnull SFSafariViewController *)controller
{
    NSLog(@"[SafariView] SafariView dismissed.");

    [controller dismissViewControllerAnimated: true completion:nil];
    [self.bridge.eventDispatcher sendAppEventWithName:@"InAppBrowserTabOnDismiss" body:nil];
}

RCT_EXPORT_METHOD(openURL:(NSString *)urlString callback:(RCTResponseSenderBlock)callback)
{
    NSLog(@"[SafariView] SafariView opened.");

    NSURL *url = [[NSURL alloc] initWithString:urlString];

    self.safariView = [[SFSafariViewController alloc] initWithURL:url];
    self.safariView.modalPresentationStyle = UIModalPresentationOverFullScreen;
    self.safariView.delegate = self;

    UIViewController *ctrl = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    [ctrl presentViewController:self.safariView animated:YES completion:nil];
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"InAppBrowserTabOnShow" body:nil];

    [[NSNotificationCenter defaultCenter]
        addObserver:self
        selector:@selector(doCloseAfterLoginOnNotification:)
        name:@"AuthZRedirectNotification"
        object:nil];
}

RCT_EXPORT_METHOD(isAvailable:(RCTResponseSenderBlock)callback)
{
    NSLog(@"[SafariView] SafariView availabre.");

    if ([SFSafariViewController class]) {
        return callback(@[[NSNull null], @true]);
    } else {
        return callback(@[RCTMakeError(@"[SafariView] SafariView is unavailable.", nil, nil)]);
    }
}

RCT_EXPORT_METHOD(dismiss)
{
    NSLog(@"[SafariView] SafariView dismissed.");

    [self safariViewControllerDidFinish:self.safariView];
}

RCT_EXPORT_METHOD(hasSupportInAppBrowserTab:(RCTResponseSenderBlock)callback)
{
    return callback(@[[NSNull null], @true]);
}

@end
