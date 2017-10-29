package webpract.com.practical;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.util.Calendar;

import webpract.com.practical.webservices.JobSchedulerService;

/**
 * Created by wmtandroid5 on 28/10/17.
 */

public class Util {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss");

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCurrentTime() {
        return dateFormat.format(Calendar.getInstance().
                getTimeInMillis());
    }

    public static String getLongFromDate(String date) throws ParseException {
        return String.valueOf(dateFormat.parse(date).getTime());
    }


    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(10 * 1000); // wait at least
        builder.setOverrideDeadline(5 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
