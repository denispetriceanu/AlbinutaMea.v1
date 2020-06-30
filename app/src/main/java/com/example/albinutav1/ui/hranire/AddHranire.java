package com.example.albinutav1.ui.hranire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddHranire extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_addhranire, container, false);


        final EditText idStup = root.findViewById(R.id.editTextHranire14);
        final EditText dataHranire = root.findViewById(R.id.editTextHranire15);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        dataHranire.setText(dateFormat.format(date));
        final EditText tipHranire = root.findViewById(R.id.editTextHranire16);
        final EditText tipHrana = root.findViewById(R.id.editTextHranire17);
        final EditText produs = root.findViewById(R.id.editTextHranire18);
        final EditText producator = root.findViewById(R.id.editTextHranire19);
        final EditText cantitate = root.findViewById(R.id.editTextHranire20);
        final EditText nota = root.findViewById(R.id.editTextHranire21);

        Button btnAdd = root.findViewById(R.id.button8);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HranireSendServerAsyncT ob = new HranireSendServerAsyncT();

                utilityFunction change = new utilityFunction();
                String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                ob.sendPost("POST", ip + "/hranire_post",
                        change.changeTheDiacritics(idStup.getText().toString()),
                        change.changeTheDiacritics(dataHranire.getText().toString()),
                        change.changeTheDiacritics(tipHranire.getText().toString()),
                        change.changeTheDiacritics(tipHrana.getText().toString()),
                        produs.getText().toString(), change.changeTheDiacritics(producator.getText().toString()),
                        cantitate.getText().toString(),
                        change.changeTheDiacritics(nota.getText().toString())
                );
                Toast.makeText(getContext(), "Stupul a fost adăugat!", Toast.LENGTH_SHORT).show();
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        Button btnCancel = root.findViewById(R.id.button9);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Nu ați realizat nici o modificare", Toast.LENGTH_LONG).show();

                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        return root;
    }
}
