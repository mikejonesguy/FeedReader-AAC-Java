package feedreader.aac.java.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import feedreader.aac.java.App;
import feedreader.aac.java.api.FeedFetcher;
import feedreader.aac.java.model.AppDatabase;
import feedreader.aac.java.model.Article;
import feedreader.aac.java.model.Feed;

public class ArticleListViewModel extends ViewModel {
    private AppDatabase database;
    private FeedFetcher fetcher;
    private LiveData<List<Article>> articles;
    private MutableLiveData<Boolean> fetching;

    public ArticleListViewModel() {
        super();
        database = AppDatabase.getDatabase(App.current);
        fetcher = new FeedFetcher();
        fetching = new MutableLiveData<>();
        fetching.setValue(false);
    }

    public LiveData<List<Article>> getArticles() {
        if (articles == null) {
            articles = database.articleModel().loadAll();
        }
        return articles;
    }

    public LiveData<Boolean> isFetching() {
        return fetching;
    }

    public void setFetching(boolean isFetching) {
        fetching.setValue(isFetching);
    }

    public void fetchArticles() {
        setFetching(true);
        fetcher.start(this);
    }

    public void insertAsync(Feed feed) {
        new InsertTask(this, feed).execute();
    }

    static class InsertTask extends AsyncTask<Object, Void, Void> {
        private Feed feed;
        private ArticleListViewModel viewModel;

        InsertTask(ArticleListViewModel viewModel, Feed feed) {
            super();
            this.viewModel = viewModel;
            this.feed = feed;
        }

        @Override
        public Void doInBackground(Object... params) {
            AppDatabase.getDatabase(App.current).articleModel().insertAll(feed.getArticleList());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            // tell the view that we're finished fetching articles
            viewModel.setFetching(false);
        }
    }
}
