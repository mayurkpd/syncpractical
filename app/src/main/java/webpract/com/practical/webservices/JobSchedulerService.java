package webpract.com.practical.webservices;

/**
 * Created by Mayur on 29-Oct-17.
 */

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import webpract.com.practical.Constant;
import webpract.com.practical.Util;
import webpract.com.practical.database.AppDatabase;

public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob:");
        new SyncTask().execute();
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob:");
        return false;
    }

    private class SyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase mDatabse = AppDatabase.getDatabase(getApplicationContext());
            if (mDatabse.brandModel().getAllBrandItemsList().isEmpty()) {
                WebService.updateData(getApplication(), Constant.GET);
            } else if (!mDatabse.brandModel().getUnSyncItems().isEmpty()) {
                WebService.sendData(getApplication());
            } else {
                WebService.updateData(getApplication(), Constant.POST);
            }
            return null;
        }
    }
}