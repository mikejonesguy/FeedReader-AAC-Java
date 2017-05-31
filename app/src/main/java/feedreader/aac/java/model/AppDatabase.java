package feedreader.aac.java.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Article.class}, version = 1)
// Use Converters for conversion between Long values in the database and Date values in the pojo
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE = null;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-db").build();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public void destroyInstance() {
        INSTANCE = null;
    }

    public abstract ArticleDao articleModel();

}
