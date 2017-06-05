/*
 * Copyright (c) 2011. WillowTree Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sung.customview.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.sung.customview.R;

import java.io.IOException;
import java.util.HashMap;

/**
 * User: Michael Lake Date: 11/21/11 Time: 5:36 PM
 * Allows you to set a custom font to a textview via XML.
 *
 * Start by adding xmlns:oak="http://oak/oak/schema" to the base of your view.
 * You can then specify a particular font with
 * oak:font="Your-font.ttf"
 *
 * Make sure the font you want to use is in a proper assets/fonts/ folder.
 *
 */
public class TextViewWithFont extends TextView {

    public static final String TAG = "TextViewWithFont";

    private static HashMap<String, Typeface> mFontMap;

    public TextViewWithFont(Context context) {
        this(context, null);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public TextViewWithFont(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        if (isInEditMode()) return;

        String fontName;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithFont);
            if (typedArray != null) {
                try {
                    fontName = typedArray.getString(R.styleable.TextViewWithFont_oakFont);
                    if (fontName != null) {
                        setTypeface(getStaticTypeFace(context, fontName));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        int fontNameRes = typedArray.getResourceId(R.styleable.TextViewWithFont_oakFont, -1);
                        if (fontNameRes != -1) {
                            fontName = context.getString(fontNameRes);
                            if (fontName != null) {
                                setTypeface(getStaticTypeFace(context, fontName));
                            }
                        }
                    } catch (IllegalArgumentException f) {
                        f.printStackTrace();
                    }
                }
                typedArray.recycle();
            }
        }
    }

    /**
     * @param context      Context for fetching Typeface
     * @param fontFileName Typeface to fetch. Must match a typeface name in /assets/fonts
     * @return Typeface
     */
    public static Typeface getStaticTypeFace(Context context, String fontFileName) {
        if (mFontMap == null) {
            initializeFontMap(context);
        }
        Typeface typeface = mFontMap.get(fontFileName);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + fontFileName);
        }
        return typeface;
    }

    private static void initializeFontMap(Context context) {
        mFontMap = new HashMap<String, Typeface>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] fontFileNames = assetManager.list("fonts");
            for (String fontFileName : fontFileNames) {
                Log.d(TAG, "Found font in assets: " + fontFileName);
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                mFontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
