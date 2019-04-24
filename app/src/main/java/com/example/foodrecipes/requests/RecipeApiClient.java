package com.example.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.foodrecipes.AppExecuter;
import com.example.foodrecipes.requests.responses.RecipeResponse;
import com.example.foodrecipes.utill.Constants;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.foodrecipes.utill.Constants.TIMEOUT;

public class RecipeApiClient {
    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<Recipe> mRecipe;
    private MutableLiveData<Boolean> requestTimeOut = new MutableLiveData<>();
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private RetrieveRecipeIngrediantRunnable mRetrieveRecipeIngrediant;

    public RecipeApiClient() {

        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public static RecipeApiClient getInstance() {
        if (instance == null)
            instance = new RecipeApiClient();
        return instance;
    }

    public LiveData<List<Recipe>> getRecipes() { return mRecipes; }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public LiveData<Boolean> isRequestTimeout() {
        return requestTimeOut;
    }

    public void searchRecipeApi(String query, int pageNum) {
        if (mRetrieveRecipeRunnable != null) {
            mRetrieveRecipeRunnable = null;
        }//because we wanna instantiated brand new one
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(query, pageNum);
        final Future handler = AppExecuter.getInstant().networkIo().submit(mRetrieveRecipeRunnable);
        AppExecuter.getInstant().networkIo().schedule(new Runnable() {
            @Override
            public void run() {
                //Tell the user when time out
                handler.cancel(true);//this execute after time out
            }
        }, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchRecipeById(String rId) {
        if (mRetrieveRecipeIngrediant != null) {
            mRetrieveRecipeIngrediant = null;
        }//because we wanna instantiated brand new one
        mRetrieveRecipeIngrediant = new RetrieveRecipeIngrediantRunnable(rId);
        final Future handler = AppExecuter.getInstant().networkIo().submit(mRetrieveRecipeIngrediant);

        requestTimeOut.setValue(false);
        AppExecuter.getInstant().networkIo().schedule(new Runnable() {
            @Override
            public void run() {
                //Tell the user when time out
                requestTimeOut.postValue(true);
                handler.cancel(true);//this execute after time out
            }
        }, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    //______________________________________________________________________
    private class RetrieveRecipeRunnable implements Runnable {
        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest) return;
                if (response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if (pageNumber == 1) mRecipes.postValue(list);
                    else {
                        List<Recipe> currentList = mRecipes.getValue();
                        currentList.addAll(list);
                        mRecipes.postValue(currentList);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }
        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return ServiceGenerator
                    .getRecipeApi()
                    .searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
        }

        private void cancelRequest() {
            Log.d(TAG, "CancelRequest: canceling the search request");
            cancelRequest = true;
        }
    }
//______________________________________________________________________________ The runnable class for ingrediant


    private class RetrieveRecipeIngrediantRunnable implements Runnable {
        private String rcipeId;
        boolean cancelRequest;

        public RetrieveRecipeIngrediantRunnable(String rcipeId) {
            this.rcipeId = rcipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipeIngrediant(rcipeId).execute();
                if (cancelRequest) return;
                if (response.code() == 200) {
                    Recipe recipe = ((RecipeResponse) response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }
        }

        private Call<RecipeResponse> getRecipeIngrediant(String rcipeId) {
            return ServiceGenerator
                    .getRecipeApi()
                    .getResponse(Constants.API_KEY, rcipeId);
        }

        private void cancelRequest() {
            Log.d(TAG, "CancelRequest: canceling the search request");
            cancelRequest = true;
        }
    }

    public void cancelRequest() {
        if (mRetrieveRecipeRunnable != null) mRetrieveRecipeRunnable.cancelRequest();
        if (mRetrieveRecipeIngrediant != null) mRetrieveRecipeIngrediant.cancelRequest();

    }
}
