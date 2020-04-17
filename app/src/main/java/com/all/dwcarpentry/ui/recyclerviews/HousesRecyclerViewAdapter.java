//package com.all.dwcarpentry.ui.recyclerviews;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import com.all.dwcarpentry.R;
//import com.all.dwcarpentry.data.House;
//import com.firebase.ui.database.paging.DatabasePagingOptions;
//import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
//import com.firebase.ui.database.paging.LoadingState;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class HousesRecyclerViewAdapter extends FirebaseRecyclerPagingAdapter<House, HouseViewHolder>
//{
//    private Fragment parentFragment;
//    private OnHouseCardClickedListener onHouseCardClickedListener;
//    public HousesRecyclerViewAdapter(@NonNull DatabasePagingOptions<House> options, Fragment parentFragment, OnHouseCardClickedListener listener)
//    {
//        super(options);
//        this.parentFragment = parentFragment;
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull HouseViewHolder holder, int position, @NonNull House model)
//    {
//        holder.bind(model, parentFragment);
//    }
//
//    @Override
//    protected void onLoadingStateChanged(@NonNull LoadingState state)
//    {
//
//    }
//
//    @NonNull
//    @Override
//    public HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//    {
//        Log.v("TEST", "OnCreateViewHolder was called.");
//        return new HouseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.house_card, parent, false), onHouseCardClickedListener);
//    }
//
//    public interface OnHouseCardClickedListener
//    {
//        void onHouseCardClicked(String houseKey);
//    }
//}
