package com.aganchiran.chimera.chimerafront.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aganchiran.chimera.R;
import com.aganchiran.chimera.chimeracore.character.CharacterModel;
import com.aganchiran.chimera.chimerafront.dialogs.CreateEditCharacterDialog;
import com.aganchiran.chimera.chimerafront.utils.CharacterAdapter;
import com.aganchiran.chimera.chimerafront.utils.DragItemListener;
import com.aganchiran.chimera.chimerafront.utils.DropToDeleteListener;
import com.aganchiran.chimera.chimerafront.utils.SizeUtil;
import com.aganchiran.chimera.viewmodels.CharacterListVM;

import java.io.Serializable;
import java.util.List;

public class CharacterListActivity extends ActivityWithUpperBar {

    private static final int NONE = 0;
    private static final int DELETE = 1;
    private static final int SEND = 2;


    private static final LinearLayout.LayoutParams VISIBLE = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    private static final LinearLayout.LayoutParams INVISIBLE = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0);

    private FloatingActionButton addCharacterButton;
    private CharacterListVM characterListVM;
    private CharacterAdapter adapter;

    private int selectionMode = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_character_list);
        characterListVM = ViewModelProviders.of(this).get(CharacterListVM.class);

        final RecyclerView recyclerView = findViewById(R.id.character_recycler_view);
        setupGrid(characterListVM.getAllCharacters(), recyclerView);

        setupButtons();

        final ImageView deleteArea = findViewById(R.id.delete_area);
        deleteArea.setOnDragListener(new DropToDeleteListener(adapter, characterListVM));

        if (getIntent().getBooleanExtra("SELECTION_SCREEN", false)) {
            selectionMode = SEND;
            enableSelectMode();
        }

        super.onCreate(savedInstanceState);
    }

    private void setupGrid(LiveData<List<CharacterModel>> data, RecyclerView recyclerView) {
        final View characterCard = getLayoutInflater().inflate(R.layout.item_character, null);
        final View characterLayout = characterCard.findViewById(R.id.character_item_layout);
        int characterWidth = SizeUtil.getViewWidth(characterLayout);
        int screenWidth = SizeUtil.getScreenWidth(CharacterListActivity.this);
        int columnNumber = screenWidth / characterWidth;

        recyclerView.setLayoutManager(
                new GridLayoutManager(CharacterListActivity.this, columnNumber));
        recyclerView.setHasFixedSize(true);

        adapter = new CharacterAdapter();
        adapter.setListener(new CharacterAdapter.OnItemClickListener<CharacterModel>() {
            @Override
            public void onItemClick(CharacterModel characterModel) {
                Intent intent = new Intent(CharacterListActivity.this,
                        CharacterProfileActivity.class);
                intent.putExtra("CHARACTER", characterModel);
                startActivity(intent);
            }
        });
        adapter.setEditCharacter(new CharacterAdapter.EditCharacter() {
            @Override
            public void perform(final CharacterModel character) {
                CreateEditCharacterDialog dialog = new CreateEditCharacterDialog();
                dialog.setListener(new CreateEditCharacterDialog.CreateCharacterDialogListener() {
                    @Override
                    public void saveCharacter(String newName, String newDescription) {
                        character.setName(newName);
                        character.setDescription(newDescription);

                        characterListVM.update(character);
                    }

                    @Override
                    public CharacterModel getCharacter() {
                        return character;
                    }
                });
                assert getFragmentManager() != null;
                dialog.show(getSupportFragmentManager(), "edit character");
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setOnDragListener(new DragItemListener(adapter) {
            @Override
            protected void onDrop(View hiddenView) {
                super.onDrop(hiddenView);
                new ReorderCharacterAsyncTask(adapter, characterListVM).execute();
            }
        });

        data.observe(this, new Observer<List<CharacterModel>>() {
            @Override
            public void onChanged(@Nullable List<CharacterModel> characterModels) {
                adapter.setItemModels(characterModels);
            }
        });
    }

    private void setupButtons() {
        addCharacterButton = findViewById(R.id.add_character_button);
        addCharacterButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        ((FloatingActionButton) v).hide();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        ((FloatingActionButton) v).show();
                        break;
                }
                return true;
            }
        });
        addCharacterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEditCharacterDialog dialog = new CreateEditCharacterDialog();
                dialog.setListener(new CreateEditCharacterDialog.CreateCharacterDialogListener() {
                    @Override
                    public void saveCharacter(String name, String description) {
                        CharacterModel characterModel = new CharacterModel(name, description);
                        characterListVM.insert(characterModel);
                    }

                    @Override
                    public CharacterModel getCharacter() {
                        return null;
                    }
                });
                assert getFragmentManager() != null;
                dialog.show(getSupportFragmentManager(), "create character");
            }
        });

        Button acceptDeletion = findViewById(R.id.accept_button);
        acceptDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectionMode) {
                    case DELETE:
                        deleteSelectedCharacters();
                        break;
                    case SEND:
                        sendSelectedCharacters();
                        break;
                }
            }
        });

        Button cancelDeletion = findViewById(R.id.cancel_button);
        cancelDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectionMode) {
                    case SEND:
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                    default:
                        cancelCharacterSelection();
                }
            }
        });
    }

    private void cancelCharacterSelection() {
        selectionMode = NONE;
        adapter.disableSelectMode();
        findViewById(R.id.selection_interface).setLayoutParams(INVISIBLE);
        addCharacterButton.show();
    }

    private void deleteSelectedCharacters() {
        for (CharacterModel characterModel : adapter.getCheckedItemModels()) {
            characterListVM.delete(characterModel);
        }
        cancelCharacterSelection();
    }

    private void sendSelectedCharacters() {
        Intent intent = new Intent();
        List<CharacterModel> characters = adapter.getCheckedItemModels();
        intent.putExtra("CHARACTERS", (Serializable) characters);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_character_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_characters:
                if (!adapter.getSelectModeEnabled()) {
                    selectionMode = DELETE;
                    enableSelectMode();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableSelectMode() {
        adapter.enableSelectMode();
        findViewById(R.id.selection_interface).setLayoutParams(VISIBLE);
        addCharacterButton.hide();
    }

    private static class ReorderCharacterAsyncTask extends AsyncTask<Void, Void, Void> {
        private CharacterAdapter adapter;
        private CharacterListVM characterListVM;

        private ReorderCharacterAsyncTask(CharacterAdapter adapter, CharacterListVM characterListVM) {
            this.adapter = adapter;
            this.characterListVM = characterListVM;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                CharacterModel characterModel = adapter.getItemAt(i);
                characterModel.setDisplayPosition(i);
            }
            characterListVM.updateCharacters(adapter.getItemModels());
            return null;
        }
    }

}
