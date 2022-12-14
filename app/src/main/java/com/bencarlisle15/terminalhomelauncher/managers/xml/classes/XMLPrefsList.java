package com.bencarlisle15.terminalhomelauncher.managers.xml.classes;

import androidx.annotation.NonNull;

import com.bencarlisle15.terminalhomelauncher.tuils.Tuils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francescoandreuzzi on 06/03/2018.
 */

public class XMLPrefsList {

    public final List<XMLPrefsEntry> list = new ArrayList<>();

    public void add(XMLPrefsEntry entry) {
        list.add(entry);
    }

    public void add(String key, String value) {
        list.add(new XMLPrefsEntry(key, value));
    }

    public XMLPrefsEntry get(Object o) {
        if (o instanceof Integer) return at((Integer) o);

        for (XMLPrefsEntry e : list) if (e.equals(o)) return e;
        return null;
    }

    public XMLPrefsEntry at(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public List<String> values() {
        List<String> vs = new ArrayList<>();
        for (XMLPrefsEntry entry : list) vs.add(entry.key + "=" + entry.value);
        return vs;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (XMLPrefsEntry entry : list) {
            builder.append(entry.key).append(" -> ").append(entry.value).append(Tuils.NEWLINE);
        }

        return builder.toString().trim();
    }
}