package feedreader.aac.java.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@SuppressWarnings("unused")
public interface ArticleDao {
    // this is run async auto-magically because it's wrapped in LiveData
    @Query("SELECT * FROM article ORDER BY published DESC")
    LiveData<List<Article>> loadAll();

    // this is not async by default -- have to put it in an AsyncTask
    @Insert(onConflict = REPLACE)
    void insertAll(List<Article> articles);

    // also not async by default
    @Delete
    void delete(Article article);
}
