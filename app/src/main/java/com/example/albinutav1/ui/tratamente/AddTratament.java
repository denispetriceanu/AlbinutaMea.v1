package com.example.albinutav1.ui.tratamente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddTratament extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_addtratament, container, false);

        final TextView id_stup = root.findViewById(R.id.editText13);
        final TextView data = root.findViewById(R.id.editText14);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        data.setText(dateFormat.format(date));
        final TextView afectiune = root.findViewById(R.id.editText15);
        final TextView produs = root.findViewById(R.id.editText16);
        final TextView mod_admin = root.findViewById(R.id.editText17);
        final TextView familii_albine = root.findViewById(R.id.editText18);
        final TextView doza = root.findViewById(R.id.editText19);
        final TextView cantitate = root.findViewById(R.id.editTextHranire20);
        final TextView observatii = root.findViewById(R.id.editText21);


        final Button adauga = root.findViewById(R.id.button8);
        final Button anuleaza = root.findViewById(R.id.button9);

        adauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                utilityFunction ob = new utilityFunction();
                TratamenteSendServerAsyncT send = new TratamenteSendServerAsyncT();
                String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                send.sendPost(
                        "POST", ip + "/tratament/post",
                        ob.changeTheDiacritics(id_stup.getText().toString()),
                        ob.changeTheDiacritics(data.getText().toString()),
                        ob.changeTheDiacritics(afectiune.getText().toString()),
                        ob.changeTheDiacritics(produs.getText().toString()),
                        ob.changeTheDiacritics(mod_admin.getText().toString()),
                        ob.changeTheDiacritics(familii_albine.getText().toString()),
                        ob.changeTheDiacritics(doza.getText().toString()),
                        ob.changeTheDiacritics(cantitate.getText().toString()),
                        ob.changeTheDiacritics(observatii.getText().toString())
                );
                Toast.makeText(getContext(), "Tratamentul a fost adăugat în baza de date", Toast.LENGTH_SHORT).show();
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        anuleaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        getContext(),
                        "Nu ati adăugat nici un tratament!",
                        Toast.LENGTH_SHORT).show();
//                data.onEditorAction(EditorInfo.IME_ACTION_DONE);
//                nr_stupi.onEditorAction(EditorInfo.IME_ACTION_DONE);
//                locatie.onEditorAction(EditorInfo.IME_ACTION_DONE);
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });
        return root;
    }
}
