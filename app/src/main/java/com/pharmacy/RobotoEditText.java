package com.pharmacy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by PCCS-0007 on 08-Feb-18.
 */

public class RobotoEditText extends EditText {
    public RobotoEditText(Context context) {
        super(context);
        if (isInEditMode())
            return;
        parseAttributes(null);
    }

    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode())
            return;
        parseAttributes(attrs);



    }

    private void parseAttributes(AttributeSet attrs) {
        int typeface;
        try {
            if (attrs == null) { //Not created from xml
                typeface = RobotoTextView.Roboto.ROBOTO_REGULAR;
            } else {
                TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
                typeface = values.getInt(R.styleable.RobotoTextView_typeface, RobotoTextView.Roboto.ROBOTO_REGULAR);
                values.recycle();
            }
            setTypeface(getRoboto(typeface));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private Typeface getRoboto(int typeface) {
        return getRoboto(getContext(), typeface);
    }

    public static Typeface getRoboto(Context context, int typeface) {
        switch (typeface) {
            case RobotoEditText.Roboto.ROBOTO_BLACK:
                if (RobotoEditText.Roboto.sRobotoBlack == null) {
                    RobotoEditText.Roboto.sRobotoBlack = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Black.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBlack;
            case RobotoEditText.Roboto.ROBOTO_BLACK_ITALIC:
                if (RobotoEditText.Roboto.sRobotoBlackItalic == null) {
                    RobotoEditText.Roboto.sRobotoBlackItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-BlackItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBlackItalic;
            case RobotoEditText.Roboto.ROBOTO_BOLD:
                if (RobotoEditText.Roboto.sRobotoBold == null) {
                    RobotoEditText.Roboto.sRobotoBold = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Bold.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBold;
            case RobotoEditText.Roboto.ROBOTO_BOLD_CONDENSED:
                if (RobotoEditText.Roboto.sRobotoBoldCondensed == null) {
                    RobotoEditText.Roboto.sRobotoBoldCondensed = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBoldCondensed;
            case RobotoEditText.Roboto.ROBOTO_BOLD_CONDENSED_ITALIC:
                if (RobotoEditText.Roboto.sRobotoBoldCondensedItalic == null) {
                    RobotoEditText.Roboto.sRobotoBoldCondensedItalic = Typeface.createFromAsset(
                            context.getAssets(),
                            "fonts/Roboto-BoldCondensedItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBoldCondensedItalic;
            case RobotoEditText.Roboto.ROBOTO_BOLD_ITALIC:
                if (RobotoEditText.Roboto.sRobotoBoldItalic == null) {
                    RobotoEditText.Roboto.sRobotoBoldItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-BoldItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoBoldItalic;
            case RobotoEditText.Roboto.ROBOTO_CONDENSED:
                if (RobotoEditText.Roboto.sRobotoCondensed == null) {
                    RobotoEditText.Roboto.sRobotoCondensed = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Condensed.ttf");
                }
                return RobotoEditText.Roboto.sRobotoCondensed;
            case RobotoEditText.Roboto.ROBOTO_CONDENSED_ITALIC:
                if (RobotoEditText.Roboto.sRobotoCondensedItalic == null) {
                    RobotoEditText.Roboto.sRobotoCondensedItalic = Typeface
                            .createFromAsset(context.getAssets(),
                                    "fonts/Roboto-CondensedItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoCondensedItalic;
            case RobotoEditText.Roboto.ROBOTO_ITALIC:
                if (RobotoEditText.Roboto.sRobotoItalic == null) {
                    RobotoEditText.Roboto.sRobotoItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Italic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoItalic;
            case RobotoEditText.Roboto.ROBOTO_LIGHT:
                if (RobotoEditText.Roboto.sRobotoLight == null) {
                    RobotoEditText.Roboto.sRobotoLight = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Light.ttf");
                }
                return RobotoEditText.Roboto.sRobotoLight;
            case RobotoEditText.Roboto.ROBOTO_LIGHT_ITALIC:
                if (RobotoEditText.Roboto.sRobotoLightItalic == null) {
                    RobotoEditText.Roboto.sRobotoLightItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-LightItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoLightItalic;
            case RobotoEditText.Roboto.ROBOTO_MEDIUM:
                if (RobotoEditText.Roboto.sRobotoMedium == null) {
                    RobotoEditText.Roboto.sRobotoMedium = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Medium.ttf");
                }
                return RobotoEditText.Roboto.sRobotoMedium;
            case RobotoEditText.Roboto.ROBOTO_MEDIUM_ITALIC:
                if (RobotoEditText.Roboto.sRobotoMediumItalic == null) {
                    RobotoEditText.Roboto.sRobotoMediumItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-MediumItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoMediumItalic;
            default:
            case RobotoEditText.Roboto.ROBOTO_REGULAR:
                if (RobotoEditText.Roboto.sRobotoRegular == null) {
                    RobotoEditText.Roboto.sRobotoRegular = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Regular.ttf");
                }
                return RobotoEditText.Roboto.sRobotoRegular;
            case RobotoEditText.Roboto.ROBOTO_THIN:
                if (RobotoEditText.Roboto.sRobotoThin == null) {
                    RobotoEditText.Roboto.sRobotoThin = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-Thin.ttf");
                }
                return RobotoEditText.Roboto.sRobotoThin;
            case RobotoEditText.Roboto.ROBOTO_THIN_ITALIC:
                if (RobotoEditText.Roboto.sRobotoThinItalic == null) {
                    RobotoEditText.Roboto.sRobotoThinItalic = Typeface.createFromAsset(
                            context.getAssets(), "fonts/Roboto-ThinItalic.ttf");
                }
                return RobotoEditText.Roboto.sRobotoThinItalic;

        }
    }


    public static class Roboto {
        /*
         * From attrs.xml file: <enum name="robotoBlack" value="0" /> <enum
         * name="robotoBlackItalic" value="1" /> <enum name="robotoBold"
         * value="2" /> <enum name="robotoBoldItalic" value="3" /> <enum
         * name="robotoBoldCondensed" value="4" /> <enum
         * name="robotoBoldCondensedItalic" value="5" /> <enum
         * name="robotoCondensed" value="6" /> <enum
         * name="robotoCondensedItalic" value="7" /> <enum name="robotoItalic"
         * value="8" /> <enum name="robotoLight" value="9" /> <enum
         * name="robotoLightItalic" value="10" /> <enum name="robotoMedium"
         * value="11" /> <enum name="robotoMediumItalic" value="12" /> <enum
         * name="robotoRegular" value="13" /> <enum name="robotoThin" value="14"
         * /> <enum name="robotoThinItalic" value="15" />
         */
        public static final int ROBOTO_BLACK = 0;
        public static final int ROBOTO_BLACK_ITALIC = 1;
        public static final int ROBOTO_BOLD = 2;
        public static final int ROBOTO_BOLD_ITALIC = 3;
        public static final int ROBOTO_BOLD_CONDENSED = 4;
        public static final int ROBOTO_BOLD_CONDENSED_ITALIC = 5;
        public static final int ROBOTO_CONDENSED = 6;
        public static final int ROBOTO_CONDENSED_ITALIC = 7;
        public static final int ROBOTO_ITALIC = 8;
        public static final int ROBOTO_LIGHT = 9;
        public static final int ROBOTO_LIGHT_ITALIC = 10;
        public static final int ROBOTO_MEDIUM = 11;
        public static final int ROBOTO_MEDIUM_ITALIC = 12;
        public static final int ROBOTO_REGULAR = 13;
        public static final int ROBOTO_THIN = 14;
        public static final int ROBOTO_THIN_ITALIC = 15;

        private static Typeface sRobotoBlack;
        private static Typeface sRobotoBlackItalic;
        private static Typeface sRobotoBold;
        private static Typeface sRobotoBoldItalic;
        private static Typeface sRobotoBoldCondensed;
        private static Typeface sRobotoBoldCondensedItalic;
        private static Typeface sRobotoCondensed;
        private static Typeface sRobotoCondensedItalic;
        private static Typeface sRobotoItalic;
        private static Typeface sRobotoLight;
        private static Typeface sRobotoLightItalic;
        private static Typeface sRobotoMedium;
        private static Typeface sRobotoMediumItalic;
        private static Typeface sRobotoRegular;
        private static Typeface sRobotoThin;
        private static Typeface sRobotoThinItalic;
    }
}
