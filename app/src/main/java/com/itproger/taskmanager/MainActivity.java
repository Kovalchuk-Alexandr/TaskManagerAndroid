package com.itproger.taskmanager;

import static androidx.core.os.LocaleListCompat.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DataBase dataBase;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private EditText list_name_field;
    private SharedPreferences sharedPreferences;
    private TextView info_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = new DataBase(this);
        listView = findViewById(R.id.task_list);
        list_name_field = findViewById(R.id.list_name_field);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        String list_name = sharedPreferences.getString("list_name", "");
        list_name_field.setText(list_name);

        // Создаем анимацию: мигание надписи
        info_app = findViewById(R.id.info_app);
        info_app.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_info_text));

        loadAllTask();
        changeTextAction();
    }

    private void changeTextAction() {
        list_name_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("list_name", String.valueOf(list_name_field.getText()));
                editor.apply();

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllTask() {
        // Получаем все записи из БД
        ArrayList<String> allTask = dataBase.getAllTask();
        if(arrayAdapter == null) {      // Вызов при старте программы
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.task_list_row, R.id.text_label_row, allTask);
            listView.setAdapter(arrayAdapter);
        } else {        // Вызов по ходу выполнения программы
            arrayAdapter.clear();           // очищаем адаптер
            arrayAdapter.addAll(allTask);   // добавляем заново все записи
            arrayAdapter.notifyDataSetChanged();    // отображаем
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Изменяем иконку меню
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    // Отслеживаем нажатие на кнопку в меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         // Проверяем, на что нажал пользователь, если это кнопка меню
        if(item.getItemId() == R.id.add_new_task) {
            final EditText userTaskField = new EditText(this);
            // Добавляем всплывающее окно, что можно добавлять новый элемент
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Добавление нового задания")      // Заголовок окна
                    .setMessage("Что бы вы хотели добавить?")   // Подсказка
                    .setView(userTaskField)        // Текстовое поле
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Получаем текст, который вводит пользователь
                            String task = String.valueOf(userTaskField.getText());
                            dataBase.insertData(task);      // Добавляем записи
                            loadAllTask();                  // Обновляем записи
                        }
                    })
                    .setNegativeButton("Ничего", null) // Ничего не обрабатываем
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View button) {
        View parent = (View)button.getParent();
        TextView textView = parent.findViewById(R.id.text_label_row);
        final String task = String.valueOf(textView.getText());

        // Анимация плавного удаления
        parent.animate().alpha(0).setDuration(1500).withEndAction(new Runnable() {
            @Override
            public void run() {
                dataBase.deleteData(task);
                loadAllTask();
                parent.animate().alpha(1).setDuration(0);
            }
        });
    }
}