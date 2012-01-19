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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Theme {
    static final int[] glowAlphas = new int[]{ 90, 35, 15 };
    
    private Paint backgroundPaint;
    
    private int dotRadius = 2;
    private Paint dotsPaint;
    
    private int pegRadius = 4;
    private Paint shadowPaint;
    
    private Paint[] pegsPaint;
//    private Paint[] winningPaint;
    private Paint[] normalPegsPaint;
    private Paint[] loserPegsPaint;
    private Paint[] linesPaint;
    
    private Paint[] promptPaint;
    private Paint[] promptBorderPaint;
    
    private Paint playerPaint[];
    private Paint[][] glow;
    
    private Paint[] lastPlacementPaint;
    
    private int lineThickness = 1;
    
//    private int cursorRadius = 15;
    private Paint cursorPaint;
    
    private Paint darkenPaint;

    public Theme(String key) {
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setARGB(150, 150, 150, 150);
        
        darkenPaint = new Paint();
        darkenPaint.setAntiAlias(true);
        darkenPaint.setARGB(80, 0, 0, 0);
        darkenPaint.setStrokeWidth(5f);
        
        lastPlacementPaint = new Paint[3];
        lastPlacementPaint[1] = new Paint();
        lastPlacementPaint[1].setAntiAlias(true);
        lastPlacementPaint[1].setARGB(190, 240, 240, 240);
        lastPlacementPaint[1].setStrokeWidth(2f);
        lastPlacementPaint[2] = new Paint();
        lastPlacementPaint[2].setAntiAlias(true);
        lastPlacementPaint[2].setARGB(190, 15, 15, 15);
        lastPlacementPaint[2].setStrokeWidth(2f);
        
        //TODO: turn back on themes
        if(key.equals("bw")) {
            int color = 255;
            int offset = 0;
            
            playerPaint = new Paint[3];
            
            playerPaint[1] = new Paint();
            playerPaint[1].setAntiAlias(true);
            playerPaint[1].setARGB(255, 9, 9, 9 + offset);
            
            playerPaint[2]= new Paint();
            playerPaint[2].setAntiAlias(true);
            playerPaint[2].setARGB(255, color, color - offset, color - offset);
            
            glow = new Paint[3][3];
            
            for(int i=0; i < 3; i++) {
	            glow[1][i] = new Paint();
	            glow[1][i].setAntiAlias(true);
	            glow[1][i].setARGB(glowAlphas[i], color - offset, color - offset, color);
            }
            
            for(int i=0; i < 3; i++) {
	            glow[2][i] = new Paint();
	            glow[2][i].setAntiAlias(true);
	            glow[2][i].setARGB(glowAlphas[i], color, color - offset, color - offset);
            }
            
            dotsPaint = new Paint();
            dotsPaint.setAntiAlias(true);
            dotsPaint.setARGB(255, 160, 160, 160);
            
            pegsPaint = new Paint[3];
            
            linesPaint = new Paint[3];
            linesPaint[2] = playerPaint[2];
            
            linesPaint[1] = new Paint();
            linesPaint[1].setAntiAlias(true);
            linesPaint[1].setARGB(255, 255 - offset, 255 - offset, 255);

            normalPegsPaint = new Paint[3];
            normalPegsPaint[1] = playerPaint[1];
            normalPegsPaint[2] = playerPaint[2];

//            normalPegsPaint[1] = new Paint();
//            normalPegsPaint[1].setARGB(255, 20, 20, 20);
//            normalPegsPaint[2] = new Paint();
//            normalPegsPaint[2].setARGB(255, 160, 200, 200);

            loserPegsPaint = new Paint[3];
            for(int i=1; i < 3; i++) {
                loserPegsPaint[i] = new Paint();
                loserPegsPaint[i].setAntiAlias(true);
                loserPegsPaint[i].setColor(normalPegsPaint[i].getColor());
                loserPegsPaint[i].setAlpha(100);
            }
            
            cursorPaint = new Paint();
            cursorPaint.setAntiAlias(true);
            cursorPaint.setARGB(120, 200, 200, 200);
            
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
//            backgroundPaint.setARGB(255, 70, 70, 70);
            backgroundPaint.setARGB(255, 10, 10, 10);
            
            promptPaint = new Paint[3];
            promptPaint[1] = new Paint();
            promptPaint[1].setAntiAlias(true);
            promptPaint[1].setARGB(80, color - offset, color - offset, color);
            
            promptPaint[2] = new Paint();
            promptPaint[2].setAntiAlias(true);
            promptPaint[2].setARGB(80, color, color - offset, color - offset);

            promptBorderPaint = new Paint[3];
            promptBorderPaint[1] = new Paint();
            promptBorderPaint[1].setAntiAlias(true);
            promptBorderPaint[1].setARGB(120, color - offset, color - offset, color);
            promptBorderPaint[1].setStyle(Paint.Style.STROKE);
            promptBorderPaint[1].setStrokeWidth(5);
            
            promptBorderPaint[2] = new Paint();
            promptBorderPaint[2].setAntiAlias(true);
            promptBorderPaint[2].setARGB(120, color, color - offset, color - offset);
            promptBorderPaint[2].setStyle(Paint.Style.STROKE);
            promptBorderPaint[2].setStrokeWidth(5);
        }
        else if(key.equals("white")) {
            int color = 255;
            int offset = 0;
            
            playerPaint = new Paint[3];

            playerPaint[1] = new Paint();
            playerPaint[1].setAntiAlias(true);
            playerPaint[1].setARGB(255, 9, 9, 9 + offset);
            
            playerPaint[2] = new Paint();
            playerPaint[2].setAntiAlias(true);
            playerPaint[2].setARGB(255, color, color - offset, color - offset);
            
            glow = new Paint[3][3];
            
            glow[2][0] = new Paint();
            glow[2][0].setAntiAlias(true);
            glow[2][0].setARGB(glowAlphas[0], color, color - offset, color - offset);
            
            glow[2][1] = new Paint();
            glow[2][1].setAntiAlias(true);
            glow[2][1].setARGB(0, 0, 0, 0);
            
            glow[2][2] = new Paint();
            glow[2][2].setAntiAlias(true);
            glow[2][2].setARGB(0, 0, 0, 0);
            
            color = 40;
            glow[1][0] = new Paint();
            glow[1][0].setAntiAlias(true);
            glow[1][0].setARGB(glowAlphas[0], color - offset, color - offset, color);
            
            glow[1][1] = new Paint();
            glow[1][1].setAntiAlias(true);
            glow[1][1].setARGB(0, 0, 0, 0);

            glow[1][2] = new Paint();
            glow[1][2].setAntiAlias(true);
            glow[1][2].setARGB(0, 0, 0, 0);
            
            dotsPaint = new Paint();
            dotsPaint.setAntiAlias(true);
            dotsPaint.setARGB(255, 85, 85, 85);
            
            pegsPaint = new Paint[3];
            
            linesPaint = new Paint[3];
            linesPaint[2] = playerPaint[2];
            
            linesPaint[1] = new Paint();
            linesPaint[1].setAntiAlias(true);
            linesPaint[1].setARGB(255, 0, 0, 0);

            normalPegsPaint = new Paint[3];
            normalPegsPaint[1] = playerPaint[1];
            normalPegsPaint[2] = playerPaint[2];

//            normalPegsPaint[1] = new Paint();
//            normalPegsPaint[1].setARGB(255, 20, 20, 20);
//            normalPegsPaint[2] = new Paint();
//            normalPegsPaint[2].setARGB(255, 160, 200, 200);

            loserPegsPaint = new Paint[3];
            for(int i=1; i < 3; i++) {
                loserPegsPaint[i] = new Paint();
                loserPegsPaint[i].setAntiAlias(true);
                loserPegsPaint[i].setColor(normalPegsPaint[i].getColor());
                loserPegsPaint[i].setAlpha(100);
            }
            
            cursorPaint = new Paint();
            cursorPaint.setAntiAlias(true);
            cursorPaint.setARGB(120, 200, 200, 200);
            
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setARGB(255, 140, 140, 140);

            promptPaint = new Paint[3];
            promptPaint[1] = new Paint();
            promptPaint[1].setAntiAlias(true);
            promptPaint[1].setARGB(80, color - offset, color - offset, color);
            
            promptPaint[2] = new Paint();
            promptPaint[2].setAntiAlias(true);
            promptPaint[2].setARGB(80, color, color - offset, color - offset);

            promptBorderPaint = new Paint[3];
            promptBorderPaint[1] = new Paint();
            promptBorderPaint[1].setAntiAlias(true);
            promptBorderPaint[1].setARGB(120, color - offset, color - offset, color);
            promptBorderPaint[1].setStyle(Paint.Style.STROKE);
            promptBorderPaint[1].setStrokeWidth(5);
            
            promptBorderPaint[2] = new Paint();
            promptBorderPaint[2].setAntiAlias(true);
            promptBorderPaint[2].setARGB(120, color, color - offset, color - offset);
            promptBorderPaint[2].setStyle(Paint.Style.STROKE);
            promptBorderPaint[2].setStrokeWidth(5);
        }
        else { // default ("rb")
            int color = 230;
            int offset = 86;
            
            playerPaint = new Paint[3];
            
            playerPaint[1] = new Paint();
            playerPaint[1].setAntiAlias(true);
            playerPaint[1].setARGB(255, color - offset, color - offset, color);
            
            playerPaint[2] = new Paint();
            playerPaint[2].setAntiAlias(true);
            playerPaint[2].setARGB(255, color, color - offset, color - offset);
            
            glow = new Paint[3][3];
            
            for(int i=0; i < 3; i++) {
	            glow[1][i] = new Paint();
	            glow[1][i].setAntiAlias(true);
	            glow[1][i].setARGB(glowAlphas[i], color - offset, color - offset, color);
            }
            
            for(int i=0; i < 3; i++) {
	            glow[2][i] = new Paint();
	            glow[2][i].setAntiAlias(true);
	            glow[2][i].setARGB(glowAlphas[i], color, color - offset, color - offset);
            }
            
            dotsPaint = new Paint();
            dotsPaint.setAntiAlias(true);
            dotsPaint.setARGB(255, 160, 160, 160);
            
            pegsPaint = new Paint[3];
            
            linesPaint = new Paint[3];
            linesPaint[2] = playerPaint[2];
            
            linesPaint[1] = new Paint();
            linesPaint[1].setAntiAlias(true);
            linesPaint[1].setARGB(255, 200 - offset, 200 - offset, 200);

            normalPegsPaint = new Paint[3];
            normalPegsPaint[1] = playerPaint[1];
            normalPegsPaint[2] = playerPaint[2];

//            normalPegsPaint[1] = new Paint();
//            normalPegsPaint[1].setARGB(255, 20, 20, 20);
//            normalPegsPaint[2] = new Paint();
//            normalPegsPaint[2].setARGB(255, 160, 200, 200);

            loserPegsPaint = new Paint[3];
            for(int i=1; i < 3; i++) {
                loserPegsPaint[i] = new Paint();
                loserPegsPaint[i].setAntiAlias(true);
                loserPegsPaint[i].setColor(normalPegsPaint[i].getColor());
                loserPegsPaint[i].setAlpha(100);
            }
            
            cursorPaint = new Paint();
            cursorPaint.setAntiAlias(true);
            cursorPaint.setARGB(120, 200, 200, 200);
            
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setARGB(0, 0, 0, 0);

            lastPlacementPaint[2] = new Paint();
            lastPlacementPaint[2].setAntiAlias(true);
            lastPlacementPaint[2].setARGB(190, 240, 240, 240);
            lastPlacementPaint[2].setStrokeWidth(2f);
            
            promptPaint = new Paint[3];
            promptPaint[1] = new Paint();
            promptPaint[1].setAntiAlias(true);
            promptPaint[1].setARGB(80, color - offset, color - offset, color);
            
            promptPaint[2] = new Paint();
            promptPaint[2].setAntiAlias(true);
            promptPaint[2].setARGB(80, color, color - offset, color - offset);

            promptBorderPaint = new Paint[3];
            promptBorderPaint[1] = new Paint();
            promptBorderPaint[1].setAntiAlias(true);
            promptBorderPaint[1].setARGB(120, color - offset, color - offset, color);
            promptBorderPaint[1].setStyle(Paint.Style.STROKE);
            promptBorderPaint[1].setStrokeWidth(5);
            
            promptBorderPaint[2] = new Paint();
            promptBorderPaint[2].setAntiAlias(true);
            promptBorderPaint[2].setARGB(120, color, color - offset, color - offset);
            promptBorderPaint[2].setStyle(Paint.Style.STROKE);
            promptBorderPaint[2].setStrokeWidth(5);
        }
    }
    
    public void resetValues(float scale, boolean bumpDotRadius) {
        cursorPaint.setStrokeWidth(scale);
        pegRadius = (int) (scale / 6.5);
        dotRadius = Math.max(1, (int) (pegRadius / 2.5)); 
        if(bumpDotRadius) {
            dotRadius++;
        }
        int newThickness = Math.max(1, (int) (scale / 18));
        if(newThickness != lineThickness) {
            lineThickness = newThickness;
            for(int i=0; i < linesPaint.length; i++) {
                if(linesPaint[i] != null)
                    linesPaint[i].setStrokeWidth(lineThickness);
            }
            for(int i=0; i < pegsPaint.length; i++) {
                if(pegsPaint[i] != null)
                    pegsPaint[i].setStrokeWidth(lineThickness);
            }
            
            for(int i=0; i < 3; i++) {
            	for(int p=1; p < 3; p++) {
                    if(glow[p][1] != null)
                        glow[p][i].setStrokeWidth(lineThickness);
            	}
            }
            
            darkenPaint.setStrokeWidth(lineThickness * 2);
        }
    }
    
    public void setPaints(int winner) {
    	if(winner == 0) {
    		pegsPaint[1] = normalPegsPaint[1];
    		pegsPaint[2] = normalPegsPaint[2];
    	}
    	else {
    		int loser;
    		if(winner == 1) {
    			loser = 2;
    		}
    		else {
    			loser = 1;
    		}
    		pegsPaint[winner] = normalPegsPaint[winner];
    		pegsPaint[loser] = loserPegsPaint[loser];
    	}
    }
    
    public void drawLine(Canvas canvas, float[] point, float[] point2, int p, boolean futureLine) {
        
        canvas.drawLine(point[0] - 1, point[1], point2[0] - 1, point2[1], glow[p][0]);
        canvas.drawLine(point[0] + 1, point[1], point2[0] + 1, point2[1], glow[p][0]);
        canvas.drawLine(point[0], point[1] - 1, point2[0], point2[1] - 1, glow[p][0]);
        canvas.drawLine(point[0], point[1] + 1, point2[0], point2[1] + 1, glow[p][0]);
            
        canvas.drawLine(point[0] - 1, point[1] - 1, point2[0] - 1, point2[1] - 1, glow[p][0]);
        canvas.drawLine(point[0] - 1, point[1] + 1, point2[0] - 1, point2[1] + 1, glow[p][0]);
        canvas.drawLine(point[0] + 1, point[1] - 1, point2[0] + 1, point2[1] - 1, glow[p][0]);
        canvas.drawLine(point[0] + 1, point[1] + 1, point2[0] + 1, point2[1] + 1, glow[p][0]);
        
        canvas.drawLine(point[0] - 2, point[1], point2[0] - 2, point2[1], glow[p][1]);
        canvas.drawLine(point[0] + 2, point[1], point2[0] + 2, point2[1], glow[p][1]);
        canvas.drawLine(point[0], point[1] - 2, point2[0], point2[1] - 2, glow[p][1]);
        canvas.drawLine(point[0], point[1] + 2, point2[0], point2[1] + 2, glow[p][1]);
        
        canvas.drawLine(point[0] - 2, point[1] - 2, point2[0] - 2, point2[1] - 2, glow[p][2]);
        canvas.drawLine(point[0] - 2, point[1] + 2, point2[0] - 2, point2[1] + 2, glow[p][2]);
        canvas.drawLine(point[0] + 2, point[1] - 2, point2[0] + 2, point2[1] - 2, glow[p][2]);
        canvas.drawLine(point[0] + 2, point[1] + 2, point2[0] + 2, point2[1] + 2, glow[p][2]);

        canvas.drawLine(point[0], point[1], point2[0], point2[1], linesPaint[p]);
    }
    
    public void drawBorderLine(Canvas canvas, float[] point, float[] point2, int p) {
        canvas.drawLine(point[0], point[1], point2[0], point2[1], dotsPaint);
        canvas.drawLine(point[0], point[1], point2[0], point2[1], pegsPaint[p]);
        
        Paint paint = null;
        for(int i=1; i <= 6; i++) {
            if(i == 1) {
                paint = pegsPaint[p];
            }
            else if(i == 2) {
                paint = linesPaint[p];
            }
            else if(i == 3) {
                paint = glow[p][0];
            }
            else if(i == 4) {
                paint = glow[p][1];
            }
            else if(i == 5) {
                paint = glow[p][2];
            }

            if(point[1] == point2[1]) {
                canvas.drawLine(point[0] - 3, point[1] - i, point2[0] + 3, point2[1] - i, paint);
                canvas.drawLine(point[0] - 3, point[1] + i, point2[0] + 3, point2[1] + i, paint);
            }
            else {
                canvas.drawLine(point[0] - i, point[1] - 3, point2[0] - i, point2[1] + 3, paint);
                canvas.drawLine(point[0] + i, point[1] - 3, point2[0] + i, point2[1] + 3, paint);
            }
        }
    }
    
    public void drawBoard(Canvas canvas, RectF rect, float scale) {
        canvas.drawRoundRect(rect, scale, scale, backgroundPaint);
    }
    
    public void drawHole(Canvas canvas, float[] point) {
        canvas.drawCircle(point[0], point[1], dotRadius, dotsPaint);
    }
    
    public void drawPeg(Canvas canvas, float[] point, int p, boolean darken) {
        canvas.drawCircle(point[0], point[1], pegRadius + 6, glow[p][2]);
		canvas.drawCircle(point[0], point[1], pegRadius + 5, glow[p][2]);
		canvas.drawCircle(point[0], point[1], pegRadius + 4, glow[p][1]);
		canvas.drawCircle(point[0], point[1], pegRadius + 3, glow[p][1]);
        canvas.drawCircle(point[0], point[1], pegRadius + 2, linesPaint[p]);
		canvas.drawCircle(point[0], point[1], pegRadius + 1, linesPaint[p]);
	
        canvas.drawCircle(point[0], point[1], pegRadius + 1, pegsPaint[p]);
        canvas.drawCircle(point[0], point[1], pegRadius, pegsPaint[p]);

        if(darken) {
            canvas.drawCircle(point[0], point[1], pegRadius + 4, darkenPaint);
        }
    }
    
    public void drawFuturePeg(Canvas canvas, float[] point, int p) {
    	drawPeg(canvas, point, p, false);
    }
    
    public void drawPegBeingPlaced(Canvas canvas, float[] point) {
    	canvas.drawCircle(point[0], point[1], pegRadius + 3, shadowPaint);
    }
    
    public void drawCrosshairs(Canvas canvas, float upperLeftX, float upperLeftY, float lowerRightX, float lowerRightY, float crosshairX, float crosshairY) {
        canvas.drawLine(upperLeftX, crosshairY, lowerRightX, crosshairY, cursorPaint);
        canvas.drawLine(crosshairX, upperLeftY, crosshairX, lowerRightY, cursorPaint);
    }
    
    public void drawLastPlacement(Canvas canvas, float x, float y, int p) {
        int halfLine = pegRadius + 1;
        canvas.drawLine(x, y - halfLine, x, y + halfLine, lastPlacementPaint[p]);
        canvas.drawLine(x - halfLine, y, x + halfLine, y, lastPlacementPaint[p]);
    }
    
    public void drawPrompt(Canvas canvas, RectF rect, int p) {
        canvas.drawRect(rect, promptBorderPaint[p]);

//        if(drawSides)
//            canvas.drawRect(sideRect, theme.sidePaint[1]);
//        
//        if(drawSides || board.turn == 1) 
//            canvas.drawRect(sideRect, theme.sideBorderPaint[1]);
////        if (board.turn == 1)
//        if(drawSides)
//        {
//            canvas.drawRect(sideRect, theme.sideBorderPaint[1]);
//        }
//        sideRect = getSideBounds(2, 0);
//        if(drawSides)
//            canvas.drawRect(sideRect, theme.sidePaint[2]);
//        if(drawSides || board.turn == 2) 
//            canvas.drawRect(sideRect, theme.sideBorderPaint[2]);
////        if (board.turn == 2) 
//        if(drawSides)
//        {
//            canvas.drawRect(sideRect, theme.sideBorderPaint[2]);
//        }
    }
    
    public void darkenLine(Canvas canvas, float[] point, float[] point2, int p) {
        canvas.drawLine(point[0], point[1], point2[0], point2[1], darkenPaint);
    }
}
