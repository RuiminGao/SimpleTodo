package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    EditText etComment;
    EditText etEdit;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etEdit = findViewById(R.id.etEdit);
        etComment = findViewById(R.id.etComment);
        btnSave = findViewById(R.id.btnSave);

        getSupportActionBar().setTitle("Edit item");

        etEdit.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        etComment.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_COMMENT));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, etEdit.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_COMMENT, etComment.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                setResult(RESULT_OK, intent);
                finish();
            }
        });



        Switch swEditMode = (Switch)findViewById(R.id.swEditMode);
        KeyListener commentsKeyListener = etComment.getKeyListener();
        KeyListener editKeyListener = etEdit.getKeyListener();
        etComment.setKeyListener(null);
        etEdit.setKeyListener(null);

        swEditMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"Edit mode on.",Toast.LENGTH_SHORT).show();
                    etComment.setKeyListener(commentsKeyListener);
                    etEdit.setKeyListener(editKeyListener);
                }else{
                    Toast.makeText(getApplicationContext(),"Edit mode off.",Toast.LENGTH_SHORT).show();
                    etComment.setKeyListener(null);
                    etEdit.setKeyListener(null);
                }
            }
        });
    }

}