package pham.ntu.grabtheater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator PC on 3/31/2016.
 */
class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String imagePath;
    private List<Movie> moviesList;

    public ImageAdapter(Context c, List<Movie> moviesList) {
        mContext = c;
        this.moviesList = moviesList;
    }

    //to do: count number of snakes
    public int getCount() {
        return moviesList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 500));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        if (moviesList.size() != 0) {
            try {
                imagePath = moviesList.get(position).getPoster_path();
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
            }catch (IndexOutOfBoundsException e){}
        }
        return imageView;
    }
}
