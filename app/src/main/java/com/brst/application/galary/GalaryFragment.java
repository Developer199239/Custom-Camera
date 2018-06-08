package com.brst.application.galary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brst.application.R;

import java.util.ArrayList;

public class GalaryFragment extends Fragment {

    ArrayList<String> cameraFiles;
    RecyclerView recyclerView;
    GalaryAdapter mAdapter;
    boolean camaraFront;

    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_galary, null);

        recyclerView = view.findViewById(R.id.recycler_view);

        bundle=getArguments();
        if (bundle!=null){
            camaraFront=bundle.getBoolean("camaraFront");
            cameraFiles=bundle.getStringArrayList("list");
        }

         if (cameraFiles != null) {
            if (cameraFiles.size() > 0) {
                int numberOfColumns = 2;
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
                mAdapter = new GalaryAdapter(getContext(), cameraFiles,camaraFront);
                //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
               // recyclerView.addItemDecoration(itemDecoration);
                recyclerView.setAdapter(mAdapter);
            }
        }

        return view;
    }
}
