package com.example.pratik.retrofit1;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String url = "http://www.androidbegin.com/";
    CustomBaseAdapter customBaseAdapter;
    ListView listView;
    ProgressDialog progressdialog;
    Country country;
    DatabaseHandler databaseHandler;
    List<Worldpopulation> worldpopulationList;
    List<Country> countryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        country = new Country();
        worldpopulationList = new ArrayList<Worldpopulation>();
        countryList = new ArrayList<Country>();
        databaseHandler = new DatabaseHandler(MainActivity.this);

        if (isNetworkAvailable()) {
            progressdialog = new ProgressDialog(MainActivity.this);
            progressdialog.setMessage("Please Wait....");
            progressdialog.setCancelable(false);
            progressdialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitObjectAPI service = retrofit.create(RetrofitObjectAPI.class);

            Call<Country> call = service.getStudentDetails();

            call.enqueue(new Callback<Country>() {
                @Override
                public void onResponse(Call<Country> call, Response<Country> response) {

                    progressdialog.dismiss();
                    worldpopulationList = response.body().getWorldpopulation();
                    databaseHandler.addContact(worldpopulationList);
                    databaseHandler.getAllContacts();
                    customBaseAdapter = new CustomBaseAdapter(MainActivity.this, worldpopulationList);
                    listView.setAdapter(customBaseAdapter);

                }

                @Override
                public void onFailure(Call<Country> call, Throwable t) {

                }
            });

        } else {
            List<Worldpopulation> worldpopulationList1 = databaseHandler.getAllContacts();
            customBaseAdapter = new CustomBaseAdapter(MainActivity.this, worldpopulationList1);
            listView.setAdapter(customBaseAdapter);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
