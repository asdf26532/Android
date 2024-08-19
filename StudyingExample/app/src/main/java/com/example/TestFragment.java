package com.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.databinding.FragTestBinding;

public class TestFragment extends Fragment {

    private FragTestBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragTestBinding.inflate(inflater, container, false);
        mBinding.btnFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.tvFragment.setText("안드로이드 공부중");
            }
        });

        return mBinding.getRoot();
    }
}
