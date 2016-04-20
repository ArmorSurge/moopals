package com.caddosierra.mafiaparty.timer;

import android.graphics.Color;

public class Filter {
    private int color;

    public Filter(int mColor)
    {
        color = mColor;
    }

    public Integer getFiltered(float shadeFactor){
        shadeFactor = 1f - shadeFactor;
        return Color.argb(
                Color.alpha(color),
                (int) ((float) Color.red(color) * shadeFactor),
                (int) ((float) Color.green(color) * shadeFactor),
                (int) ((float) Color.blue(color) * shadeFactor));
    }
}
