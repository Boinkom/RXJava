package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;

@Dao
public interface DataDao {
    @Query("SELECT * FROM data_table WHERE name LIKE :query")
    Observable<List<DataModel>> searchData(String query);
}
