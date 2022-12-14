package com.bencarlisle15.terminalhomelauncher.commands;

import com.bencarlisle15.terminalhomelauncher.R;
import com.bencarlisle15.terminalhomelauncher.commands.main.Param;
import com.bencarlisle15.terminalhomelauncher.commands.main.specific.ParamCommand;
import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

public class Command {

    public CommandAbstraction cmd;
    public Object[] mArgs;
    public int nArgs;

    public int indexNotFound = -1;

    public String exec(ExecutePack info) throws Exception {
        info.set(mArgs);

        if (cmd instanceof ParamCommand) {
            if (indexNotFound == 0) {
                return info.context.getString(R.string.output_invalid_param) + Tuils.SPACE + mArgs[0];
            }

            ParamCommand pCmd = (ParamCommand) cmd;
            Param param = (Param) mArgs[0];

            int[] args = param.args();

            if (indexNotFound != -1) {
                return param.onArgNotFound(info, indexNotFound);
            }

            if (pCmd.defaultParamReference() != null) {
                if (args.length > nArgs) {
                    return param.onNotArgEnough(info, nArgs);
                }
            } else {
                if (args.length + 1 > nArgs) {
                    return param.onNotArgEnough(info, nArgs);
                }
            }
        } else if (indexNotFound != -1) {
            return cmd.onArgNotFound(info, indexNotFound);
        } else {
            int[] args = cmd.argType();
            if (nArgs < args.length || (mArgs == null && args.length > 0)) {
                return cmd.onNotArgEnough(info, nArgs);
            }
        }

        return cmd.exec(info);
    }

    public int nextArg() {
        boolean useParamArgs = cmd instanceof ParamCommand && mArgs != null && mArgs.length >= 1;

        int[] args;
        if (useParamArgs) {
            if (!(mArgs[0] instanceof Param)) args = null;
            else args = ((Param) mArgs[0]).args();
        } else {
            args = cmd.argType();
        }

        if (args == null || args.length == 0) {
            return 0;
        }

        try {
            return args[useParamArgs ? nArgs - 1 : nArgs];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
