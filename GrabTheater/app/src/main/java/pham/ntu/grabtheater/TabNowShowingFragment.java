package pham.ntu.grabtheater;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pham.ntu.grabtheater.data.MovieContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabNowShowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabNowShowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabNowShowingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters
    private static final int MOVIE_LOADER = 0;
    public static ImageAdapterWithCursorAdapter mMovieImageAdapterWithCursorAdapter;
    public static List<Movie> moviesList = new ArrayList<Movie>();
    public static int totalPages = 0;
    String movieTitle = null;
    int pageNum = 1;
    ItemsListClickHandler handler;
    @BindView(R.id.gridView)
    GridView gridview;
    @BindView(R.id.button_next)
    Button nextButton;
    @BindView(R.id.button_previous)
    Button previousButton;
    private OnFragmentInteractionListener mListener;

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
        if (savedInstanceState != null) {
            pageNum = savedInstanceState.getInt("page number");
        }
        super.onCreate(savedInstanceState);
    }

    private void updateMovieList() {
        GetDataTask dataTaskForNowShowing = new GetDataTask(this.getContext(), MainActivity.additionalUrl, true, pageNum);
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
        ButterKnife.bind(this, rootView);

        mMovieImageAdapterWithCursorAdapter = new ImageAdapterWithCursorAdapter(getActivity(), null, 0);
        gridview.setAdapter(mMovieImageAdapterWithCursorAdapter);
        gridview.setDrawSelectorOnTop(false);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                handler.onHandleItemClick(position);
//            }
//        });
        if (pageNum == 1) previousButton.setEnabled(false);
        else previousButton.setEnabled(true);

        return rootView;
    }

    @OnClick(R.id.button_next)
    public void onClickNext() {
        if (pageNum < totalPages) {
            previousButton.setEnabled(true);
            pageNum++;
            if (pageNum == totalPages) nextButton.setEnabled(false);
            GetDataTask dataTaskForNowShowing = new GetDataTask(this.getContext(), MainActivity.additionalUrl, true, pageNum);
            dataTaskForNowShowing.execute();
            int i = 1000000;
            while (i > 0) {
                i--;
                gridview.invalidateViews();
            }
        }
    }

    @OnClick(R.id.button_previous)
    public void onClickPrevious() {
        if (pageNum > 1) {
            pageNum--;
            if (pageNum == 1) previousButton.setEnabled(false);
            nextButton.setEnabled(true);
            GetDataTask dataTaskForNowShowing = new GetDataTask(this.getContext(), MainActivity.additionalUrl, true, pageNum);
            dataTaskForNowShowing.execute();
            int i = 1000000;
            while (i > 0) {
                i--;
                gridview.invalidateViews();
            }
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
        try {
            handler = (ItemsListClickHandler) getActivity();

        } catch (ClassCastException e) {
            Log.e(TabNowShowingFragment.class.getSimpleName(), "The activity does not implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("page number", pageNum);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.NơwPlayingEntry.COLUMN_POSITION + " ASC";
        Uri movieInNowPlayingUri = MovieContract.NơwPlayingEntry.buildNowPlayingPage(pageNum);
        return new CursorLoader(getActivity(),
                movieInNowPlayingUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieImageAdapterWithCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieImageAdapterWithCursorAdapter.swapCursor(null);
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

    public interface ItemsListClickHandler {
        public void onHandleItemClick(int position);
    }

}
