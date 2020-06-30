package com.example.albinutav1.ui.stup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import java.util.Objects;

public class AddStup extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_addstup, container, false);
        final String id_stupina_auto = getArguments().getString("id_stupina_autocomplete");
        final utilityFunction forMove = new utilityFunction();

        final EditText id_stupina = root.findViewById(R.id.textView14);
        if (id_stupina_auto != null) {
            id_stupina.setText(id_stupina_auto);
        }

        final EditText culoare = root.findViewById(R.id.textView15);
        final EditText rasa = root.findViewById(R.id.textView16);
        final EditText tip = root.findViewById(R.id.textView17);
        final EditText numar = root.findViewById(R.id.textView18);
        final EditText mod_const = root.findViewById(R.id.textView19);
        final EditText varsta = root.findViewById(R.id.textView20);
        final EditText ram_puiet = root.findViewById(R.id.rame_puiet_add);
        final EditText ram_hrana = root.findViewById(R.id.rame_hrana_add);

        Button btnAdd = root.findViewById(R.id.button4);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StupSendServerAsyncT ob = new StupSendServerAsyncT();
                if (!("".equals(id_stupina.getText().toString())
                        && "".equals(culoare.getText().toString()) && "".equals(rasa.getText().toString())
                        && "".equals(tip.getText().toString()) && "".equals(numar.getText().toString())
                        && "".equals(mod_const.getText().toString()) && "".equals(varsta.getText().toString()))) {

                    id_stupina.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    culoare.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    rasa.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    tip.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    numar.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    mod_const.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    varsta.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    utilityFunction change = new utilityFunction();
                    String ip = Objects.requireNonNull(getContext()).getResources().getString(R.string.ip);
                    ob.sendPost("POST", ip + "/stup_post",
                            id_stupina.getText().toString(),
                            change.changeTheDiacritics(culoare.getText().toString()),
                            change.changeTheDiacritics(rasa.getText().toString()),
                            change.changeTheDiacritics(tip.getText().toString()),
                            numar.getText().toString(),
                            change.changeTheDiacritics(mod_const.getText().toString()),
                            varsta.getText().toString(), ram_puiet.getText().toString(), ram_hrana.getText().toString()
                    );
                    Toast.makeText(getContext(), "Stupul a fost adăugat!", Toast.LENGTH_LONG).show();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Nu ați completat toate câmpurile!", Toast.LENGTH_LONG).show();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                }
            }
        });

        Button btnCancel = root.findViewById(R.id.button5);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Nu ați realizat nici o modificare", Toast.LENGTH_LONG).show();

                id_stupina.onEditorAction(EditorInfo.IME_ACTION_DONE);
                culoare.onEditorAction(EditorInfo.IME_ACTION_DONE);
                rasa.onEditorAction(EditorInfo.IME_ACTION_DONE);
                tip.onEditorAction(EditorInfo.IME_ACTION_DONE);
                numar.onEditorAction(EditorInfo.IME_ACTION_DONE);
                mod_const.onEditorAction(EditorInfo.IME_ACTION_DONE);
                varsta.onEditorAction(EditorInfo.IME_ACTION_DONE);

                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        return root;
    }

}