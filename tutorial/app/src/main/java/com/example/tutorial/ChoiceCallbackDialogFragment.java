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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.callback.ChoiceCallback;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.callback.SelectIdPCallback;

/**
 * Reference implementation of handing Advice with {@link DialogFragment}
 */
public class ChoiceCallbackDialogFragment extends DialogFragment {

    private MainActivity listener;
    private Node node;

    public static ChoiceCallbackDialogFragment newInstance(Node node) {
        ChoiceCallbackDialogFragment fragment = new ChoiceCallbackDialogFragment();
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
        View view = inflater.inflate(R.layout.choice_callback_dialog_fragment, container, false);
        node = (Node) getArguments().getSerializable("NODE");

        ChoiceCallback callback = (ChoiceCallback) node.getCallback(ChoiceCallback.class);


        Spinner spinner = view.findViewById(R.id.choice);
        spinner.setPrompt(callback.getPrompt());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, callback.getChoices());

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.setSelectedIndex(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button next = view.findViewById(R.id.next);

        next.setOnClickListener(v -> {

            dismiss();

            node.next(getContext(), listener);

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
