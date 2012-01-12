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

import net.schwagereit.t1j.Board;
import net.schwagereit.t1j.Match;

public class MyBoard {
    int[][] pegs;
    boolean[][][][] lines;
    
    int[] lastFutureLinesPeg = new int[2];
    boolean[][][][] futureLines;
    
    boolean[][] winningPegs;
    boolean[][][] winningLines;
    
    boolean[][] checkGrid;
    
    int size;

    int lastTurnX;
    int lastTurnY;
    
    int turn;
    int winner;
    
    int[] cursor = new int[2];
    
    boolean singlePlayer = false;
    Match match;
    
    public MyBoard(int size, Match match, boolean random, boolean blueFirst) {
    	singlePlayer = (match != null);
        this.size = size;
    	this.match = match;
    	reset(random, blueFirst);
    }
    
    public void sync(Board board) {
    	for(int x=0; x < size; x++) {
        	for(int y=0; y < size; y++) {
        		int val = board.getPin(x, y);
        		if(val == Board.YPLAYER) {
        			val = 2;
        		}
        		if(val != pegs[x][y]) {
        	        pegs[x][y] = val;
        		}
        		
        		for(int n=0; n < 4; n++) {
        			if(board.isBridged(x, y, n)) {
        				int line = n;
        				line -= 1;
        				if(line < 1) {
        					line += 8;
        				}
        				lines[x][y][line][val] = true;
                        int[] dif = MyBoard.getDifference(line);
                        int x2 = x + dif[0];
                        int y2 = y + dif[1];
                        int inverse = MyBoard.inverse(line);
        				lines[x2][y2][inverse][val] = true;
        			}
        		}
        	}
    	}
    }
    
    public void reset(boolean random, boolean blueFirst) {
    	winner = 0;
    	winningPegs = null;
    	winningLines = null;
        pegs = new int[size][size];
        lines = new boolean[size][size][9][3];
        futureLines = new boolean[size][size][9][3];
        checkGrid = new boolean[size][size];
        hideCursor();
        
        lastTurnX = -1;
        lastTurnY = -1;
    	turn = 1;
    	
        if(random) {
            if(Math.random() < 0.5) {
                swapTurn();
            } 
        }
        else if(!blueFirst) {
            swapTurn();
        }
    }
    
    public void setCursor(int x, int y) {
    	if(!isCursorValid(x, y, turn)) {
    		hideCursor();
    	}
    	else {
	        cursor[0] = x;
	        cursor[1] = y;
    	}
    }
    
    public void hideCursor() {
        cursor[0] = -1;
        cursor[1] = -1;
    }
    
    public void addPeg(int x, int y, boolean fromAI) {
    	if(winner > 0) {
    		return;
    	}
    	
    	if(!TwixtUtils.isPositionValid(this, x, y, turn)) {
    	    return;
    	}

   		pegs[x][y] = turn;
        
        try
        {
            for(int li=1; li < 9; li++)
            {
                int liToCheck = li;
                int[] dif = MyBoard.getDifference(liToCheck);
                int x1 = x;
                int y1 = y;
                int x2 = x + dif[0];
                int y2 = y + dif[1]; 
                if(isPositionReal(x2, y2))
                {
                    if(pegs[x2][y2] == turn)
                    {
                        if(liToCheck > 4)
                        {
                            liToCheck = MyBoard.inverse(liToCheck);
                            int x3 = x1;
                            int y3 = y1;
                            x1 = x2;
                            y1 = y2;
                            x2 = x3;
                            y2 = y3;
                        }

                        if(!areThereConflicts(x1, y1, liToCheck, turn))
                        {
                            lines[x1][y1][liToCheck][turn] = true;
                            lines[x2][y2][MyBoard.inverse(liToCheck)][turn] = true;

//                            if(move.forceLinks)
//                            {
//                                CheckTable[] ct = Board.CHECKTABLE[liToCheck];
//
//                                //System.Console.WriteLine("Starting check");
//                                for(int i2=0; i2 < ct.length; i2++)
//                                {
//                                    //System.Console.WriteLine("Checking:" + i);
//                                    CheckTable table = ct[i2];
//                                    int xToCheck = table.x + x1;
//                                    int yToCheck = table.y + y1;
//
//                                    if(TwixtUtils.isPositionValid(this, xToCheck, yToCheck))
//                                    {
//                                        boolean[] linesToCheck = board.l[xToCheck][yToCheck];
//                                        for(int j=0; j < table.lines.length; j++)
//                                        {
//                                            //System.Console.WriteLine("Checking line:" + j);
//                                            int line = table.lines[j];
//                                            if(linesToCheck[line])
//                                            {
//                                                removeLink(xToCheck, yToCheck, line);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //setLines(x, y, turn, true);

        lastTurnX = x;
        lastTurnY = y;
        if(!checkGameOver(turn)) {
        	swapTurn();
        } else {
            lightUpWinningConnection();
        }
    }
    
    public void clearFutureLines() {
        lastFutureLinesPeg[0] = -1;
        lastFutureLinesPeg[1] = -1;
        for(int x=0; x < futureLines.length; x++) {
            for(int y=0; y < futureLines[x].length; y++) {
                for(int i=0; i < futureLines[x][y].length; i++) {
                    for(int j=0; j < futureLines[x][y][i].length; j++) {
                        futureLines[x][y][i][j] = false;
                    }
                }
            }
        }
    }
    
    public void setFutureLines(int x, int y) {
        if(lastFutureLinesPeg[0] != x || lastFutureLinesPeg[1] != y) {
            clearFutureLines();
            lastFutureLinesPeg[0] = x;
            lastFutureLinesPeg[1] = y;
        }
        
        if(winner > 0) {
            return;
        }
        
        if(!TwixtUtils.isPositionValid(this, x, y, turn)) {
            return;
        }
        
        try
        {
            for(int li=1; li < 9; li++)
            {
                int liToCheck = li;
                int[] dif = MyBoard.getDifference(liToCheck);
                int x1 = x;
                int y1 = y;
                int x2 = x + dif[0];
                int y2 = y + dif[1]; 
                if(isPositionReal(x2, y2))
                {
                    if(pegs[x2][y2] == turn)
                    {
                        if(liToCheck > 4)
                        {
                            liToCheck = MyBoard.inverse(liToCheck);
                            int x3 = x1;
                            int y3 = y1;
                            x1 = x2;
                            y1 = y2;
                            x2 = x3;
                            y2 = y3;
                        }

                        if(!areThereConflicts(x1, y1, liToCheck, turn))
                        {
                            futureLines[x1][y1][liToCheck][turn] = true;
                            futureLines[x2][y2][MyBoard.inverse(liToCheck)][turn] = true;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void lightUpWinningConnection() {
        winningPegs = new boolean[size][size];
        winningLines = new boolean[size][size][9];
        
        for(int x=0; x < size; x++) {
            for(int y=0; y < size; y++) {
                checkGrid[x][y] = false;
            }
        }
        
        for(int i=0; i < size; i++) {
            if(winner == 1) {
                if(trace(i, 0, winner, true)) {
                    break;
                }
            }
            else if(winner == 2) {
                if(trace(0, i, winner, true)) {
                    break;
                }
            }
        }
    }
    
    public boolean checkGameOver(int player) {
    	for(int x=0; x < size; x++) {
    		for(int y=0; y < size; y++) {
    			checkGrid[x][y] = false;
    		}
    	}
    	
    	for(int i=0; i < size; i++) {
    		if(player == 1) {
    			if(trace(i, 0, player, false)) {
    				winner = player;
    				break;
    			}
    		}
    		else if(player == 2) {
    			if(trace(0, i, player, false)) {
    				winner = player;
    				break;
    			}
    		}
    	}
    	
    	return (winner > 0);
    }

    public boolean isValid(int x, int y, int player) {
    	if(x < 0 || x >= size || y < 0 || y >= size) {
    		return false;
    	}
    	
		if(player == 1 && (x == 0 || x == size - 1)) {
			return false;
		}
		
		if(player == 2 && (y == 0 || y == size - 1)) {
			return false;
		}
		
		if(x == 0 && y == 0) {
			return false;
		}
		if(x == 0 && y == size - 1) {
			return false;
		}
		if(x == size - 1 && y == 0) {
			return false;
		}
		if(x == size - 1 && y == size - 1) {
			return false;
		}
		
		if(pegs[x][y] != 0) {
			return false;
		}
		
		return true;
    }
    public boolean isCursorValid(int x, int y, int player) {
        if(x < 0 || x >= size || y < 0 || y >= size) {
            return false;
        }
        
        return true;
    }
    public boolean trace(int x, int y, int player, boolean lightUpWinner) {
    	if(pegs[x][y] != player) {
    		return false;
    	}

    	if(player == 1 && y == size - 1) {
    	    if(lightUpWinner) {
    	        winningPegs[x][y] = true;
    	    }
    		return true;
    	}
    	
    	if(player == 2 && x == size - 1) {
            if(lightUpWinner) {
                winningPegs[x][y] = true;
            }
    		return true;
    	}
    	
    	if(checkGrid[x][y]) {
    		return false;
    	}
    	
    	checkGrid[x][y] = true;
    	
    	for(int i=1; i < 9; i++) {
    	    if(lines[x][y][i][player]) {
    	        int x2 = x + DIFFERENCE[i][0];
    	        int y2 = y + DIFFERENCE[i][1];
    	        if(trace(x2, y2, player, lightUpWinner)) {
    	            if(lightUpWinner) {
    	                winningPegs[x][y] = true;
    	                winningLines[x][y][i] = true;
    	                winningLines[x2][y2][inverse(i)] = true;
    	            }
    	            return true;
    	        }
    	    }
    	}
    	
    	return false; 
    }
    
    public int safeGetPeg(int x, int y) {
    	if(x < 0 || y < 0 || x >= size || y >= size) {
    		return 0;
    	}
    	
    	return pegs[x][y];
    }
    
    public void swapTurn() {
    	if(singlePlayer)
    		return;
    	
        if(turn == 2) {
        	turn = 1;
        }
        else {
        	turn = 2;
        }
    }
    
    public void undo() {
    	if(winner > 0) {
    		return;
    	}
    	
    	if(lastTurnX < 0 || lastTurnY < 0 || lastTurnX >= size || lastTurnY >= size) {
    		return;
    	}
    	swapTurn();
    	pegs[lastTurnX][lastTurnY] = -1;
    	
    	//TODO...
//    	setLines(lastTurnX, lastTurnY, turn, false);
    }
    
    public boolean checkLine(int x, int y, int x2, int y2, int player) {
    	int peg2 = safeGetPeg(x2, y2);
    	if(peg2 != player) {
    		return false;
    	}
    	
    	
    	return true;
    }
    
    
    public static int[][] DIFFERENCE;
    public static int[] INVERSE;
    public static CheckTable[][] CHECKTABLE;

    static
    {
        DIFFERENCE = new int[9][];
        DIFFERENCE[1] = new int[]{1, -2};
        DIFFERENCE[2] = new int[]{2, -1};
        DIFFERENCE[3] = new int[]{2, 1};
        DIFFERENCE[4] = new int[]{1, 2};
        DIFFERENCE[5] = new int[]{-1, 2};
        DIFFERENCE[6] = new int[]{-2, 1};
        DIFFERENCE[7] = new int[]{-2, -1};
        DIFFERENCE[8] = new int[]{-1, -2};

        INVERSE = new int[9];
        INVERSE[1] = 5;
        INVERSE[2] = 6;
        INVERSE[3] = 7;
        INVERSE[4] = 8;
        INVERSE[5] = 1;
        INVERSE[6] = 2;
        INVERSE[7] = 3;
        INVERSE[8] = 4;

        CHECKTABLE = new CheckTable[5][];

        CHECKTABLE[1] = new CheckTable[4];
        CHECKTABLE[1][0] = new CheckTable(0, -1, new int[]{2, 3, 4});
        CHECKTABLE[1][1] = new CheckTable(0, -2, new int[]{3, 4});
        CHECKTABLE[1][2] = new CheckTable(1, 0, new int[]{7, 8});
        CHECKTABLE[1][3] = new CheckTable(1, -1, new int[]{6, 7, 8});

        CHECKTABLE[2] = new CheckTable[4];
        CHECKTABLE[2][0] = new CheckTable(0, -1, new int[]{4, 3});
        CHECKTABLE[2][1] = new CheckTable(1, -1, new int[]{5, 4, 3});
        CHECKTABLE[2][2] = new CheckTable(1, 0, new int[]{7, 8, 1});
        CHECKTABLE[2][3] = new CheckTable(2, 0, new int[]{7, 8});

        CHECKTABLE[3] = new CheckTable[4];
        CHECKTABLE[3][0] = new CheckTable(0, 1, new int[]{1, 2});
        CHECKTABLE[3][1] = new CheckTable(1, 1, new int[]{8, 1, 2});
        CHECKTABLE[3][2] = new CheckTable(1, 0, new int[]{4, 5, 6});
        CHECKTABLE[3][3] = new CheckTable(2, 0, new int[]{5, 6});
    
        CHECKTABLE[4] = new CheckTable[4];
        CHECKTABLE[4][0] = new CheckTable(0, 1, new int[]{1, 2, 3});
        CHECKTABLE[4][1] = new CheckTable(0, 2, new int[]{1, 2});
        CHECKTABLE[4][2] = new CheckTable(1, 0, new int[]{6, 5});
        CHECKTABLE[4][3] = new CheckTable(1, 1, new int[]{6, 7, 5});
    }

    public static int[] getDifference(int i)
    {
        return DIFFERENCE[i];
    }

    public static int inverse(int i)
    {
        return INVERSE[i];
    }
    
    public boolean[] getLines(int x, int y, int player) {
        boolean[] ret = new boolean[9];
        
        try
        {
            for(int li=1; li < 9; li++)
            {
                int liToCheck = li;
                int[] dif = MyBoard.getDifference(liToCheck);
                int x1 = x;
                int y1 = y;
                int x2 = x + dif[0];
                int y2 = y + dif[1]; 
                if(isPositionReal(x2, y2))
                {
                    if(pegs[x2][y2] == player)
                    {
                        if(liToCheck > 4)
                        {
                            liToCheck = MyBoard.inverse(liToCheck);
                            int x3 = x1;
                            int y3 = y1;
                            x1 = x2;
                            y1 = y2;
                            x2 = x3;
                            y2 = y3;
                        }

                        if(!areThereConflicts(x1, y1, liToCheck, player))
                        {
                            ret[li] = true; 
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    public boolean areThereConflicts(int x1, int y1, int li, int turn)
    {
        CheckTable[] ct = MyBoard.CHECKTABLE[li];

        for(int i=0; i < ct.length; i++)
        {
            CheckTable table = ct[i];
            int xToCheck = table.x + x1;
            int yToCheck = table.y + y1;

            if(isPositionReal(xToCheck, yToCheck))
            {
                boolean[][] linesToCheck = lines[xToCheck][yToCheck];
                int playerToCheck = pegs[xToCheck][yToCheck];
                for(int j=0; j < table.lines.length; j++)
                {
                    if(linesToCheck[table.lines[j]][playerToCheck])
                    {
                        if(playerToCheck != turn)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    
    boolean isPositionReal(int x, int y)
    {
        if(x < 0 || x >= size || y < 0 || y >= size)
        {
            return false;
        }

        if(TwixtUtils.isCorner(this, x, y))
        {
            return false;
        }

        return true;
    }
}
