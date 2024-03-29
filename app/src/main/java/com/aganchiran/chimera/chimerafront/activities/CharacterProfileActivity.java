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
import com.aganchiran.chimera.chimeracore.character.CharacterModel;
import com.aganchiran.chimera.chimerafront.fragments.ChaCombatFragment;
import com.aganchiran.chimera.chimerafront.fragments.ChaConsumablesFragment;
import com.aganchiran.chimera.chimerafront.fragments.ChaDetailsFragment;
import com.aganchiran.chimera.viewmodels.CharacterProfileVM;

public class CharacterProfileActivity extends ActivityWithUpperBar {

    public static final int DETAILS_TAB = 0;
    public static final int CONSUMABLES_TAB = 1;
    public static final int COMBAT_TAB = 2;

    private CharacterProfileVM characterProfileVM;
    private CharacterModel character;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_character_profile);
        characterProfileVM = ViewModelProviders.of(this).get(CharacterProfileVM.class);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        character = (CharacterModel) getIntent().getSerializableExtra("CHARACTER");


        if (getIntent().getBooleanExtra("FROMCOMBAT", false)) {
            tabLayout.getTabAt(COMBAT_TAB).select();
        }

        super.onCreate(savedInstanceState);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(character.getName());

        characterProfileVM.getCharacterById(character.getId()).observe(this, new Observer<CharacterModel>() {
            @Override
            public void onChanged(@Nullable CharacterModel characterModel) {
                character = characterModel;
                if (characterModel != null) {
                    toolbarTitle.setText(characterModel.getName());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("CHARACTER", character);
        setResult(RESULT_OK, intent);
        super.onBackPressed();

    }

    public CharacterModel getCharacter() {
        return character;
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
            CharacterModel characterModel =
                    (CharacterModel) getIntent().getSerializableExtra("CHARACTER");
            switch (position) {
                case DETAILS_TAB:
                    return ChaDetailsFragment.newInstance(characterModel);
                case CONSUMABLES_TAB:
                    return ChaConsumablesFragment.newInstance(characterModel);
                case COMBAT_TAB:
                    return ChaCombatFragment.newInstance(characterModel);
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
