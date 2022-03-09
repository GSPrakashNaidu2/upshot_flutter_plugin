package com.example.flutter_upshot_plugin;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.brandkinesis.BKProperties;
import com.brandkinesis.BKUserInfo;
import com.brandkinesis.BrandKinesis;
import com.brandkinesis.activitymanager.BKActivityTypes;
import com.brandkinesis.callback.BrandKinesisCallback;
import com.brandkinesis.utils.BKAppStatusUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterUpshotPlugin */
public class FlutterUpshotPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  HashMap<String, List<HashMap<String, Object>>> badgesResult;
  BKActivityTypes activityTypeResult;
  Map<String, Object> actionDataResult;
  HashMap<String, Object> showActivityData;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_upshot_plugin");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();
  }

  private void setUpshotGlobalCallback() {
    BrandKinesis bkInstance = BrandKinesis.getBKInstance();
    bkInstance.setBrandkinesisCallback(new BrandKinesisCallback() {
      @Override
      public void userStateCompletion(boolean status) {

      }

      @Override
      public void onUserInfoUploaded(boolean uploadSuccess) {

      }

      @Override
      public void onMessagesAvailable(List<HashMap<String, Object>> message) {

      }

      @Override
      public void brandkinesisCampaignDetailsLoaded() {

      }

      @Override
      public void onBadgesAvailable(HashMap<String, List<HashMap<String, Object>>> badges) {
        Log.e("Badges","Badges are: \n" + badges.values());
        badgesResult = badges;
      }

      @Override
      public void onAuthenticationError(String errorMsg) {

      }

      @Override
      public void onAuthenticationSuccess() {
        // TODO

      }

      @Override
      public void onActivityError(int error) {

      }

      @Override
      public void onActivityCreated(BKActivityTypes activityType) {

      }

      @Override
      public void onActivityDestroyed(BKActivityTypes activityType) {

      }

      @Override
      public void getBannerView(View bannerView, String tag) {

      }

      @Override
      public void onErrorOccurred(int errorCode) {

      }

      @Override
      public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes activityType, Map<String, Object> actionData) {
//        activityTypeResult = activityType;
//        actionDataResult = actionData;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          showActivityData.putIfAbsent("activityType",activityType);
          showActivityData.putIfAbsent("actionData",activityType);
        }
      }
    });
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    if(call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    /**
     *................................. initialiseBrandKinesis ..........................
     *  */
    else if(call.method.equals("initialiseBrandKinesis")){
      setUpshotGlobalCallback();
      BrandKinesis.initialiseBrandKinesis(context, null);
      BrandKinesis brandKinesis =  BrandKinesis.getBKInstance();

      BKAppStatusUtil.BKAppStatusListener listener = new BKAppStatusUtil.BKAppStatusListener() {
        @Override
        public void onAppComesForeground(Activity activity) {
          Log.e("App status==", "Foreground:: " + brandKinesis);
        }

        @Override
        public void onAppGoesBackground() {
          Log.e("App status==", "Background");
        }

        @Override
        public void onAppRemovedFromRecentsList() {
          Log.e("App status==", "removed from recent list");
        }
      };
      BKAppStatusUtil.getInstance().register(context, listener);

    }
    /**
     *................................. initialiseBrandKinesisWithOptions ..........................
     */
    else if(call.method.equals("initialiseBrandKinesisWithOptions")){
      setUpshotGlobalCallback();
      HashMap<String , Object> data = call.argument("data");
      String appId = call.argument("appId");
      String ownerId = call.argument("ownerId");
      assert data != null;
      assert appId != null;
      assert ownerId != null;
        Bundle bundle = new Bundle();
        data.put(BKProperties.BK_APPLICATION_ID, appId);
        data.put(BKProperties.BK_APPLICATION_OWNER_ID, ownerId);
        data.put(BKProperties.BK_FETCH_LOCATION, true);
        data.put(BKProperties.BK_ENABLE_DEBUG_LOGS,  false);
        data.put(BKProperties.BK_USE_EXTERNAL_STORAGE, true);
        // intialize BrandKinesis (BKAuthCallback will provide the status of initialisation of Upshot)
        BrandKinesis.initialiseBrandKinesis(context, bundle, null);
      BrandKinesis brandKinesis =  BrandKinesis.getBKInstance();

      BKAppStatusUtil.BKAppStatusListener listener = new BKAppStatusUtil.BKAppStatusListener() {
        @Override
        public void onAppComesForeground(Activity activity) {
          Log.e("App status==", "Foreground:: " + brandKinesis);
        }

        @Override
        public void onAppGoesBackground() {
          Log.e("App status==", "Background");
        }

        @Override
        public void onAppRemovedFromRecentsList() {
          Log.e("App status==", "removed from recent list");
        }
      };
      BKAppStatusUtil.getInstance().register(context, listener);
    }
    /**
     *............................................... createEvent ..................................
     */
    else if(call.method.equals("createEvent")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String eventName = call.argument("eventName");
      HashMap<String , Object> data = call.argument("data");
      assert eventName != null;
      assert data != null;
      Log.e("Event Name is:","Event Name is:" + eventName);
      Log.e("Data is:","Data is:" + data.values());
      String eventID =  bkInstance.createEvent(eventName, data, false);
      if (eventID.isEmpty()){
        result.error("401","Not created",0);
        Log.e("Creating Event","Event not created");
      }else{
        result.success(eventID);
        Log.e("Creating Event","Event created");
      }
    }
    /**
     *....................................... createLocationEvent ..................................
     */
    else if(call.method.equals("createLocationEvent")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String latitude = call.argument("latitude");
      String longitude = call.argument("longitude");
      assert latitude != null;
      assert longitude != null;
      Log.e("Latitude",latitude);
      Log.e("Longitude",longitude);

       String eventId =  bkInstance.createLocationEvent(Double.parseDouble(latitude), Double.parseDouble(longitude));

      if (!eventId.isEmpty()) {
        result.success("sending location to upshot");
        Log.e("Location Event","Location event created" + eventId);
      } else {
        result.error("401","Location event not created",0);
        Log.e("Location Event","Location event not created");
      }
    }
    /**
     *................................ setValueAndClose ............................................
     */
    else if(call.method.equals("setValueAndClose")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String eventName = call.argument("eventName");
      HashMap<String , Object> data = call.argument("data");
      assert eventName != null;
      assert data != null;
      String eventIdValue = bkInstance.createEvent(eventName, data, true);
      BrandKinesis.getBKInstance().closeEvent(eventIdValue, data);
      Log.e("Set & Close","Set And Closed the Event");
      result.success("Set And Closed the Event");
    }
    /**
     *....................................... closeEventForId ......................................
     */
    else if(call.method.equals("closeEventForId")){
      String eventId = call.argument("eventId");
      assert eventId != null;
      BrandKinesis.getBKInstance().closeEvent(eventId);
      Log.e("Close","Closed the Event");
      result.success("Closed the Event");
    }
    /**
     *....................................... dispatchEventWithTime ................................
     */
    else if(call.method.equals("dispatchEventWithTime")){
      //         Pass the time how much time want to dispatch
//        Dispatch interval Min time value: 10 secs
//        Dispatch interval Max time value: 120 secs
      long time = call.argument("time");
      BrandKinesis.getBKInstance().setDispatchEventTime(time);
    }
    /**
     *....................................... removeTutorial .......................................
     */
    else if(call.method.equals("removeTutorial")) {
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      bkInstance.removeTutorial(context);
      result.success("Removed the tutorial");
    }
    /**
     *...................................... terminateUpshot .......................................
     */
    else if(call.method.equals("terminateUpshot")) {
      BrandKinesis bk = BrandKinesis.getBKInstance();
      bk.terminate(context);
    }
    /**
     *...................................... createPageViewEvent .......................................
     */
    else if(call.method.equals("createPageViewEvent")) {
      HashMap<String, Object> data = call.argument("data");
      String pageName = call.argument("pageName");
      assert  pageName != null;
      assert data != null;
      data.put(BrandKinesis.BK_CURRENT_PAGE, pageName);
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String eventID = bkInstance.createEvent(BKProperties.BKPageViewEvent.NATIVE, data, true);
      if (!eventID.isEmpty()) {
        result.success("createPageViewEvent created");
        Log.e("createPageViewEvent","createPageViewEvent created" + eventID);
      } else {
        result.error("401","createPageViewEvent not created",0);
        Log.e("createPageViewEvent","createPageViewEvent not created");
      }
      result.success(eventID);
    }
    /**
     *...................................... createPageViewEvent .......................................
     */
    else if(call.method.equals("createAttributionEvent")) {
//      String appToken = "{ADJUST_APP_TOKEN}";
//      String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
//      AdjustConfig config = new AdjustConfig(this, appToken, environment);
//      config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
//        @Override
//        public void onAttributionChanged(AdjustAttribution attribution) {
//        }
//      });
//      Adjust.onCreate(config);
//
//      new OnAttributionChangedListener() {
//        @Override
//        public void onAttributionChanged(AdjustAttribution attribution) {
//          String bkAttributionSource = "adjust";
//          String bkUtmSource = attribution.network;
//          String bkUtmMedium = attribution.trackerName;
//          String bkUtmCampaign = attribution.campaign;
//          HashMap<Object, Object> data = new HashMap<>();
//          data.put(BKProperties.BKAttributionEventType.BK_ATTRIBUTION_SOURCE, bkAttributionSource);
//          data.put(BKProperties.BKAttributionEventType.BK_UTM_SOURCE, bkUtmSource);
//          data.put(BKProperties.BKAttributionEventType.BK_UTM_MEDIUM, bkUtmMedium);
//          data.put(BKProperties.BKAttributionEventType.BK_UTM_CAMPAIGN, bkUtmCampaign);
//          BrandKinesis.getBKInstance().createAttributionEvent(data);
//        }
//      };
    }
    else if(call.method.equals("sendUserDetails")){
      HashMap<String , Object> data = call.argument("data");
      HashMap<String , Object> others = call.argument("others");
      Bundle userInfo = new Bundle();
      assert data != null;
      Log.e(" Data :","Data is:" + (String) data.get("first_name"));
      userInfo.putString(BKUserInfo.BKUserData.FIRST_NAME, (String) data.get("first_name"));
      userInfo.putInt(BKUserInfo.BKUserData.AGE, (int) data.get("age"));
      userInfo.putInt(BKUserInfo.BKUserData.GENDER, BKUserInfo.BKGender.MALE);
      userInfo.putString(BKUserInfo.BKUserData.EMAIL,(String) data.get("mail"));
      userInfo.putInt(BKUserInfo.BKUserDOBdata.DAY, (int) data.get("day"));
      userInfo.putInt(BKUserInfo.BKUserDOBdata.MONTH,(int) data.get("month"));
      userInfo.putInt(BKUserInfo.BKUserDOBdata.YEAR, (int) data.get("year"));
      userInfo.putString(BKUserInfo.BKExternalIds.FACEBOOK, (String) data.get("facebookId"));
      userInfo.putString(BKUserInfo.BKExternalIds.TWITTER, (String) data.get("twitterId"));
      userInfo.putString(BKUserInfo.BKExternalIds.APPUID,(String) data.get("appUID"));
// OTHERS

      Objects.requireNonNull(others).put("city" , (String) others.get("city"));
      Objects.requireNonNull(others).put("state" ,(String) others.get("state"));
      userInfo.putSerializable(BKUserInfo.BKUserData.OTHERS , others);

      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        bkInstance.setUserInfoBundle(userInfo, null);

    }
    /**
     *...................................... createCustomEvent .......................................
     */
    else if(call.method.equals("createCustomEvent")) {
      try {
        BrandKinesis bkInstance = BrandKinesis.getBKInstance();
        HashMap<String, Object> data = call.argument("data");
        String eventName = call.argument("eventName");
        boolean isTimed = call.argument("isTimed");
        assert eventName != null;
        assert data != null;
        String eventId = bkInstance.createEvent(eventName, data, isTimed);
        if (eventId != null) {
          Log.e("200", eventId);
          result.success(eventId);
        } else {
          Log.e("401", "Not able to create an event");
          result.error("401","Custom Event Not Created",0);
        }
      }catch (Exception e) {

      }
    }
    /**
     *........................................ get Badges ..........................................
     */
    else if(call.method.equals("getBadges")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      bkInstance.getBadges(null);
      result.success(badgesResult);
    }
    /**
     *...................................... showActivity .......................................
     */
    else if(call.method.equals("showActivity")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String tag = call.argument("tag");
      Log.e("Argument","Tag is" + tag);
      bkInstance.getActivity(context, BKActivityTypes.ACTIVITY_ANY, tag);
      Log.e("Activity Data","Activity Data" + showActivityData.values().toString());
      result.success(showActivityData);
    }
    else {
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
