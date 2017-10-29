package webpract.com.practical;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import webpract.com.practical.database.AppDatabase;
import webpract.com.practical.database.DBModel;
import webpract.com.practical.webservices.WebService;

/**
 * Created by wmtandroid5 on 28/10/17.
 */

public class MainViewModel extends AndroidViewModel {
    AppDatabase appDatabase;
    private final LiveData<List<DBModel>> brandList;
    Application appContext;

    public MainViewModel(Application application) {
        super(application);
        appContext = application;
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        this.brandList = appDatabase.brandModel().getAllBrandItems();

    }

    public LiveData<List<DBModel>> getBrandList() {
        return brandList;
    }

    public void addBrand(final DBModel brandModel) {
        new addAsyncTask(appDatabase).execute(brandModel);
    }

    private static class addAsyncTask extends AsyncTask<DBModel, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final DBModel... params) {
            db.brandModel().addBrand(params[0]);

            return null;
        }

    }

    public void sendData(Application appContext) {
        if (Util.isNetworkAvailable(appContext)) {
            WebService.sendData(appContext);
        }
    }
}
