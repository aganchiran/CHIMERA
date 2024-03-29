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

import android.os.Bundle;

import com.aganchiran.chimera.R;
import com.aganchiran.chimera.chimerafront.fragments.CamCharactersFragment;

public class PlayerCharactersActivity extends ActivityWithUpperBar {

    private CamCharactersFragment charactersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player_characters);
        super.onCreate(savedInstanceState);
        charactersFragment = CamCharactersFragment.newInstance(null);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.character_list, charactersFragment)
                .commit();

    }
}
