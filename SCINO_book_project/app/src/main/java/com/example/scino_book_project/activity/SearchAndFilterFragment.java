package com.example.scino_book_project.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scino_book_project.R;
import com.example.scino_book_project.adapter.BookItem;
import com.example.scino_book_project.adapter.SimpleBookItem;
import com.example.scino_book_project.helper.DataBaseHelper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class SearchAndFilterFragment extends Fragment implements AdapterView.OnItemLongClickListener {


    public SearchAndFilterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Spinner nameList, nameCategory;
    private ArrayList<SimpleBookItem> mBookList;

    private MyCustomAdapter  dataCheckedAdapter;
    private MySimpleCustomAdapter dataAdapter;

    private ListView listView;
    private LinearLayout linear;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_and_filter, container, false);
        listView=(ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemLongClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkLikeReaded(parent, position);
                //startItem(parent, view, position, id);

            }
        });

        nameList = (Spinner) rootView.findViewById(R.id.spinnerList);

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
        data.add(0,getString(R.string.without_list));
        data.add(0, getString(R.string.no_used));

        ArrayAdapter<String> adapterList = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, data);
        adapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        nameList.setAdapter(adapterList);
        nameList.setPrompt("Title");
        nameList.setSelection(0);

        nameList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), getString(R.string.choose_item) + " " + nameList.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        nameCategory = (Spinner) rootView.findViewById(R.id.spinnerReaded);

        data = new ArrayList<>();
        data.add(getString(R.string.no_used));
        data.add(getString(R.string.readed));
        data.add(getString(R.string.unreaded));
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, data);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        nameCategory.setAdapter(adapterCat);
        // заголовок
        nameCategory.setPrompt("Title");
        // выделяем элемент
        nameCategory.setSelection(0);


        Button button = (Button)rootView.findViewById(R.id.buttonSearch);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText b = (EditText)rootView.findViewById(R.id.editTextNameBook);
                String bookName = b.getText().toString();
                String listName = nameList.getSelectedItem().toString();
                String categoryName = nameCategory.getSelectedItem().toString();

                if ((bookName.isEmpty() || bookName.charAt(0)==' ' )&& categoryName.equals(getString(R.string.no_used)) && !listName.equals(getString(R.string.no_used))){

                    mBookList = new ArrayList<>();

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


                    ArrayList<ArrayList<String>> data= myDbHelper.getBookList(listName);

                    myDbHelper.close();


                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }

                else if((bookName.isEmpty()  || bookName.charAt(0)==' ') && listName.equals(getString(R.string.no_used)) && !categoryName.equals(getString(R.string.no_used))){
                    /////только по категории
                    mBookList = new ArrayList<>();

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


                    ArrayList<ArrayList<String>> data;
                    if (categoryName.equals(getString(R.string.readed))){
                        data = myDbHelper.getReadedBookList(true);
                    }
                    else if(categoryName.equals(getString(R.string.unreaded))){
                        data = myDbHelper.getReadedBookList(false);
                    }
                    else {
                        ArrayList<String> s = new ArrayList<>();
                        s.add(getString(R.string.empty_list));
                        s.add("fff");
                        s.add("fff");
                        data = new ArrayList<>();
                        data.add(s);

                    }

                    myDbHelper.close();

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(categoryName.equals(getString(R.string.no_used)) &&  listName.equals(getString(R.string.no_used)) && !(bookName.isEmpty()  || bookName.charAt(0)==' ')){
                    /////только по имени книги

                    mBookList = new ArrayList<>();

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


                    ArrayList<ArrayList<String>> data = myDbHelper.getBookByName(bookName);


                    myDbHelper.close();

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    b.setText("");
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(!listName.equals(getString(R.string.no_used)) && !categoryName.equals(getString(R.string.no_used)) && (bookName.isEmpty()  || bookName.charAt(0)==' ')){
                    ///по списку и категории
                    mBookList = new ArrayList<>();

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


                    ArrayList<ArrayList<String>> dataFromList= myDbHelper.getBookList(listName);

                    ArrayList<ArrayList<String>> dataFromCategory;
                    if (categoryName.equals(getString(R.string.readed))){
                        dataFromCategory = myDbHelper.getReadedBookList(true);
                    }
                    else if(categoryName.equals(getString(R.string.unreaded))){
                        dataFromCategory = myDbHelper.getReadedBookList(false);
                    }
                    else {
                        ArrayList<String> s = new ArrayList<>();
                        s.add(getString(R.string.empty_list));
                        s.add("fff");
                        s.add("fff");
                        dataFromCategory = new ArrayList<>();
                        dataFromCategory.add(s);

                    }

                    myDbHelper.close();
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    for(int i =0; i< dataFromCategory.size();i++)
                    {
                        if(dataFromList.contains(dataFromCategory.get(i)))
                            data.add(dataFromCategory.get(i));
                    }


                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(!listName.equals(getString(R.string.no_used)) && categoryName.equals(getString(R.string.no_used)) && !(bookName.isEmpty()  || bookName.charAt(0)==' ')){
                    ////по списку и книге

                    mBookList = new ArrayList<>();

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


                    ArrayList<ArrayList<String>> dataFromList= myDbHelper.getBookList(listName);

                    ArrayList<ArrayList<String>> dataFromBook= myDbHelper.getBookByName(bookName);

                    myDbHelper.close();
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    for(int i =0; i< dataFromBook.size();i++)
                    {
                        if(dataFromList.contains(dataFromBook.get(i)))
                            data.add(dataFromBook.get(i));
                    }

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    b.setText("");
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(listName.equals(getString(R.string.no_used)) && !categoryName.equals(getString(R.string.no_used)) && !(bookName.isEmpty()  || bookName.charAt(0)==' ')){
                    ///по категории и книге
                    mBookList = new ArrayList<>();

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

                    ArrayList<ArrayList<String>> dataFromCategory;
                    if (categoryName.equals(getString(R.string.readed))){
                        dataFromCategory = myDbHelper.getReadedBookList(true);
                    }
                    else if(categoryName.equals(getString(R.string.unreaded))){
                        dataFromCategory = myDbHelper.getReadedBookList(false);
                    }
                    else {
                        ArrayList<String> s = new ArrayList<>();
                        s.add(getString(R.string.empty_list));
                        s.add("fff");
                        s.add("fff");
                        dataFromCategory = new ArrayList<>();
                        dataFromCategory.add(s);

                    }

                    ArrayList<ArrayList<String>> dataFromBook= myDbHelper.getBookByName(bookName);

                    myDbHelper.close();

                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    for(int i =0; i< dataFromBook.size();i++)
                    {
                        if(dataFromCategory.contains(dataFromBook.get(i))) {
                            data.add(dataFromBook.get(i));
                        }
                    }

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    b.setText("");
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(!listName.equals(getString(R.string.no_used)) && !categoryName.equals(getString(R.string.no_used)) && !(bookName.isEmpty()  || bookName.charAt(0)==' ')){
                    ////по списку, и категории, и по книге
                    mBookList = new ArrayList<>();

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
                    String cat;

                    if(categoryName.equals(getString(R.string.readed)))cat = "yes";
                    else if(categoryName.equals(getString(R.string.readed)))cat = "no";
                    else cat = "no";

                    ArrayList<ArrayList<String>> data = myDbHelper.getByBookAndListAndCategory(bookName, listName, cat);
                    myDbHelper.close();

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    b.setText("");
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }
                else if(listName.equals(getString(R.string.no_used)) && categoryName.equals(getString(R.string.no_used)) && (bookName.isEmpty()  || bookName.charAt(0)==' ')) {
                   ////ничего не выбрано
                    mBookList = new ArrayList<>();



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


                    ArrayList<ArrayList<String>> dataReaded = myDbHelper.getReadedBookList(true);
                    ArrayList<ArrayList<String>> dataUnreaded =myDbHelper.getReadedBookList(false);
                    ArrayList<ArrayList<String>> data = new  ArrayList<>();
                    data.addAll(dataReaded);
                    data.addAll(dataUnreaded);
                    myDbHelper.close();

                    for (int i = 0 ; i <data.size(); i++){
                        SimpleBookItem bookItem;
                        if (data.get(i).get(2).equals("no"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        else if(data.get(i).get(2).equals("yes"))
                            bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.readable);
                        else bookItem = new SimpleBookItem(data.get(i).get(0),data.get(i).get(1), R.drawable.unreadable);
                        mBookList.add(bookItem);
                    }
                    dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                    listView.setAdapter(dataAdapter);
                    if (dataAdapter.isEmpty())
                        Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
                }

            }
        });

        return rootView;
    }
    private void checkLikeReaded(AdapterView<?> parent, int position) {
        final SimpleBookItem selectedItem = (SimpleBookItem) parent.getItemAtPosition(position);
        final String bookItem = selectedItem.getBookName();
        final String authorItem = selectedItem.getAuthorName();
        final Object iconItem = selectedItem.getPicture();

        DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw new Error("Unable to open database");
        }

        int swch = -2;
        if (iconItem.equals(R.drawable.unreadable)) {
            swch = myDbHelper.switchReadable(bookItem, authorItem, getString(R.string.without_list), true);
            final String TAG = getActivity().toString();
            Log.d(TAG, " this is  unreadble");
        } else if (iconItem.equals(R.drawable.readable)) {
            swch = myDbHelper.switchReadable(bookItem, authorItem, getString(R.string.without_list), false);
            final String TAG = getActivity().toString();
            Log.d(TAG, " this is  readble");
        }

        final String TAG = getActivity().toString();
        Log.d(TAG, " нет никакого адаптера");

        myDbHelper.close();



        mBookList.remove(selectedItem);
        SimpleBookItem hm;
        if (swch ==1)
            hm = new SimpleBookItem(bookItem, authorItem, R.drawable.readable);
        else if (swch==0)
            hm = new SimpleBookItem(bookItem, authorItem, R.drawable.unreadable);
        else hm = new SimpleBookItem(bookItem, authorItem, R.drawable.ic_action_search);
        mBookList.add(position, hm);

        dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
        listView.setAdapter(dataAdapter);

        Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();

        if(swch==1) Toast.makeText(getActivity(),getString(R.string.readed), Toast.LENGTH_SHORT).show();
        else if(swch==0) Toast.makeText(getActivity(),getString(R.string.unreaded), Toast.LENGTH_SHORT).show();
    }


    public boolean onItemLongClick(final AdapterView<?> parent,final View view,final int position,final long id) {

        startItem(parent, position);
        return true;
    }


    private void startItem(final AdapterView<?> parent,final int position){


        final String[] menuItems ={getString(R.string.delete_str),  getString(R.string.update_book),
                getString(R.string.update_author), getString(R.string.choise), getString(R.string.choose_all)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.menu_str)); // заголовок для диалога

        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if (item == 0) {
                    deleteItem(parent, position);
                } else if (item == 1) {
                    updateBook(parent,position);
                } else if (item == 2) {
                    updateAuthor(parent, position);
                } else if (item == 3) {
                    makeChoice(false);
                } else if (item == 4) {
                    makeChoice(true);
                }

            }
        });
        builder.setCancelable(false)
                .setNegativeButton("Cansel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create().show();
    }


    private void updateAuthor(AdapterView<?> parent, final int position) {
        final SimpleBookItem selectedItem = (SimpleBookItem) parent.getItemAtPosition(position);
        final String bookItem = selectedItem.getBookName();
        final String authorItem = selectedItem.getAuthorName();
        final Object iconItem = selectedItem.getPicture();


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.update_author));
        alertDialog.setMessage(getString(R.string.new_author_string));

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setPositiveButton(getString(R.string.yes_str),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthorName = input.getText().toString();

                        if (!newAuthorName.equals("") && !newAuthorName.isEmpty() && newAuthorName.charAt(0) != ' ') {
                            DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());
                            try {
                                myDbHelper.createDataBase();
                            } catch (IOException ioe) {
                                throw new Error("Unable to create database");
                            }

                            try {
                                myDbHelper.openDataBase();
                            } catch (SQLException sqle) {
                                throw new Error("Unable to open database");
                            }


                            myDbHelper.replaceAuthor(authorItem, newAuthorName);
                            myDbHelper.close();
                            mBookList.remove(selectedItem);
                            SimpleBookItem hm = new SimpleBookItem(bookItem, newAuthorName, iconItem);
                            mBookList.add(position, hm);
                            dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item,mBookList);
                            listView.setAdapter(dataAdapter);
                            Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        alertDialog.setNegativeButton(getString(R.string.no_str),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    private void updateBook(AdapterView<?> parent,final int position) {

        final SimpleBookItem selectedItem = (SimpleBookItem) parent.getItemAtPosition(position);
        final String bookItem = selectedItem.getBookName();
        final String authorItem = selectedItem.getAuthorName();
        final Object iconItem = selectedItem.getPicture();


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.update_book));
        alertDialog.setMessage(getString(R.string.new_book_string));

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setPositiveButton(getString(R.string.yes_str),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newBookName = input.getText().toString();

                        if (!newBookName.equals("") && !newBookName.isEmpty() && newBookName.charAt(0) != ' ') {
                            DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());
                            try {
                                myDbHelper.createDataBase();
                            } catch (IOException ioe) {
                                throw new Error("Unable to create database");
                            }

                            try {
                                myDbHelper.openDataBase();
                            } catch (SQLException sqle) {
                                throw new Error("Unable to open database");
                            }

                            myDbHelper.replaceBook(bookItem, newBookName);
                            myDbHelper.close();

                            mBookList.remove(selectedItem);
                            SimpleBookItem hm = new SimpleBookItem(newBookName, authorItem, iconItem);

                            mBookList.add(position, hm);

                            dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                            listView.setAdapter(dataAdapter);

                            Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        alertDialog.setNegativeButton(getString(R.string.no_str),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    private void deleteItem(AdapterView<?> parent, int position) {
        final SimpleBookItem selectedItem = (SimpleBookItem) parent.getItemAtPosition(position);
        final String bookItem = selectedItem.getBookName();


        AlertDialog.Builder quitDialog = new AlertDialog.Builder(getActivity());
        quitDialog.setTitle(getString(R.string.delete_str));

        quitDialog.setPositiveButton(getString(R.string.yes_str), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (bookItem.equals(getString(R.string.empty_list))) return;
                DataBaseHelper myDbHelper = new DataBaseHelper(getActivity());
                try {
                    myDbHelper.createDataBase();
                } catch (IOException ioe) {
                    throw new Error("Unable to create database");
                }

                try {
                    myDbHelper.openDataBase();
                } catch (SQLException sqle) {
                    throw new Error("Unable to open database");
                }

                if (!myDbHelper.deleteBook(bookItem)) {
                    return;
                }

                myDbHelper.deleteBook(bookItem);
                myDbHelper.close();

                mBookList.remove(selectedItem);
                dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                listView.setAdapter(dataAdapter);

                if (dataAdapter.isEmpty())
                    Toast.makeText(getActivity(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
            }
        });

        quitDialog.setNegativeButton(getString(R.string.no_str), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();


    }

    private void makeChoice( boolean swch){
        //mBookList = new ArrayList<>();

        ArrayList<BookItem> itemList = new ArrayList<>();
        for (int i =0; i <listView.getAdapter().getCount(); i++ ){
            SimpleBookItem simpleBookItem = (SimpleBookItem) listView.getAdapter().getItem(i);
            BookItem bookItem = new BookItem(simpleBookItem.getBookName(), simpleBookItem.getAuthorName(), swch, simpleBookItem.getPicture());
            itemList.add(bookItem);
        }

        dataCheckedAdapter = new MyCustomAdapter(getActivity(), R.layout.check_list_item, itemList);
        listView.setAdapter(dataCheckedAdapter);

        linear = (LinearLayout)rootView.findViewById(R.id.container);

        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin=15;
        Button button = new Button(getActivity());
        //button;
        button.setText(getString(R.string.delete_str));
        button.setLayoutParams(params);
        linear.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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


                ArrayList<BookItem> itemList = dataCheckedAdapter.itemList;
                for(int i=0;i<itemList.size();i++){
                    BookItem bookItem = itemList.get(i);
                    if(bookItem.isSelected()){
                        myDbHelper.deleteBook(bookItem.getBookName());
                        mBookList.remove(dataAdapter.itemList.get(i));
                    }
                }

                linear.removeAllViews();

                myDbHelper.close();

                dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                listView.setAdapter(dataAdapter);
            }
        });


        LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.MATCH_PARENT);
        params2.gravity = 16;
        Button button2 = new Button(getActivity());
        button2.setText("Прочитанно");
        button2.setLayoutParams(params2);
        linear.addView(button2);
        button2.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View v) {
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


                                           ArrayList<BookItem> itemList = dataCheckedAdapter.itemList;
                                           for(int i=0;i<itemList.size();i++){
                                               BookItem bookItem = itemList.get(i);
                                               if(bookItem.isSelected()){

                                                   myDbHelper.switchReadable(bookItem.getBookName(), bookItem.getAuthorName(), getString(R.string.without_list), true);
                                                   BookItem newBookItem = new BookItem(bookItem.getBookName(), bookItem.getAuthorName(), true, R.drawable.readable);
                                                   dataCheckedAdapter.itemList.remove(bookItem);
                                                   dataCheckedAdapter.itemList.add(i, newBookItem);
                                                   SimpleBookItem newSimpleBookItem = new SimpleBookItem(bookItem.getBookName(),bookItem.getAuthorName(),R.drawable.readable);
                                                   mBookList.remove(dataAdapter.getItem(i));
                                                   mBookList.add(i, newSimpleBookItem);
                                               }
                                           }


                                           linear.removeAllViews();

                                           myDbHelper.close();

                                           dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                                           listView.setAdapter(dataAdapter);


                                       }
                                   }
        );


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.MATCH_PARENT);
        params3.rightMargin = 0;
        Button button3 = new Button(getActivity());
        button3.setText("Непрочитанно");
        button3.setLayoutParams(params3);
        linear.addView(button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                ArrayList<BookItem> itemList = dataCheckedAdapter.itemList;
                for(int i=0;i<itemList.size();i++){
                    BookItem bookItem = itemList.get(i);
                    if(bookItem.isSelected()){

                        myDbHelper.switchReadable(bookItem.getBookName(), bookItem.getAuthorName(), getString(R.string.without_list), false);
                        BookItem newBookItem = new BookItem(bookItem.getBookName(), bookItem.getAuthorName(), true, R.drawable.unreadable);
                        dataCheckedAdapter.itemList.remove(bookItem);
                        dataCheckedAdapter.itemList.add(i, newBookItem);
                        SimpleBookItem newSimpleBookItem = new SimpleBookItem(bookItem.getBookName(),bookItem.getAuthorName(),R.drawable.unreadable);
                        mBookList.remove(dataAdapter.getItem(i));
                        mBookList.add(i, newSimpleBookItem);
                    }
                }

                linear.removeAllViews();
                myDbHelper.close();

                dataAdapter = new MySimpleCustomAdapter(getActivity(), R.layout.list_item, mBookList);
                listView.setAdapter(dataAdapter);
            }
        });
    }


    private class MyCustomAdapter extends ArrayAdapter<BookItem> {

        private ArrayList<BookItem> itemList;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<BookItem> itemList) {
            super(context, textViewResourceId, itemList);
            this.itemList = new ArrayList<>();
            this.itemList.addAll(itemList);
        }

        private class ViewHolder {
            TextView text1;
            TextView text2;
            CheckBox checkFeed;
            ImageView img;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.check_list_item, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);
                holder.checkFeed = (CheckBox)convertView.findViewById(R.id.checkFeed);
                holder.img = (ImageView)convertView.findViewById(R.id.img);

                convertView.setTag(holder);

                holder.checkFeed.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        BookItem bookItem = (BookItem) cb.getTag();
                        Toast.makeText(getActivity(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        bookItem.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            BookItem bookItem = itemList.get(position);

            holder.text1.setText(bookItem.getBookName());
            holder.text2.setText(bookItem.getAuthorName());
            holder.checkFeed.setChecked(bookItem.isSelected());
            holder.checkFeed.setTag(bookItem);
            holder.img.setImageResource((int) bookItem.getPicture());

            return convertView;
        }
    }


    private class MySimpleCustomAdapter extends ArrayAdapter<SimpleBookItem> {

        private ArrayList<SimpleBookItem> itemList;

        public MySimpleCustomAdapter(Context context, int textViewResourceId, ArrayList<SimpleBookItem> itemList) {
            super(context, textViewResourceId, itemList);
            this.itemList = new ArrayList<>();
            this.itemList.addAll(itemList);

        }

        private class ViewHolder {
            TextView text1;
            TextView text2;
            ImageView img;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);
                holder.img = (ImageView) convertView.findViewById(R.id.img);

                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SimpleBookItem bookItem = itemList.get(position);

            holder.text1.setText(bookItem.getBookName());
            holder.text2.setText(bookItem.getAuthorName());
            holder.img.setImageResource((int) bookItem.getPicture());

            return convertView;
        }

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
