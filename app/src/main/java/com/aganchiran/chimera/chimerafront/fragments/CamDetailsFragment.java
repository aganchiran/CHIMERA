package com.aganchiran.chimera.chimerafront.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aganchiran.chimera.R;
import com.aganchiran.chimera.chimeracore.campaign.CampaignModel;
import com.aganchiran.chimera.chimerafront.activities.EventMapActivity;
import com.aganchiran.chimera.chimerafront.dialogs.CreateEditCampaignDialog;
import com.aganchiran.chimera.viewmodels.CampaignDetailsVM;

public class CamDetailsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_CAMPAIGN_MODEL = "campaign_model";
    private CampaignDetailsVM campaignDetailsVM;
    private LiveData<CampaignModel> campaign;


    public CamDetailsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CamDetailsFragment newInstance(CampaignModel campaignModel) {
        CamDetailsFragment fragment = new CamDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CAMPAIGN_MODEL, campaignModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater
                .inflate(R.layout.fragment_campaign_details, container, false);
        campaignDetailsVM = ViewModelProviders.of(this).get(CampaignDetailsVM.class);

        if (getArguments() != null) {
            final CampaignModel campaignModel = (CampaignModel) getArguments()
                    .getSerializable(ARG_CAMPAIGN_MODEL);

            if (campaignModel != null) {
                campaign = campaignDetailsVM.getCampaignById(campaignModel.getId());
                campaign.observe(this, new Observer<CampaignModel>() {
                    @Override
                    public void onChanged(@Nullable CampaignModel campaignModel) {
                        assert campaignModel != null;
                        ((TextView) rootView.findViewById(R.id.campaign_image))
                                .setText(campaignModel.getName());
                        ((TextView) rootView.findViewById(R.id.description_text_view))
                                .setText(campaignModel.getDescription());
                    }
                });

                final Button eventMapButton = rootView.findViewById(R.id.event_map_button);
                eventMapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EventMapActivity.class);
                        intent.putExtra("CAMPAIGN", campaign.getValue());
                        startActivity(intent);
                    }
                });
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_campaign_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_campaign:
                CreateEditCampaignDialog dialog = new CreateEditCampaignDialog();
                dialog.setListener(new CreateEditCampaignDialog.CreateCampaignDialogListener() {
                    @Override
                    public void saveCampaign(String newName, String newDescription) {
                        final CampaignModel campaignModel = campaign.getValue();
                        if (campaignModel != null) {
                            campaignModel.setName(newName);
                            campaignModel.setDescription(newDescription);
                        }
                        campaignDetailsVM.update(campaignModel);
                    }

                    @Override
                    public CampaignModel getCampaign() {
                        return campaign.getValue();
                    }
                });
                assert getFragmentManager() != null;
                dialog.show(getFragmentManager(), "edit campaign");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
