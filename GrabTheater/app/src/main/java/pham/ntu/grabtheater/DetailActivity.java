package pham.ntu.grabtheater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import android.support.v7.app.ActionBarActivity;


public class DetailActivity extends ActionBarActivity {
    DetailFragment df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            df = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, df)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(df.getIsLiked()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        public static List<Movie> moviesList = new ArrayList<Movie>();
        public static List<Video> trailersList = new ArrayList<Video>();
        public static List<String> moviesTitleList = new ArrayList<String>();
        private Movie mMovie;
        private String backdropImagePath;
        private String posterImagePath;
        public ExpandableListView gridview;
        public static ImageAdapter mMovieImageAdapter;
        public static String additionalUrl = null;
        private String additionalUrlToGetTrailers = null;
        private YouTubePlayer YPlayer;
        private static final int RECOVERY_DIALOG_REQUEST = 1;
        com.like.LikeButton likeButton;
        boolean isLiked = false;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra("Movie")) {

                mMovie = (Movie) intent.getSerializableExtra("Movie");

                trailersList.clear();
                additionalUrlToGetTrailers = mMovie.getId()+"/videos";
                GetDataTask dataTaskToLoadTrailers = new GetDataTask(this.getActivity(),additionalUrlToGetTrailers,false);
                dataTaskToLoadTrailers.execute();

                likeButton = (com.like.LikeButton) rootView.findViewById(R.id.like_button);
                if(intent.hasExtra("Hide Like Button")) likeButton.setVisibility(View.GONE);
                setUpImageViews(rootView);
                final TextView titleTextview = (TextView) rootView.findViewById(R.id.title_textview);
                titleTextview.setText(mMovie.getTitle());
                final TextView overviewTextview = (TextView) rootView.findViewById(R.id.overview_textview);
                overviewTextview.setText(mMovie.getOverview());

                additionalUrl = mMovie.getId()+"/similar";
                GetDataTask dataTaskForDetailActivity = new GetDataTask(this.getActivity(),additionalUrl,true,1);
                dataTaskForDetailActivity.execute();

                gridview = (ExpandableListView) rootView.findViewById(R.id.related_movies_gridView);
                gridview.setExpanded(true);
                mMovieImageAdapter = new ImageAdapter(getActivity(),DetailFragment.moviesList);
                gridview.setAdapter(mMovieImageAdapter);
                gridview.setDrawSelectorOnTop(false);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Movie",
                                DetailFragment.moviesList.get(position));
                        startActivity(intent);
                    }
                });
                if(moviesTitleList.contains(mMovie.getTitle())) {
                    likeButton.setEnabled(false);
                    likeButton.setLiked(true);
                }

                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = TabFavouritesFragment.likedMovies.edit();
                        moviesTitleList.add(mMovie.getTitle());
                        Gson gson = new Gson();
                        String json = gson.toJson(mMovie);
                        editor.putString(mMovie.getTitle(), json);
                        editor.commit();
                        likeButton.setLiked(true);
                        likeButton.setEnabled(false);
                        isLiked = true;
                    }
                });

                YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

                youTubePlayerFragment.initialize(Config.YOUTUBE_API_KEY, new OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                        if (!b) {
                            YPlayer = youTubePlayer;
                            if(trailersList.size()!=0){
                                String video = trailersList.get(0).getKey();
                                YPlayer.cueVideo(video);
                            }

//                            YPlayer.loadVideo("2zNSgSzhBfM");
//                            YPlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
                        // TODO Auto-generated method stub

                    }
                });

            }
            return rootView;
        }

        private void setUpImageViews(View rootView) {
            ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_image);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
            backdropImagePath = mMovie.getBackdrop_path();
            if(backdropImagePath.equals("null")) backdropImageView.setVisibility(View.GONE);
            else {
                backdropImagePath = TabNowShowingFragment.imgBaseUrl
                        + backdropImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
                Picasso.with(getActivity()).load(backdropImagePath).resize(600,
                        400).onlyScaleDown().centerInside().placeholder(R.drawable.ic_place_holder)
                        .error(R.drawable.ic_error_fallback).into(backdropImageView);
            }
            posterImagePath = mMovie.getPoster_path();
            posterImagePath = TabNowShowingFragment.imgBaseUrl
                    + posterImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;

            Picasso.with(getActivity()).load(posterImagePath).resize(150,
                    300).centerInside().placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(posterImageView);
        }

        public boolean getIsLiked(){
            return isLiked;
        }
    }
}
