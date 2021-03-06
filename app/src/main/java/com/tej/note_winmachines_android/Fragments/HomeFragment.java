package com.tej.note_winmachines_android.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.tej.note_winmachines_android.Activities.MapsActivity;
import com.tej.note_winmachines_android.Activities.Category;
import com.tej.note_winmachines_android.Adapters.NotesAdapter;
import com.tej.note_winmachines_android.Adapters.onNoteClicked;
import com.tej.note_winmachines_android.DataLayer.DBAccess;
import com.tej.note_winmachines_android.Model.Note;
import com.tej.note_winmachines_android.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements onNoteClicked {
    Button btnAdd, btnMap, btnSubjects;
    TextView txtTitle;
    ImageView imgSearch;
    ImageView imgCross;
    RecyclerView notesRecycler;
    NotesAdapter adapter;
    SearchView etsearch;
    Dialog dialog;

    ArrayList<Note> notesObj = new ArrayList<Note>();

    private static final int noteid = 0;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnSubjects = rootView.findViewById(R.id.btnSubjects);
        btnMap = rootView.findViewById(R.id.btnmap);
        imgSearch = rootView.findViewById(R.id.rightBarButton);
        imgCross = rootView.findViewById(R.id.leftBarButton);
        txtTitle = rootView.findViewById(R.id.toolTitle);
        notesRecycler = rootView.findViewById(R.id.notesRecycler);
        etsearch = rootView.findViewById(R.id.etsearch);
        txtTitle.setText(R.string.notes);

        etsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                adapter.getFilter().filter(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryString) {
                adapter.getFilter().filter(queryString);
                return false;
            }
        });


        imgSearch.setOnClickListener(v -> {
            // Toast.makeText(getContext(),"Search",Toast.LENGTH_SHORT).show();
            etsearch.setVisibility(View.VISIBLE);
        });
        btnSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Category.class);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("allNotes", "yes");
            startActivity(intent);
        });

        adapter = new NotesAdapter(getContext(), DBAccess.fetchNotes(), this);

        notesRecycler.setAdapter(adapter);
        notesRecycler.setLayoutManager((new LinearLayoutManager(this.getContext())));
        // Inflate the layout for this fragment
        return rootView;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAdd.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.toNoteDetail));
    }

    @Override
    public void onClickItem(View view, int item) {

        Bundle bundle = new Bundle();
        bundle.putString("item", "" + item);
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.toNoteDetail, bundle);

    }

    @Override
    public void onLongClickItem(View view, int item) {
        mapDialog(adapter.getData().get(item), item);
    }

    Note note;

    public void mapDialog(Note note, int pos) {
        note = adapter.data.get(pos);
        dialog = new Dialog((requireContext()));
        dialog.setContentView(R.layout.maponlong_layout);
        Button btnmap = dialog.findViewById(R.id.btn_navigate);
        Button btnMove = dialog.findViewById(R.id.btnMove);
        ImageView img_move_delete = dialog.findViewById(R.id.img_move_delete);

        btnmap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("pos", pos);
            startActivity(intent);
            dialog.dismiss();
        });
        btnMove.setOnClickListener(v -> {
            startActivityForResult(new Intent(requireContext(), Category.class).putExtra("from", "addNote"), 200);

            dialog.dismiss();
        });
        img_move_delete.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            DBAccess.updateNote(note.getNote_id(),data.getLongExtra("selectedSubjectId", -1L));
            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!!")
                    .setContentText("You successfully moved a note.")
                    .showCancelButton(true)
                    .setConfirmText("Yes")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .show();
        }
    }
}