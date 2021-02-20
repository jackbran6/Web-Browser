package com.example.webbrowser20;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.Control;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webbrowser20.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History extends AppCompatActivity {

    private ArrayList<Website> historyList;
    private WebsiteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        Intent incomingIntent = getIntent();
        String bookmarkListAsString = getIntent().getStringExtra("History_List");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Website>>(){}.getType();
        historyList = gson.fromJson(bookmarkListAsString, type);
        Collections.reverse(historyList);

        ListView listView = findViewById(R.id.historyList);
        adapter = new WebsiteAdapter(this,R.layout.custom_list_view, historyList);
        listView.setAdapter(adapter);

        ImageButton back = findViewById(R.id.historyBackButton);
        back.setOnClickListener((View v) -> {
            finish();
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteOrGo(view, position, parent);
            }
        });
    }

    private void deleteOrGo(View view, int position, AdapterView<?> parent){
        AlertDialog.Builder builder = new AlertDialog.Builder(History.this);

        builder.setPositiveButton("Visit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String selected = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
                Intent openLink = new Intent(History.this, MainActivity.class);
                Intent intent = openLink.putExtra("Clicked_URL",selected);
                startActivity(openLink);
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Website website = historyList.get(position);
                historyList.remove(position);

                Gson gsonTwo = new Gson();
                String json = gsonTwo.toJson(historyList);

                SharedPref.write(SharedPref.HISTORY_LIST, json);

                adapter.notifyDataSetChanged();



            }
        });

        AlertDialog dialog = builder.create();
        dialog.setTitle("Delete or Visit Page?");
        dialog.show();

    }
}


