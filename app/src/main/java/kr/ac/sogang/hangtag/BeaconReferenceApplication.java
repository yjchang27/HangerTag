package kr.ac.sogang.hangtag;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyoung on 12/13/13.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "AndroidProximityRef";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private SpecifyViewActivity specifyViewActivity = null;

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        // Region region = new Region("backgroundRegion", null, null, null);
        ArrayList<Region> regionList = new ArrayList<Region>();
        regionList.add(new Region("1",
                Identifier.parse("05F62A3D-F60F-44BC-B36E-2B80FD6C9679"),
                Identifier.parse("4660"),
                Identifier.parse("1")));
        regionList.add(new Region("2",
                Identifier.parse("05F62A3D-F60F-44BC-B36E-2B80FD6C9679"),
                Identifier.parse("4660"),
                Identifier.parse("2")));
        regionList.add(new Region("3",
                Identifier.parse("05F62A3D-F60F-44BC-B36E-2B80FD6C9679"),
                Identifier.parse("4660"),
                Identifier.parse("3")));
        regionBootstrap = new RegionBootstrap(this, regionList);

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        // If you wish to test beacon detection in the Android Emulator, you can use code like this:
        // BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
        // ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
    }

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        System.out.println("\nENTERED!!!\n");
        Log.d(TAG, "did enter region.");

        Log.d(TAG, "Sending notification. [" + arg0.getUniqueId() + "]");
        sendNotification(arg0.getUniqueId());

        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching MainActivity");

            // The very first time since boot that we detect an beacon, we launch the
            // MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            if (specifyViewActivity != null) {
                Log.d(TAG, "specifyViewActivity is not null");
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                //specifyViewActivity.logToDisplay("I see a beacon again" );
            } else {
                Log.d(TAG, "specifyViewActivity is null");
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
            }
        }


    }

    @Override
    public void didExitRegion(Region region) {
        System.out.println("\nBye~\n");
        Log.d(TAG, "did exit the region.");
        if (specifyViewActivity != null) {
            //specifyViewActivity.logToDisplay("I no longer see a beacon.");
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        System.out.println("\nGood to meet you again!\n");
        Log.d(TAG, "did switch the state of the beacon");
        if (specifyViewActivity != null) {
            //specifyViewActivity.logToDisplay("I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private void sendNotification(String uniqueId) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("New item found")
                        .setContentText("An beacon of item " + uniqueId + " is nearby.")
                        .setSmallIcon(R.drawable.ic_shopping_basket_24dp)
                        .setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(DetailViewActivity.class);
        // Adds the Intent to the top of the stack
        Intent intent = new Intent(this, DetailViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ItemSet itemSet = new ItemSet();
        itemSet.imageList.add(SpecifyViewActivity.setBtImage(uniqueId));
        intent.putExtra("itemSet", itemSet);
        intent.putExtra("name", (String)null);
        intent.putExtra("id", Integer.parseInt(uniqueId));
        intent.setAction(Long.toString(System.currentTimeMillis()));
        stackBuilder.addNextIntent(intent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(uniqueId), builder.build());
    }

    public void setMonitoringActivity(SpecifyViewActivity activity) {
        this.specifyViewActivity = activity;
    }
}