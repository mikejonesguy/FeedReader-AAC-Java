package feedreader.aac.java.api;

import android.util.Log;

import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.simpleframework.xml.transform.Transform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import feedreader.aac.java.model.Feed;
import feedreader.aac.java.viewmodel.ArticleListViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

public class FeedFetcher implements Callback<Feed> {
    private static final String TAG = "AAC.FeedFetcher";
    private static final String BASE_URL = "http://feeds.feedburner.com/";
    private ArticleListViewModel viewModel = null;

    public void start(ArticleListViewModel viewModel) {
        this.viewModel = viewModel;

        // Use this DateFormat and the DateFormatTransformer to parse the published date for our Article pojo
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(Date.class, new DateFormatTransformer(format));
        Persister serializer = new Persister(matcher);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(SimpleXmlConverterFactory.create(serializer)).build();

        Rest api = retrofit.create(Rest.class);

        Call<Feed> call = api.loadFeed();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Feed> call, Response<Feed> response) {
        if (response.isSuccessful()) {
        // now persist the data to the local database
            viewModel.insertAsync(response.body());
        } else {
            viewModel.setFetching(false);
            Log.d(TAG, "onResponse: " + response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<Feed> call, Throwable t) {
        viewModel.setFetching(false);
        Log.d(TAG, "onFailure: ", t);
    }

    interface Rest {
        @GET("androidcentral")
        Call<Feed> loadFeed();
    }

    class DateFormatTransformer implements Transform<Date> {
        private DateFormat dateFormat;

        DateFormatTransformer(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public Date read(String value) throws Exception {
            return dateFormat.parse(value);
        }


        @Override
        public String write(Date value) throws Exception {
            return dateFormat.format(value);
        }

    }
}
