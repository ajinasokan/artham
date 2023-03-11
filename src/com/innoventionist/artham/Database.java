package com.innoventionist.artham;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private final DictionaryActivity activity;

    Database(DictionaryActivity activity) {
        this.activity = activity;
    }

    List<byte[]> words = new ArrayList<byte[]>();

    char mc[] = {'\t', '\n', ' ', '!', '"', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '?', 'A', 'B', 'C', 'D', 'T', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', 'á', '⌡', '÷', '°', '\u0d02', '\u0d03', '\u0d05', '\u0d06', '\u0d07', '\u0d08', '\u0d09', '\u0d0a', '\u0d0b', '\u0d0e', '\u0d0f', '\u0d10', '\u0d12', '\u0d13', '\u0d14', '\u0d15', '\u0d16', '\u0d17', '\u0d18', '\u0d19', '\u0d1a', '\u0d1b', '\u0d1c', '\u0d1d', '\u0d1e', '\u0d1f', '\u0d20', '\u0d21', '\u0d22', '\u0d23', '\u0d24', '\u0d25', '\u0d26', '\u0d27', '\u0d28', '\u0d2a', '\u0d2b', '\u0d2c', '\u0d2d', '\u0d2e', '\u0d2f', '\u0d30', '\u0d31', '\u0d32', '\u0d33', '\u0d34', '\u0d35', '\u0d36', '\u0d37', '\u0d38', '\u0d39', '\u0d3e', '\u0d3f', '\u0d40', '\u0d41', '\u0d42', '\u0d43', '\u0d46', '\u0d47', '\u0d48', '\u0d4a', '\u0d4b', '\u0d4c', '\u0d4d', '\u0d57', '\u0d6a', '\u0d7a', '\u0d7b', '\u0d7c', '\u0d7d', '\u0d7e', '\u200b', '\u200c', '\u200d', '\u2026', '\u25e6', 'W'};
    Map<Character, Integer> map = new HashMap<Character, Integer>();
    Map<String, String> abbrev = new HashMap<String, String>();

    void init() {
        initMaps();
        new WordLoader(activity).execute("");
    }


    public static boolean isAlpha(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (!(c >= 65 && c <= 90) && !(c >= 97 && c <= 122)) {
                return false;
            }
        }
        return true;
    }

    void initMaps() {
        map.put('.', 0);
        map.put('a', 40);
        map.put('b', 250027);
        map.put('c', 442287);
        map.put('d', 798440);
        map.put('e', 1034955);
        map.put('f', 1176111);
        map.put('g', 1400580);
        map.put('h', 1520524);
        map.put('i', 1673246);
        map.put('j', 1823020);
        map.put('k', 1855912);
        map.put('l', 1890140);
        map.put('m', 2019656);
        map.put('n', 2197725);
        map.put('o', 2265893);
        map.put('p', 2367378);
        map.put('q', 2741375);
        map.put('r', 2766672);
        map.put('s', 3012731);
        map.put('t', 3642997);
        map.put('u', 3897415);
        map.put('v', 3975986);
        map.put('w', 4039956);
        map.put('x', 4152666);
        map.put('y', 4155610);
        map.put('z', 4167792);

        abbrev.put("n	", "Noun");
        abbrev.put("v	", "Verb");
        abbrev.put("a	", "Adjective");
        abbrev.put("adv	", "Adverb");
        abbrev.put("pron	", "Pronoun");
        abbrev.put("propn	", "Proper noun");
        abbrev.put("phrv	", "Phrasal verb");
        abbrev.put("conj	", "Conjunction");
        abbrev.put("interj	", "Interjection");
        abbrev.put("prep	", "Preposition");
        abbrev.put("pfx	", "Prefix");
        abbrev.put("sfx	", "Suffix");
        abbrev.put("idm	", "Idiom");
        abbrev.put("abbr	", "Abbreviation");
        abbrev.put("auxv	", "Auxiliary verb");
    }




    int searchWord(String query) {
        query = query.toLowerCase().trim();
        query = query.replaceAll("[^a-z ]", " ");

        int left = 0;
        int right = words.size() - 1;
        int middle = -1;
        String current = "";

        while (left <= right) {
            middle = (left + right) / 2;
            current = b2ascii(words.get(middle)).split("\t")[0].toLowerCase().trim().replaceAll("[^a-z ]", " ");
            if (current.equals(query))
                break;
            if (left == right)
                break;
            if (current.compareTo(query) < 0)
                left = middle + 1;
            if (current.compareTo(query) > 0)
                right = middle - 1;
        }
        if (middle - 1 >= 0)
            if (b2ascii(words.get(middle - 1)).startsWith(query)) {
                middle = middle - 1;
            }
        if (!b2ascii(words.get(middle)).startsWith(query)) {
            if (middle + 1 < words.size())
                if (b2ascii(words.get(middle + 1)).startsWith(query)) {
                    middle = middle + 1;
                }
        }

        return middle;
    }

    Spanned getMeaning(int pos) {
        LinkedHashMap<String, List<String>> meanings = new LinkedHashMap<String, List<String>>();

        String out = "";
        try {
            InputStream inputStream = activity.getResources().openRawResource(R.raw.mdb);
            inputStream.skip(pos);
            int data = inputStream.read();
            while (data != 127) {
                out += mc[data];
                data = inputStream.read();
            }
            inputStream.close();

            Object key[] = abbrev.keySet().toArray();
            String lines[] = out.split("\n");

            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < lines.length; j++) {
                    if (lines[j].startsWith(key[i].toString())) {
                        if (!meanings.containsKey(abbrev.get(key[i]))) {
                            meanings.put(abbrev.get(key[i]), new ArrayList<String>());
                        }
                        meanings.get(abbrev.get(key[i])).add(lines[j].replace(key[i].toString(), ""));
                        //lines[j] = lines[j].replace(key[i].toString(), abbrev.get(key[i]) + ". ");
                        lines[j] = "";
                    }
                }
            }
            out = "";
            for (Map.Entry<String, List<String>> entry : meanings.entrySet()) {
                out += "<i>" + entry.getKey() + "</i><br><br>";
                for (int i = 0; i < entry.getValue().size(); i++) {
                    out += "○  " + entry.getValue().get(i) + "<br><br>";
                }
            }
            for (String line : lines) {
                if (line.length() > 0) out += line + "<br><br>";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (out.endsWith("<br><br>"))
            out = out.substring(0, out.length() - 8);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return (Html.fromHtml(out, Html.FROM_HTML_MODE_LEGACY));
        } else {
            return (Html.fromHtml(out));
        }
    }


    class WordLoader extends AsyncTask<String, Integer, String> {
        private final DictionaryActivity activity;

        WordLoader(DictionaryActivity activity) {
            this.activity = activity;
        }

        String line = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                activity.getResources().openRawResource(R.raw.wdbd)));
                while ((line = bufferedReader.readLine()) != null) {
                    words.add(line.getBytes(Charset.forName("US-ASCII")));
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.showSuggestions(activity.searchView.getQuery().toString());
                }
            });
            return "";
        }
    }

    public static String b2ascii(byte[] array) {
        try {
            return new String(array, "US-ASCII");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
