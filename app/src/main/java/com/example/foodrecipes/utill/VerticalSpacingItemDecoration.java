package com.example.foodrecipes.utill;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacingHeight;

    public VerticalSpacingItemDecoration(int spacingHeight) {
        this.spacingHeight = spacingHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.top = spacingHeight;
    }
}
