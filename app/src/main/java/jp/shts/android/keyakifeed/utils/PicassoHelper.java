package jp.shts.android.keyakifeed.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jp.shts.android.keyakifeed.views.transformations.CircleTransformation;

public class PicassoHelper {

    private static final CircleTransformation CIRCLE_TRANSFORMATION = new CircleTransformation();

    public static void load(ImageView target, String url) {
        Picasso.with(target.getContext())
                .load(url)
                .fit()
                .centerCrop()
                .into(target);
    }

    public static void loadAndCircleTransform(Context context, ImageView target, String url) {
        Picasso.with(context)
                .load(url)
                .fit()
                .centerCrop()
                .transform(CIRCLE_TRANSFORMATION)
                .into(target);
    }

    public static void loadAndCircleTransform(ImageView target, String url) {
        Picasso.with(target.getContext())
                .load(url)
                .fit()
                .centerCrop()
                .transform(CIRCLE_TRANSFORMATION)
                .into(target);
    }

}