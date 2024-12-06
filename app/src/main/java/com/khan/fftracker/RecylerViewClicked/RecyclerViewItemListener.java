package com.khan.fftracker.RecylerViewClicked;

import androidx.annotation.NonNull;

import java.util.HashMap;

public interface RecyclerViewItemListener {
    void onItemClick(@NonNull HashMap<String, Object> get, int pos);
    void onItemClickObject(int id, @NonNull Object any, int pos);

}