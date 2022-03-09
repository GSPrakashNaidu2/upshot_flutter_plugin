#import "FlutterUpshotPlugin.h"
#if __has_include(<flutter_upshot_plugin/flutter_upshot_plugin-Swift.h>)
#import <flutter_upshot_plugin/flutter_upshot_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_upshot_plugin-Swift.h"
#endif

@implementation FlutterUpshotPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterUpshotPlugin registerWithRegistrar:registrar];
}
@end
