package com.bencarlisle15.terminalhomelauncher.commands.main.specific;

import com.bencarlisle15.terminalhomelauncher.commands.CommandAbstraction;
import com.bencarlisle15.terminalhomelauncher.commands.ExecutePack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francescoandreuzzi on 03/03/2017.
 */

public abstract class RedirectCommand implements CommandAbstraction {

    public final List<Object> beforeObjects = new ArrayList<>();
    public final List<Object> afterObjects = new ArrayList<>();

    public abstract String onRedirect(ExecutePack pack);

    @SuppressWarnings("SameReturnValue")
    public abstract int getHint();

    public abstract boolean isWaitingPermission();

    public void cleanup() {
        beforeObjects.clear();
        afterObjects.clear();
    }
}
