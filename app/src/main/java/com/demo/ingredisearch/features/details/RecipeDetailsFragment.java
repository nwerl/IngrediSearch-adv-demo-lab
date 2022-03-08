package com.demo.ingredisearch.features.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.demo.ingredisearch.R;
import com.demo.ingredisearch.RecipeApplication;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.ViewHelper;

public class RecipeDetailsFragment extends Fragment {
    private ViewHelper mViewHelper;
    private RecipeDetailsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_details, container, false);
        getViews(root);
        ViewHelper.showSubtitle(this, null);

        return root;
    }

    private void getViews(View root) {
        ScrollView mScrollView = root.findViewById(R.id.parent);
        mRecipeImage = root.findViewById(R.id.recipe_image);
        mRecipeTitle = root.findViewById(R.id.recipe_title);
        mRecipeRank = root.findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = root.findViewById(R.id.ingredients_container);

        LinearLayout mLoadingContainer = root.findViewById(R.id.loadingContainer);
        LinearLayout mNoResultsContainer = root.findViewById(R.id.noresultsContainer);
        LinearLayout mErrorContainer = root.findViewById(R.id.errorContainer);

        mViewHelper = new ViewHelper(mScrollView, mLoadingContainer,
                mErrorContainer, mNoResultsContainer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createViewModel();
        setupObservers();

        RecipeDetailsFragmentArgs arguments = RecipeDetailsFragmentArgs.fromBundle(requireArguments());
        mViewModel.searchRecipe(arguments.getRecipeId());
    }

    private void setupObservers() {
        mViewModel.getRecipe().observe(getViewLifecycleOwner(), response -> {
            if (response != null)
                handleResponse(response);
        });
    }

    private void createViewModel() {
        RecipeApplication app = (RecipeApplication) requireActivity().getApplication();
        mViewModel = new ViewModelProvider(getViewModelStore(),
                new RecipeDetailsViewModelFactory(app.getInjection().getRecipeRepository()))
                .get(RecipeDetailsViewModel.class);
    }

    private <T> void handleResponse(Resource<Recipe> response) {
        switch (response.status) {
            case SUCCESS:
                if (response.data != null) {
                    mViewHelper.hideOthers();
                    showRecipe(response.data);
                } else {
                    mViewHelper.showNoResults();
                }
                break;
            case LOADING:
                mViewHelper.showLoading();
                break;
            case ERROR:
                mViewHelper.showError();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private AppCompatImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;

    private void showRecipe(Recipe recipe) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(recipe.getImageUrl())
                .into(mRecipeImage);

        mRecipeTitle.setText(recipe.getTitle());
        mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

        mRecipeIngredientsContainer.removeAllViews();
        for (String ingredient : recipe.getIngredients()) {
            TextView textView = new TextView(getContext());
            textView.setText(ingredient);
            textView.setTextSize(15);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            mRecipeIngredientsContainer.addView(textView);
        }
    }
}
