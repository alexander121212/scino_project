package com.example.scino_book_project.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scino_book_project.R;
import com.example.scino_book_project.helper.DataBaseHelper;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class CreateListFragment extends android.support.v4.app.Fragment {

    public CreateListFragment() {
        // Required empty public constructor
    }
    private EditText nameList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_list, container, false);



        nameList = (EditText) rootView.findViewById(R.id.editTextNameJanr);


        Button addListButton = (Button) rootView.findViewById(R.id.listBooksButton);
        addListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String listN = nameList.getText().toString();

                if(listN.isEmpty()){// || listN.charAt(0)==' ') {
                    Toast.makeText(getActivity(), getString(R.string.field) + " " + getString(R.string.name_list) + " " + getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    return;}
                else if(listN.charAt(0)==' '|| listN.charAt(0)=='\n' || listN.charAt(0)=='\t'){// || listN.charAt(0)==' ') {
                    Toast.makeText(getActivity(), getString(R.string.field) + " " +getString(R.string.name_list)+" " + getString(R.string.incorrect_full), Toast.LENGTH_SHORT).show();
                    return;}

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

                if(myDbHelper.addList(listN)==1){
                    nameList.setText("");
                    Toast.makeText(getActivity(), getString(R.string.successful) , Toast.LENGTH_SHORT).show();
                }
                else if(myDbHelper.addList(listN)==0){
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