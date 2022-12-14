package com.bencarlisle15.terminalhomelauncher.commands.main;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.bencarlisle15.terminalhomelauncher.commands.CommandGroup;
import com.bencarlisle15.terminalhomelauncher.commands.CommandsPreferences;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;
import com.bencarlisle15.terminalhomelauncher.commands.main.raw.Flash;
import com.bencarlisle15.terminalhomelauncher.managers.AliasManager;
import com.bencarlisle15.terminalhomelauncher.managers.AppsManager;
import com.bencarlisle15.terminalhomelauncher.managers.ContactManager;
import com.bencarlisle15.terminalhomelauncher.managers.RssManager;
import com.bencarlisle15.terminalhomelauncher.managers.TerminalManager;
import com.bencarlisle15.terminalhomelauncher.managers.music.MusicManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.XMLPrefsManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Behavior;
import com.bencarlisle15.terminalhomelauncher.tuils.interfaces.Redirectator;
import com.bencarlisle15.terminalhomelauncher.tuils.libsuperuser.ShellHolder;

import java.io.File;
import java.lang.reflect.Method;

import okhttp3.OkHttpClient;

/**
 * Created by francescoandreuzzi on 24/01/2017.
 */

public class MainPack extends ExecutePack {

    //	current directory
    public File currentDirectory;

    //	resources references
    public final Resources res;

    //	internet
    public WifiManager wifi;

    //	3g/data
    public Method setMobileDataEnabledMethod;
    public ConnectivityManager connectivityMgr;
    public Object connectMgr;

    //	contacts
    public final ContactManager contacts;

    //	music
    public final MusicManager player;

    //	apps & assocs
    public final AliasManager aliasManager;
    public final AppsManager appsManager;

    public final CommandsPreferences cmdPrefs;

    public String lastCommand;

    public final Redirectator redirectator;

    public ShellHolder shellHolder;

    public final RssManager rssManager;

    public final OkHttpClient client;

    public int commandColor = TerminalManager.NO_COLOR;

    public MainPack(Context context, CommandGroup commandGroup, AliasManager alMgr, AppsManager appmgr, MusicManager p,
                    ContactManager c, Redirectator redirectator, RssManager rssManager, OkHttpClient client) {
        super(commandGroup);

        this.currentDirectory = XMLPrefsManager.get(File.class, Behavior.home_path);

        this.rssManager = rssManager;

        this.client = client;

        this.res = context.getResources();

        this.context = context;

        this.aliasManager = alMgr;
        this.appsManager = appmgr;

        this.cmdPrefs = new CommandsPreferences();

        this.player = p;
        this.contacts = c;

        this.redirectator = redirectator;
    }

    public void dispose() {
        Flash.modifyTorchMode(context, false);
    }

    public void destroy() {
        if (player != null) player.destroy();
        appsManager.onDestroy();
        if (rssManager != null) rssManager.dispose();
        contacts.destroy(context);
    }

    @Override
    public void clear() {
        super.clear();

        commandColor = TerminalManager.NO_COLOR;
    }
}
