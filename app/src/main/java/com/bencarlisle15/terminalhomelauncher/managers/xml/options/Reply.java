package com.bencarlisle15.terminalhomelauncher.managers.xml.options;

import com.bencarlisle15.terminalhomelauncher.managers.notifications.reply.ReplyManager;
import com.bencarlisle15.terminalhomelauncher.managers.xml.classes.XMLPrefsElement;
import com.bencarlisle15.terminalhomelauncher.managers.xml.classes.XMLPrefsSave;

/**
 * Created by francescoandreuzzi on 17/01/2018.
 */

public enum Reply implements XMLPrefsSave {

    reply_enabled {
        @Override
        public String defaultValue() {
            return "true";
        }

        @Override
        public String type() {
            return XMLPrefsSave.BOOLEAN;
        }

        @Override
        public String info() {
            return "If false, notification reply will be disabled";
        }
    };

    @Override
    public XMLPrefsElement parent() {
        return ReplyManager.instance;
    }

    @Override
    public String label() {
        return name();
    }

    @Override
    public String[] invalidValues() {
        return null;
    }

    @Override
    public String getLowercaseString() {
        return label();
    }

    @Override
    public String getString() {
        return label();
    }
}
