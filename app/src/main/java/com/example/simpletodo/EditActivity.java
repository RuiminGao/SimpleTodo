package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    EditText etComment;
    EditText etEdit;
    Button btnSave;
    Spinner spYear;
    Spinner spMonth;
    Spinner spDay;

    ArrayList<String> years, months, days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etEdit = findViewById(R.id.etEdit);
        etComment = findViewById(R.id.etComment);
        btnSave = findViewById(R.id.btnSave);


        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        spYear = findViewById(R.id.spYear);
        spMonth = findViewById(R.id.spMonth);
        spDay = findViewById(R.id.spDay);

        years = new ArrayList<>();
        for(int yearAdd = 0; yearAdd <= 5; yearAdd ++) {
            years.add(String.valueOf(currentYear + yearAdd));
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(adapterYear);

        months = new ArrayList<>();
        months.add("Jan.");
        months.add("Feb.");
        months.add("Mar.");
        months.add("Apr.");
        months.add("May");
        months.add("Jun.");
        months.add("Jul.");
        months.add("Aug.");
        months.add("Sept.");
        months.add("Oct.");
        months.add("Nov.");
        months.add("Dec.");
        ArrayAdapter<String> adapterMonth;
        adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(adapterMonth);

        days = new ArrayList<>();
        for(int day = 1; day <= 31; day ++) {
            days.add(String.valueOf(day));
        }
        ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, days);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adapterDay);

       // getSupportActionBar().setTitle("Edit item");

        etEdit.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        etComment.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_COMMENT));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, etEdit.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_COMMENT, etComment.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                intent.putExtra(MainActivity.KEY_ITEM_MODE, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_MODE));
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