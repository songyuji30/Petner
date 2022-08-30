package com.jica.petner_yuji.Adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewWidth extends RecyclerView.ItemDecoration {

    private final int divWidth;

    public RecyclerViewWidth(int divWidth)
    {
        this.divWidth = divWidth;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = divWidth;
    }
}