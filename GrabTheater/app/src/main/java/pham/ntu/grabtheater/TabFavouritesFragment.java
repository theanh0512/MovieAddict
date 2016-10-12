package pham.ntu.grabtheater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TabFavouritesFragment extends Fragment {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static SharedPreferences likedMovies;
    public static List<Movie> moviesFavouriteList = new ArrayList<Movie>();
    public static GridView favourite_gridview;
    public static ImageAdapter mMovieImageAdapter;

    private OnFragmentInteractionListener mListener;

    public TabFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        likedMovies = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        moviesFavouriteList.clear();
        for(int i=0;i< DetailFragment.moviesTitleList.size();i++){
            String json = likedMovies.getString(DetailFragment.moviesTitleList.get(i), "");
            Movie movie = gson.fromJson(json, Movie.class);
            moviesFavouriteList.add(movie);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_tab_favourites, container, false);
        favourite_gridview = (GridView) rootView.findViewById(R.id.gridView_favourite);
        mMovieImageAdapter = new ImageAdapter(getActivity(),moviesFavouriteList);
        favourite_gridview.setAdapter(mMovieImageAdapter);
        favourite_gridview.setDrawSelectorOnTop(false);
        favourite_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = moviesFavouriteList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Movie",movie);
                bundle.putBoolean("Hide Like Button",true);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Bundle",bundle);
                startActivity(intent);
            }
        });
        return rootView;
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
