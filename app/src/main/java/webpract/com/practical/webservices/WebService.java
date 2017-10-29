package webpract.com.practical.webservices;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import webpract.com.practical.Constant;
import webpract.com.practical.Util;
import webpract.com.practical.database.AppDatabase;
import webpract.com.practical.database.DBModel;

/**
 * Created by Mayur on 29-Oct-17.
 */

public class WebService {
    final String TAG = this.getClass().getSimpleName();


    public static JSONObject push(URL url, Map<String, Object> params) {

        byte[] postDataBytes = new byte[0];

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                postDataBytes = postData.toString().getBytes("UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constant.POST);
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject mResponse = new JSONObject(sb.toString());
                    if (mResponse.optString(Constant.ERROR_CODE, "2").equalsIgnoreCase("1")) {
                        Log.e("AppTrackEvent->", mResponse.toString() + "\nSync Completed");
                        return mResponse;
                    } else {
//                        return false;
                        Log.e("AppTrackEvent->", mResponse.toString() + "\nSync InCompleted");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AppTrackEvent->", "Sync InCompleted");
                }
            } else if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST
                    && conn.getResponseCode() <= HttpURLConnection.HTTP_INTERNAL_ERROR) {
                Log.e("AppTrackEvent->", "Sync InCompleted");

            } else {
                Log.e("AppTrackEvent->", "Sync InCompleted");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getData(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constant.GET);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject mResponse = new JSONObject(sb.toString());
                    if (mResponse.optString(Constant.ERROR_CODE, "2").equalsIgnoreCase("1")) {
                        Log.e("AppTrackEvent->", mResponse.toString() + "\nget data Completed");
                        return mResponse;
                    } else {
//                        return false;
                        Log.e("AppTrackEvent->", mResponse.toString() + "\nget data InCompleted");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("AppTrackEvent->", "Sync InCompleted");
                }
            } else if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST
                    && conn.getResponseCode() <= HttpURLConnection.HTTP_INTERNAL_ERROR) {
                Log.e("AppTrackEvent->", "Sync InCompleted");

            } else {
                Log.e("AppTrackEvent->", "Sync InCompleted");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void sendData(Application appContext) {
        if (Util.isNetworkAvailable(appContext)) {
            new sendData(appContext).execute();
        }
    }


    private static class sendData extends AsyncTask<Void, Void, Boolean> {
        Application appContext;

        public sendData(Application appContext) {
            this.appContext = appContext;
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            try {
                AppDatabase appDatabase = AppDatabase.getDatabase(appContext);
                List<DBModel> dbModels = appDatabase.brandModel().getUnSyncItems();
                if (dbModels == null || dbModels.isEmpty()) {
                    return false;
                }
                JSONObject mSendData = new JSONObject();
                JSONArray mBrandArray = new JSONArray();
                for (DBModel model : dbModels) {
                    if (model.getIsSync() == 0) {
                        JSONObject mBrand = new JSONObject();
                        mBrand.put(Constant.NAME, model.getName());
                        mBrand.put(Constant.DESCRIPTION, model.getDescription());
                        mBrandArray.put(mBrand);
                    }
                }
                if (mBrandArray.length() > 0) {
                    mSendData.put(Constant.BRAND, mBrandArray);
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("data", mSendData.toString());
                    if (WebService.push(new URL(Constant.INSERT_URL), params) != null && appDatabase != null) {
                        appDatabase.brandModel().updateBrand();
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                updateData(appContext, Constant.GET);
            }
        }
    }

    static void updateData(Context appContext, String reqType) {
        if (Util.isNetworkAvailable(appContext)) {
            new updateDataTask(appContext, reqType).execute();
        }
    }

    private static class updateDataTask extends AsyncTask<Void, Void, Void> {
        Context appContext;
        String reqType;

        public updateDataTask(Context appContext, String reqType) {
            this.appContext = appContext;
            this.reqType = reqType;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase appDatabase = AppDatabase.getDatabase(appContext);
            JSONObject mData = null;
            try {
                switch (reqType) {
                    case Constant.GET:
                        mData = WebService.getData(new URL(Constant.FETCH_URL));
                        break;
                    case Constant.POST:

                        DBModel mModel = appDatabase.brandModel().getLastRecord();
                        if (mModel == null) {
                            return null;
                        }
                        Map<String, Object> params = new LinkedHashMap<>();
                        try {
                            params.put("timestamp", Util.getLongFromDate(mModel.getDate()));
                            mData = WebService.push(new URL(Constant.FETCH_URL), params);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (mData != null) {
                    JSONArray mBrandArrayGet = mData.optJSONArray(Constant.BRAND_LIST);
                    List<DBModel> modelListInsert = new ArrayList<>();
                    int length = mBrandArrayGet.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject item = mBrandArrayGet.getJSONObject(i);
                        modelListInsert.add(new DBModel(item.getString(Constant.NAME), item.getString(Constant.DESCRIPTION),
                                item.getString(Constant.CREATED_AT), Integer.valueOf(item.getString(Constant.ID)), 1));
                    }
                    if (!modelListInsert.isEmpty()) {
                        appDatabase.brandModel().clearAllSync();
                        appDatabase.brandModel().addAllBrand(modelListInsert.toArray(new DBModel[modelListInsert.size()]));
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
