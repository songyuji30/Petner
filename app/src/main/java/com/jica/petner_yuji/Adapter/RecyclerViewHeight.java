package com.jica.petner_yuji.Adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHeight extends RecyclerView.ItemDecoration {

    private final int divHeight;

    public RecyclerViewHeight(int divHeight)
    {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = divHeight;
    }
}