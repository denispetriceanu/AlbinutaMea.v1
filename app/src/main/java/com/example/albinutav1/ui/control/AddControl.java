package com.example.albinutav1.ui.control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddControl extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_addcontrol, container, false);
        final EditText idStupina = root.findViewById(R.id.editTextControl14);
        final EditText dataControl = root.findViewById(R.id.editTextControl15);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        dataControl.setText(dateFormat.format(date));
        final EditText examinare = root.findViewById(R.id.editTextControl16);
        final EditText stare = root.findViewById(R.id.editTextControl17);
        final EditText proba = root.findViewById(R.id.editTextControl18);
        final EditText concluzii = root.findViewById(R.id.editTextControl19);
        final EditText veterinar = root.findViewById(R.id.editTextControl20);
        final EditText observatii = root.findViewById(R.id.editTextControl21);

        final Button send = root.findViewById(R.id.sendCntrol);
        final Button cancel = root.findViewById(R.id.cancelControl);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });
        final utilityFunction ob = new utilityFunction();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlSendServerAsyncT send = new ControlSendServerAsyncT();
                String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                send.sendPost("POST", ip + "/controlveterinar_post", idStupina.getText().toString(), dataControl.getText().toString(),
                        ob.changeTheDiacritics(stare.getText().toString()), ob.changeTheDiacritics(examinare.getText().toString()),
                        ob.changeTheDiacritics(proba.getText().toString()), ob.changeTheDiacritics(concluzii.getText().toString()),
                        ob.changeTheDiacritics(veterinar.getText().toString()), ob.changeTheDiacritics(observatii.getText().toString()));
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });
        return root;
    }
}