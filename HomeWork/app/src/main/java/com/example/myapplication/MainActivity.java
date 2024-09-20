package com.example.myapplication;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import com.jakewharton.rxbinding4.widget.RxTextView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private DataDao dataDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "data_db").build();
        dataDao = db.dataDao();

        searchEditText = findViewById(R.id.searchEditText);
        loadDataFromInternet();
        setupSearch();
    }

    private void loadDataFromInternet() {
        compositeDisposable.add(
                apiService.getData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dataModels -> {
                            Toast.makeText(this, "Data loaded from internet", Toast.LENGTH_SHORT).show();
                        }, throwable -> {
                            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void setupSearch() {
        compositeDisposable.add(
                RxTextView.textChanges(searchEditText)
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .filter(text -> text.length() >= 3)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(query -> searchInDatabase(query.toString()))
        );
    }

    private void searchInDatabase(String query) {
        compositeDisposable.add(
                dataDao.searchData("%" + query + "%")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dataModels -> {
                            Toast.makeText(this, "Search results updated", Toast.LENGTH_SHORT).show();
                        }, throwable -> {
                            Toast.makeText(this, "Search failed", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
