package com.example.sistemagerenciamentoprojetos.domain.membros;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa;
import com.example.sistemagerenciamentoprojetos.domain.tarefas.TarefaDao;


@Database(entities = {Membro.class, Tarefa.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;


    public abstract MembroDao membroDao();
    public abstract TarefaDao tarefaDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "projeto_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}