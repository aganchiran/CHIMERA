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

import com.aganchiran.chimera.chimeracore.campaign.CampaignDAO;
import com.aganchiran.chimera.chimeracore.campaign.CampaignModel;
import com.aganchiran.chimera.chimeradata.ChimeraDB;

import java.util.Arrays;
import java.util.List;

public class CampaignRepo {

    private CampaignDAO campaignDAO;
    private LiveData<List<CampaignModel>> allCampaigns;

    public CampaignRepo(Application application) {
        ChimeraDB database = ChimeraDB.getInstance(application);
        campaignDAO = database.campaignDAO();
        allCampaigns = campaignDAO.getAllCampaigns();
    }

    public void insert(CampaignModel campaignModel) {
        new InsertCampaignAsyncTask(campaignDAO).execute(campaignModel);
    }

    public void update(CampaignModel campaignModel) {
        new UpdateCampaignAsyncTask(campaignDAO).execute(campaignModel);

    }

    public void updateCampaigns(List<CampaignModel> campaignModelList) {
        CampaignModel[] campaignArray = new CampaignModel[campaignModelList.size()];
        campaignArray = campaignModelList.toArray(campaignArray);
        new UpdateCampaignListAsyncTask(campaignDAO).execute(campaignArray);
    }

    public void delete(CampaignModel campaignModel) {
        new DeleteCampaignAsyncTask(campaignDAO).execute(campaignModel);

    }

    public void deleteAllCampaigns() {
        new DeleteAllCampaignAsyncTask(campaignDAO).execute();

    }

    public LiveData<CampaignModel> getCampaignById(int id) {
        return campaignDAO.getCampaignById(id);
    }

    public LiveData<List<CampaignModel>> getAllCampaigns() {
        return allCampaigns;
    }


    /////////////////////////
    //////////TASKS//////////
    /////////////////////////

    private static class InsertCampaignAsyncTask extends AsyncTask<CampaignModel, Void, Void> {

        private CampaignDAO campaignDAO;

        private InsertCampaignAsyncTask(CampaignDAO campaignDAO) {
            this.campaignDAO = campaignDAO;
        }

        @Override
        protected Void doInBackground(CampaignModel... campaignModels) {
            campaignDAO.insert(campaignModels[0]);
            return null;
        }
    }

    private static class UpdateCampaignAsyncTask extends AsyncTask<CampaignModel, Void, Void> {

        private CampaignDAO campaignDAO;

        private UpdateCampaignAsyncTask(CampaignDAO campaignDAO) {
            this.campaignDAO = campaignDAO;
        }

        @Override
        protected Void doInBackground(CampaignModel... campaignModels) {
            campaignDAO.update(campaignModels[0]);
            return null;
        }
    }

    private static class UpdateCampaignListAsyncTask extends AsyncTask<CampaignModel, Void, Void> {

        private CampaignDAO campaignDAO;

        private UpdateCampaignListAsyncTask(CampaignDAO campaignDAO) {
            this.campaignDAO = campaignDAO;
        }


        @Override
        protected final Void doInBackground(CampaignModel... campaignModels) {
            campaignDAO.updateCampaigns(Arrays.asList(campaignModels));
            return null;
        }
    }

    private static class DeleteCampaignAsyncTask extends AsyncTask<CampaignModel, Void, Void> {

        private CampaignDAO campaignDAO;

        private DeleteCampaignAsyncTask(CampaignDAO campaignDAO) {
            this.campaignDAO = campaignDAO;
        }

        @Override
        protected Void doInBackground(CampaignModel... campaignModels) {
            campaignDAO.delete(campaignModels[0]);
            return null;
        }
    }

    private static class DeleteAllCampaignAsyncTask extends AsyncTask<Void, Void, Void> {

        private CampaignDAO campaignDAO;

        private DeleteAllCampaignAsyncTask(CampaignDAO campaignDAO) {
            this.campaignDAO = campaignDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            campaignDAO.deleteAllCampaigns();
            return null;
        }
    }
}
