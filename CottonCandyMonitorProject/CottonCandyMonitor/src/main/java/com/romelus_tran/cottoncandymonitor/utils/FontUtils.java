package com.romelus_tran.cottoncandymonitor.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Brian on 12/7/13.
 */
public class FontUtils {
    public static final String FONT_CAVIAR_DREAMS = "fonts/CaviarDreams.ttf";
    public static final String FONT_CAVIAR_DREAMS_BOLD = "fonts/CaviarDreams_Bold.ttf";
    public static final String FONT_CAVIAR_DREAMS_BOLDITALIC = "fonts/CaviarDreams_BoldItalic.ttf";
    public static final String FONT_CAVIAR_DREAMS_ITALIC = "fonts/CaviarDreams_Italic.ttf";

    // store the opened typefaces(fonts)
    private static final Hashtable<String, Typeface>  mCache = new Hashtable<String, Typeface>();

    private static Context _context;

    public static void getContext(Context context) {
        if (_context == null) {
            _context = context;
        }
    }

    /**
     * Load the given font from assets
     *
     * @param fontName font name
     * @return Typeface object representing the font painting
     */
    public static Typeface loadFontFromAssets(String fontName) {

        // make sure we load each font only once
        if (!mCache.containsKey(fontName)) {
            Typeface typeface = Typeface.createFromAsset(_context.getAssets(), fontName);
            mCache.put(fontName, typeface);
        }

        return mCache.get(fontName);
    }
}
