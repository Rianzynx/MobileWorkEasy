package com.example.helpdeskunipassismobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewSolicitationActivity extends BaseActivity {

    private Spinner spinner;
    private Spinner spinnerCategoria;
    private EditText textDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_new_solicitation);

        spinner = findViewById(R.id.spinner);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        textDate = findViewById(R.id.textDate);

        // Opções do spinner
        String[] options = {"🟢 Baixa", "🟡 Média", "🔴 Alta"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        String[] categoryOptions = {
                "Folha de Pagamento",
                "Benefícios",
                "Recrutamento",
                "Treinamento",
                "Férias",
                "Outros"
        };

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoryAdapter);
        spinnerCategoria.setSelection(0);

        // Listener para spinner, evita Toast na inicialização
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSelection) {
                    firstSelection = false;
                    return; // ignora o evento inicial
                }
                String selectedOption = options[position];
                Toast.makeText(NewSolicitationActivity.this, "Selecionado: " + selectedOption, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada a fazer
            }
        });

        // Configura a data atual como hint
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        textDate.setHint(currentDate);

        // Campo data não editável, só clicável para abrir DatePicker
        textDate.setFocusable(false);
        textDate.setClickable(true);

        textDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(NewSolicitationActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Atualiza o texto com a data selecionada formatada
                        calendar.set(year, month, dayOfMonth);
                        String selectedDate = sdf.format(calendar.getTime());
                        textDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
    }
}
