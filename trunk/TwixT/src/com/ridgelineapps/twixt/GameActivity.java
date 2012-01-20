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

import java.util.Observable;
import java.util.Observer;

import net.schwagereit.t1j.CheckPattern;
import net.schwagereit.t1j.GeneralSettings;
import net.schwagereit.t1j.Match;
import net.schwagereit.t1j.MatchData;
import net.schwagereit.t1j.Zobrist;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements OnTouchListener{
    MyBoard board;
    GameView view;
    Match match;
    boolean singlePlayer = false;

    boolean aiMoving = false;
    boolean humanJustMoved = false;
    
    public static long MIN_TURN_TIME = 750L;
    
    boolean offsetTouch;
    boolean showCursor;
    
    PointF touchOffset;
    
    int humanPlayer = 2;
    int aiPlayer = 1;
    
    int[] lastBoardPoint;
    
    public static final int TOUCH_OFFSET = 150;
    public long aiStartTurn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        int players = getIntent().getExtras().getInt("players");
        int size = 24;
        String themeStr = prefs.getString("themePref", "rb");
        String sizeString = prefs.getString("sizePref", "24");
        showCursor = prefs.getBoolean("showGridPref", true);
        
        boolean random = true; //prefs.getBoolean("randomFirstPref", true);
        boolean blueFirst = true; //prefs.getBoolean("darkFirstPref", true);
        boolean showLastPlacement = prefs.getBoolean("showLastPlacementPref", true);
        boolean humanRed = prefs.getBoolean("humanRedPref", true);
        
        boolean showAreaLines = prefs.getBoolean("areaLinesPref", false);

        if(humanRed) {
            humanPlayer = 2;
            aiPlayer = 1;
        }
        else {
            humanPlayer = 1;
            aiPlayer = 2;
        }
        
        offsetTouch = prefs.getBoolean("offsetTouchPref", true);
        
        
        size = Integer.parseInt(sizeString);
        
        if(players == 1) {
        	singlePlayer = true;
        	
            CheckPattern.getInstance().loadPattern();
            Zobrist.getInstance().initialize();
        	
        	boolean usePly = true;
            if(prefs.getBoolean("aiUseTimePref", false)) {
                try {
	            	String timeStr = prefs.getString("aiTimePref", "5");
            		GeneralSettings.getInstance().mdTime = Integer.parseInt(timeStr);
            		usePly = false;
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            }
            if(usePly) {
                int ply = 5;
                try {
                    String plyStr = prefs.getString("aiSearchDepthPref", "5");
                    ply = Integer.parseInt(plyStr);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                
                GeneralSettings.getInstance().mdPly = ply;
            }
            GeneralSettings.getInstance().mdFixedPly = usePly;
            GeneralSettings.getInstance().correct();
        	
            boolean humanFirst = true;
            if(random) {
            	humanFirst = (Math.random() < 0.5);
            }
        	
            board = new MyBoard(size, match, false, true);
            board.turn = (humanFirst?humanPlayer:aiPlayer);
    		match = new Match();

    		MatchData matchData = new MatchData();
//    		matchData.loadPreferences();
    		matchData.mdPlayerY = "fred";
    		matchData.mdPlayerX = "jane";
    		matchData.mdYsize = size;
    		matchData.mdXsize = size;
    		matchData.mdYhuman = humanRed;
    		matchData.mdXhuman = !humanRed;
    		if(humanRed)
    		    matchData.mdYstarts = humanFirst;
    		else
                matchData.mdYstarts = !humanFirst;
    		matchData.mdPieRule = false;
    		
    		match.addObserver(new Observer() {
    			@Override
    			public void update(Observable observable, Object data) {
    				if(humanJustMoved) {
    					board.turn = aiPlayer;
    					humanJustMoved = false;
    				} else {
    				    long diff = System.currentTimeMillis() - aiStartTurn; 
    				    if( diff < MIN_TURN_TIME) {
    				        try {
    				            Thread.sleep(MIN_TURN_TIME - diff);
    				        }
    				        catch(Exception e) {
    				            e.printStackTrace();
    				        }
    				    }
    					board.turn = humanPlayer;
    					aiMoving = false;
    				}
    				int lastMove = match.getHighestMoveNr();
    				if(lastMove > 0) {
    					board.lastTurnX = match.getMoveX(lastMove);
    					board.lastTurnY = match.getMoveY(lastMove);
    				}
    				board.sync(match.getBoardDisplay());
    				if(view != null)
    					view.postInvalidate();
    			}
    		});
    		match.prepareNewMatch(matchData, false);
    		
    		if(!humanFirst) {
	    		aiMoving = true;
	    		humanJustMoved = false;
				if(view != null)
					view.postInvalidate();
				match.computeMove();
//		        match.evaluateAndUpdateGui();
    		}
        }
        else {
        	singlePlayer = false;
        	match = null;
            board = new MyBoard(size, null, random, blueFirst);
        }
        
        Theme theme = new Theme(themeStr);
        
        view = new GameView(this, board, theme);
        view.setOnTouchListener(this);
//        view.setSystemUiVisibility(View.STATUS_BAR_HIDDEN); 
        view.showLastPlacement = showLastPlacement;
        view.showAreaLines = showAreaLines;
        setContentView(view);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			// Not great, but simplest way to stop AI threads
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
	    finish();
	    return;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
    	if(singlePlayer && aiMoving) {
    		return false;
    	}
    	
        float x = event.getX();
        float y = event.getY();
        
        if(touchOffset != null && board != null) {
            x += touchOffset.x;
            y += touchOffset.y;
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(board.turn == 1) { // && event.getX() < view.translateToScreen(0, 0)[0]) {
            	if(offsetTouch) {
            		touchOffset = new PointF(TOUCH_OFFSET, 0);
//                    RectF bounds = view.getSideBounds(1, offset);
//                    if(bounds != null && bounds.contains(event.getX(), event.getY())) {
//                        PointF peg = view.getStartingPosForOffboardPeg();
//                        touchOffset = new PointF(peg.x - event.getX(), peg.y - event.getY());
//                    }
            	}
            }
            if(board.turn == 2) { // && event.getX() > view.translateToScreen(board.size - 1, board.size - 1)[0]
            	if(offsetTouch) {
            		touchOffset = new PointF(-TOUCH_OFFSET, 0);
//                    RectF bounds = view.getSideBounds(2, offset);
//                    if(bounds != null && bounds.contains(event.getX(), event.getY())) {
//                        PointF peg = view.getStartingPosForOffboardPeg();
//                        touchOffset = new PointF(peg.x - event.getX(), peg.y - event.getY());
//                    }
            	}
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            if(lastBoardPoint != null) {
                // Handle moving off the screen, is there a better way?
                if(view.isYWithinBounds(event.getY())) {
                    int[] boardPoint = lastBoardPoint; // Use the last location from move rather from this event since the UI shows that location as the peg placement.
                    board.hideCursor();
                    addPeg(boardPoint[0], boardPoint[1]);
                    cleanupPegDrawing();
                } else {
                    cleanupPegDrawing();
                }
            }
            
            lastBoardPoint = null;
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(board.winner == 0) {
                int[] boardPoint = view.translateToBoard(x, y);
                lastBoardPoint = boardPoint;
                if(showCursor) {
                    if(!view.isYWithinBounds(event.getY())) {
                        board.hideCursor();
                    }
                    else {
                        board.setCursor(boardPoint[0], boardPoint[1]);
                    }
                }
                if(view.setPlacingPegLoc(x, y, event.getX(), event.getY())) {
                	board.setFutureLines(boardPoint[0], boardPoint[1]);
                	view.invalidate();
                }
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            cleanupPegDrawing();
        } 
        
        return true;
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.undo) {
//            undo();
//        }
//        return true;
//    }    
    
    public void undo() {
        if(singlePlayer) {
            if(!aiMoving) {
                if(match.getHighestMoveNr() > 0)
                    match.removeMove();
            }
        }
        else {
            board.undo();
        }
    }

    public void cleanupPegDrawing() {
        touchOffset = null;
        view.placingPegLoc = null;    
        view.lastPlacingBoardLoc = null;
        view.shadowPegLoc = null;
        view.touch = null;
        board.hideCursor();
        board.clearFutureLines();
        view.invalidate();
    }
    
    public void addPeg(int x, int y) {
    	if(!singlePlayer) {
    		board.addPeg(x, y, false);
    	}
    	else {
    		if(board.isValid(x, y, humanPlayer)) {
    			System.out.println("addPeg:" + x + ", " + y);
    			aiStartTurn = System.currentTimeMillis();
	    		aiMoving = true;
	    		humanJustMoved = true;
		        match.setlastMove(x, y);
		        match.evaluateAndUpdateGui();
    		}
    	}
    }
}
