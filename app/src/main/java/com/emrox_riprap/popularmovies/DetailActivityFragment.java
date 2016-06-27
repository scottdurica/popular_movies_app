package com.emrox_riprap.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;
    private String movieId;
    //TODO Uncomment for Stage 2
//    private TrailerListAdapter mTrailerListAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO Uncomment for Stage 2
//        updateTrailerInfo();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //should never be null, but checking anyways...
        if (getActivity().getIntent() != null && getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY)!=null){
            mMovie = (Movie) getActivity().getIntent().getParcelableExtra(Movie.PARCEL_KEY);
            movieId = String.valueOf(mMovie.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView title = (TextView)rootView.findViewById(R.id.tv_title);
        TextView overview = (TextView)rootView.findViewById(R.id.tv_overview);
        TextView releaseDate = (TextView)rootView.findViewById(R.id.tv_year);
        TextView voterRating = (TextView)rootView.findViewById(R.id.tv_rating);

        ImageView poster = (ImageView)rootView.findViewById(R.id.iv_detail_fragment_poster);
        //should never be null, but checking anyways...
        if (mMovie != null){
            title.setText(mMovie.getTitle());
            overview.setText(mMovie.getOverview());
            Picasso.with(getActivity()).load(mMovie.getPosterPath()).into(poster);
            releaseDate.setText(mMovie.getReleaseDate().substring(0,4));
            voterRating.setText(String.valueOf(mMovie.getVoterRating())+ "/10");
        }
        //TODO Uncomment for Stage 2
//        mTrailerListAdapter = new TrailerListAdapter(getActivity());
//        ListView trailerList = (ListView)rootView.findViewById(R.id.lv_trailers);
//        trailerList.setAdapter(mTrailerListAdapter);
//        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String clipId = mTrailerListAdapter.mDataList.get(position).getClipKey();
////                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + clipId)));
//                watchYoutubeVideo(clipId);
//            }
//        });
        return rootView;
    }
        //TODO Uncomment for Stage 2
//    public void watchYoutubeVideo(String id){
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
//            startActivity(intent);
//        } catch (ActivityNotFoundException ex) {
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://www.youtube.com/watch?v=" + id));
//            startActivity(intent);
//        }
//    }
    //TODO Uncomment for Stage 2
//    private void updateTrailerInfo(){
//        FetchMovieTrailersTask task = new FetchMovieTrailersTask();
//        String[]paramaters = new String[]{movieId};
//        task.execute(movieId);
//    }
    //TODO Uncomment for Stage 2
//    public class FetchMovieTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {
//
//        public final String TAG = DetailActivityFragment.FetchMovieTrailersTask.class.getSimpleName();
//
//        @Override
//        protected ArrayList<Trailer> doInBackground(String... params) {
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String mFetchedJsonStr = null;
//            String format = "json";
//            String apiKey = getString(R.string.movie_db_api_key);
//
//            try {
//
//                final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
//                final String PATH_MOVIE = "movie";
//                final String PATH_VIDEOS = "videos";
//                final String API_KEY_PARAM = "api_key";
//                String movieId = params[0];
//
//                Uri buildUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
//                        .appendPath(PATH_MOVIE)
//                        .appendPath(movieId)
//                        .appendPath(PATH_VIDEOS)
//                        .appendQueryParameter(API_KEY_PARAM, apiKey)
//                        .build();
//                URL url = new URL(buildUri.toString());
//
//                // Create the request to MovieDB and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                mFetchedJsonStr = buffer.toString();
//                try {
//                    return getMovieTrailerDataFromJson(mFetchedJsonStr);
//                } catch (JSONException e) {
//                    Log.e(TAG, e.getMessage(), e);
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Error ", e);
//                // If the code didn't successfully get the data, there's no point in attempting
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(TAG, "Error closing stream", e);
//                    }
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Trailer> trailerList) {
//            if(trailerList !=null){
//                mTrailerListAdapter.mDataList.clear();
//                for (Trailer t: trailerList) {
//                    mTrailerListAdapter.mDataList.add(t);
//                }
//                mTrailerListAdapter.notifyDataSetChanged();
//            }
//        }
//
//        private ArrayList<Trailer> getMovieTrailerDataFromJson(String movieJsonStr)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String TMDB_LIST = "results";
//            ArrayList<Trailer> trailerList = new ArrayList<Trailer>();
//
//
//            JSONObject movieJson = new JSONObject(movieJsonStr);
//            int id = movieJson.getInt("id");
//
//
//            JSONArray videosArray = movieJson.getJSONArray(TMDB_LIST);
//
//
//
//            for(int i = 0; i < videosArray.length(); i++) {
//
//                JSONObject videoObj = videosArray.getJSONObject(i);
//                //if this video type is a Trailer
//                if (videoObj.getString("type").equalsIgnoreCase("Trailer")){
//                    String trailerId;
//                    String title;
//                    String clipKey;
//
//                    trailerId = videoObj.getString("id");
//                    int movieId = id;
//                    title = videoObj.getString("name");
//                    clipKey = videoObj.getString("key");
//
//                    Trailer trailer = new Trailer(trailerId,movieId,title,clipKey);
//
//                    trailerList.add(trailer);
//
//                }
//
//
//            }
//
//            return trailerList;
//
//        }
//
//    }
//

}
