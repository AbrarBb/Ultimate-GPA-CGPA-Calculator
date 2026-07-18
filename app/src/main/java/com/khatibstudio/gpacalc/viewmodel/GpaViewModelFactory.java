package com.khatibstudio.gpacalc.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GpaViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public GpaViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GpaViewModel.class)) {
            return (T) new GpaViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
