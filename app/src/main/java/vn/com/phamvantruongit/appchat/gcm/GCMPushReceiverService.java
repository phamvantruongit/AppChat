package vn.com.phamvantruongit.appchat.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;

import vn.com.phamvantruongit.appchat.Constants;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        String message=bundle.getString("message");
        String title=bundle.getString("title");
        String id=bundle.getString("id");
        sendNotification(message,title,id);
    }

    private void sendNotification(String message, String title, String id) {
        Intent pushNotification=new Intent(Constants.PUSH_NOTIFICATION);
        pushNotification.putExtra("message",message);
        pushNotification.putExtra("name",title);
        pushNotification.putExtra("id",id);
        NotificationHandler notificationHandler=new NotificationHandler(getApplicationContext());
        if(!NotificationHandler.isAppIsInBackground(getApplicationContext())){
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }else {
            notificationHandler.showNotificationMessage(title,message);
        }

    }
}
