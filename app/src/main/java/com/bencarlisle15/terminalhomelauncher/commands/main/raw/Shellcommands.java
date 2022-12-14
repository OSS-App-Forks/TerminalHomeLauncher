package com.bencarlisle15.terminalhomelauncher.commands.main.raw;

import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.commands.CommandAbstraction;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by francescoandreuzzi on 19/04/16.
 */
public class Shellcommands implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        Collection<String> cmds = getOSCommands();
        List<String> commands = new ArrayList<>(cmds);

        commands.sort(Tuils::alphabeticCompare);

        Tuils.addPrefix(commands, Tuils.DOUBLE_SPACE);
        Tuils.addSeparator(commands, Tuils.SPACE);
        Tuils.insertHeaders(commands, true);

        return Tuils.toPlanString(commands, Tuils.EMPTYSTRING);
    }

    private final String[] path = {
            "/system/bin",
            "/system/xbin"
    };

    private Set<String> getOSCommands() {
        Set<String> commands = new HashSet<>();

        for (String s : path) {
            String[] f = new File(s).list();
            if (f != null) {
                commands.addAll(Arrays.asList(f));
            }
        }

        return commands;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int helpRes() {
        return R.string.help_shellcommands;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return null;
    }
}
