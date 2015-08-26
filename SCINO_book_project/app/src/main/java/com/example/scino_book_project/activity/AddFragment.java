package com.example.scino_book_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.scino_book_project.R;
import com.example.scino_book_project.helper.DataBaseHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class AddFragment extends Fragment {

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private EditText nameBook;
    private EditText nameAuthor;
    private Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        nameBook = (EditText) rootView.findViewById(R.id.editTextNameBook);
        nameAuthor = (EditText) rootView.findViewById(R.id.editTextnameAuthor);

        DataBaseHelper myDbHelper  = new DataBaseHelper(getActivity());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw new Error("Unable to open database");
        }

        ArrayList<String> data = new ArrayList<>();
        if(myDbHelper.getCount()>0) {
            data.addAll(myDbHelper.getLists());
        }

        myDbHelper.close();
        //"Без списка"
        data.add(0,getString(R.string.without_list));

        //if(data.size()>2){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            spinner.setAdapter(adapter);
            // заголовок
            spinner.setPrompt("Title");
            // выделяем элемент
            spinner.setSelection(0);
            // устанавливаем обработчик нажатия
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // показываем позиция нажатого элемента
                    Toast.makeText(getActivity(), /*"Выбран пункт "*/ getString(R.string.choose_item) + " " + spinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });



        Button button = (Button) rootView.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String bookName =nameBook.getText().toString();
                String authorName = nameAuthor.getText().toString();
                String listName = spinner.getSelectedItem().toString();
                if (bookName.isEmpty())// || bookName.charAt(0)==' ' || authorName.isEmpty() || authorName.charAt(0) == ' ')
                {
                    Toast.makeText(getActivity(), getString(R.string.field) + " " +getString(R.string.name_book)+" "+ getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( bookName.charAt(0)==' ' || bookName.charAt(0)=='\n' || bookName.charAt(0)=='\t') {
                    Toast.makeText(getActivity(), getString(R.string.field) + " " +getString(R.string.name_book)+" " + getString(R.string.incorrect_full), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( authorName.isEmpty()) {
                    Toast.makeText(getActivity(),getString(R.string.field) + " " +getString(R.string.name_author)+" "+ getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( authorName.charAt(0)==' ' || authorName.charAt(0)=='\n' || authorName.charAt(0)=='\t') {
                    Toast.makeText(getActivity(), getString(R.string.field) + " " +getString(R.string.name_author)+" " + getString(R.string.incorrect_full), Toast.LENGTH_SHORT).show();
                    return;
                }

                DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());


                try {
                    myDbHelper.createDataBase();
                } catch (IOException ioe) {
                    throw new Error("Unable to create database");
                }

                try {
                    myDbHelper.openDataBase();
                }catch(SQLException sqle){
                    throw new Error("Unable to open database");
                }

                if (myDbHelper.addBookAuthorList(bookName, authorName,  listName)==1){
                    myDbHelper.addBookAuthorList(bookName, authorName, listName);
                    nameBook.setText("");
                    nameAuthor.setText("");
                    Toast.makeText(getActivity(), getString(R.string.successful) , Toast.LENGTH_SHORT).show();
                }
                else if(myDbHelper.addBookAuthorList(bookName, authorName,  listName)==0){
                    Toast.makeText(getActivity(), getString(R.string.exist) , Toast.LENGTH_SHORT).show();
                }

                myDbHelper.close();

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
