package com.example.helpdeskunipassismobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helpdeskunipassismobile.model.FuncionarioEmpresa;
import com.example.helpdeskunipassismobile.model.UserDTO;

public class UserActivity extends BaseActivity {

    private TextView textNome, textSetor, textCPF, textEmail, textTelefone, textEndereco, textDataNascimento, textDataAdmissao, textStatusColaborador;

    private static UserDTO usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_user);

        // Inicializa os campos do layout
        textNome = findViewById(R.id.textNome);
        textSetor = findViewById(R.id.textSetor);
        textCPF = findViewById(R.id.textCPF);
        textEmail = findViewById(R.id.textEmail);
        textTelefone = findViewById(R.id.textTelefone);
        textEndereco = findViewById(R.id.textEndereco);
        textDataNascimento = findViewById(R.id.textDataNascimento);
        textDataAdmissao = findViewById(R.id.textDataAdmissao);
        textStatusColaborador = findViewById(R.id.textStatusColaborador);

        // Carregar os dados salvos no SharedPreferences
        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nome = prefs.getString("nomeFuncionario", "Não disponível");
        String setor = prefs.getString("setorFuncionario", "Não disponível");
        String cpf = prefs.getString("cpfFuncionario", "Não disponível");
        String email = prefs.getString("emailFuncionario", "Não disponível");
        String telefone = prefs.getString("telefoneFuncionario", "Não disponível");
        String endereco = prefs.getString("enderecoFuncionario", "Não disponível");
        String dataNascimento = prefs.getString("dataNascimentoFuncionario", "Não disponível");
        String dataAdmissao = prefs.getString("dataAdmissaoFuncionario", "Não disponível");
        String status = prefs.getString("statusFuncionario", "Não disponível");

        textNome.setText(nome);
        textSetor.setText(setor);
        textCPF.setText(cpf);
        textEmail.setText(email);
        textTelefone.setText(telefone);
        textEndereco.setText(endereco);
        textDataNascimento.setText(dataNascimento);
        textDataAdmissao.setText(dataAdmissao);
        textStatusColaborador.setText(status);

        usuario = new UserDTO();
        usuario.setNome(nome);
        usuario.setAvatar("");
    }

    // Método estático para acesso de outras Activities
    public static UserDTO getUsuario() {
        return usuario;
    }
}
