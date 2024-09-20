package com.example.myapplication;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("data")
    Observable<List<DataModel>> getData();
}
