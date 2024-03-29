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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.aganchiran.chimera.R;

public class MainMenuActivity extends ActivityWithUpperBar {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_main_menu);
        int orientation = getResources().getConfiguration().orientation;
        LinearLayout ol = findViewById(R.id.orientation_layout);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ol.setOrientation(LinearLayout.VERTICAL);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ol.setOrientation(LinearLayout.HORIZONTAL);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LinearLayout ol = findViewById(R.id.orientation_layout);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ol.setOrientation(LinearLayout.VERTICAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ol.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    public void goToCommingSoonList(final View view) {
        Intent intent = new Intent(this, PlayerCharactersActivity.class);
        startActivity(intent);
    }

    public void goToCampaignList(final View view) {
        Intent intent = new Intent(this, CampaignListActivity.class);
        startActivity(intent);
    }

}
