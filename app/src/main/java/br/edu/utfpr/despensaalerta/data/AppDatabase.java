package br.edu.utfpr.despensaalerta.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalDate;

import br.edu.utfpr.despensaalerta.model.Item;
import br.edu.utfpr.despensaalerta.model.SituacaoItem;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
@TypeConverters(Conversores.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NOME_BANCO = "despensa_alerta.db";

    private static volatile AppDatabase instancia;

    public abstract ItemDao itemDao();

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, NOME_BANCO)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    inserirDadosExemplo(db);
                                }
                            })
                            .build();
                }
            }
        }
        return instancia;
    }

    private static void inserirDadosExemplo(SupportSQLiteDatabase db) {
        LocalDate hoje = LocalDate.now();
        inserir(db, "Leite integral", "Laticínios", 2, "Geladeira", hoje.minusDays(2), null);
        inserir(db, "Iogurte natural", "Laticínios", 4, "Geladeira", hoje.plusDays(3), null);
        inserir(db, "Peito de frango", "Carnes", 1, "Freezer", hoje.plusDays(45), null);
        inserir(db, "Arroz branco 5kg", "Grãos", 1, "Armário", hoje.plusDays(180), null);
        inserir(db, "Feijão carioca", "Grãos", 2, "Armário", hoje.plusDays(6), null);
        inserir(db, "Detergente", "Limpeza", 3, "Área de serviço", hoje.plusDays(365), null);
        inserir(db, "Pasta de dente", "Higiene", 1, "Banheiro", hoje.minusDays(10), null);
        inserir(db, "Pão de forma", "Padaria", 1, "Armário", hoje.minusDays(1), SituacaoItem.DESCARTADO);
        inserir(db, "Queijo mussarela", "Laticínios", 1, "Geladeira", hoje.plusDays(5), SituacaoItem.CONSUMIDO);
    }

    private static void inserir(SupportSQLiteDatabase db, String nome, String categoria,
                                int quantidade, String local, LocalDate validade,
                                SituacaoItem baixa) {
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("categoria", categoria);
        valores.put("quantidade", quantidade);
        valores.put("local", local);
        valores.put("dataValidade", validade.toEpochDay());
        if (baixa == null) {
            valores.put("situacao", SituacaoItem.ATIVO.name());
            valores.putNull("dataBaixa");
        } else {
            valores.put("situacao", baixa.name());
            valores.put("dataBaixa", LocalDate.now().toEpochDay());
        }
        db.insert("itens", SQLiteDatabase.CONFLICT_ABORT, valores);
    }
}
