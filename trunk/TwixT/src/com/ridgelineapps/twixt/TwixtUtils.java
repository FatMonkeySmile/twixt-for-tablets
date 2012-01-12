/*
 * Copyright (C) 2012 TwixT for Tablets (http://code.google.com/p/twixt-for-tablets)
 * 
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ridgelineapps.twixt;

import java.util.Random;

public class TwixtUtils
{
    static Random rand = new Random();

    static boolean NextBool()
    {
        return (rand.nextInt(2) % 2 == 0);
    }


    static boolean isPositionValid(MyBoard board, int x, int y, int turn)
    {
        if(x < 0 || y < 0 || x >= board.size || y >= board.size) {
            return false;
        }
        
        // don't allow pegs in corners
        if(x == 0 && (y == 0 || y == board.size - 1)) {
            return false;
        }
        if(y == 0 && (x == 0 || x == board.size - 1)) {
            return false;
        }
        
        // don't allow pegs in enemy goals
        if(turn == 1 && (x == 0 || x == board.size - 1)) {
            return false;
        }
        
        if(turn == 2 && (y == 0 || y == board.size - 1)) {
            return false;
        }
        
        if(board.pegs[x][y] != 0) {
            return false;
        }
        
        
        if(x < 0 || x >= board.size || y < 0 || y >= board.size)
        {
            return false;
        }

        if(isCorner(board, x, y))
        {
            return false;
        }

        return true;
    }

    static boolean isCorner(MyBoard board, int x, int y)
    {
        if(x == 0 && y == 0)
        {
            return true;
        }

        if(x == 0 && y + 1 == board.size)
        {
            return true;
        }

        if(x + 1 == board.size && y == 0)
        {
            return true;
        }

        if(x + 1 == board.size && y + 1 == board.size)
        {
            return true;
        }

        return false;
    }
}
