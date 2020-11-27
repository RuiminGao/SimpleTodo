package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    List<String> comments;

    Button btnAdd;
    FloatingActionButton fabReturn;
    EditText etNewTask;
    RecyclerView rvTasks;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etNewTask = findViewById(R.id.etNewTask);
        rvTasks = findViewById(R.id.rvTasks);
        fabReturn = findViewById(R.id.fabReturn);

        loadItems();
        loadComments();

        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                String itemRemoved = items.remove(position);
                String commentRemoved = comments.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Removed.",Toast.LENGTH_SHORT).show();
                saveItems();
                saveComments();
                Timer timer = new Timer();
                fabReturn.setVisibility(View.VISIBLE);
                fabReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        items.add(position,itemRemoved);
                        comments.add(position,commentRemoved);
                        itemsAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Deletion cancelled.", Toast.LENGTH_SHORT).show();
                        saveItems();
                        saveComments();
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
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_COMMENT, comments.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items, comments, onLongClickListener, onClickListener);
        rvTasks.setAdapter(itemsAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etNewTask.getText().toString();
                if(todoItem.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your item.", Toast.LENGTH_SHORT).show();
                } else {
                    items.add(todoItem);
                    comments.add("");
                    itemsAdapter.notifyItemInserted(items.size() - 1);
                    etNewTask.setText("");
                    Toast.makeText(getApplicationContext(), "Added.", Toast.LENGTH_SHORT).show();
                    saveItems();
                    saveComments();
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
            items.set(position, itemText);
            comments.set(position, itemComment);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            saveComments();
            Toast.makeText(getApplicationContext(), "Updated.", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private File getCommentsFile() {
        return new File(getFilesDir(), "comments.txt");
    }

    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    private  void loadComments() {
        try {
            comments = new ArrayList<>(FileUtils.readLines(getCommentsFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading comments", e);
            comments = new ArrayList<>();
        }
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    private void saveComments() {
        try {
            FileUtils.writeLines(getCommentsFile(), comments);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing comments", e);
        }
    }
}