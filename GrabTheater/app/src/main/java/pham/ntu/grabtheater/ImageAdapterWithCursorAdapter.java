package pham.ntu.grabtheater;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator PC on 3/31/2016.
 */
class ImageAdapterWithCursorAdapter extends CursorAdapter {
    private Context mContext;
    private String imagePath;
    private List<Movie> moviesList;

    public ImageAdapterWithCursorAdapter(Context c, Cursor cursor, int flags) {
        super(c, cursor, flags);
        mContext = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ImageView imageView = (ImageView) view;
        try {
            imagePath = cursor.getString(TabNowShowingFragment.COL_POSTER_PATH);
            imagePath = Config.IMG_BASE_URL
                    + imagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
            Picasso.with(mContext).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mContext)
                            .load(imagePath)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }
    }
}
