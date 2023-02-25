package com.itproger.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "task_manager";  // Название БД
    private static final int db_version = 1;  // Название БД

    // Прописываем структуру таблицы
    private static final String db_table = "task";
    private static final String db_column = "taskName";

    // в БД task_manager -> task создаем таблицу
    // Структура таблицы
    // id taskName
    // 1 Купить картошку
    // 2 Купить машину

    // В этом конструкторе принимаем только контекст
    public DataBase(@Nullable Context context) {
        // другие параметры передаем, как значения других переменных
        super(context, db_name, null, db_version);
    }

    // Создаем таблицу
    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {

        // Используем форматированный вывод вместо
        // String query = "CREATE TABLE " + db_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
        //                + db_column + " TEXT NOT NULL);";
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL);", db_table, db_column);
        sqLiteDB.execSQL(query);
    }

    // При апгрейде БД, удаляем таблицу и создаем заново (запускаем метод onCreate)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int i, int i1) {
        String query = String.format("DELETE TABLE IF EXIST %s", db_table);
        sqLiteDB.execSQL(query);
        onCreate(sqLiteDB);
    }

    // Метод для добавления записей в БД
    public void  insertData(String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_column, task);
        db.insertWithOnConflict(db_table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Метод для удаления записей в БД
    public void  deleteData(String taskName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(db_table, db_column + " = ?", new String[]{taskName});
        db.close();
    }

    public ArrayList<String> getAllTask() {
        ArrayList<String> allTask = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(db_table,new String[] {db_column}, null, null, null, null, null);

        // Перебираем все записи БД
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(db_column);   // Получаем индекс записи
            allTask.add(cursor.getString(index));           // и по этому индексу получаем саму запись
        }                                                   // и добавляем ее в массив
        cursor.close();
        db.close();
        return allTask;
    }
}
