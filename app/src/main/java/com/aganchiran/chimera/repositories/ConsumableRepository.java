package com.aganchiran.chimera.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.aganchiran.chimera.chimeracore.ConsumableDAO;
import com.aganchiran.chimera.chimeracore.ConsumableModel;
import com.aganchiran.chimera.chimeradata.ChimeraDB;

import java.util.List;


public class ConsumableRepository {

    private ConsumableDAO consumableDAO;
    private LiveData<List<ConsumableModel>> allConsumables;

    public ConsumableRepository(Application application) {
        ChimeraDB database = ChimeraDB.getInstance(application);
        consumableDAO = database.consumableDAO();
        allConsumables = consumableDAO.getAllConsumables();
    }

    public void insert(ConsumableModel consumableModel) {
        new InsertConsumableAsyncTask(consumableDAO).execute(consumableModel);
    }

    public void update(ConsumableModel consumableModel) {
        new UpdateConsumableAsyncTask(consumableDAO).execute(consumableModel);

    }

    public void delete(ConsumableModel consumableModel) {
        new DeleteConsumableAsyncTask(consumableDAO).execute(consumableModel);

    }

    public void deleteAllConsumables() {
        new DeleteAllConsumableAsyncTask(consumableDAO).execute();

    }

    public LiveData<List<ConsumableModel>> getCharacterConsumables(int characterId){
        return consumableDAO.getCharacterConsumables(characterId);
    }

    public LiveData<ConsumableModel> getConsumableById(int id) {
        return consumableDAO.getConsumableById(id);
    }

    public LiveData<List<ConsumableModel>> getAllConsumables() {
        return allConsumables;
    }

    private static class InsertConsumableAsyncTask extends AsyncTask<ConsumableModel, Void, Void> {

        private ConsumableDAO consumableDAO;

        private InsertConsumableAsyncTask(ConsumableDAO consumableDAO) {
            this.consumableDAO = consumableDAO;
        }

        @Override
        protected Void doInBackground(ConsumableModel... consumableModels) {
            consumableDAO.insert(consumableModels[0]);
            return null;
        }
    }

    private static class UpdateConsumableAsyncTask extends AsyncTask<ConsumableModel, Void, Void> {

        private ConsumableDAO consumableDAO;

        private UpdateConsumableAsyncTask(ConsumableDAO consumableDAO) {
            this.consumableDAO = consumableDAO;
        }

        @Override
        protected Void doInBackground(ConsumableModel... consumableModels) {
            consumableDAO.update(consumableModels[0]);
            return null;
        }
    }

    private static class DeleteConsumableAsyncTask extends AsyncTask<ConsumableModel, Void, Void> {

        private ConsumableDAO consumableDAO;

        private DeleteConsumableAsyncTask(ConsumableDAO consumableDAO) {
            this.consumableDAO = consumableDAO;
        }

        @Override
        protected Void doInBackground(ConsumableModel... consumableModels) {
            consumableDAO.delete(consumableModels[0]);
            return null;
        }
    }

    private static class DeleteAllConsumableAsyncTask extends AsyncTask<Void, Void, Void> {

        private ConsumableDAO consumableDAO;

        private DeleteAllConsumableAsyncTask(ConsumableDAO consumableDAO) {
            this.consumableDAO = consumableDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            consumableDAO.deleteAllConsumables();
            return null;
        }
    }

    private static class GetConsumableAsyncTask extends AsyncTask<ConsumableModel, Void, LiveData<ConsumableModel>> {

        private ConsumableDAO consumableDAO;

        private GetConsumableAsyncTask(ConsumableDAO consumableDAO) {
            this.consumableDAO = consumableDAO;
        }

        @Override
        protected LiveData<ConsumableModel> doInBackground(ConsumableModel... consumableModels) {
            return consumableDAO.getConsumableById(consumableModels[0].getId());
        }
    }

}
