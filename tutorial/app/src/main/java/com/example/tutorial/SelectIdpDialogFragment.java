/*
 *  Copyright (c) 2022 ForgeRock. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license. See the LICENSE file for details.
 */

package com.example.tutorial;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.callback.SelectIdPCallback;

/**
 * Reference implementation of handing Advice with {@link DialogFragment}
 */
public class SelectIdpDialogFragment extends DialogFragment {

    private MainActivity listener;
    private Node node;

    public static SelectIdpDialogFragment newInstance(Node node) {
        SelectIdpDialogFragment fragment = new SelectIdpDialogFragment();
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
        View view = inflater.inflate(R.layout.select_idp_dialog_fragment, container, false);
        node = (Node) getArguments().getSerializable("NODE");
        SelectIdPCallback callback = (SelectIdPCallback) node.getCallback(SelectIdPCallback.class);

        LinearLayout selectIdpLayout = view.findViewById(R.id.selectIdpLayout);

        for (SelectIdPCallback.IdPValue idp : callback.getProviders()) {
            Button idpButton = new Button(getContext());
            idpButton.setText(idp.getProvider());

            idpButton.setOnClickListener(v -> {
                callback.setValue(idp.getProvider());
                dismiss();
                node.next(getContext(), listener);
            });
            selectIdpLayout.addView(idpButton);
            Space space = new Space(getContext());
            space.setMinimumWidth(20);
            selectIdpLayout.addView(space);

        }

        Space space = new Space(getContext());
        space.setMinimumWidth(20);
        selectIdpLayout.addView(space);

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
