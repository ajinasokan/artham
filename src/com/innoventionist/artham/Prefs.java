package com.innoventionist.artham;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Prefs {

    private final DictionaryActivity activity;

    Prefs(DictionaryActivity activity) {
        this.activity = activity;
    }

    SharedPreferences sharedPref;
    SharedPreferences.Editor editer;

    void init() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        editer = sharedPref.edit();
    }

    void load() {
        activity.recents = new ArrayList<>();
        activity.bookmarks = new ArrayList<>();
        activity.styleIndex = 1;
        activity.clipboardContent = "";

        if (sharedPref.contains("pref")) {
            activity.log("has new prefs");

            String prefString = sharedPref.getString("pref", "");
            try {
                JSONObject pref = new JSONObject(prefString);
                JSONArray jrecents = pref.getJSONArray("recents");
                for (int i = 0; i < jrecents.length(); i++) {
                    activity.recents.add(jrecents.getString(i));
                }
                JSONArray jbookmarks = pref.getJSONArray("bookmarks");
                for (int i = 0; i < jbookmarks.length(); i++) {
                    activity.bookmarks.add(jbookmarks.getString(i));
                }
                activity.styleIndex = pref.getInt("theme");
                activity.clipboardContent = pref.getString("clipboardContent");
            } catch (JSONException e) {
                if(activity.isDebuggable) {
                    e.printStackTrace();
                }
            }
        } else if (sharedPref.contains("r1")) {
            activity.log("has only old prefs. restoring.");

            final int maxRecents = 10;
            for (int i = 1; i <= maxRecents; i++) {
                String val = sharedPref.getString("r" + i, "");
                if (!val.equals(""))
                    activity.recents.add(val);
            }
            activity.styleIndex = sharedPref.getInt("theme", 1);

            save();
        }
    }

    void save() {
        activity.log("saving prefs");
        try {
            JSONObject pref = new JSONObject();
            JSONArray jrecents = new JSONArray(activity.recents);
            JSONArray jbookmarks = new JSONArray(activity.bookmarks);
            pref.put("recents", jrecents);
            pref.put("bookmarks", jbookmarks);
            pref.put("theme", activity.styleIndex);
            pref.put("clipboardContent", activity.clipboardContent);
            editer.putString("pref", pref.toString());
            editer.apply();
        } catch (JSONException e) {
            if(activity.isDebuggable) {
                e.printStackTrace();
            }
        }
    }
}
