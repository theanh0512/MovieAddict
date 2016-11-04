package pham.ntu.grabtheater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import pham.ntu.grabtheater.adapter.ImageAdapterWithBaseAdapter;
import pham.ntu.grabtheater.entity.Movie;

public class TabFavouritesFragment extends Fragment {
    public static SharedPreferences likedMovies;
    public static List<Movie> moviesFavouriteList = new ArrayList<>();
    public static ImageAdapterWithBaseAdapter mMovieImageAdapterWithBaseAdapter;
    @BindView(R.id.gridView_favourite)
    GridView favourite_gridview;

    private OnFragmentInteractionListener mListener;

    public TabFavouritesFragment() {
        // Required empty public constructor
    }

    public void invalidateGridview() {
        favourite_gridview.invalidateViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_tab_favourites, container, false);
        ButterKnife.bind(this, rootView);

        updateFavouriteList();

        favourite_gridview.setAdapter(mMovieImageAdapterWithBaseAdapter);
        favourite_gridview.setDrawSelectorOnTop(false);
        return rootView;
    }

    public void updateFavouriteList() {
        likedMovies = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Gson gson = new Gson();
        moviesFavouriteList.clear();
        Set<String> titleSet = likedMovies.getStringSet("titleSet", null);
        if (titleSet != null) {
            String[] titleArray = titleSet.toArray(new String[titleSet.size()]);
            for (int i = 0; i < titleArray.length; i++) {
                String json = likedMovies.getString(titleArray[i], "");
                Movie movie = gson.fromJson(json, Movie.class);
                moviesFavouriteList.add(movie);
            }
        }
        mMovieImageAdapterWithBaseAdapter = new ImageAdapterWithBaseAdapter(getActivity(), moviesFavouriteList);
    }

    @OnItemClick(R.id.gridView_favourite)
    public void itemClick(int position) {
        Movie movie = moviesFavouriteList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Movie", movie);
        bundle.putBoolean("Liked", true);
        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Bundle", bundle);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
