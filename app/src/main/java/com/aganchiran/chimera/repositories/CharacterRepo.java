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

package com.aganchiran.chimera.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.aganchiran.chimera.chimeracore.character.CharacterDAO;
import com.aganchiran.chimera.chimeracore.character.CharacterModel;
import com.aganchiran.chimera.chimeradata.ChimeraDB;

import java.util.Arrays;
import java.util.List;


public class CharacterRepo {

    private CharacterDAO characterDAO;
    private LiveData<List<CharacterModel>> allCharacters;
    private OnInsertListener onInsertListener;

    public CharacterRepo(Application application) {
        ChimeraDB database = ChimeraDB.getInstance(application);
        characterDAO = database.characterDAO();
        allCharacters = characterDAO.getAllCharacters();
    }

    public void insert(CharacterModel characterModel) {
        new InsertCharacterAsyncTask(characterDAO, onInsertListener).execute(characterModel);
    }

    public void update(CharacterModel characterModel) {
        new UpdateCharacterAsyncTask(characterDAO).execute(characterModel);

    }

    public void updateCharacters(List<CharacterModel> characterModelList) {
        CharacterModel[] characterArray = new CharacterModel[characterModelList.size()];
        characterArray = characterModelList.toArray(characterArray);
        new UpdateCharacterListAsyncTask(characterDAO).execute(characterArray);
    }

    public void delete(CharacterModel characterModel) {
        new DeleteCharacterAsyncTask(characterDAO).execute(characterModel);

    }

    public void deleteAllCharacters() {
        new DeleteAllCharacterAsyncTask(characterDAO).execute();

    }

    public LiveData<CharacterModel> getCharacterById(int id) {
        return characterDAO.getCharacterById(id);
    }

    public LiveData<List<CharacterModel>> getAllCharacters() {
        return allCharacters;
    }

    public LiveData<List<CharacterModel>> getCampaignCharacters(int campaignId) {
        return characterDAO.getCampaignCharacters(campaignId);
    }

    public LiveData<List<CharacterModel>> getCharactersWithoutCampaign() {
        return characterDAO.getCharactersWithoutCampaign();
    }

    /////////////////////////
    //////////TASKS//////////
    /////////////////////////

    private static class InsertCharacterAsyncTask extends AsyncTask<CharacterModel, Void, Long> {

        private CharacterDAO characterDAO;
        private OnInsertListener onInsertListener;

        private InsertCharacterAsyncTask(CharacterDAO characterDAO, OnInsertListener onInsertListener) {
            this.characterDAO = characterDAO;
            this.onInsertListener = onInsertListener;
        }

        @Override
        protected Long doInBackground(CharacterModel... characterModels) {
            return characterDAO.insert(characterModels[0]);
        }

        @Override
        protected void onPostExecute(Long characterId) {
            if (onInsertListener != null) {
                onInsertListener.onInsert(characterId);
            }
        }
    }

    private static class UpdateCharacterAsyncTask extends AsyncTask<CharacterModel, Void, Void> {

        private CharacterDAO characterDAO;

        private UpdateCharacterAsyncTask(CharacterDAO characterDAO) {
            this.characterDAO = characterDAO;
        }

        @Override
        protected Void doInBackground(CharacterModel... characterModels) {
            characterDAO.update(characterModels[0]);
            return null;
        }
    }

    private static class UpdateCharacterListAsyncTask extends AsyncTask<CharacterModel, Void, Void> {

        private CharacterDAO characterDAO;

        private UpdateCharacterListAsyncTask(CharacterDAO characterDAO) {
            this.characterDAO = characterDAO;
        }


        @Override
        protected final Void doInBackground(CharacterModel... characterModels) {
            characterDAO.updateCharacters(Arrays.asList(characterModels));
            return null;
        }
    }

    private static class DeleteCharacterAsyncTask extends AsyncTask<CharacterModel, Void, Void> {

        private CharacterDAO characterDAO;

        private DeleteCharacterAsyncTask(CharacterDAO characterDAO) {
            this.characterDAO = characterDAO;
        }

        @Override
        protected Void doInBackground(CharacterModel... characterModels) {
            characterDAO.delete(characterModels[0]);
            return null;
        }
    }

    private static class DeleteAllCharacterAsyncTask extends AsyncTask<Void, Void, Void> {

        private CharacterDAO characterDAO;

        private DeleteAllCharacterAsyncTask(CharacterDAO characterDAO) {
            this.characterDAO = characterDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            characterDAO.deleteAllCharacters();
            return null;
        }
    }

    public void setListener(OnInsertListener onInsertListener) {
        this.onInsertListener = onInsertListener;
    }

    public interface OnInsertListener {
        void onInsert(Long characterId);
    }
}
