/*
 This file is part of CHIMERA: Companion for Humans Intending to
 Master Extreme Role Adventures ("CHIMERA").

 CHIMERA is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 CHIMERA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with CHIMERA.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aganchiran.chimera.chimerafront.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aganchiran.chimera.R;
import com.aganchiran.chimera.chimeracore.campaign.CampaignModel;
import com.aganchiran.chimera.chimeracore.character.CharacterModel;
import com.aganchiran.chimera.viewmodels.CreateEditCharacterVM;

public class CreateEditCharacterActivity extends AppCompatActivity {

    private CreateEditCharacterVM createEditCharacterVM;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CharacterModel characterModel;
    private int campaignId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_create_edit_character);
        createEditCharacterVM = ViewModelProviders.of(this).get(CreateEditCharacterVM.class);

        nameEditText = findViewById(R.id.name_value);
        descriptionEditText = findViewById(R.id.description_value);

        characterModel = (CharacterModel) getIntent().getSerializableExtra("CHARACTER");
        if (characterModel != null) {
            nameEditText.setText(characterModel.getName());
            descriptionEditText.setText(characterModel.getDescription());
            campaignId = characterModel.getCampaignId();
        } else {
            CampaignModel campaign = (CampaignModel) getIntent().getSerializableExtra("CAMPAIGN");
            campaignId = campaign.getId();
        }

        super.onCreate(savedInstanceState);
    }

    public void saveCharacter(View view) {
        if (characterModel == null) {
            createCharacter();
        } else {
            updateCharacter(characterModel);
        }
    }

    private void updateCharacter(CharacterModel characterModel) {
        final String name = nameEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        characterModel.setName(name);
        characterModel.setDescription(description);
        createEditCharacterVM.update(characterModel);
        finish();
    }

    private void createCharacter() {
        final String name = nameEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        CharacterModel characterModel = new CharacterModel(name, description, campaignId);
        createEditCharacterVM.insert(characterModel);
        finish();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
