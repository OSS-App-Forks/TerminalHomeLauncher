package com.bencarlisle15.terminalhomelauncher.managers.notifications;

import static com.bencarlisle15.terminalhomelauncher.managers.TerminalManager.FORMAT_INPUT;
import static com.bencarlisle15.terminalhomelauncher.managers.TerminalManager.FORMAT_NEWLINE;
import static com.bencarlisle15.terminalhomelauncher.managers.TerminalManager.FORMAT_PREFIX;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.bencarlisle15.terminalhomelauncher.BuildConfig;
import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.managers.TimeManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.XMLPrefsManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Behavior;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Ui;
import com.bencarlisle15.terminalhomelauncher.tuils.PrivateIOReceiver;
import com.bencarlisle15.terminalhomelauncher.tuils.PublicIOReceiver;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

public class KeeperService extends Service {

    public static final int ONGOING_NOTIFICATION_ID = 1001;
    public static final String CMD_KEY = "cmd", PATH_KEY = "path";

    private String title, subtitle, clickCmd, inputFormat, prefix, suPrefix;
    private boolean showHome, upDown;
    private int inputColor, timeColor, priority;

    private CharSequence[] lastCommands = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1 || startId == 0) {

            title = XMLPrefsManager.get(Behavior.tui_notification_title);
            subtitle = XMLPrefsManager.get(Behavior.tui_notification_subtitle);
            clickCmd = XMLPrefsManager.get(Behavior.tui_notification_click_cmd);
            inputFormat = XMLPrefsManager.get(Behavior.input_format);
            showHome = XMLPrefsManager.getBoolean(Behavior.tui_notification_click_showhome);
            inputColor = XMLPrefsManager.getColor(Behavior.tui_notification_input_color);
            timeColor = XMLPrefsManager.getColor(Behavior.tui_notification_time_color);
            prefix = XMLPrefsManager.get(Ui.input_prefix);
            upDown = XMLPrefsManager.getBoolean(Behavior.tui_notification_lastcmds_updown);
            suPrefix = XMLPrefsManager.get(Ui.input_root_prefix);

            priority = XMLPrefsManager.getInt(Behavior.tui_notification_priority);
            if (priority > 2) priority = 2;
            if (priority < -2) priority = -2;

            String path = intent != null ? intent.getStringExtra(PATH_KEY) : Environment.getExternalStorageDirectory().getAbsolutePath();

            startForeground(ONGOING_NOTIFICATION_ID, buildNotification(getApplicationContext(), title, subtitle, Tuils.getHint(path),
                    clickCmd, showHome, lastCommands, upDown, priority));

            int lastCmdSize = XMLPrefsManager.getInt(Behavior.tui_notification_lastcmds_size);
            if (lastCmdSize > 0) {
                lastCommands = new CharSequence[lastCmdSize];
            }

        } else {
//            new cmd
//            update the list

            if (lastCommands != null) updateCmds(intent.getStringExtra(CMD_KEY));

            String path = intent != null ? intent.getStringExtra(PATH_KEY) : Environment.getExternalStorageDirectory().getAbsolutePath();

            NotificationManagerCompat.from(getApplicationContext()).notify(KeeperService.ONGOING_NOTIFICATION_ID,
                    KeeperService.buildNotification(getApplicationContext(), title, subtitle, Tuils.getHint(path),
                            clickCmd, showHome, lastCommands, upDown, priority));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //    0 = most recent
//    4 = oldest

    //    * = null
//    3 cases
//    1: |*|*|*|*|*| -> lastNull = 0
//    2: |a|b|c|*|*| -> lastNull = n < length
//    3: |a|b|c|d|e| -> lastNull = -1
    private void updateCmds(String cmd) {
        try {
            int lastNull = lastNull();
            int toCopy = lastNull == -1 ? lastCommands.length - 1 : lastNull;
            System.arraycopy(lastCommands, 0, lastCommands, 1, toCopy);
            lastCommands[0] = formatInput(cmd, inputFormat, prefix, suPrefix, inputColor, timeColor);
        } catch (Exception e) {
            Tuils.log(e);
        }
    }

    private static CharSequence formatInput(String cmd, String inputFormat, String prefix, String suPrefix, int inputColor, int timeColor) {
        if (cmd == null) return null;
        boolean su = cmd.startsWith("su ");

        SpannableString si = Tuils.span(inputFormat, inputColor);

        CharSequence s = TimeManager.instance.replace(si, timeColor);
        s = TextUtils.replace(s,
                new String[]{FORMAT_INPUT, FORMAT_PREFIX, FORMAT_NEWLINE, FORMAT_INPUT.toUpperCase(), FORMAT_PREFIX.toUpperCase(), FORMAT_NEWLINE.toUpperCase()},
                new CharSequence[]{cmd, su ? suPrefix : prefix, Tuils.NEWLINE, cmd, su ? suPrefix : prefix, Tuils.NEWLINE});

        return s;
    }

    private int lastNull() {
        for (int c = 0; c < lastCommands.length; c++) if (lastCommands[c] == null) return c;
        return -1;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        lastCommands = null;
        return true;
    }

    public static Notification buildNotification(Context c, String title, String subtitle, String cmdLabel, String clickCmd, boolean showHome, CharSequence[] lastCommands, boolean upDown, int priority) {
        if (priority < -2 || priority > 2) priority = NotificationCompat.PRIORITY_DEFAULT;

        PendingIntent pendingIntent;
        if (showHome) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (clickCmd != null && clickCmd.length() > 0) {
                startMain.putExtra(PrivateIOReceiver.TEXT, clickCmd);
            }

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_MUTABLE;
            }

            pendingIntent = PendingIntent.getActivity(
                    c,
                    0,
                    startMain,
                    flags
            );
        } else if (clickCmd != null && clickCmd.length() > 0) {
            Intent cmdIntent = new Intent(PublicIOReceiver.ACTION_CMD);
            cmdIntent.putExtra(PrivateIOReceiver.TEXT, clickCmd);

            int flags = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_MUTABLE;
            }

            pendingIntent = PendingIntent.getBroadcast(
                    c,
                    0,
                    cmdIntent,
                    flags
            );
        } else {
            pendingIntent = null;
        }

        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int oPriority = Tuils.scale(new int[]{0, 4}, new int[]{2, 4}, priority + 2);
                if (oPriority < 2 || oPriority > 4)
                    oPriority = NotificationManager.IMPORTANCE_UNSPECIFIED;

                NotificationChannel notificationChannel = new NotificationChannel(BuildConfig.APPLICATION_ID, c.getString(R.string.app_name), oPriority);
                ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(c, BuildConfig.APPLICATION_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(c.getString(R.string.start_notification))
                    .setWhen(System.currentTimeMillis())
                    .setPriority(priority)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent);

            NotificationCompat.Style style = null;
            if (lastCommands != null && lastCommands[0] != null) {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                if (upDown) {
                    for (CharSequence lastCommand : lastCommands) {
                        if (lastCommand == null) break;
                        inboxStyle.addLine(lastCommand);
                    }
                } else {
                    for (int j = lastCommands.length - 1; j >= 0; j--) {
                        if (lastCommands[j] == null) continue;
                        inboxStyle.addLine(lastCommands[j]);
                    }
                }

                style = inboxStyle;
            }

            if (style != null) builder.setStyle(style);
            else {
                builder.setContentTitle(title);
                builder.setContentText(subtitle);
            }

            RemoteInput remoteInput = new RemoteInput.Builder(PrivateIOReceiver.TEXT)
                    .setLabel(cmdLabel)
                    .build();

            Intent i = new Intent(PublicIOReceiver.ACTION_CMD);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_MUTABLE;
            }

            NotificationCompat.Action.Builder actionBuilder = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher,
                    cmdLabel,
                    PendingIntent.getBroadcast(c.getApplicationContext(), 40, i, flags))
                    .addRemoteInput(remoteInput);

            builder.addAction(actionBuilder.build());

            return builder.build();
        }
    }
}
