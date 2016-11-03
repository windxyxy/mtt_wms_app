package com.djx.wms.anter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by gfgh on 2016/3/15.
 */
public class tags extends GridView {


    public tags(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public tags(Context context) {
        super(context);
    }

    public tags(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}