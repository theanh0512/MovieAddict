package pham.ntu.grabtheater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 12/10/2016.
 */

public class DetailFragment extends Fragment {
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
    TextView titleTextview;
    TextView overviewTextview;
    TextView releaseDateTextview;
    TextView voteAverageTextview;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle details = this.getArguments();
        if (details !=null) {

            mMovie = (Movie) details.getSerializable("Movie");

            trailersList.clear();
            additionalUrlToGetTrailers = mMovie.getId()+"/videos";
            GetDataTask dataTaskToLoadTrailers = new GetDataTask(this.getActivity(),additionalUrlToGetTrailers,false);
            dataTaskToLoadTrailers.execute();

            likeButton = (com.like.LikeButton) rootView.findViewById(R.id.like_button);
            if(details.containsKey("Hide Like Button")) likeButton.setVisibility(View.GONE);
            setUpImageViews(rootView);

            titleTextview = (TextView) rootView.findViewById(R.id.title_textview);
            overviewTextview = (TextView) rootView.findViewById(R.id.overview_textview);
            releaseDateTextview = (TextView) rootView.findViewById(R.id.release_date_textview);
            voteAverageTextview = (TextView) rootView.findViewById(R.id.vote_average_textview);

            updateViews();

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
                    Movie movie = DetailFragment.moviesList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Movie",movie);
                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Bundle",bundle);
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

            addYoutubeFragment();

        }
        return rootView;
    }

    public void updateViews() {
        titleTextview.setText(mMovie.getTitle());
        overviewTextview.setText(mMovie.getOverview());
        releaseDateTextview.setText(mMovie.getRelease_date());
        voteAverageTextview.setText(Double.toString(mMovie.getVote_average())+"/10");
    }

    private void addYoutubeFragment() {
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
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
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setUpImageViews(View rootView) {
        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_image);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image);
        backdropImagePath = mMovie.getBackdrop_path();
        if(backdropImagePath.equals("null")) backdropImageView.setVisibility(View.GONE);
        else {
            backdropImagePath = Config.IMG_BASE_URL
                    + backdropImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
            Picasso.with(getActivity()).load(backdropImagePath).resize(600,
                    400).onlyScaleDown().centerInside().placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(backdropImageView);
        }
        posterImagePath = mMovie.getPoster_path();
        posterImagePath = Config.IMG_BASE_URL
                + posterImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;

        Picasso.with(getActivity()).load(posterImagePath).resize(150,
                300).centerInside().placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_error_fallback).into(posterImageView);
    }

    public boolean getIsLiked(){
        return isLiked;
    }
}
