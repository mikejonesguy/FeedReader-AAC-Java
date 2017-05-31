package feedreader.aac.java;

import android.app.Application;

public class App extends Application {
    public static App current = null;

    @Override
    public void onCreate() {
        super.onCreate();
        current = this;
    }
}