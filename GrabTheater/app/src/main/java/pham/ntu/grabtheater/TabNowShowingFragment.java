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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabNowShowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabNowShowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabNowShowingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters

    public static String imgBaseUrl = "http://image.tmdb.org/t/p/w500/";
    //public static final String PREFIX_API_KEY = "?api_key=";
    String movieTitle = null;
    public static GridView gridview;
    public static ImageAdapter mMovieImageAdapter;
    private OnFragmentInteractionListener mListener;
    public static List<Movie> moviesList = new ArrayList<Movie>();
    int pageNum =1;
    Button nextButton, previousButton;
    public static int totalPages =0;

    public TabNowShowingFragment() {
        // Required empty public constructor
    }

    public static TabNowShowingFragment newInstance(String param1, String param2) {
        TabNowShowingFragment fragment = new TabNowShowingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    private void updateMovieList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String additionalUrl = preferences.getString(getString(R.string.pref_sort_types_key),getString(R.string.pref_sort_types_nowplaying));
        MainActivity.additionalUrl = additionalUrl;
        GetDataTask dataTaskForNowShowing = new GetDataTask(getActivity(),MainActivity.additionalUrl,true,pageNum);
        dataTaskForNowShowing.execute();
    }

    @Override
    public void onStart() {
        updateMovieList();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_now_showing, container, false);
        //while(TabNowShowingFragment.moviesList.size()==0){}
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        mMovieImageAdapter = new ImageAdapter(getActivity(),TabNowShowingFragment.moviesList);
        gridview.setAdapter(mMovieImageAdapter);
        gridview.setDrawSelectorOnTop(false);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                movieTitle = moviesList.get(position).getTitle();
//                Toast.makeText(getActivity(), movieTitle, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Movie",
                        TabNowShowingFragment.moviesList.get(position));
                startActivity(intent);
            }
        });

        nextButton = (Button) rootView.findViewById(R.id.button_next);
        previousButton = (Button) rootView.findViewById(R.id.button_previous);
        if(pageNum==1) previousButton.setEnabled(false);
        else previousButton.setEnabled(true);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNum<totalPages) {
                    previousButton.setEnabled(true);
                    pageNum++;
                    if(pageNum==totalPages) nextButton.setEnabled(false);
                    GetDataTask dataTaskForNowShowing = new GetDataTask(getActivity(),MainActivity.additionalUrl,true,pageNum);
                    dataTaskForNowShowing.execute();
                    int i = 1000000;
                    while(i>0){
                        i--;
                        gridview.invalidateViews();
                    }
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageNum>1){
                    pageNum--;
                    if(pageNum==1) previousButton.setEnabled(false);
                    nextButton.setEnabled(true);
                    GetDataTask dataTaskForNowShowing = new GetDataTask(getActivity(),MainActivity.additionalUrl,true,pageNum);
                    dataTaskForNowShowing.execute();
                    int i = 1000000;
                    while(i>0){
                        i--;
                        gridview.invalidateViews();
                    }
                }
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
