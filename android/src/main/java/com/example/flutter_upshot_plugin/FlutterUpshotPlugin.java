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
import com.brandkinesis.callback.BKActivityCallback;
import com.brandkinesis.callback.BKBadgeAccessListener;
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
  HashMap<String, Object> showActivityData;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_upshot_plugin");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();
  }

  BKAppStatusUtil.BKAppStatusListener listener = new BKAppStatusUtil.BKAppStatusListener() {
    @Override
    public void onAppComesForeground(Activity activity) {
      BrandKinesis.initialiseBrandKinesis(context, null);
      Log.e("App status==", "Foreground:: " );
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

    if(call.method.equals("getSDKVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    /**
     *................................. initialiseBrandKinesis ..........................
     *  */
    else if(call.method.equals("initializeUsingConfigFile")){
      setUpshotGlobalCallback();
      BrandKinesis.initialiseBrandKinesis(context, null);

      BKAppStatusUtil.getInstance().register(context, listener);

    }
    /**
     *................................. initialiseBrandKinesisWithOptions ..........................
     */
    else if(call.method.equals("initializeUsingOptions")){
      setUpshotGlobalCallback();
      HashMap<String , Object> data = call.argument("data");
      String appId = call.argument("appId");
      String ownerId = call.argument("ownerId");
      boolean fetchLocation = call.argument("fetchLocation");
      boolean enableDebugLogs = call.argument("enableDebugLogs");
      boolean useExternalStorage = call.argument("useExternalStorage");
      assert appId != null;
      assert ownerId != null;
        Bundle bundle = new Bundle();
        bundle.putString(BKProperties.BK_APPLICATION_ID, appId);
      bundle.putString(BKProperties.BK_APPLICATION_OWNER_ID, ownerId);
      bundle.putBoolean(BKProperties.BK_FETCH_LOCATION, fetchLocation);
      bundle.putBoolean(BKProperties.BK_ENABLE_DEBUG_LOGS,  enableDebugLogs);
      bundle.putBoolean(BKProperties.BK_USE_EXTERNAL_STORAGE, useExternalStorage);
        // intialize BrandKinesis (BKAuthCallback will provide the status of initialisation of Upshot)
        BrandKinesis.initialiseBrandKinesis(context, bundle, null);

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
      String eventID =  bkInstance.createEvent(eventName, data, false);
      if (eventID.isEmpty()){
        result.error("401","Not created",0);
      }else{
        result.success(eventID);
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
       String eventId =  bkInstance.createLocationEvent(Double.parseDouble(latitude), Double.parseDouble(longitude));

      if (!eventId.isEmpty()) {
        result.success(eventId);
      } else {
        result.error("401","Location event not created",0);
      }
    }
    /**
     *................................ setValueAndClose ............................................
     */
    else if(call.method.equals("setValueAndClose")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String eventName = call.argument("eventName");
      boolean isTimed = call.argument("isTimed");
      HashMap<String , Object> data = call.argument("data");
      assert eventName != null;
      assert data != null;
      String eventIdValue = bkInstance.createEvent(eventName, data, isTimed);
      BrandKinesis.getBKInstance().closeEvent(eventIdValue, data);
      if (eventIdValue.isEmpty()){
        result.error("401","Not able to close",0);
      }else{
        result.success("Set And Closed the Event");
      }
    }
    /**
     *....................................... closeEventForId ......................................
     */
    else if(call.method.equals("closeEventForId")){
      String eventId = call.argument("eventId");
      assert eventId != null;
      BrandKinesis.getBKInstance().closeEvent(eventId);
      result.success("Closed the Event");
    }
    /**
     *....................................... dispatchEventWithTime ................................
     */
    else if(call.method.equals("dispatchEventWithTime")){
      //         Pass the time how much time want to dispatch
//        Dispatch interval Min time value: 10 secs
//        Dispatch interval Max time value: 120 secs
      int time = call.argument("time");
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
    else if(call.method.equals("terminate")) {
      BrandKinesis bk = BrandKinesis.getBKInstance();
      bk.terminate(context);
    }
    /**
     *...................................... createPageViewEvent .......................................
     */
    else if(call.method.equals("createPageViewEvent")) {
      HashMap<String, Object> data = call.argument("data");
      boolean isTimed = call.argument("isTimed");
      String pageName = call.argument("pageName");
      assert  pageName != null;
      assert data != null;
      data.put(BrandKinesis.BK_CURRENT_PAGE, pageName);
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String eventID = bkInstance.createEvent(BKProperties.BKPageViewEvent.NATIVE, data, isTimed);
      if (!eventID.isEmpty()) {
        result.success(eventID);
      } else {
        result.error("401","createPageViewEvent not created",0);
      }
    }
    /**
     *...................................... createAttributionEvent .......................................
     */
    else if(call.method.equals("createAttributionEvent")) {
          String bkAttributionSource = call.argument("attributionSource");
          String bkUtmSource = call.argument("utmSource");
          String bkUtmMedium = call.argument("utmMedium");
          String bkUtmCampaign = call.argument("utmCampaign");
          HashMap<String, String> data = new HashMap<>();
          data.put(BKProperties.BKAttributionEventType.BK_ATTRIBUTION_SOURCE, bkAttributionSource);
          data.put(BKProperties.BKAttributionEventType.BK_UTM_SOURCE, bkUtmSource);
          data.put(BKProperties.BKAttributionEventType.BK_UTM_MEDIUM, bkUtmMedium);
          data.put(BKProperties.BKAttributionEventType.BK_UTM_CAMPAIGN, bkUtmCampaign);
          BrandKinesis.getBKInstance().createAttributionEvent(data);
    }
    else if(call.method.equals("sendUserDetails")){
      HashMap<String , Object> data = call.argument("data");
      HashMap<String , Object> others = call.argument("others");
      Bundle userInfo = new Bundle();
      assert data != null;

      userInfo.putString(BKUserInfo.BKUserData.FIRST_NAME, (String) data.get("first_name"));
      userInfo.putInt(BKUserInfo.BKUserData.AGE, (int) data.get("age"));
      userInfo.putInt(BKUserInfo.BKUserData.GENDER, (int) data.get("gender"));
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
          result.success(eventId);
        } else {
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
      bkInstance.getBadges(new BKBadgeAccessListener() {
        @Override
        public void onBadgesAvailable(HashMap<String, List<HashMap<String, Object>>> hashMap) {
          Objects.requireNonNull(result).success(hashMap);
        }
      });
    }
    /**
     *...................................... showActivity .......................................
     */
    else if(call.method.equals("showActivity")){
      BrandKinesis bkInstance = BrandKinesis.getBKInstance();
      String tag = call.argument("tag");
      bkInstance.getActivity(context, BKActivityTypes.ACTIVITY_ANY, tag, new BKActivityCallback() {
        @Override
        public void onActivityError(int i) {

        }

        @Override
        public void onActivityCreated(BKActivityTypes bkActivityTypes) {

        }

        @Override
        public void onActivityDestroyed(BKActivityTypes bkActivityTypes) {

        }

        @Override
        public void brandKinesisActivityPerformedActionWithParams(BKActivityTypes bkActivityTypes, Map<String, Object> map) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            showActivityData.putIfAbsent("activityType",bkActivityTypes);
            showActivityData.putIfAbsent("actionData",map);
          }
          Objects.requireNonNull(result).success(showActivityData);
        }
      });


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
