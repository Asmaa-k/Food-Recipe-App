package com.example.foodrecipes.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repository.RecipeRepository;

import java.util.List;
//Choosing: ViewModel Or AndroidViewModel(application)

public class RecipeViewModel extends ViewModel {

    private static RecipeRepository mRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformQuery;

    public RecipeViewModel() {


        mIsViewingRecipes = false;//cause when it start it should viewing the category
        mIsPerformQuery = false;
        mRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRepository.getRecipes();
    }
    public LiveData<Boolean> isQueryExhausted() { return mRepository.isQueryExhausted(); }
    public void searchRecipeApi(String query, int pageNumber) {
        mIsViewingRecipes = true;
        mIsPerformQuery = true;
        mRepository.searchRecipeApi(query, pageNumber);
    }

    public void searchNextPage() {
        //imp check(if not perform query but viewing recipe)
        if(!mIsPerformQuery
                && mIsViewingRecipes
                && !isQueryExhausted().getValue()){
            mRepository.searchNextPage();
        }
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        mIsViewingRecipes = isViewingRecipes;
    }

    public boolean isPerformQuery() {
        return mIsPerformQuery;
    }

    public void setIsPerformQuery(boolean isPerformQuery) {
        mIsPerformQuery = isPerformQuery;
    }

    public boolean onBackPressed() {
        if (mIsPerformQuery) {
            // cancel the query
            mRepository.cancelRequest();
            mIsPerformQuery = false;
        }
        if (mIsViewingRecipes) {
            mIsViewingRecipes = false;
            return false;
        }
        return true;
        /*
         * if it viewing the recipes return false
         * if not return true
         */

    }
}
