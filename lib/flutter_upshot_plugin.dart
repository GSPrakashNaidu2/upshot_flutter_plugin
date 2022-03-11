import 'dart:async';
import 'dart:collection';
import 'dart:developer';

import 'package:flutter/services.dart';

class FlutterUpshotPlugin {
  static const MethodChannel _channel = MethodChannel('flutter_upshot_plugin');

  /// The method which will return the platform version of the device
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Initialize the BrandKinesis method
  /// Automatically initialize the SDK in main.dart : initMethod
  /// This method will initialize the BrandKineses without any Options like AppId, OwnerId, etc,.
  static Future<void> initialiseBrandKinesis() async {
    await _channel.invokeMethod("initialiseBrandKinesis");
    return;
  }

  /// Initialize the BrandKinesis method
  /// This method will not call default, If you want to call this you have to pass below params
  /// This method will initialize the BrandKineses with Options like AppId, OwnerId, etc,.
  static Future<void> initialiseBrandKinesisWithOptions(
      String appId,
      String ownerId,
      bool fetchLocation,
      bool useExternalStorage,
      bool enableDebugLogs) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("appId", () => appId);
    values.putIfAbsent("ownerId", () => ownerId);
    values.putIfAbsent("fetchLocation", () => fetchLocation);
    values.putIfAbsent("useExternalStorage", () => useExternalStorage);
    values.putIfAbsent("enableDebugLogs", () => enableDebugLogs);
    await _channel.invokeMethod("initialiseBrandKinesisWithOptions", values);
    return;
  }

  /// This Method is to create an event
  /// Required eventName and Data as arguments to create an event
  /// If eventId is null event is not created else event will be created and return to the flutter methods
  static Future<String?> createEvent(
      String? eventName, HashMap<String, Object>? data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("eventName", () => eventName);
    log(values.toString());
    String? eventId = await _channel.invokeMethod("createEvent", values);
    log(eventId.toString() + "\t Event Created");
    return eventId;
  }

  /// This Method is to create a Location event
  /// Required latitude and longitude as arguments to create a Location event
  /// If eventId is null event is not created else event will be created and return to the flutter methods
  /// The Location details will be sent to Upshot
  static Future<String> createLocationEvent(
      String? latitude, String? longitude) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("latitude", () => latitude);
    values.putIfAbsent("longitude", () => longitude);
    String eventId = await _channel.invokeMethod("createLocationEvent", values);
    return eventId;
  }

  /// This Method will take values and close the event
  /// Required data, isTimed, and Data as arguments to close an event
  static Future<void> setValueAndClose(
      String? eventName, HashMap<String, Object>? data, bool isTimed) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("isTimed", () => isTimed);
    values.putIfAbsent("eventName", () => eventName);
    await _channel.invokeMethod("setValueAndClose", values);
    return;
  }

  /// To close the event with id
  /// EventId should pass to close the event : Mandatory
  static Future<void> closeEventForId(String? eventId) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("eventId", () => eventId);
    await _channel.invokeMethod("closeEventForId", values);
    return;
  }

  static Future<void> dispatchEventWithTime(int time) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("time", () => time);
    await _channel.invokeMethod("dispatchEventWithTime", values);
    return;
  }

  /// To Remove Upshot tutorial
  static Future<void> removeTutorial() async {
    await _channel.invokeMethod("removeTutorial");
    return;
  }

  /// To terminate Upshot
  static Future<void> terminateUpshot() async {
    await _channel.invokeMethod("terminateUpshot");
    return;
  }

  /// This will create a page view event
  /// pageName, data, and isTimed are the required parameters to create an page view event
  static Future<String?> createPageViewEvent(
      String pageName, HashMap<String, Object> data, bool isTimed) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("isTimed", () => isTimed);
    values.putIfAbsent("pageName", () => pageName);
    String? eventId =
        await _channel.invokeMethod("createPageViewEvent", values);
    log(eventId.toString() + "Created Page View Event");
    return eventId;
  }

  /// This method will create an attribution event
  /// Need to pass 4 parameters to create this event
  /// attributionSource: Mandatory, utmSource: Mandatory, utmMedium: Mandatory, utmCampaign: Mandatory
  static Future<void> createAttributionEvent(String attributionSource,
      String utmSource, String utmMedium, String utmCampaign) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("attributionSource", () => attributionSource);
    values.putIfAbsent("utmSource", () => utmSource);
    values.putIfAbsent("utmMedium", () => utmMedium);
    values.putIfAbsent("utmCampaign", () => utmCampaign);
    await _channel.invokeMethod("createAttributionEvent", values);
    return;
  }

  /// Thi method is to send user details to Upshot
  /// Will be having two types of data 1. Personal details and 2. Others
  /// Other parameters ex: City, State etc,.
  static Future<void> sendUserDetails(
      HashMap<String, Object> data, HashMap<String, Object> others) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("others", () => others);
    log(values.toString());
    await _channel.invokeMethod("sendUserDetails", values);
  }

  /// This will create a custom event
  /// eventName, data, and isTimed are the required parameters to create a custom event
  static Future<String?> createCustomEvent(
      String eventName, bool isTimed, HashMap<String, Object> data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("eventName", () => eventName);
    values.putIfAbsent("isTimed", () => isTimed);
    String? eventId = await _channel.invokeMethod("createCustomEvent", values);
    log(eventId.toString() + "Created custom Event");
    return eventId;
  }

  /// This method will give us the activity and actionData
  /// Required parameter is tag : whichever activity you want to get/show
  static Future<HashMap<String, Object>> showActivity(String tag) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("tag", () => tag);
    HashMap<String, Object> activityData =
        await _channel.invokeMethod("showActivity", values);
    log(activityData.toString());
    return activityData;
  }

  /// To get the user badges from upshot
  /// No need to pass any parameters to get user badges
  static Future<HashMap<String, List<HashMap<String, Object>>>?>
      getBadges() async {
    var badges;
    try {
      badges = await _channel.invokeMethod("getBadges");
      log("badges.toString()");
      log(badges.toString());
      return badges;
    } catch (e) {}
  }
}
