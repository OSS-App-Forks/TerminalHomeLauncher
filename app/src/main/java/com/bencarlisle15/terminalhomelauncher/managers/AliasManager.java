package com.bencarlisle15.terminalhomelauncher.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bencarlisle15.terminalhomelauncher.BuildConfig;
import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.managers.xml.XMLPrefsManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.options.Behavior;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliasManager {

    public static final String ACTION_LS = BuildConfig.APPLICATION_ID + ".alias_ls";
    public static final String ACTION_ADD = BuildConfig.APPLICATION_ID + ".alias_add";
    public static final String ACTION_RM = BuildConfig.APPLICATION_ID + ".alias_rm";

    public static final String NAME = "name";

    public static final String PATH = "alias.txt";

    private List<Alias> aliases;
    private final String paramSeparator;
    private final String aliasLabelFormat;
    private final boolean replaceAllMarkers;

    private final Context context;

    private final String paramMarker;
    private final Pattern parameterPattern;

    private final BroadcastReceiver receiver;

    public AliasManager(Context c) {
        this.context = c;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD);
        filter.addAction(ACTION_LS);
        filter.addAction(ACTION_RM);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case ACTION_ADD:
                        add(context, intent.getStringExtra(NAME), intent.getStringExtra(XMLPrefsManager.VALUE_ATTRIBUTE));
                        break;
                    case ACTION_RM:
                        remove(context, intent.getStringExtra(NAME));
                        break;
                    case ACTION_LS:
                        Tuils.sendOutput(context, printAliases());
                        break;
                }
            }
        };

        paramMarker = XMLPrefsManager.get(Behavior.alias_param_marker);
        parameterPattern = Pattern.compile(Pattern.quote(paramMarker));
        paramSeparator = XMLPrefsManager.get(Behavior.alias_param_separator);
        aliasLabelFormat = XMLPrefsManager.get(Behavior.alias_content_format);
        replaceAllMarkers = XMLPrefsManager.getBoolean(Behavior.alias_replace_all_markers);

        reload();
    }

    public String printAliases() {
        String output = Tuils.EMPTYSTRING;
        for (Alias a : aliases) {
            output = output.concat(a.name + " --> " + a.value + Tuils.NEWLINE);
        }

        return output.trim();
    }

    //    [0] = aliasValue
//    [1] = aliasName
//    [2] = residualString
    public String[] getAlias(String alias, boolean supportSpaces) {
        if (supportSpaces) {
            StringBuilder args = new StringBuilder(Tuils.EMPTYSTRING);

            String aliasValue;
            while (true) {
                aliasValue = getALias(alias);
                if (aliasValue != null) break;
                else {
                    int index = alias.lastIndexOf(Tuils.SPACE);
                    if (index == -1) return new String[]{null, null, alias};

                    args.insert(0, alias.substring(index + 1) + Tuils.SPACE);
                    args = new StringBuilder(args.toString().trim());
                    alias = alias.substring(0, index);
                }
            }

            return new String[]{aliasValue, alias, args.toString()};
        } else {
            return new String[]{getALias(alias), alias, Tuils.EMPTYSTRING};
        }
    }

    //    this prevents some errors related to the % sign
    private final String SECURITY_REPLACEMENT = "{#@";
    private final Pattern securityPattern = Pattern.compile(Pattern.quote(SECURITY_REPLACEMENT));

    public String format(String aliasValue, String params) {
        params = params.trim();
        if (params.length() == 0) return aliasValue;

        int before = aliasValue.length();
        aliasValue = parameterPattern.matcher(aliasValue).replaceAll(SECURITY_REPLACEMENT);
        int replaced = (aliasValue.length() - before) / Math.abs(SECURITY_REPLACEMENT.length() - paramMarker.length());

        String[] split = params.split(Pattern.quote(paramSeparator), replaced);

        for (String s : split) {
            aliasValue = securityPattern.matcher(aliasValue).replaceFirst(s);
        }

        if (replaceAllMarkers)
            aliasValue = securityPattern.matcher(aliasValue).replaceAll(split[0]);

        return aliasValue;
    }

    private String getALias(String name) {
        for (Alias a : aliases) {
            if (name.equals(a.name)) return a.value;
        }

        return null;
    }

    private boolean removeAlias(String name) {
        for (int c = 0; c < aliases.size(); c++) {
            Alias a = aliases.get(c);
            if (name.equals(a.name)) {
                aliases.remove(c);
                return true;
            }
        }

        return false;
    }

    private final Pattern pv = Pattern.compile("%v", Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
    private final Pattern pa = Pattern.compile("%a", Pattern.CASE_INSENSITIVE | Pattern.LITERAL);

    public String formatLabel(String aliasName, String aliasValue) {
        String a = aliasLabelFormat;
        a = Tuils.patternNewline.matcher(a).replaceAll(Matcher.quoteReplacement(Tuils.NEWLINE));
        a = pv.matcher(a).replaceAll(Matcher.quoteReplacement(aliasValue));
        a = pa.matcher(a).replaceAll(Matcher.quoteReplacement(aliasName));
        return a;
    }

    public void reload() {
        if (aliases != null) aliases.clear();
        else aliases = new ArrayList<>();

        File root = Tuils.getFolder();
        if (root == null) return;

        File file = new File(root, PATH);

        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Could not create new file at " + file.getAbsolutePath());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] splatted = line.split("=");
                if (splatted.length < 2) continue;

                String name;
                StringBuilder value = new StringBuilder(Tuils.EMPTYSTRING);
                name = splatted[0];

                for (int c = 1; c < splatted.length; c++) {
                    value.append(splatted[c]);
                    if (c != splatted.length - 1) value.append("=");
                }

                name = name.trim();
                value = new StringBuilder(value.toString().trim());

                if (name.equalsIgnoreCase(value.toString())) {
                    Tuils.sendOutput(Color.RED, context,
                            context.getString(R.string.output_notaddingalias1) + Tuils.SPACE + name + Tuils.SPACE + context.getString(R.string.output_notaddingalias2));
                } else if (value.toString().startsWith(name + Tuils.SPACE)) {
                    Tuils.sendOutput(Color.RED, context,
                            context.getString(R.string.output_notaddingalias1) + Tuils.SPACE + name + Tuils.SPACE + context.getString(R.string.output_notaddingalias3));
                } else {
                    aliases.add(new Alias(name, value.toString(), parameterPattern));
                }
            }
        } catch (Exception e) {
            Tuils.log(e);
        }
    }

    public void dispose() {
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(receiver);
    }

    public void add(Context context, String name, String value) {
        for (Alias a : aliases) {
            if (name.equals(a.name)) {
                Tuils.sendOutput(context, R.string.unavailable_name);
                return;
            }
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(Tuils.getFolder(), PATH), true);
            fos.write((Tuils.NEWLINE + name + "=" + value).getBytes());
            fos.close();

            aliases.add(new Alias(name, value, parameterPattern));
        } catch (Exception e) {
            Tuils.sendOutput(context, e.toString());
        }

    }

    public void remove(Context context, String name) {
        reload();

        if (!removeAlias(name)) {
            Tuils.sendOutput(context, R.string.invalid_name);
            return;
        }

        try {
            File inputFile = new File(Tuils.getFolder(), PATH);
            File tempFile = new File(Tuils.getFolder(), PATH + "2");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String prefix = name + "=";
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) continue;
                writer.write(line + Tuils.NEWLINE);
            }
            writer.close();
            reader.close();

            if (!tempFile.renameTo(inputFile)) {
                throw new IOException("Could not rename file to " + inputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            Tuils.sendOutput(context, e.toString());
        }
    }

    public List<Alias> getAliases(boolean excludeEmtpy) {
        List<Alias> l = new ArrayList<>(aliases);
        if (excludeEmtpy) {
            for (int c = 0; c < l.size(); c++) {
                if (l.get(c).name.length() == 0) {
                    l.remove(c);
                    break;
                }
            }
        }

        return l;
    }

    public static class Alias {
        public final String name;
        public final String value;
        public final boolean isParametrized;

        public Alias(String name, String value, Pattern parameterPattern) {
            this.name = name;
            this.value = value;

            isParametrized = parameterPattern.matcher(value).find();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Alias && ((Alias) obj).name.equals(name)) || obj.equals(name);
        }
    }
}
