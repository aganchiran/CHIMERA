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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.aganchiran.chimera.R;
import com.aganchiran.chimera.chimeracore.campaign.CampaignModel;
import com.aganchiran.chimera.chimerafront.fragments.CamCharactersFragment;
import com.aganchiran.chimera.chimerafront.fragments.CamCombatsFragment;
import com.aganchiran.chimera.chimerafront.fragments.CamDetailsFragment;
import com.aganchiran.chimera.viewmodels.CampaignProfileVM;

public class CampaignProfileActivity extends ActivityWithUpperBar {

    public static final int DETAILS_TAB = 0;
    public static final int CHARACTERS_TAB = 1;
    public static final int COMBATS_TAB = 2;

    private CampaignModel campaign;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CampaignProfileVM campaignProfileVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_campaign_profile);
        campaignProfileVM = ViewModelProviders.of(this).get(CampaignProfileVM.class);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        campaign = (CampaignModel) getIntent().getSerializableExtra("CAMPAIGN");
        if (getIntent().getBooleanExtra("FROMCOMBAT", false)) {
            tabLayout.getTabAt(COMBATS_TAB).select();
        }

        super.onCreate(savedInstanceState);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(campaign.getName());

        campaignProfileVM.getCampaignById(campaign.getId()).observe(this, new Observer<CampaignModel>() {
            @Override
            public void onChanged(@Nullable CampaignModel campaignModel) {
                if (campaignModel != null) {
                    toolbarTitle.setText(campaignModel.getName());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("CAMPAIGN", campaign);
        setResult(RESULT_OK, intent);
        super.onBackPressed();

    }

    public CampaignModel getCampaign() {
        return campaign;
    }

    public void setCampaign(CampaignModel campaign) {
        this.campaign = campaign;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            CampaignModel campaignModel =
                    (CampaignModel) getIntent().getSerializableExtra("CAMPAIGN");
            switch (position) {
                case DETAILS_TAB:
                    return CamDetailsFragment.newInstance(campaignModel);
                case CHARACTERS_TAB:
                    return CamCharactersFragment.newInstance(campaignModel);
                case COMBATS_TAB:
                    return CamCombatsFragment.newInstance(campaignModel);
                default:
                    throw new RuntimeException("This tab does not exist");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
