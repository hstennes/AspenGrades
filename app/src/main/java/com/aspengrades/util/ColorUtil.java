package com.aspengrades.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.aspengrades.main.R;

public class ColorUtil {

    /**
     * Returns the color to be used to represent the given letter grade
     * @param context The context
     * @param grade The letter grade
     * @return The color representing the grade
     */
    public static int colorFromGrade(Context context, float grade){
        int gradeInt = Math.round(grade);
        if(context == null) return 0;
        if(gradeInt > 89) return ContextCompat.getColor(context, R.color.colorA);
        else if(gradeInt > 79) return ContextCompat.getColor(context, R.color.colorB);
        else if(gradeInt > 69) return ContextCompat.getColor(context, R.color.colorC);
        else if(gradeInt > 59) return ContextCompat.getColor(context, R.color.colorD);
        else if(gradeInt == -1) return ContextCompat.getColor(context, R.color.colorN);
        else return ContextCompat.getColor(context, R.color.colorF);
    }

}
