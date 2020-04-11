package com.kunal.onlineconsultation.Sendbird;

import android.util.Log;

import com.sendbird.android.SendBird;

public class PushUtils {

    public static void registerPushTokenForCurrentUser() {
        registerPushTokenForCurrentUser(null);
    }

    public static void registerPushTokenForCurrentUser(final SendBird.RegisterPushTokenWithStatusHandler handler) {
        MyFirebaseMessagingService.getPushToken(new MyFirebaseMessagingService.ITokenResult() {
            @Override
            public void onPushTokenReceived(String pushToken) {
                Log.d("Token", "++ pushToken : " + pushToken);
                SendBird.registerPushTokenForCurrentUser(pushToken, handler);
            }
        });
    }

    public static void unregisterPushTokenForCurrentUser(final SendBird.UnregisterPushTokenHandler handler) {
        MyFirebaseMessagingService.getPushToken(new MyFirebaseMessagingService.ITokenResult() {
            @Override
            public void onPushTokenReceived(String pushToken) {
                SendBird.unregisterPushTokenForCurrentUser(pushToken, handler);
            }
        });
    }

    public static void unregisterPushTokenAllForCurrentUser(SendBird.UnregisterPushTokenHandler handler) {
        SendBird.unregisterPushTokenAllForCurrentUser(handler);
    }

}
