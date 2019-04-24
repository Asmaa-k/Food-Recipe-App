package com.example.foodrecipes.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApi;
import com.example.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {
    private static RecipeRepository instance;
    private static RecipeApiClient mApiClient;
    private String mQuery;
    private int mPageNum;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();//making change to live data before Its return data

    public RecipeRepository() {
        mApiClient = RecipeApiClient.getInstance();
        initMediator();
    }

    public static RecipeRepository getInstance() {
        if (instance == null)
            instance = new RecipeRepository();
        return instance;
    }

    private void initMediator() {
        LiveData<List<Recipe>> recipeListApiSource = mApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                } else {
                    //Search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Recipe> list) {
        if (list != null) {
            if (list.size() % 30 != 0)
                mIsQueryExhausted.setValue(true);
        } else mIsQueryExhausted.setValue(true);
    }

    public LiveData<Boolean> isQueryExhausted() { return mIsQueryExhausted; }

    public LiveData<List<Recipe>> getRecipes() { return mRecipes;/*we wanna make change to it before it return from client*/ }

    public LiveData<Recipe> getRecipe() {
        return mApiClient.getRecipe();
    }

    public LiveData<Boolean> isRequestTimeout() {
        return mApiClient.isRequestTimeout();
    }

    public void searchRecipeApi(String query, int pageNumber) {
        if (pageNumber == 0) pageNumber = 1;
        mQuery = query;
        mPageNum = pageNumber;
        mIsQueryExhausted.setValue(false);
        mApiClient.searchRecipeApi(query, pageNumber);
    }

    public void searchRecipeById(String rId) {
        mApiClient.searchRecipeById(rId);
    }

    public void searchNextPage() {
        searchRecipeApi(mQuery, mPageNum + 1);
    }

    public void cancelRequest() {
        mApiClient.cancelRequest();
    }
}

