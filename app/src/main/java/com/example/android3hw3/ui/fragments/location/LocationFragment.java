package com.example.android3hw3.ui.fragments.location;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android3hw3.R;
import com.example.android3hw3.base.BaseFragment;
import com.example.android3hw3.databinding.FragmentCharacterBinding;
import com.example.android3hw3.databinding.FragmentLocationBinding;
import com.example.android3hw3.models.CharacterModel;
import com.example.android3hw3.models.LocationModel;
import com.example.android3hw3.models.RickAndMortyResponse;
import com.example.android3hw3.ui.adapters.LocationAdapter;
import com.example.android3hw3.ui.fragments.character.CharacterViewModel;

import java.util.ArrayList;
import java.util.List;


public class LocationFragment extends BaseFragment<FragmentLocationBinding> {

    private LocationAdapter locationAdapter = new LocationAdapter(LocationAdapter.diffCallBack);
    private LocationViewModel locationViewModel = new LocationViewModel();
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = true;
    private int postVisible, visibleCount, totalCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        return binding.getRoot();
    }

    @Override
    protected void setupViews() {
        linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.locationRecView.setLayoutManager(linearLayoutManager);
        binding.locationRecView.setAdapter(locationAdapter);
    }

    @Override
    protected void setupListener() {
        binding.locationRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleCount = linearLayoutManager.getChildCount();
                    totalCount = linearLayoutManager.getItemCount();
                    postVisible = linearLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleCount + postVisible) >= totalCount) {
                        if (locationViewModel.locationPage != totalCount && (locationViewModel.locationPage < totalCount)) {
                            locationViewModel.locationPage++;
                            if (!loading && (locationViewModel.locationPage < totalCount)) {

                                fetchLocation();
                            }
                        }
                    }
                }
            }
        });
    }

    private void fetchLocation() {
        if (isNetwork()) {
            locationViewModel.getList().observe(getViewLifecycleOwner(), new Observer<RickAndMortyResponse<LocationModel>>() {
                @Override
                public void onChanged(RickAndMortyResponse<LocationModel> locationModelRickAndMortyResponse) {
                    if (!loading) {
                        ArrayList<LocationModel> list = new ArrayList<>(locationAdapter.getCurrentList());
                        list.addAll(locationModelRickAndMortyResponse.getResults());
                        locationAdapter.submitList(list);
                        if (list != locationAdapter.getCurrentList()) {
                            locationAdapter.submitList(list);
                        }
                    }

                }
            });
        } else
            locationAdapter.submitList((List<LocationModel>) locationViewModel.getList());
    }

    private boolean isNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void setupRequest() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationViewModel.locationPage =1;
        loading = true;
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationViewModel.getList().observe(getViewLifecycleOwner(), new Observer<RickAndMortyResponse<LocationModel>>() {
            @Override
            public void onChanged(RickAndMortyResponse<LocationModel> locationModelRickAndMortyResponse) {
                if (loading) {
                    ArrayList<LocationModel> list = new ArrayList<>(locationAdapter.getCurrentList());
                    list.addAll(locationModelRickAndMortyResponse.getResults());
                    locationAdapter.submitList(list);
                    loading = false;
                }
            }
        });
    }
}