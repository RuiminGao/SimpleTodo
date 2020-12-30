package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_COMMENT = "item_comment";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String KEY_ITEM_MODE = "item_mode";
    public static final int EDIT_TEXT_CODE = 20;

    ArrayList<List<String>> items;
    ArrayList<List<String>> comments;

    Button btnAdd;
    FloatingActionButton fabReturn;
    EditText etNewTask;
    ArrayList<RecyclerView> rvTasks;

    ArrayList<ItemsAdapter> itemsAdapter;
    Spinner spinner;
    ArrayList<String> timeModes;

    Typeface typeface;
    TextView[] categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btnAdd);
        etNewTask = findViewById(R.id.etNewTask);
        rvTasks = new ArrayList<RecyclerView>();
        rvTasks.add(findViewById(R.id.rvToday));
        rvTasks.add(findViewById(R.id.rvThisWeek));
        rvTasks.add(findViewById(R.id.rvFuture));
        fabReturn = findViewById(R.id.fabReturn);
        spinner = findViewById(R.id.spinner);

        /***
        typeface = Typeface.createFromAsset(getAssets(), "fonts/NerkoOne-Regular.ttf");
        categories = new TextView[3];
        categories[0] = findViewById(R.id.tvToday);
        categories[1] = findViewById(R.id.tvThisWeek);
        categories[2] = findViewById(R.id.tvFuture);
        categories[0].setTypeface(typeface);
        categories[1].setTypeface(typeface);
        categories[2].setTypeface(typeface);
         ***/

        timeModes = new ArrayList<String>();
        timeModes.add("Today");
        timeModes.add("This Week");
        timeModes.add("Future");

        items = new ArrayList<List<String>> ();
        comments = new ArrayList<List<String>> ();
        itemsAdapter = new ArrayList<ItemsAdapter> ();

        loadItems();
        loadComments();

        Drawable divider = ContextCompat.getDrawable(this, R.drawable.item);
        Toast.makeText(getApplicationContext(), String.valueOf(divider.getIntrinsicHeight()), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeModes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ItemsAdapter.OnLongClickListener[] onLongClickListener = new ItemsAdapter.OnLongClickListener[3];
        for (int mode : new int[]{0, 1, 2}) {
            onLongClickListener[mode] = new ItemsAdapter.OnLongClickListener() {
                @Override
                public void onItemLongClicked(int position) {
                    removeItem(position, mode);
                }
            };
        }

        ItemsAdapter.OnClickListener[] onClickListener = new ItemsAdapter.OnClickListener[3];
        for (int mode : new int[]{0, 1, 2}){
            onClickListener[mode] =new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(mode).get(position));
                i.putExtra(KEY_ITEM_COMMENT, comments.get(mode).get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                i.putExtra(KEY_ITEM_MODE, mode);
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
    }
        for (int mode : new int[]{0, 1, 2}) {
            itemsAdapter.add(new ItemsAdapter(items.get(mode), comments.get(mode), onLongClickListener[mode], onClickListener[mode]));
            rvTasks.get(mode).setAdapter(itemsAdapter.get(mode));
            rvTasks.get(mode).setLayoutManager(new LinearLayoutManager(this));
            rvTasks.get(mode).addItemDecoration(new ViewDivider(this));
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etNewTask.getText().toString();
                if(todoItem.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your item.", Toast.LENGTH_SHORT).show();
                } else {
                    //get the option
                    String option = spinner.getSelectedItem().toString();
                    int mode = timeModes.indexOf(option);
                    items.get(mode).add(todoItem);
                    comments.get(mode).add("");
                    itemsAdapter.get(mode).notifyItemInserted(items.get(mode).size() - 1);
                    etNewTask.setText("");
                    Toast.makeText(getApplicationContext(), "Added.", Toast.LENGTH_SHORT).show();
                    saveItems(mode);
                    saveComments(mode);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            String itemComment = data.getStringExtra(KEY_ITEM_COMMENT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            int mode = data.getExtras().getInt(KEY_ITEM_MODE);
            items.get(mode).set(position, itemText);
            comments.get(mode).set(position, itemComment);
            itemsAdapter.get(mode).notifyItemChanged(position);
            saveItems(mode);
            saveComments(mode);
            Toast.makeText(getApplicationContext(), "Updated.", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(int mode) {
        return new File(getFilesDir(), "data"+mode+".txt");
    }

    private File getCommentsFile(int mode) {
        return new File(getFilesDir(), "comments"+mode+".txt");
    }

    private void loadItems() {
        for (int mode : new int[]{0, 1, 2}) {
            try {
                items.add(new ArrayList<>(FileUtils.readLines(getDataFile(mode), Charset.defaultCharset())));
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading items", e);
                items.add(new ArrayList<>());
            }
        }
    }
    private  void loadComments() {
        for (int mode : new int[]{0, 1, 2}) {
            try {
                comments.add(new ArrayList<>(FileUtils.readLines(getCommentsFile(mode), Charset.defaultCharset())));
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading comments", e);
                comments.add(new ArrayList<>());
            }
        }
    }

    private void saveItems(int mode) {
        try {
            FileUtils.writeLines(getDataFile(mode), items.get(mode));
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    private void saveComments(int mode) {
        try {
            FileUtils.writeLines(getCommentsFile(mode), comments.get(mode));
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing comments", e);
        }
    }

    private void removeItem(int position, int mode) {
        String itemRemoved = items.get(mode).remove(position);
        String commentRemoved = comments.get(mode).remove(position);
        itemsAdapter.get(mode).notifyItemRemoved(position);
        Toast.makeText(getApplicationContext(),"Removed.",Toast.LENGTH_SHORT).show();
        saveItems(mode);
        saveComments(mode);
        Timer timer = new Timer();
        fabReturn.setVisibility(View.VISIBLE);
        fabReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.get(mode).add(position,itemRemoved);
                comments.get(mode).add(position,commentRemoved);
                itemsAdapter.get(mode).notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Deletion cancelled.", Toast.LENGTH_SHORT).show();
                saveItems(mode);
                saveComments(mode);
                fabReturn.setVisibility(View.INVISIBLE);
            }
        });
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fabReturn.setVisibility(View.INVISIBLE);
            }
        };
        timer.schedule(task,2000);
    }


}