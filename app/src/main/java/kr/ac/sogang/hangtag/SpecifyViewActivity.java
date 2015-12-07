package kr.ac.sogang.hangtag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by Kidsnow on 2015-04-12.
 */


public class SpecifyViewActivity extends Activity implements View.OnClickListener, BeaconConsumer {
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private static final String TAG = "AndroidProximityReferenceApplication";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private SpecifyViewActivity specifyViewActivity = null;
    String user_name=null;
    ArrayList<String> currentId = new ArrayList<>();
    ArrayList<Double> currentDist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent_from = getIntent();
        if (intent_from != null){
            user_name = (String)intent_from.getSerializableExtra("name");
        }

        setContentView(R.layout.activity_specify_view);

        ImageView topBar = (ImageView)findViewById(R.id.TopBar2);
        topBar.setAdjustViewBounds(true);

        currentId.add("NULL");
        currentId.add("NULL");
        currentId.add("NULL");
        currentDist.add(0.0);
        currentDist.add(0.0);
        currentDist.add(0.0);

        verifyBluetooth();

        beaconManager.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.specification1:
                Log.i("First", "OK");
                break;
            case R.id.specification2:
                Log.i("Second", "OK");
                break;
            case R.id.specification3:
                Log.i("Third", "OK");
                break;
            case R.id.specification4:
                Log.i("Fourth", "OK");
                break;
        }
    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        final ArrayList<Integer> isExist = new ArrayList<>();
        isExist.add(0);
        isExist.add(0);
        isExist.add(0);
        isExist.add(0);
        isExist.add(0);
        isExist.add(0);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final ArrayList<String> inputId = new ArrayList<>();
                final ArrayList<Double> inputDist = new ArrayList<>();
                if (beacons.size() > 0) {
                    Iterator itr = beacons.iterator();

                    for (int i = 0; i < beacons.size(); i ++) {
                        Beacon beacon = (Beacon)itr.next();
                        inputId.add(beacon.getId3().toString());
                        inputDist.add(beacon.getDistance());
                        //checkId(beacon.getId3().toString(), isAlive);
                    }
                    for (int i = 0; i < beacons.size(); i ++) {
                        for (int j = 0; j < 3 ; j++){
                            if(inputId.get(i).equals(currentId.get(j))) {
                                isExist.set(j, 1);
                                isExist.set(i+3, 1);
                            }
                        }
                    }

                    for (int i = 0; i < 3; i ++) {
                        if(isExist.get(i)==0 && !currentId.get(i).equals("NULL")){
                            currentId.remove(i);
                            currentId.add("NULL");
                        }
                    }

                    for (int i = 0; i < beacons.size(); i ++) {
                        if (isExist.get(i+3)==0) {
                            for (int j = 0; j < 3; j++) {
                                if (currentId.get(j).equals("NULL")){
                                    currentId.set(j,inputId.get(i));
                                    currentDist.set(j,inputDist.get(i));
                                    break;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < 6; i++)
                        isExist.set(i,0);
                    int k = inputId.size();
                    for (int i = 0; i < k;i++) {
                        inputId.remove(0);
                        inputDist.remove(0);
                    }
                }
                else{
                    for(int i = 0; i < 3; i++) {
                        currentId.set(i, "NULL");
                        currentDist.set(i, 0.0);
                    }
                }
                ToDisplay();

            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void ToDisplay() {
        runOnUiThread(new Runnable() {
            public void run() {
                ImageButton bt1 = (ImageButton)findViewById(R.id.specification1);
                bt1.setImageResource(setBtImage(currentId.get(0)));

                if(!currentId.get(0).equals("NULL")) {
                    bt1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            ItemSet itemSet = new ItemSet();
                            itemSet.imageList.add(setBtImage(currentId.get(0)));
                            intent.putExtra("itemSet", itemSet);
                            intent.putExtra("name", user_name);
                            intent.putExtra("id", Integer.parseInt(currentId.get(0)));
                            startActivity(intent);
                        }
                    });
                }
                else
                    bt1.setOnClickListener(null);

                    ImageButton bt2 = (ImageButton) findViewById(R.id.specification2);
                    bt2.setImageResource(setBtImage(currentId.get(1)));


                if(!currentId.get(1).equals("NULL")) {
                    bt2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            ItemSet itemSet = new ItemSet();
                            itemSet.imageList.add(setBtImage(currentId.get(1)));
                            intent.putExtra("itemSet", itemSet);
                            intent.putExtra("name", user_name);
                            intent.putExtra("id", Integer.parseInt(currentId.get(1)));
                            startActivity(intent);
                        }
                    });
                }
                else
                    bt2.setOnClickListener(null);

                ImageButton bt3 = (ImageButton)findViewById(R.id.specification3);
                bt3.setImageResource(setBtImage(currentId.get(2)));

                if(!currentId.get(2).equals("NULL")) {
                    bt3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            ItemSet itemSet = new ItemSet();
                            itemSet.imageList.add(setBtImage(currentId.get(2)));
                            intent.putExtra("itemSet", itemSet);
                            intent.putExtra("name", user_name);
                            intent.putExtra("id", Integer.parseInt(currentId.get(2)));
                            startActivity(intent);
                        }
                    });
                }
                else
                    bt3.setOnClickListener(null);
            }
        });
    }

    private int setBtImage(String id) {
        if (id.equals("1"))
            return R.mipmap.bluetee;
        else if (id.equals("2"))
            return R.mipmap.camopants;
        else if (id.equals("3"))
            return R.mipmap.blackcard;
        else
            return R.mipmap.bluoff;
    }

}

