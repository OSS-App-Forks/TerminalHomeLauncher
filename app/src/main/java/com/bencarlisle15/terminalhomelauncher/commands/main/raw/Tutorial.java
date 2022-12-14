package com.bencarlisle15.terminalhomelauncher.commands.main.raw;

import android.content.Intent;

import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.commands.CommandAbstraction;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

/**
 * Created by francescoandreuzzi on 10/07/2017.
 */

public class Tutorial implements CommandAbstraction {

    final String url = "https://github.com/bencarlisle15/TerminalHomeLauncher/wiki";

    @Override
    public String exec(ExecutePack pack) throws Exception {
        Intent intent = Tuils.webPage(url);
        pack.context.startActivity(intent);
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int helpRes() {
        return R.string.help_tutorial;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }
}
