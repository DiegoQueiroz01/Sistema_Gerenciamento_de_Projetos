package com.example.sistemagerenciamentoprojetos.domain.membros.cadastrarMembro;

import android.content.Context;
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase;
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro;
import com.example.sistemagerenciamentoprojetos.domain.membros.MembroDao;

public class CadastroMembroService {
    private MembroDao membroDao;

    public CadastroMembroService(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.membroDao = db.membroDao();
    }

    public String cadastrarMembro(String nome, String cargo, String email) {
        // Regra de Negócio: Validar campos vazios
        if (nome == null || nome.isEmpty() || cargo == null || cargo.isEmpty() || email == null || email.isEmpty()) {
            return "Todos os campos são obrigatórios.";
        }

        // Regra de Negócio: Não pode cadastrar dois usuários com as mesmas informações (validando pelo email único)
        Membro membroExistente = membroDao.buscarPorEmail(email);
        if (membroExistente != null) {
            return "Erro: Já existe um membro cadastrado com este e-mail.";
        }

        // Se passar pelas regras, salva no banco de dados
        Membro novoMembro = new Membro(nome, cargo, email);
        membroDao.inserir(novoMembro);

        return "Membro cadastrado com sucesso!";
    }
}
