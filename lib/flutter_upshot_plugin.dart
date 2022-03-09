import 'dart:async';
import 'dart:collection';

import 'package:flutter/services.dart';

class FlutterUpshotPlugin {
  static const MethodChannel _channel = MethodChannel('flutter_upshot_plugin');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> initialiseBrandKinesis() async {
    await _channel.invokeMethod("initialiseBrandKinesis");
    print('Success : \n\n\n Initialised........');
    return;
  }

  static Future<void> initialiseBrandKinesisWithOptions(
      String appId, String ownerId) async {
    await _channel
        .invokeMethod("initialiseBrandKinesisWithOptions", [appId, ownerId]);
    print('Success : \n\n\n Initialised with options........');
    return;
  }

  static Future<String?> createEvent(
      String? eventName, HashMap<String, Object>? data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("eventName", () => eventName);
    print(values);
    String? eventId = await _channel.invokeMethod("createEvent", values);
    print(eventId.toString() + "Event Created");
    return eventId;
  }

  static Future<void> createLocationEvent(
      String? latitude, String? longitude) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("latitude", () => latitude);
    values.putIfAbsent("longitude", () => longitude);
    print(latitude.toString() + " : " + longitude.toString());
    await _channel.invokeMethod("createLocationEvent", values);
    return;
  }

  static Future<void> setValueAndClose(
      String? eventName, HashMap<String, Object>? data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("eventName", () => eventName);
    await _channel.invokeMethod("setValueAndClose", values);
    return;
  }

  static Future<void> closeEventForId(String? eventId) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("eventId", () => eventId);
    await _channel.invokeMethod("closeEventForId", values);
    return;
  }

  static Future<void> dispatchEventWithTime(double? time) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("time", () => time);
    await _channel.invokeMethod("dispatchEventWithTime", values);
    return;
  }

  static Future<void> removeTutorial() async {
    await _channel.invokeMethod("removeTutorial");
    return;
  }

  static Future<void> terminateUpshot() async {
    await _channel.invokeMethod("terminateUpshot");
    return;
  }

  static Future<String?> createPageViewEvent(
      String pageName, HashMap<String, Object> data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("pageName", () => pageName);
    String? eventId =
        await _channel.invokeMethod("createPageViewEvent", values);
    print(eventId.toString() + "Created Page View Event");
    return eventId;
  }

  static Future<void> createAttributionEvent() async {}

  static Future<void> sendUserDetails(
      HashMap<String, Object> data, HashMap<String, Object> others) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("others", () => others);
    print(values);
    await _channel.invokeMethod("sendUserDetails", values);
  }

  static Future<String?> createCustomEvent(
      String eventName, bool isTimed, HashMap<String, Object> data) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("data", () => data);
    values.putIfAbsent("eventName", () => eventName);
    values.putIfAbsent("isTimed", () => isTimed);
    String? eventId = await _channel.invokeMethod("createCustomEvent", values);
    print(eventId.toString() + "Created custom Event");
    return eventId;
  }

  static Future<HashMap<String, dynamic>> showActivity(String tag) async {
    Map<String, dynamic> values = <String, dynamic>{};
    values.putIfAbsent("tag", () => tag);
    HashMap<String, dynamic> activityData =
        await _channel.invokeMethod("showActivity", values);
    return activityData;
  }

  static Future<HashMap<String, List<HashMap<String, Object>>>?>
      getBadges() async {
    HashMap<String, List<HashMap<String, Object>>>? badges =
        await _channel.invokeMethod("getBadges");
    print(badges);
    return badges;
  }
}
