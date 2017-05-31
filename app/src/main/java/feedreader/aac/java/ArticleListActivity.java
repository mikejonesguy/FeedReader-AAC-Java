package feedreader.aac.java;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import feedreader.aac.java.model.Article;
import feedreader.aac.java.viewmodel.ArticleListViewModel;
import feedreader.aac.java.viewmodel.SharedViewModel;

public class ArticleListActivity extends LifecycleAppCompatActivity {

    private boolean twoPane;
    private SimpleItemRecyclerViewAdapter adapter;
    private SharedViewModel sharedViewModel;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // shared view model
        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        // recycler view
        adapter = new SimpleItemRecyclerViewAdapter();
        RecyclerView articleList = (RecyclerView) findViewById(R.id.articleList);
        articleList.setAdapter(adapter);

        // list view model
        ArticleListViewModel listViewModel = ViewModelProviders.of(this).get(ArticleListViewModel.class);
        listViewModel.getArticles().observe(this, articles -> adapter.setValues(articles));

        listViewModel.isFetching().observe(this, fetching -> {
            // only show the refreshing indicator if the user initiated a refresh
            if (fetching == null || !fetching) {
                swipeContainer.setRefreshing(false);
            }
        });

        // swipe to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(listViewModel::fetchArticles);

        // support for tablets
        if (findViewById(R.id.detailContainer) != null) {
            twoPane = true;
        }

        // fresh start -- refresh the feed and add the detail fragment if twoPane == true
        if (savedInstanceState == null) {
            listViewModel.fetchArticles();
            if (twoPane) {
                // Create the detail fragment and add it to the activity using a fragment transaction.
                ArticleDetailFragment fragment = new ArticleDetailFragment();
                fragment.setArguments(new Bundle());
                getSupportFragmentManager().beginTransaction().add(R.id.detailContainer, fragment).commit();
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Article> values;

        SimpleItemRecyclerViewAdapter() {
            this.values = new ArrayList<>();
            setHasStableIds(true);
        }

        void setValues(List<Article> articles) {
            values.clear();
            values.addAll(articles);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Article article = values.get(position);

            holder.publishedView.setText(article.relativeDate());
            holder.titleView.setText(article.getTitle());
            holder.contentView.setOnClickListener(v -> {
                // notify view model of selected article
                sharedViewModel.select(article);
                if (!twoPane) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailFragment.ARG_TITLE, article.getTitle());
                    intent.putExtra(ArticleDetailFragment.ARG_HTML, article.getHtml());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return values.get(position).id();
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View contentView;
            final TextView titleView;
            final TextView publishedView;

            ViewHolder(View view) {
                super(view);
                contentView = view;
                titleView = (TextView) view.findViewById(R.id.titleView);
                publishedView = (TextView) view.findViewById(R.id.publishedView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + titleView.getText() + "'";
            }
        }
    }
}
