package com.bencarlisle15.terminalhomelauncher.commands.main.raw;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.UIManager;
import com.bencarlisle15.terminalhomelauncher.commands.CommandAbstraction;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;
import com.bencarlisle15.terminalhomelauncher.commands.main.MainPack;
import com.bencarlisle15.terminalhomelauncher.commands.main.specific.ParamCommand;
import com.bencarlisle15.terminalhomelauncher.managers.xml.XMLPrefsManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.classes.XMLPrefsSave;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Behavior;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Ui;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;
import com.bencarlisle15.terminalhomelauncher.tuils.interfaces.Reloadable;

/**
 * Created by francescoandreuzzi on 21/05/2018.
 */

public class Tuiweather extends ParamCommand {

    private enum Param implements com.bencarlisle15.terminalhomelauncher.commands.main.Param {

        update {
            @Override
            public String exec(ExecutePack pack) {
                if (!XMLPrefsManager.getBoolean(Ui.show_weather)) {
                    return pack.context.getString(R.string.weather_disabled);
                } else if (!XMLPrefsManager.wasChanged(Behavior.weather_key, false)) {
                    return pack.context.getString(R.string.weather_cant_update);
                } else {
                    LocalBroadcastManager.getInstance(pack.context.getApplicationContext()).sendBroadcast(new Intent(UIManager.ACTION_WEATHER_MANUAL_UPDATE));
                }

                return null;
            }
        },
        enable {
            @Override
            public String exec(ExecutePack pack) {
                XMLPrefsSave save = Ui.show_weather;

                save.parent().write(save, "true");
                ((Reloadable) pack.context).addMessage(save.parent().path(), save.label() + " -> " + "true");
                ((Reloadable) pack.context).reload();

                return null;
            }
        },
        disable {
            @Override
            public String exec(ExecutePack pack) {
                XMLPrefsSave save = Ui.show_weather;

                save.parent().write(save, "false");
                ((Reloadable) pack.context).addMessage(save.parent().path(), save.label() + " -> " + "false");
                ((Reloadable) pack.context).reload();

                return null;
            }
        },
        tutorial {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPage("https://github.com/bencarlisle15/TerminalHomeLauncher/wiki/Weather/_edit"));
                return null;
            }
        },
        set_key {
            @Override
            public int[] args() {
                return new int[]{CommandAbstraction.PLAIN_TEXT};
            }

            @Override
            public String exec(ExecutePack pack) {
                Behavior.weather_key.parent().write(Behavior.weather_key, pack.getString());
                return null;
            }
        };

        static Param get(String p) {
            p = p.toLowerCase();
            Param[] ps = values();
            for (Param p1 : ps)
                if (p.endsWith(p1.label()))
                    return p1;
            return null;
        }

        static String[] labels() {
            Param[] ps = values();
            String[] ss = new String[ps.length];

            for (int count = 0; count < ps.length; count++) {
                ss[count] = ps[count].label();
            }

            return ss;
        }

        @Override
        public int[] args() {
            return new int[0];
        }

        @Override
        public String label() {
            return Tuils.MINUS + name();
        }

        @Override
        public String onNotArgEnough(ExecutePack pack, int n) {
            return pack.context.getString(R.string.help_shortcut);
        }

        @Override
        public String onArgNotFound(ExecutePack pack, int index) {
            return pack.context.getString(R.string.output_appnotfound);
        }
    }

    @Override
    protected Param paramForString(MainPack pack, String param) {
        return Param.get(param);
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int helpRes() {
        return R.string.help_weather;
    }

    @Override
    public String[] params() {
        return Param.labels();
    }

    @Override
    protected String doThings(ExecutePack pack) {
        return null;
    }
}
