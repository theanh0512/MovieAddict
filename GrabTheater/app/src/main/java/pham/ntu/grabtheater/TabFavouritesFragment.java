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

public class TabFavouritesFragment extends Fragment {
    public static SharedPreferences likedMovies;
    public static List<Movie> moviesFavouriteList = new ArrayList<>();
    public static ImageAdapter mMovieImageAdapter;
    @BindView(R.id.gridView_favourite)
    GridView favourite_gridview;

    private OnFragmentInteractionListener mListener;

    public TabFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_tab_favourites, container, false);
        ButterKnife.bind(this, rootView);
        mMovieImageAdapter = new ImageAdapter(getActivity(), moviesFavouriteList);
        favourite_gridview.setAdapter(mMovieImageAdapter);
        favourite_gridview.setDrawSelectorOnTop(false);
        return rootView;
    }

    @OnItemClick(R.id.gridView_favourite)
    public void itemClick(int position) {
        Movie movie = moviesFavouriteList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Movie", movie);
        bundle.putBoolean("Hide Like Button", true);
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
