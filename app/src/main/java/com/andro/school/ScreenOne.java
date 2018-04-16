package com.andro.school;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ScreenOne extends AppCompatActivity {


    DataAdapter adapter;
    Context context;
    ListView list;
    boolean endOfData = false;
    int lastButOneItem = -1, offset = 0, localPosition = 0;
    static ArrayList<HashMap<String, String>> listDataItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.screen_one);
        list = (ListView) findViewById(R.id.list);
        adapter = new DataAdapter(context);
        list.setAdapter(adapter);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView lw, int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                // Sample calculation to determine if the last
                // item is fully visible.
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (lastItem == totalItemCount && lastItem != 0) {
                    if (lastButOneItem != lastItem) {
                        //to avoid multiple calls for last item
                        if (getInternetState()) {
                            methodCall();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setMessage("Please check your internet.");
                            dialog.setNeutralButton("OK", null);
                            dialog.create().show();
                        }
                        lastButOneItem = lastItem;
                    }
                }
            }
        });
        if (getInternetState())
            methodCall();
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Please check your internet.");
            dialog.setNeutralButton("OK", null);
            dialog.create().show();
        }
    }

    void methodCall() {
        class ServiceCall extends AsyncTask<String, String, String> {
            ProgressDialog dialog;

            @Override
            public void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(context);
                try {
                    dialog.show();
                } catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
            }

            @Override
            public String doInBackground(String... params) {
                String data = "";
                try {
                    URL url = new URL("https://data.cityofnewyork.us/resource/97mf-9njv.json?$limit=25&$offset=" + params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    try {
                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            String line;
                            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            while ((line = br.readLine()) != null) {
                                data += line;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Input Output Exception", Toast.LENGTH_LONG).show();
                }
                return data;
            }

            @Override
            public void onPostExecute(String serverResult) {
                super.onPostExecute(serverResult);
                try {
                    JSONArray json_Array = new JSONArray(serverResult);
                    if (json_Array.length() == 0) {
                        endOfData = true;
                    } else {
                        for (int i = 0; i < json_Array.length(); i++) {
                            HashMap<String, String> dataMapper = new HashMap<>();
                            dataMapper.put("latitude", json_Array.getJSONObject(i).optString("latitude"));
                            dataMapper.put("school_name", json_Array.getJSONObject(i).optString("school_name"));
                            dataMapper.put("dbn", json_Array.getJSONObject(i).optString("dbn"));
                            dataMapper.put("overview_paragraph", json_Array.getJSONObject(i).optString("overview_paragraph"));
                            dataMapper.put("phone_number", json_Array.getJSONObject(i).optString("phone_number"));
                            dataMapper.put("end_time", json_Array.getJSONObject(i).optString("end_time"));
                            dataMapper.put("campus_name", json_Array.getJSONObject(i).optString("campus_name"));
                            dataMapper.put("start_time", json_Array.getJSONObject(i).optString("start_time"));
                            dataMapper.put("location", json_Array.getJSONObject(i).optString("location"));
                            dataMapper.put("longitude", json_Array.getJSONObject(i).optString("longitude"));
                            dataMapper.put("school_email", json_Array.getJSONObject(i).optString("school_email"));
                            dataMapper.put("total_students", json_Array.getJSONObject(i).optString("total_students"));
                            dataMapper.put("website", json_Array.getJSONObject(i).optString("website"));
                            listDataItems.add(dataMapper);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException jse) {
                    jse.printStackTrace();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Something went wrong.");
                    dialog.setNeutralButton("OK", null);
                    dialog.create().show();
                }
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }
        if (!endOfData) {
            new ServiceCall().execute(offset + "");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    offset = offset + 25;
                }
            }, 500);
        }
    }

    class DataAdapter extends BaseAdapter {
        TextView list_textview;
        RelativeLayout click;
        LayoutInflater li;
        Context c;

        DataAdapter(Context ct) {
            li = LayoutInflater.from(ct);
            c = ct;
        }

        @Override
        public int getCount() {
            return listDataItems.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = li.inflate(R.layout.list_item, null);
            click = (RelativeLayout) convertView.findViewById(R.id.click);
            list_textview = (TextView) convertView.findViewById(R.id.list_textview);
            if (!(position == listDataItems.size())) {
                try {
                    list_textview.setText(listDataItems.get(position).get("school_name") + "");
                    click.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            localPosition = position;
                            new ServiceCall2().execute(listDataItems.get(position).get("dbn"));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    boolean getInternetState() {
        NetworkInfo network = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (network == null || !network.isConnected())
            return false;
        if (network.isRoaming())
            return true;
        return true;
    }

    class ServiceCall2 extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
        }

        @Override
        public String doInBackground(String... params) {
            String data = "";
            try {
                URL url = new URL("https://data.cityofnewyork.us/resource/734v-jeq5.json?dbn=" + params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ((line = br.readLine()) != null) {
                        data += line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Input Output Exception", Toast.LENGTH_LONG).show();
            }
            return data;
        }

        @Override
        public void onPostExecute(String serverResult) {
            super.onPostExecute(serverResult);
            if (dialog.isShowing())
                dialog.dismiss();
            String sat_data = "";
            try {
                JSONArray json_Array = new JSONArray(serverResult);
                if (json_Array.length() != 0) {
                    sat_data = "SAT Data:\n\nTotal Test takers: "
                            + json_Array.getJSONObject(0).optString("num_of_sat_test_takers")
                            + "\nMath Avg Score: " + json_Array.getJSONObject(0).optString("sat_math_avg_score")
                            + "\nCritical Reading Average: " + json_Array.getJSONObject(0).optString("sat_critical_reading_avg_score")
                            + "\nWriting Avg Score: " + json_Array.getJSONObject(0).optString("sat_writing_avg_score");
                }
            } catch (JSONException jse) {
                jse.printStackTrace();
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Something went wrong.");
                dialog.setNeutralButton("OK", null);
                dialog.create().show();
            }
            Intent i = new Intent(context, ScreenTwo.class);
            i.putExtra("index", localPosition);
            i.putExtra("sat_data", sat_data);
            startActivity(i);
        }
    }
}
