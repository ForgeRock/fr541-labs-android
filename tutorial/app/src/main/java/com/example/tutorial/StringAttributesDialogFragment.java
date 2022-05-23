/*
 *  Copyright (c) 2022 ForgeRock. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license. See the LICENSE file for details.
 */

package com.example.tutorial;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.StringAttributeInputCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference implementation of handing Advice with {@link DialogFragment}
 */
public class StringAttributesDialogFragment extends DialogFragment {

    private MainActivity listener;
    private Node node;

    public static StringAttributesDialogFragment newInstance(Node node) {
        StringAttributesDialogFragment fragment = new StringAttributesDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("NODE", node);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.string_attributes_dialog_fragment, container, false);
        node = (Node) getArguments().getSerializable("NODE");

        LinearLayout stringAttrLayout = view.findViewById(R.id.stringAttrLayout);

        //TODO REGISTER: display and obtain


        Space space = new Space(getContext());
        space.setMinimumWidth(20);
        stringAttrLayout.addView(space);

        Button next = view.findViewById(R.id.next);
        next.setOnClickListener(v -> {
            dismiss();
            node.next(getContext(), listener);
        });

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            listener = (MainActivity) context;
        }
    }
}
