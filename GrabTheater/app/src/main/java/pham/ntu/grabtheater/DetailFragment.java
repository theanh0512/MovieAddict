package pham.ntu.grabtheater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pham.ntu.grabtheater.adapter.ImageAdapterWithBaseAdapter;
import pham.ntu.grabtheater.entity.Movie;
import pham.ntu.grabtheater.entity.Review;
import pham.ntu.grabtheater.entity.Video;

/**
 * Created by User on 12/10/2016.
 */

public class DetailFragment extends Fragment {
    public static List<Movie> moviesList = new ArrayList<>();
    public static List<Video> trailersList = new ArrayList<>();
    public static List<Review> reviewsList = new ArrayList<>();
    public static ImageAdapterWithBaseAdapter mMovieImageAdapter;
    public static String additionalUrl = null;
    boolean isLiked = false;
    @BindView(R.id.title_textview)
    TextView titleTextview;
    @BindView(R.id.overview_textview)
    TextView overviewTextview;
    @BindView(R.id.release_date_textview)
    TextView releaseDateTextview;
    @BindView(R.id.vote_average_textview)
    TextView voteAverageTextview;
    @BindView(R.id.like_button)
    com.like.LikeButton likeButton;
    @BindView(R.id.related_movies_gridView)
    pham.ntu.grabtheater.ExpandableListView gridview;
    @BindView(R.id.backdrop_image)
    ImageView backdropImageView;
    @BindView(R.id.poster_image)
    ImageView posterImageView;
    @BindView(R.id.button_view_review)
    Button ViewReviewButton;
    private Movie mMovie;
    private YouTubePlayer YPlayer;
    private SharedPreferences sharedPreferences;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle details = this.getArguments();
        if (details != null) {
            ButterKnife.bind(this, rootView);
            mMovie = details.getParcelable("Movie");

            trailersList.clear();
            String additionalUrlToGetTrailers = mMovie.getId() + "/videos";
            GetDataTask dataTaskToLoadTrailers = new GetDataTask(this.getContext(), additionalUrlToGetTrailers, false);
            dataTaskToLoadTrailers.execute();
            if (getActivity().getClass() == MainActivity.class) likeButton.setVisibility(View.GONE);
            if (details.containsKey("Liked")) isLiked = true;
            setUpImageViews();

            updateViews();

            reviewsList.clear();
            String additionalUrlToGetReiviews = mMovie.getId() + "/reviews";
            GetDataTask dataTaskToLoadReviews = new GetDataTask(this.getContext(), additionalUrlToGetReiviews, false);
            dataTaskToLoadReviews.execute();

            additionalUrl = mMovie.getId() + "/similar";
            GetDataTask dataTaskForDetailActivity = new GetDataTask(this.getContext(), additionalUrl, true, 1);
            dataTaskForDetailActivity.execute();

            gridview.setExpanded(true);
            mMovieImageAdapter = new ImageAdapterWithBaseAdapter(getActivity(), DetailFragment.moviesList);
            gridview.setAdapter(mMovieImageAdapter);
            gridview.setDrawSelectorOnTop(false);
            if (sharedPreferences.getStringSet("titleSet", null) != null
                    && sharedPreferences.getStringSet("titleSet", null).contains(mMovie.getTitle())) {
                //likeButton.setEnabled(false);
                likeButton.setLiked(true);
            }

            addYoutubeFragment();

        }
        return rootView;
    }

    @OnItemClick(R.id.related_movies_gridView)
    public void onClick(int position) {
        Movie movie = DetailFragment.moviesList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Movie", movie);
        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("Bundle", bundle);
        startActivity(intent);
    }

    @OnClick(R.id.button_view_review)
    public void onClickViewReview() {
        Intent intent = new Intent(this.getActivity(), ReviewActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.like_button)
    public void onClick() {
        if (!isLiked) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> titleSet = sharedPreferences.getStringSet("titleSet", null);
            if (titleSet == null) titleSet = new HashSet<>();
            titleSet.add(mMovie.getTitle());
            editor.putStringSet("titleSet", titleSet);
            Gson gson = new Gson();
            String json = gson.toJson(mMovie);
            editor.putString(mMovie.getTitle(), json);
            editor.apply();
            editor.clear();
            likeButton.setLiked(true);
            isLiked = true;
            TabFavouritesFragment tabFavouritesFragment = (TabFavouritesFragment) MainActivity.adapter.getRegisteredFragment(1);
            if (tabFavouritesFragment != null) {
                tabFavouritesFragment.updateFavouriteList();
                tabFavouritesFragment.invalidateGridview();
            }
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> titleSet = sharedPreferences.getStringSet("titleSet", null);
            if (titleSet == null) titleSet = new HashSet<>();
            titleSet.remove(mMovie.getTitle());
            editor.putStringSet("titleSet", titleSet);
            editor.remove(mMovie.getTitle());
            editor.apply();
            editor.clear();
            likeButton.setLiked(false);
            isLiked = false;
            TabFavouritesFragment tabFavouritesFragment = (TabFavouritesFragment) MainActivity.adapter.getRegisteredFragment(1);
            if (tabFavouritesFragment != null) {
                tabFavouritesFragment.updateFavouriteList();
                tabFavouritesFragment.invalidateGridview();
            }
        }
    }

    public void updateViews() {
        titleTextview.setText(mMovie.getTitle());
        overviewTextview.setText(mMovie.getOverview());
        releaseDateTextview.setText(mMovie.getRelease_date());
        voteAverageTextview.setText(this.getContext().getString(R.string.format_vote_average, mMovie.getVote_average()));
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
                    if (trailersList.size() != 0) {
                        String video = trailersList.get(0).getKey();
                        YPlayer.cueVideo(video);
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setUpImageViews() {

        String backdropImagePath = mMovie.getBackdrop_path();
        if (backdropImagePath.equals("null")) backdropImageView.setVisibility(View.GONE);
        else {
            backdropImagePath = Config.IMG_BASE_URL_BACKDROP
                    + backdropImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
            Picasso.with(getActivity()).load(backdropImagePath).resize(600,
                    400).onlyScaleDown().centerInside().placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(backdropImageView);
        }
        String posterImagePath = mMovie.getPoster_path();
        posterImagePath = Config.IMG_BASE_URL
                + posterImagePath + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;

        Picasso.with(getActivity()).load(posterImagePath).resize(150,
                300).centerInside().placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_error_fallback).into(posterImageView);
    }

    public boolean getIsLiked() {
        return isLiked;
    }
}
