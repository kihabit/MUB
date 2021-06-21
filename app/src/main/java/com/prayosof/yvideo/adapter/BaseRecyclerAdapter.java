package com.prayosof.yvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yogesh Y. Nikam on 11/07/20.
 */
public class BaseRecyclerAdapter <T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    @NonNull
    private final LayoutInflater mLayoutInflater;

    private final List<T> mItems;

    protected BaseRecyclerAdapter(@NonNull final Context context) {
        this(context, (List<T>) null);
    }

    protected BaseRecyclerAdapter(@NonNull final Context context, @Nullable final T[] items) {
        this(context, items == null ? null : Arrays.asList(items));
    }

    public BaseRecyclerAdapter(@NonNull final Context context, @Nullable final List<T> items) {
        mLayoutInflater = LayoutInflater.from(context);
        if (items == null) {
            mItems = new ArrayList<>(15);
        } else {
            mItems = new ArrayList<>(items);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void setItem(final int position, @Nullable final T item) {
        mItems.set(position, item);
        notifyItemChanged(position);
    }

    public void removeItem(final int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public final boolean removeItem(@NonNull final T item) {
        final int position = mItems.indexOf(item);
        if (position != -1 && mItems.remove(position) != null) {
            notifyItemRemoved(position);
            return true;
        }
        return false;
    }

    @NonNull
    protected final LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public final T getItem(final int position) {
        return mItems.get(position);
    }

    public final int indexOf(@NonNull final T item) {
        return mItems.indexOf(item);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public final void setItems(@Nullable final List<T> items) {
        mItems.clear();
        if (items != null) {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public final void setItems(@Nullable final T[] items) {
        setItems(items == null ? null : Arrays.asList(items));
    }

    public final void swap(final int i, final int j) {
        Collections.swap(mItems, i, j);
    }
}

