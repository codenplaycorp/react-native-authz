#import "RCTBridgeModule.h"

@import SafariServices;

@interface RNAuthz : NSObject <RCTBridgeModule, SFSafariViewControllerDelegate>

@property (nonatomic) SFSafariViewController *safariView;

@end