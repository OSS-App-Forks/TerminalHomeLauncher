package com.bencarlisle15.terminalhomelauncher.commands.main.raw;

import android.content.Context;
import android.os.Build;

import com.bencarlisle15.terminalhomelauncher.BuildConfig;
import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.commands.CommandAbstraction;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;
import com.bencarlisle15.terminalhomelauncher.commands.main.specific.APICommand;
import com.bencarlisle15.terminalhomelauncher.managers.TuiLocationManager;

/**
 * Created by francescoandreuzzi on 10/05/2017.
 */

public class Location implements APICommand, CommandAbstraction {

    public static final String ACTION_LOCATION_CMD_GOT = BuildConfig.APPLICATION_ID + ".loc_cmd_location";

    @Override
    public String exec(final ExecutePack pack) throws Exception {
        final Context context = pack.context;

//        if(handler != null) handler = new Handler();
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LauncherActivity.COMMAND_REQUEST_PERMISSION);
//            return context.getString(R.string.output_waitingpermission);
//        }

//        Location l = Tuils.getLocation(pack.context);
//        if(l != null) {
//            Tuils.sendOutput(context, "Lat: " + l.getLatitude() + "; Long: " + l.getLongitude());
//        } else {
//            Tuils.sendOutput(pack.context, R.string.location_error);
//        }
//
//                , new Tuils.ArgsRunnable() {
//            @Override
//            public void run() {
//                Tuils.sendOutput(context, "Lat: " + get(int.class, 0) + "; Long: " + get(int.class, 1));
//            }
//        }, new Runnable() {
//            @Override
//            public void run() {
//                Tuils.sendOutput(pack.context, R.string.location_error);
//            }
//        }, handler);

        TuiLocationManager l = TuiLocationManager.instance(context);
        if (l.locationAvailable) return "Lat: " + l.latitude + "; Long: " + l.longitude;
        else {
            l.add(context, ACTION_LOCATION_CMD_GOT);
        }

        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_location;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }

    @Override
    public boolean willWorkOn(int api) {
        return api >= Build.VERSION_CODES.GINGERBREAD;
    }
}