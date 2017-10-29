package webpract.com.practical;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import webpract.com.practical.database.DBModel;
import webpract.com.practical.webservices.JobSchedulerService;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewInit();
    }

    void viewInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBrandDialog(MainActivity.this);
            }
        });
        recyclerView = findViewById(R.id.recycler_main);
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<DBModel>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);
        try {
            funcInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void funcInit() throws Exception {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getBrandList().observe(MainActivity.this, new Observer<List<DBModel>>() {
            @Override
            public void onChanged(@Nullable List<DBModel> brands) {
                recyclerViewAdapter.addItems(brands);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initJS();
        } else {
            Toast.makeText(this, "Minimum Android ver lollipop required", Toast.LENGTH_SHORT).show();
        }
    }

    private void initJS() {

        Util.scheduleJob(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showAddBrandDialog(Context mContext) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setTitle(mContext.getString(R.string.lable_add));
        builder.setCancelable(true);
        View dialogView = inflater.inflate(R.layout.dialog_add, null);

        final EditText editTextName = dialogView.findViewById(R.id.et_brand_name);
        final EditText editTextDisc = dialogView.findViewById(R.id.et_brand_disc);
        builder.setPositiveButton(R.string.ok, null);
        builder.setView(dialogView);
        // Add action buttons

        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (TextUtils.isEmpty(editTextName.getText())) {
                            editTextName.setError(getResources().getString(R.string.empty_field));
                        } else if (TextUtils.isEmpty(editTextDisc.getText())) {
                            editTextDisc.setError(getResources().getString(R.string.empty_field));
                        } else {
                            viewModel.addBrand(new DBModel(editTextName.getText().toString(),
                                    editTextDisc.getText().toString(), Util.getCurrentTime(), 0, 0));

                            viewModel.sendData(getApplication());

                            dialog.dismiss();
                        }

                    }
                });
            }
        });
        dialog.show();
    }

}


