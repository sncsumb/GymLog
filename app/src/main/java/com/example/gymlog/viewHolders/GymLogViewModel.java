package com.example.gymlog.viewHolders;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gymlog.database.GymLogRepository;
import com.example.gymlog.database.entities.GymLog;

import java.util.List;

public class GymLogViewModel extends AndroidViewModel {
    private final GymLogRepository repository;

    private final LiveData<List<GymLog>> allLogsById;

    public GymLogViewModel (Application application, int userId) {
        super(application);
        repository = GymLogRepository.getRepository(application);
        allLogsById = repository.getAllLogsByUserIdLiveData(userId);
    }

    public LiveData<List<GymLog>> getAllLogsById() {
        return allLogsById;
    }

    public void insert(GymLog log) {
        repository.insertGymLog(log);
    }
}
