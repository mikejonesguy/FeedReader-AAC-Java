package feedreader.aac.java.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import feedreader.aac.java.model.Article;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Article> selected;

    public SharedViewModel() {
        super();
        selected = new MutableLiveData<>();
    }

    public LiveData<Article> getSelected() {
        return selected;
    }

    public void select(Article item) {
        selected.setValue(item);
    }
}
