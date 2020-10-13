package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";
    private String token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        PushUtils.acquireWakeLock(getApplicationContext());
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
        }

    }


    @Override
    public void onNewToken(String token) {
        Log.e("TAG", "Refreshed token: " + token);
    }

    private void sendNotification(String messageTitle, String messageBody, Map<String, String> data) {

        UserInfo.setPushOpen(true);
        UserInfo.setPushType(data.get("type"));
        UserInfo.setPushUserNum(data.get("userNum"));
        if (data.get("url") != null) {
            String str = data.get("url");
            int index = str.indexOf("LiNum=");
            str = str.substring(index + 6);
            UserInfo.setPushUrl(str);
            Log.e(TAG, "sendNotification: str : " + str);
        }


        Intent intent = null;
        PendingIntent pendingIntent = null;
        if (UserInfo.getIsLogin()) {
            if (BottomNaviWrap.bottomNaviWrap == null) {
                intent = new Intent(this, BottomNaviWrap.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.e(TAG, "sendNotification:111 " );
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 새로 안드로이드를 시작하게 만든다.
            } else {
                Log.e(TAG, "sendNotification: asdf " );
                intent = new Intent(this, RegInfoDetail.class);
            }


        } else {
            intent = new Intent(this, LoginPage.class);
        }


        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.app_name);

        PushUtils.releaseWakeLock();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.app_icon_small)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(notificationBuilder);
        style.bigText(messageBody);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0, notificationBuilder.build());

    }


}

@SuppressLint("SpecifyJobSchedulerIdRange")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");

        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire(); // WakeLock 깨우기

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

class PushUtils {
    private static PowerManager.WakeLock mWakeLock;

    /**
     * 핸드폰 화면이 꺼져있을 때 깨우는 메소드
     *
     * @param context
     */
    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKEUP");
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}

