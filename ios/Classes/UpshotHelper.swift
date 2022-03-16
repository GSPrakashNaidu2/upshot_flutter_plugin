//
//  UpshotHelper.swift
//  Runner
//
//  Created by Vinod K on 8/4/21.
//

import Foundation
import Upshot

 class UpshotHelper: NSObject {

     static var defaultHelper = UpshotHelper()

     func initializeUsingOptions(options: [String: Any]) {

         BrandKinesis.sharedInstance().initialize(options: options, delegate: self)
     }

     func initializeUsingConfigFile() {

         BrandKinesis.sharedInstance().initialize(withDelegate: self)
     }

     func dispatchEventWithTime(time: Int) {
        BrandKinesis.sharedInstance().dispatchInterval = time
     }

     func closeEventForId(eventId: String) {
        BrandKinesis.sharedInstance().closeEvent(forID: eventId)
     }

     func removeTutorial() {
        BrandKinesis.sharedInstance().removeTutorials()
     }

     func setValueAndClose(payload: [String: Any], eventId: String) {
        BrandKinesis.sharedInstance().setValueAndClose(payload, forEvent: eventId)
     }

     func terminate() {
         BrandKinesis.sharedInstance().terminate()
     }

     func createPageViewEvent(pageName: String) -> String? {

         return BrandKinesis.sharedInstance().createEvent(BKPageViewNative, params: [BKCurrentPage: pageName], isTimed: true)
     }

     func createCustomEvent(eventName: String, payload: [String: Any], isTimed: Bool) -> String? {
        print("payload\(payload)")

         return BrandKinesis.sharedInstance().createEvent(eventName, params: payload, isTimed: isTimed)
     }

     func createAttributionEvent(payload: [String: Any]) -> String? {

        return BrandKinesis.sharedInstance().createAttributionEvent(payload)
     }

     func createLocationEvent(payload: [String: Any]) {

         if let latitude = payload["latitude"] as? NSNumber,
             let longitude = payload["longitude"] as? NSNumber {

            let lat = CGFloat(truncating: latitude)
            let longi = CGFloat(truncating: longitude)
                 BrandKinesis.sharedInstance().createLocationEvent(lat, longitude: longi)
             }
     }

    func updateUserDetails(details: [String: Any]) {

        buildUserDetails(details: details)
    }

     func showActivity(activityType: BKActivityType, tag: String) {

         BrandKinesis.sharedInstance().showActivity(with: activityType, andTag: tag)
     }

     func getUserBadges() -> String? {

         let userBadges = BrandKinesis.sharedInstance().getUserBadges()
         if let badges = try? JSONSerialization.data(withJSONObject: userBadges, options: .prettyPrinted) {

             let badgesString = String(data: badges, encoding: .utf8)
             return badgesString
         }
         return nil
     }

     func getSDKVersion() -> String? {

         return BrandKinesis.sharedInstance().version
     }

     func getVisualInbox() {

         BrandKinesis.sharedInstance().fetchInboxInfo { inbox in
             if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

                 let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)
                 upshotChannel.invokeMethod("upshotCampaignDetails", arguments: inbox)
             }
         }
     }

    func buildUserDetails(details: [String: Any]) {

        let userInfo = BKUserInfo()
        let externalId = BKExternalId()
        let dob = BKDob()

        var others: [String: Any] = [:]

        if let lat = details["lat"] as? Double, let lng = details["lng"] as? Double {
            let location = CLLocation(latitude: CLLocationDegrees(lat), longitude: CLLocationDegrees(lng))
            userInfo.location = location
        }

        for key in details.keys {

            let type = getInfoTypeForKey(key: key)

            if type == "BKDob" {
                dob.setValue(details[key], forKey: key)

            } else if type == "BKExternalId" {
                externalId.setValue(details[key], forKey: key)

            } else if type == "UserInfo" {
                userInfo.setValue(details[key], forKey: key)

            } else {
                others[key] = details[key]
            }
        }

        userInfo.others = others
        userInfo.externalId = externalId
        userInfo.dateOfBirth = dob
        userInfo.build { status, error in

        }
    }

    func getInfoTypeForKey(key: String) -> String {

        let externalIdKeys = ["appuID",
                              "facebookID",
                              "twitterID",
                              "foursquareID",
                              "linkedinID",
                              "googleplusID",
                              "enterpriseUID",
                              "advertisingID",
                              "instagramID",
                              "pinterest"]

        let dobKeys = ["year", "month", "day"]

        let userInfoKeys = ["lastName",
                            "middleName",
                            "firstName",
                            "language",
                            "occupation",
                            "qualification",
                            "maritalStatus",
                            "phone",
                            "localeCode",
                            "userName",
                            "email",
                            "age",
                            "gender",
                            "email_opt",
                            "sms_opt",
                            "push_opt",
                            "data_opt",
                            "ip_opt"]

        if externalIdKeys.contains(key) {
            return "BKExternalId"
        }

        if dobKeys.contains(key) {
            return "BKDob"
        }

        if userInfoKeys.contains(key) {
            return "UserInfo"
        }
        return "Others"
    }

 }

 extension UpshotHelper: BrandKinesisDelegate {

     func brandKinesisAuthentication(_ brandKinesis: BrandKinesis, withStatus status: Bool, error: Error?) {
         if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

             let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)

             let s = status ? "Success" : "Fail"
             var authStatus = ["status": s, "errorMessage": ""] as [String : Any]
             if !status {
                 authStatus = ["status": s, "errorMessage": error?.localizedDescription ?? "No Error"] as [String : Any]
             }
             upshotChannel.invokeMethod("upshotAuthenticationStatus", arguments: authStatus)
         }
     }

     func brandKinesisActivityDidAppear(_ brandKinesis: BrandKinesis, for activityType: BKActivityType) {
         if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

             let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)

             let activityPayload = ["activityType": activityType.rawValue] as [String : Any]

             upshotChannel.invokeMethod("upshotActivityDidAppear", arguments: activityPayload)
         }
     }

     func brandKinesisActivityDidDismiss(_ brandKinesis: BrandKinesis, for activityType: BKActivityType) {
         if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

             let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)

             let activityPayload = ["activityType": activityType.rawValue] as [String : Any]
             upshotChannel.invokeMethod("upshotActivityDidDismiss", arguments: activityPayload)
         }
     }

     func brandKinesisActivity(_ activityType: BKActivityType, performedActionWithParams params: [AnyHashable : Any]) {

         if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

             let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)

             let activityPayload = ["activityType": activityType.rawValue, "params": params] as [String : Any]
             upshotChannel.invokeMethod("upshotActivityDeeplink", arguments: activityPayload)
         }
     }

     func brandkinesisErrorLoadingActivity(_ brandkinesis: BrandKinesis, withError error: Error?) {
         print(error?.localizedDescription ?? "No Error")
         if let controller : FlutterViewController = UIApplication.shared.keyWindow?.rootViewController as? FlutterViewController {

             let upshotChannel = FlutterMethodChannel(name: "flutter.native/upshotHelper", binaryMessenger: controller.binaryMessenger)

             let activityPayload = ["error": error?.localizedDescription ?? ""] as [String : Any]
             upshotChannel.invokeMethod("upshotActivityError", arguments: activityPayload)
         }
     }

 }

