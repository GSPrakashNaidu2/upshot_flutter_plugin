import Flutter
import UIKit
import Upshot

public class SwiftFlutterUpshotPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_upshot_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterUpshotPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
             switch call.method {                         

             case "initializeUsingConfigFile":
                 UpshotHelper.defaultHelper.initializeUsingConfigFile()

             case "initializeUsingOptions":
                 if let arguments = call.arguments as? [String: Any] {
                     UpshotHelper.defaultHelper.initializeUsingOptions(options: arguments)
                 }

             case "terminate":
                 UpshotHelper.defaultHelper.terminate()

             case "createPageViewEvent":
                 if let pageName = call.arguments as? String {
                     let eventId = UpshotHelper.defaultHelper.createPageViewEvent(pageName: pageName)
                     result(eventId)
                 }

             case "createCustomEvent":

                 if let arguments = call.arguments as? [String: Any],
                    let eventName = arguments["eventName"] as? String,
                    let params = arguments["data"] as? [String: Any],
                    let isTimed = arguments["isTimed"] as? Bool {
                     let eventId = UpshotHelper.defaultHelper.createCustomEvent(eventName: eventName, payload: params, isTimed: isTimed)
                     result(eventId)
                 }

             case "createAttributionEvent":
                 if let payload = call.arguments as? [String: Any] {
                    let eventId = UpshotHelper.defaultHelper.createAttributionEvent(payload: payload)
                    result(eventId)
                 }

             case "createLocationEvent":
                 if let payload = call.arguments as? [String: Any] {
                    UpshotHelper.defaultHelper.createLocationEvent(payload: payload)
                 }

             case "sendUserDetails":
                if let payload = call.arguments as? [String: Any] {
                    UpshotHelper.defaultHelper.updateUserDetails(details: payload)
                }

             case "showActivity":
                 if let arguments = call.arguments as? [String: Any],
                    let activityType = arguments["activityType"] as? Int,
                    let type = BKActivityType.init(rawValue: activityType),
                    let tag = arguments["tag"] as? String {
                     UpshotHelper.defaultHelper.showActivity(activityType: type, tag: tag)
                 }
             case "getBadges":
                 let badges = UpshotHelper.defaultHelper.getUserBadges()
                 result(badges)
             case "getSDKVersion":
                 let version = UpshotHelper.defaultHelper.getSDKVersion()
                 result(version)
             case "dispatchEventWithTime":
                if let time = call.arguments as? [Int: Any]{
                UpshotHelper.defaultHelper.dispatchEventWithTime(time: time)
                }
             case "removeTutorial":
                UpshotHelper.defaultHelper.removeTutorial()
             case "closeEventForId":
             if let eventId = call.arguments as? [String: Any]{
                UpshotHelper.defaultHelper.closeEventForId(eventId: eventId)
                }
             case "setValueAndClose":
             if let eventId = call.arguments as? [String: Any],
                   let payload = arguments["data"] as? String {
                UpshotHelper.defaultHelper.setValueAndClose(payload: payload,eventId: eventId)
                }
             default:
                 result(FlutterMethodNotImplemented)
             }
  }
}
