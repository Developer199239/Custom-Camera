package com.brst.application.galary;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.brst.application.R;
import com.bumptech.glide.Glide;



public class DialogFragment extends android.support.v4.app.DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog, null);


        view.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ImageView image=view.findViewById(R.id.imageViewer);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            String path = bundle.getString("path");

            boolean camaraFront=bundle.getBoolean("camaraFront");

            Log.d("aaaa","==="+camaraFront);

            Glide.with(getContext()).load(path).into(image);

        }

        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



}
