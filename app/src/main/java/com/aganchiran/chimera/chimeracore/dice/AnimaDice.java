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

package com.aganchiran.chimera.chimeracore.dice;

import java.util.Random;

public class AnimaDice {

    public static int getRollOpen() {
        int openThreshold = 90;

        final Random random = new Random();
        int roll = random.nextInt(100) + 1;
        int rollSum = roll;

        while (roll >= openThreshold) {
            roll = random.nextInt(100) + 1;
            openThreshold++;
            rollSum += roll;
        }

        return rollSum;
    }

    public static int getRoll() {
        final Random random = new Random();
        return random.nextInt(100) + 1;
    }

}
