package com.github.glomadrian.roadrunner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import com.github.glomadrian.roadrunner.painter.configuration.PathPainterConfiguration;
import com.github.glomadrian.roadrunner.painter.configuration.factory.PathPainterConfigurationFactory;
import com.github.glomadrian.roadrunner.painter.determinate.DeterminatePainter;
import com.github.glomadrian.roadrunner.painter.determinate.DeterminatePathPainter;
import com.github.glomadrian.roadrunner.painter.determinate.factory.DeterminatePainterFactory;
import com.github.glomadrian.roadrunner.path.PathContainer;
import com.github.glomadrian.roadrunner.utils.AssertUtils;
import com.github.glomadrian.roadrunner.utils.RangeUtils;

import java.text.ParseException;

/**
 * Created by Yahya Bayramoglu on 08/04/16.
 */
public class ProgressRoadRunner extends RoadRunner {

    private static final String TAG = "ProgressRoadRunner";

    // Property to create an animation
    public static final String PROGRESS = "progress";

    private int originalWidth;
    private int originalHeight;
    private String pathData;
    private PathContainer pathContainer;
    private DeterminatePathPainter progressDeterminatePainter;

    private int min = 0;
    private int max = 100;
    private int progress = 0;

    private PathPainterConfiguration pathPainterConfiguration;
    private boolean firstDraw = true;

    public ProgressRoadRunner(Context context) {
        super(context);
        throw new UnsupportedOperationException("The view can not be created programmatically yet");
    }

    public ProgressRoadRunner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPath(attrs);
        initConfiguration(attrs);
    }

    public ProgressRoadRunner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPath(attrs);
        initConfiguration(attrs);
    }

    public void setProgress(int value) {
        if (value <= max || value >= min) {
            this.progress = value;
            float progress = RangeUtils.getFloatValueInRange(min, max, 0f, 1f, value);
            if (progressDeterminatePainter != null) {
                progressDeterminatePainter.setProgress(progress);
            }
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public void setColor(int color) {
        progressDeterminatePainter.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            pathContainer = buildPathData(w, h, pathData, originalWidth, originalHeight);
            initPathPainter();
        } catch (ParseException e) {
            Log.e(TAG, "Path parse exception:", e);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!firstDraw) {
            progressDeterminatePainter.paintPath(canvas);
        } else {
            firstDraw = false;
        }
    }

    private void initPath(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.RoadRunner);
        pathData = attributes.getString(R.styleable.RoadRunner_path_data);
        originalWidth = attributes.getInteger(R.styleable.RoadRunner_path_original_width, 0);
        originalHeight = attributes.getInteger(R.styleable.RoadRunner_path_original_height, 0);

        AssertUtils.assertThis(pathData != null, "Path data must be defined", this.getClass());
        AssertUtils.assertThis(!pathData.isEmpty(), "Path data must be defined", this.getClass());
        AssertUtils.assertThis(!pathData.equals(""), "Path data must be defined", this.getClass());
        AssertUtils.assertThis(originalWidth > 0, "Original with of the path must be defined",
                this.getClass());
        AssertUtils.assertThis(originalHeight > 0, "Original height of the path must be defined",
                this.getClass());
    }

    private void initConfiguration(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.RoadRunner);
        min = attributes.getInteger(R.styleable.RoadRunner_min, min);
        max = attributes.getInteger(R.styleable.RoadRunner_max, max);
        pathPainterConfiguration =
                PathPainterConfigurationFactory.makeConfiguration(attributes, DeterminatePainter.PROGRESS);
        attributes.recycle();
    }

    private void initPathPainter() {
        progressDeterminatePainter = DeterminatePainterFactory.makeIndeterminatePathPainter(
                DeterminatePainter.PROGRESS, pathContainer, this, pathPainterConfiguration);
    }

}