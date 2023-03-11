package com.innoventionist.artham;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DictionaryActivity extends Activity {
    Database database;
    Prefs prefs;

    SearchView searchView;

    final String RECENTS = "RECENTS";
    final String MEANINGS = "MEANINGS";
    final String SUGGESTIONS = "SUGGESTIONS";

    String currentView = RECENTS;

    TextView meaningView;

    View recentsContainer, suggestionsContainer, meaningsContainer;

    ListView recentsListView, bookmarksListView, suggestionsListView;

    List<String> recents = new ArrayList<>();
    ArrayAdapter<String> recentsAdapter;

    List<String> bookmarks = new ArrayList<>();
    ArrayAdapter<String> bookmarksAdapter;

    List<String> suggestions = new ArrayList<>();
    ArrayAdapter<String> suggestionsAdapter;


    TextView recentsTitle, bookmarksTitle, meaningsTitle, suggestionsTitle;

    FloatingActionMenu fam;
    TextToSpeech t1;
    ImageView speakbtn, clearHistoryButton, bookmarkButton;
    int speechstatus;

    int styleIndex = 1;

    String clipboardContent;

    Typeface font;
    Typeface fontMedium;

    // region Logging
    public boolean isDebuggable = false;

    private void setIsDebuggable(Context context) {
        isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public void log(String message) {
        if (isDebuggable)
            Log.i("ARTHAM", message);
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setIsDebuggable(getApplicationContext());

        prefs = new Prefs(this);
        prefs.init();

        database = new Database(this);
        database.init();

        prefs.load();

        font = Typeface.createFromAsset(getAssets(), "fonts/baloochettan.ttf");
        fontMedium = Typeface.createFromAsset(getAssets(), "fonts/baloochettanm.ttf");

        boolean darkTheme = styleIndex == 3; // 1 - light, 3 - dark
        setAppTheme(darkTheme);

        setContentView(R.layout.activity_main);

        setActivityTheme(darkTheme);
        setViewTheme(darkTheme);

        searchView = findViewById(R.id.searchView);
        EditText editText = (EditText) findViewById(getResources().getIdentifier("android:id/search_src_text", null, null));
        ImageView searchIcon = (ImageView) findViewById(getResources().getIdentifier("android:id/search_mag_icon", null, null));
        ImageView closeButton = (ImageView) findViewById(getResources().getIdentifier("android:id/search_close_btn", null, null));
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        closeButton.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        int searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.TRANSPARENT);
        }

        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(darkTheme ? Color.WHITE : Color.BLACK);
        editText.setPadding(0, 0, 0, 0);

        recentsListView = findViewById(R.id.recentsListView);
        bookmarksListView = findViewById(R.id.bookmarksListView);
        suggestionsListView = findViewById(R.id.suggestionsListView);
        meaningsContainer = findViewById(R.id.meaningsContainer);
        meaningsTitle = findViewById(R.id.meaningsTitle);
        recentsContainer = findViewById(R.id.recentsContainer);
        suggestionsContainer = findViewById(R.id.suggestionsContainer);
        suggestionsTitle = findViewById(R.id.suggestionsTitle);
        recentsTitle = findViewById(R.id.recentsTitle);
        bookmarksTitle = findViewById(R.id.bookmarksTitle);
        meaningView = findViewById(R.id.meaningView);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speechstatus = status;
                if (status != TextToSpeech.ERROR) {
                }
            }
        });

        ImageButton voiceButton = (ImageButton) findViewById(R.id.voice);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognition(v);
            }
        });

        speakbtn = (ImageView) findViewById(R.id.speak);
        speakbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechstatus != TextToSpeech.ERROR) {
                    String text = meaningsTitle.getText().toString();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String utteranceId = this.hashCode() + "";
                            t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                        } else {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                            t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Make sure you have configured Text to Speech in your phone settings.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clearHistoryButton = (ImageView) findViewById(R.id.clearHistory);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryConfirm();
            }
        });

        bookmarkButton = (ImageView) findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = meaningsTitle.getText().toString();
                if(bookmarks.contains(word)) {
                    removeBookmark(word);
                } else {
                    addBookmark(word);
                }
            }
        });

        recentsListView.setVerticalScrollBarEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                switchTo(MEANINGS);
                searchView.clearFocus();
                query = query.toLowerCase().trim();
                if (!query.trim().equals("")) {
                    addRecent(query);
                    //lsttitle.setText("\"" + query + "\" means");
                    meaningsTitle.setText(query); // query.substring(0, 1).toUpperCase() + query.substring(1));

                    int middle = database.searchWord(query);

                    if (!database.b2ascii(database.words.get(middle)).split("\t")[0].trim().equals(query)) {
                        suggestionsTitle.setText("Sorry !");
                        meaningView.setText("Not found in database.");
                        return false;
                    }

                    int pos = Integer.parseInt(database.b2ascii(database.words.get(middle)).split("\t")[1]);
                    pos = database.map.get(query.toLowerCase().charAt(0)) + pos;

                    meaningView.setText(database.getMeaning(pos));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showSuggestions(newText.toLowerCase().trim());

                findViewById(R.id.voice).setVisibility(!newText.equals("") ? View.GONE : View.VISIBLE);
                findViewById(R.id.clearquery).setVisibility(newText.equals("") ? View.GONE : View.VISIBLE);

                return false;
            }
        });


        class CustomListAdapter extends ArrayAdapter<String> {
            private Context mContext;
            private int id;
            private List<String> items;
            private Typeface typeface;

            public CustomListAdapter(Context context, int textViewResourceId, List<String> list, Typeface tf) {
                super(context, textViewResourceId, list);
                mContext = context;
                id = textViewResourceId;
                items = list;
                typeface = tf;
            }

            @Override
            public View getView(int position, View v, ViewGroup parent) {
                View mView = v;
                if (mView == null) {
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    mView = vi.inflate(id, null);
                }

                TextView text = (TextView) mView.findViewById(R.id.listitem);

                if (items.get(position) != null) {
                    text.setText(items.get(position));
                    text.setTypeface(typeface);
                }

                return mView;
            }
        }

        recentsAdapter = new CustomListAdapter(this, R.layout.listitem, recents, font);
        bookmarksAdapter = new CustomListAdapter(this, R.layout.listitem, bookmarks, font);
        suggestionsAdapter = new CustomListAdapter(this, R.layout.listitem, suggestions, font);

        recentsListView.setAdapter(recentsAdapter);
        recentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(recentsAdapter.getItem(position), true);
                loadMeaning();
                searchView.clearFocus();
            }
        });
        recentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeRecent(position);
                return true;
            }
        });

        suggestionsListView.setAdapter(suggestionsAdapter);
        suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(suggestionsAdapter.getItem(position), true);
                loadMeaning();
                searchView.clearFocus();
            }
        });

        bookmarksListView.setAdapter(bookmarksAdapter);
        bookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(bookmarksAdapter.getItem(position), true);
                loadMeaning();
                searchView.clearFocus();
            }
        });
        bookmarksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeBookmarkAt(position);
                return true;
            }
        });

        setListViewHeightBasedOnChildren(recentsListView);
        setListViewHeightBasedOnChildren(bookmarksListView);

        checkClipBoard();

        FloatingActionButton rate = (FloatingActionButton) findViewById(R.id.rate_app);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStore(v);
            }
        });

        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.share_app);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp(v);
            }
        });

        FloatingActionButton website = (FloatingActionButton) findViewById(R.id.visit_website);
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebSite(v);
            }
        });

        FloatingActionButton theme = (FloatingActionButton) findViewById(R.id.toggle_theme);
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme(v);
            }
        });

        fam = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);

        findViewById(R.id.clearquery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(findViewById(getResources().getIdentifier("android:id/search_src_text", null, null)), 0);
            }
        });
    }

    private void showClearHistoryConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Clear history")
                .setMessage("This will clear entire recent searches. Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        clearRecent();
                    }})
                .setNegativeButton("Go back", null).show();
    }

    private void setAppTheme(boolean darkTheme) {
        setTheme(darkTheme ? R.style.AppThemeDark : R.style.AppThemeLight);
    }

    private void setViewTheme(boolean darkTheme) {
        SearchView searchView1 = (SearchView) findViewById(R.id.searchView);
        EditText editText = (EditText) findViewById(getResources().getIdentifier("android:id/search_src_text", null, null));
        ImageView searchIcon = (ImageView) findViewById(getResources().getIdentifier("android:id/search_mag_icon", null, null));
        ImageView closeButton = (ImageView) findViewById(getResources().getIdentifier("android:id/search_close_btn", null, null));
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        closeButton.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        int searchPlateId = getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView1.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(Color.TRANSPARENT);
        }

        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setTextColor(Color.parseColor("#555555"));
        editText.setHintTextColor(Color.parseColor("#AAAAAA"));
        editText.setPadding(0, 0, 0, 0);

        findViewById(R.id.mainlayout).setBackgroundColor(darkTheme ? Color.BLACK : Color.WHITE);
        setTintOfImage(R.id.speak, darkTheme);
        setTintOfImage(R.id.clearHistory, darkTheme);
//        setTintOfImage(R.id.title, darkTheme);
        setTintOfImage(R.id.searchIcon, darkTheme);
        setTintOfImage(R.id.voice, darkTheme);
        setTintOfImage(R.id.clearquery, darkTheme);
//        setTintOfImage(R.id.menu, darkTheme);

        setFontOfText(R.id.title, fontMedium);
        setFontOfText(R.id.suggestionsTitle, fontMedium);
        setFontOfText(R.id.bookmarksTitle, fontMedium);
        setFontOfText(R.id.meaningsTitle, fontMedium);
        setFontOfText(R.id.recentsTitle, fontMedium);
        setFontOfText(R.id.meaningView, font);
    }

    private void setFontOfText(int id, Typeface font) {
        ((TextView) findViewById(id)).setTypeface(font);
    }

    private void setTintOfImage(int id, boolean darkTheme) {
        ((ImageView) findViewById(id)).setColorFilter(darkTheme ? Color.WHITE : Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void setActivityTheme(boolean darkTheme) {
        Window window = getWindow();
        View view = window.getDecorView();
        int flags = view.getSystemUiVisibility();

        // Build.VERSION_CODES.O
        if (Build.VERSION.SDK_INT >= 26) {
            if (!darkTheme)
                flags |= 0x10; // View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else
                flags &= ~0x10;

            window.setNavigationBarColor(darkTheme ? Color.BLACK : Color.WHITE);
        }
        // Build.VERSION_CODES.M
        if (Build.VERSION.SDK_INT >= 23) {
            if (!darkTheme)
                flags |= 0x2000; // View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else
                flags &= ~0x2000;

            window.setStatusBarColor(darkTheme ? Color.BLACK : Color.WHITE);
        }
        if (Build.VERSION.SDK_INT >= 28) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setNavigationBarDividerColor(darkTheme ? Color.BLACK : Color.WHITE);
        }
        view.setSystemUiVisibility(flags);
    }

    void switchTo(String view) {
        log("Switch to " + view);

        recentsContainer.setVisibility(View.INVISIBLE);
        suggestionsContainer.setVisibility(View.INVISIBLE);
        meaningsContainer.setVisibility(View.INVISIBLE);

        ScrollView scrollView;

        if (view.equals(RECENTS)) {
            scrollView = (ScrollView) recentsContainer;
            recentsContainer.setVisibility(View.VISIBLE);
        } else if(view.equals(SUGGESTIONS)) {
            scrollView = (ScrollView) suggestionsContainer;
            suggestionsContainer.setVisibility(View.VISIBLE);
        } else {
            scrollView = (ScrollView) meaningsContainer;
            meaningsContainer.setVisibility(View.VISIBLE);
        }

        currentView = view;
        scrollView.smoothScrollTo(0, 0);
    }

    void loadMeaning() {
        String query = searchView.getQuery().toString();
        query = query.toLowerCase().trim();
        if (!query.trim().equals("")) {
            addRecent(query);
            meaningsTitle.setText(query); //+ query.substring(0, 1).toUpperCase() + query.substring(1)

            int middle = database.searchWord(query);

            if (!database.b2ascii(database.words.get(middle)).split("\t")[0].trim().equals(query)) {
                suggestionsTitle.setText("Sorry !");
                meaningView.setText("Not found in database.");
                return;
            }

            int pos = Integer.parseInt(database.b2ascii(database.words.get(middle)).split("\t")[1]);
            pos = database.map.get(query.toLowerCase().charAt(0)) + pos;

            meaningView.setText(database.getMeaning(pos));

            if(bookmarks.contains(query)) {
                bookmarkButton.setImageResource(R.drawable.bookmark_true);
            } else {
                bookmarkButton.setImageResource(R.drawable.bookmark_false);
            }

            switchTo(MEANINGS);
            searchView.clearFocus();
        }
    }

    @Override
    public void onBackPressed() {
        log("onBackPressed");
        if (currentView.equals(MEANINGS)) {
            switchTo(RECENTS);
            if (!searchView.getQuery().toString().equals("")) {
                searchView.setQuery("", false);
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(findViewById(getResources().getIdentifier("android:id/search_src_text", null, null)), 0);
            }
        } else if (!searchView.getQuery().toString().equals("") && !searchView.hasFocus()) {
            searchView.setQuery("", false);
            searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkClipBoard();
    }


    // region Recents

    void clearRecent() {
        recents.clear();
        recentsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(recentsListView);
        if (fam.isOpened()) {
            fam.close(true);
        }
        searchView.setQuery("", false);
        prefs.save();
    }

    void removeRecent(int pos) {
        recents.remove(pos);
        recentsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(recentsListView);
        prefs.save();
    }

    void addRecent(String query) {
        if(recents.contains(query)) {
           recents.remove(query);
        }
        recents.add(0, query);
        recentsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(recentsListView);
        prefs.save();
    }

    // endregion


    // region Bookmarks

    void removeBookmark(String word) {
        bookmarks.remove(word);
        bookmarksAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(bookmarksListView);
        prefs.save();
        bookmarkButton.setImageResource(R.drawable.bookmark_false);
        Toast.makeText(this, "Removed bookmark", Toast.LENGTH_SHORT).show();
    }

    void removeBookmarkAt(int pos) {
        bookmarks.remove(pos);
        bookmarksAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(bookmarksListView);
        prefs.save();
        Toast.makeText(this, "Removed bookmark", Toast.LENGTH_SHORT).show();
    }

    void addBookmark(String word) {
        bookmarks.add(0, word);
        bookmarksAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(bookmarksListView);
        prefs.save();
        bookmarkButton.setImageResource(R.drawable.bookmark_true);
        Toast.makeText(this, "Added to bookmarks", Toast.LENGTH_SHORT).show();
    }

    // endregion

    void showSuggestions(String newText) {
        newText = newText.toLowerCase();
        if (!newText.equals("") && database.words.size() != 0) {
            int middle = database.searchWord(newText);
            suggestionsAdapter.clear();
            for (int i = middle; i < middle + 5 && i < database.words.size(); i++) {
                suggestionsAdapter.add(database.b2ascii(database.words.get(i)).split("\t")[0]);
            }
            setListViewHeightBasedOnChildren(suggestionsListView);
            suggestionsTitle.setText("Did you mean ?");
            switchTo(SUGGESTIONS);
        } else {
            switchTo(RECENTS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (fam.isOpened()) {
                Rect outRect = new Rect();
                fam.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    fam.close(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    void checkClipBoard() {
        try {
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData abc = myClipboard.getPrimaryClip();
            ClipData.Item item = abc.getItemAt(0);
            String text = item.getText().toString().toLowerCase().trim();

            if (text.length() < 15 && recentsAdapter.getPosition(text) == -1 && database.isAlpha(text) && !clipboardContent.equals(text)) {
                searchView.setQuery(text.toLowerCase().trim(), false);
                showSuggestions(searchView.getQuery().toString());
                clipboardContent = text;
                prefs.save();
            }
        } catch (Exception ignored) {}
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    void openWebSite(View view) {
        String url = "https://ajinasokan.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    void openStore(View view) {
        String url = "https://play.google.com/store/apps/details?id=com.innoventionist.artham";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    void shareApp(View view) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Artham(അര്\u200Dത്ഥം) - The Biggest, Smallest, Cutest, Smartest, Offline English - Malayalam Dictionary");
        share.putExtra(Intent.EXTRA_TEXT, "Hi friends, check out Artham(അര്\u200Dത്ഥം) English-Malayalam Dictionary. It is completely offline, has the largest database with over 2,15,600+ definitions, yet being the smallest app in the Google Play Store. It also has an intelligent clipboard capture system to easily bring the meaning of the word you are looking for. Read more about the app and download it for free from here:\nhttps://play.google.com/store/apps/details?id=com.innoventionist.artham");
        startActivity(Intent.createChooser(share, "Share This App"));
    }

    void toggleTheme(View view) {
        if (styleIndex == 1) {
            styleIndex = 3;
        } else {
            styleIndex = 1;
        }
        prefs.save();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }



    void startRecognition(View view) {
        try {
            String DIALOG_TEXT = "Speak the word to search";
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, DIALOG_TEXT);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong. Please check whether your device supports voice recognition.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
        super.onActivityResult(requestCode, resultcode, intent);
        ArrayList<String> speech;
        if (resultcode == RESULT_OK) {
            if (requestCode == 1) {
                speech = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (speech.size() > 0)
                    searchView.setQuery(speech.get(0), true);
                else
                    Toast.makeText(getApplicationContext(), "Unable to recognize. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}