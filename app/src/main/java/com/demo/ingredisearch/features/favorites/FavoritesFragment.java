package com.demo.ingredisearch.features.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ingredisearch.R;
import com.demo.ingredisearch.RecipeApplication;
import com.demo.ingredisearch.adapters.RecipeAdapter;
import com.demo.ingredisearch.models.Recipe;
import com.demo.ingredisearch.util.EventObserver;
import com.demo.ingredisearch.util.Resource;
import com.demo.ingredisearch.util.ViewHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ViewHelper mViewHelper;
    private RecipeAdapter mAdapter;

    private FavoritesViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_list, container, false);
        getViews(root);
        ViewHelper.showSubtitle(this, null);

        setupRecyclerView();

        return root;
    }

    private void getViews(View root) {
        mRecyclerView = root.findViewById(R.id.list);
        LinearLayout mLoadingContainer = root.findViewById(R.id.loadingContainer);
        LinearLayout mErrorContainer = root.findViewById(R.id.errorContainer);
        LinearLayout mNoResultsContainer = root.findViewById(R.id.noresultsContainer);
        TextView noResultsTitle = root.findViewById(R.id.noresultsTitle);
        noResultsTitle.setText(getString(R.string.nofavorites));

        mViewHelper = new ViewHelper(mRecyclerView, mLoadingContainer,
                mErrorContainer, mNoResultsContainer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createViewModel();
        subscribeObservers();
    }

    private void createViewModel() {
        RecipeApplication app = (RecipeApplication) requireActivity().getApplication();
        mViewModel = new ViewModelProvider(getViewModelStore(), new FavoritesViewModelFactory(
                app.getInjection().getRecipeRepository()
        )).get(FavoritesViewModel.class);
    }

    private void subscribeObservers() {

        mViewModel.getFavorites().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                handleResource(resource);
            }
        });

        mViewModel.navToRecipeDetails().observe(getViewLifecycleOwner(), new EventObserver<>(
                this::navigateToRecipeDetails
        ));
    }

    private void handleResource(Resource<List<Recipe>> resource) {
        switch (resource.status) {
            case SUCCESS:
                mViewHelper.hideOthers();
                if (resource.data.isEmpty()) {
                    mViewHelper.showNoResults();
                } else {
                    mAdapter.setRecipes(resource.data);
                }
                break;
            case ERROR:
                mViewHelper.showError();
                break;
            case LOADING:
                mViewHelper.showLoading();
                break;
        }
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RecipeAdapter(new RecipeAdapter.Interaction() {
            @Override
            public void onRemoveFavorite(@NotNull Recipe recipe) {
                mViewModel.removeFavorite(recipe);
            }

            @Override
            public void onAddFavorite(@NotNull Recipe recipe) {
                // unused
            }

            @Override
            public void onClickItem(@NotNull Recipe recipe) {
                mViewModel.requestToNavToDetails(recipe.getRecipeId());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void navigateToRecipeDetails(String recipeId) {
        Navigation.findNavController(requireView()).navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToRecipeDetailsFragment(recipeId)
        );
    }
}
