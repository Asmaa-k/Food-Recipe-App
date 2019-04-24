package com.example.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeAdapter;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.utill.VerticalSpacingItemDecoration;
import com.example.foodrecipes.viewmodel.RecipeViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity {
    // private static final String TAG = "RecipeListActivity";

    RecipeViewModel recipeViewModel;
    RecipeAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchView;
    private static final String TAG = "RecipeListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recyclerView = findViewById(R.id.recipe_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = findViewById(R.id.search_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        initRecycleView();
        subscribeObserver();
        initSearchView();

        if (!recipeViewModel.isViewingRecipes()) {
            // display search categories
            displaySearchCategories();
        }
    }

    private void subscribeObserver() {
        recipeViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null)
                    if (recipeViewModel.isViewingRecipes()) {
                        recipeViewModel.setIsPerformQuery(false);//that mean the request is complete (when reach tha observer)
                        adapter.setRecipes(recipes);
                   }
            }
        });
        recipeViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "onChanged: the query is exhausted..." + aBoolean);
                if(aBoolean) {
                    adapter.setQueryExhausted();
                }
            }
        });
    }

    private void initRecycleView() {

        adapter = new RecipeAdapter(new OnRecipeListener() {
            @Override
            public void onRecipeClick(int position) {
                Intent intent = new Intent(RecipeListActivity.this, RecipeActivity.class);
                intent.putExtra("recipe",adapter.getSelectedRecipe(position));
                startActivity(intent);
            }
            @Override
            public void onCategoryClick(String category) {
                adapter.displayLoading();
                recipeViewModel.searchRecipeApi(category, 1);
                searchView.clearFocus();
            }
        });
        VerticalSpacingItemDecoration itemDecoration = new VerticalSpacingItemDecoration(35);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1))// -1 for up, 1 for down, 0 will always return false.
                {//Search the next page
                    recipeViewModel.searchNextPage();
                     }
            }
        });
    }

    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.displayLoading();
                recipeViewModel.searchRecipeApi(s, 1);
                searchView.clearFocus();
                return false;
                //onQueryTextSubmit :giving search result after submiting
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
                //onQueryTextChange :giving search result while typing
            }
        });
    }

    private void displaySearchCategories() {
        recipeViewModel.setIsViewingRecipes(false);
        recipeViewModel.setIsPerformQuery(false);
        adapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (recipeViewModel.onBackPressed()) super.onBackPressed();
        else displaySearchCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_categories) displaySearchCategories();
        return super.onOptionsItemSelected(item);
    }
}

