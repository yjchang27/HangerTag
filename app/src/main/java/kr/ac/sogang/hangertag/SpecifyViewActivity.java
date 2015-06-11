package kr.ac.sogang.hangertag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
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

    ArrayList<String> currentId = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_view);
        currentId.add("NULL");
        currentId.add("NULL");
        currentId.add("NULL");

        verifyBluetooth();

        Button btGoSpec1 = (Button)findViewById(R.id.specification1);
        //btGoSpec1.setBackgroundResource(R.mipmap.blouson0);
        Button btGoSpec2 = (Button)findViewById(R.id.specification2);
        //btGoSpec2.setBackgroundResource(R.mipmap.white);
        Button btGoSpec3 = (Button)findViewById(R.id.specification3);
        //btGoSpec3.setBackgroundResource(R.mipmap.white);
        Button btGoSpec4 = (Button)findViewById(R.id.specification4);
        //btGoSpec4.setBackgroundResource(R.mipmap.white);
/*
        ImageButton btGoSpec1 = (ImageButton)findViewById(R.id.specification1);
        btGoSpec1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this,DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.description = "1번 상품이다";
                itemSet.imageList.add(R.mipmap.blouson0);
                itemSet.imageList.add(R.mipmap.blouson1);
                itemSet.imageList.add(R.mipmap.blouson2);
                intent.putExtra("itemSet",itemSet);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec2 = (ImageButton)findViewById(R.id.specification2);
        btGoSpec2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.description = "2번 상품이다";
                itemSet.imageList.add(R.mipmap.coat0);
                itemSet.imageList.add(R.mipmap.coat1);
                itemSet.imageList.add(R.mipmap.coat2);
                intent.putExtra("itemSet", itemSet);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec3 = (ImageButton)findViewById(R.id.specification3);
        btGoSpec3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                itemSet.description = "3번 상품이다";
                itemSet.imageList.add(R.mipmap.denim0);
                itemSet.imageList.add(R.mipmap.denim1);
                itemSet.imageList.add(R.mipmap.denim2);
                intent.putExtra("itemSet", itemSet);
                startActivity(intent);
            }
        });

        ImageButton btGoSpec4 = (ImageButton)findViewById(R.id.specification4);
        btGoSpec4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyViewActivity.this, DetailViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ItemSet itemSet = new ItemSet();
                //itemSet.description = "4번 상품이다";
                //itemSet.imageList.add(R.mipmap.coat0);
                //itemSet.imageList.add(R.mipmap.coat1);
                //itemSet.imageList.add(R.mipmap.coat2);
                intent.putExtra("itemSet", itemSet);
                startActivity(intent);
            }
        });*/

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
                if (beacons.size() > 0) {
                    Iterator itr = beacons.iterator();

                    for (int i = 0; i < beacons.size(); i ++) {
                        Beacon beacon = (Beacon)itr.next();
                        inputId.add(beacon.getId3().toString());
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
                                    break;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < 6; i++)
                        isExist.set(i,0);
                    int k = inputId.size();
                    for (int i = 0; i < k;i++)
                        inputId.remove(0);

                }
                else{
                    for(int i = 0; i < 3; i++)
                        currentId.set(i, "NULL");
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
                Button bt1 = (Button)findViewById(R.id.specification1);
                bt1.setText(currentId.get(0));
                Button bt2 = (Button)findViewById(R.id.specification2);
                bt2.setText(currentId.get(1));
                Button bt3 = (Button)findViewById(R.id.specification3);
                bt3.setText(currentId.get(2));
            }
        });
    }

}

