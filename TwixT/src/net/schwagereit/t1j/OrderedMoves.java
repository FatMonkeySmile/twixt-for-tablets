/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package net.schwagereit.t1j;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 *
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
final class OrderedMoves
{
   private static final int INITIAL_CAPACITY = 30;

   static private final class ValuedMove
   {
      final int value;
      final Move move;

      /**
       * Cons'tor.
       * @param moveIn the moveIn
       * @param valueIn its valueIn
       */
      public ValuedMove(Move moveIn, int valueIn)
      {
         this.move = moveIn;
         this.value = valueIn;
      }

      /**
       * Print object as String.
       * @return Representation as String
       */
      public String toString()
      {
         return "(" + move + ": " + value + ")";
      }

   }

   private static List valuedMoves;

   // 1st for X, 2nd for Y
   static final Map[] killerMoves = { new HashMap(), new HashMap() };


   private Iterator moveIterator;

   // private final int ply;
   // private final int maxPly;
   //private int currentPlayer;

   private boolean gameover;

   private final Match match;

   /**
    * Cons'tor.
    * @param inMatch matchdata
    */
   OrderedMoves(Match inMatch)
   {
      match = inMatch;
   }

   /**
    * In the map of killerMoves found, a move-counter is incremented.
    * @param move the move
    * @param player the player
    */
   static public void addKiller(Move move, int player)
   {
      int ref = (player == Board.XPLAYER) ? 0 : 1;

      Object countObject = killerMoves[ref].get(move);

      if (countObject == null)
      {
         killerMoves[ref].put(move, new Integer(1));
      }
      else
      {
         killerMoves[ref].put(move, new Integer(((Number) countObject).intValue() + 1));
      }
   }

   /**
    * Compute sort-value for new moves. Value is equal to number of hits as killermoves before.
    * @param move move
    * @param ref 0 for xplayer, 1 for yplayer
    * @return value
    */
   private static int getSortValue(Move move, int ref)
   {
      Object countObject = killerMoves[ref].get(move);
      return (countObject == null) ? 0 : ((Number) countObject).intValue();
   }

   /**
    * Sort moves.
    * @param moves the moves to sort
    * @param player next Player
    * @return sorted list
    */
   private List sortMoves(Set moves, int player)
   {
      final int ref = (player == Board.XPLAYER) ? 0 : 1;
      List list = new ArrayList(moves);
      Collections.sort(list, new Comparator()
      {
         public int compare(Object oOne, Object oTwo)
         {
            return (getSortValue((Move)oTwo, ref) - getSortValue((Move)oOne, ref));
         }
      });
      return list;
   }


   /**
    * print. please delete.

   static public void printXkiller()
   {
      Iterator iterator = killerMoves[0].entrySet().iterator();
      while (iterator.hasNext())
      {
         Map.Entry o = (Map.Entry) iterator.next();
         System.out.println(o.getKey() + ":" + o.getValue());
      }
   }
    */
   
   /**
    * Initialize list of valued moves. (done for each move)
    */
   static public void initOrderedMoves()
   {
      valuedMoves = new ArrayList(INITIAL_CAPACITY);
      killerMoves[0].clear();
      killerMoves[1].clear();
   }

   /**
    * Find all relevant moves for player.
    *
    * @param player X- or Y-player, the next player
    * @param isMaxPly true if current ply is starting ply
    */
   public final void generateMoves(final int player, boolean isMaxPly)
   {
      if ( isMaxPly && valuedMoves.size() > 0)
      {
         Collections.sort(valuedMoves, new Comparator()
         {
            public int compare(Object o1, Object o2)
            {
               return (((ValuedMove)o1).value - ((ValuedMove)o2).value) * player;
            }
         });
         List orderedMoves = new ArrayList(INITIAL_CAPACITY);
         for (Iterator iterator = valuedMoves.iterator(); iterator.hasNext();)
         {
            ValuedMove valuedMove = (ValuedMove) iterator.next();
            orderedMoves.add(valuedMove.move);
         }
         valuedMoves.clear();
         moveIterator = orderedMoves.iterator();
      }
      else
      {
         generateNewMoves(player);
      }
   }

   /**
    * Find all relevant moves for player.
    *
    * @param player X- or Y-player, the next player
    */
   public final void generateNewMoves(final int player)
   {
      //currentPlayer = player;

      gameover = false;
      Set moves = new HashSet();
      Board ownBoard = match.getBoard(player);
      Evaluation ownEval = ownBoard.getEval();
      Board oppBoard = match.getBoard(-player);
      Evaluation oppEval = oppBoard.getEval();

      // eval for opponent
      oppEval.evaluateY(Board.YPLAYER);
      int oppVal = oppEval.valueOfY(true, Board.YPLAYER);

      // gameover?
      if (oppVal == 0)
      {
         gameover = true;
         return;
      }

      // own eval
      ownEval.evaluateY(Board.YPLAYER);
      ownEval.valueOfY(true, Board.YPLAYER);

      // check own critical points
      Set s = ownEval.getCritical();
      if (s.isEmpty())
      {
         //there are some situations where the last pin set has to be
         //   taken as last hope to find a good move
         int xc = match.getMoveX(match.getMoveNr());
         int yc = match.getMoveY(match.getMoveNr());
         s.add(new Evaluation.CritPos(xc, yc, Evaluation.CritPos.DOWN));
         s.add(new Evaluation.CritPos(xc, yc, Evaluation.CritPos.UP));
      }

      // iterate over all own critical points
      for (Iterator iter = s.iterator(); iter.hasNext();)
      {
         Evaluation.CritPos element = (Evaluation.CritPos) iter.next();

         int xe = element.getX();
         int ye = element.getY();
         if (!element.isDir())
         {
            // put pin (player is always yPlayer on own board)
            if (ownBoard.bridgeAllowed(xe, ye, 1)
                  && ownBoard.pinAllowed(xe - 1, ye - 2, Board.YPLAYER))
            {
               moves.add(new Move(xe - 1, ye - 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe, ye, 0)
                  && ownBoard.pinAllowed(xe - 2, ye - 1, Board.YPLAYER))
            {
               moves.add(new Move(xe - 2, ye - 1, player));
            }

            if (ownBoard.bridgeAllowed(xe, ye, 2)
                  && ownBoard.pinAllowed(xe + 1, ye - 2, Board.YPLAYER))
            {
               moves.add(new Move(xe + 1, ye - 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe, ye, 3)
                  && ownBoard.pinAllowed(xe + 2, ye - 1, Board.YPLAYER))
            {
               moves.add(new Move(xe + 2, ye - 1, player));
            }
         }
         else
         // (elements.isDir())
         {
            if (ownBoard.bridgeAllowed(xe - 1, ye + 2, 2)
                  && ownBoard.pinAllowed(xe - 1, ye + 2, Board.YPLAYER))
            {
               moves.add(new Move(xe - 1, ye + 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe - 2, ye + 1, 3)
                  && ownBoard.pinAllowed(xe - 2, ye + 1, Board.YPLAYER))
            {
               moves.add(new Move(xe - 2, ye + 1, player));
            }

            if (ownBoard.bridgeAllowed(xe + 1, ye + 2, 1)
                  && ownBoard.pinAllowed(xe + 1, ye + 2, Board.YPLAYER))
            {
               moves.add(new Move(xe + 1, ye + 2, player));
            }
            else if (ownBoard.bridgeAllowed(xe + 2, ye + 1, 0)
                  && ownBoard.pinAllowed(xe + 2, ye + 1, Board.YPLAYER))
            {
               moves.add(new Move(xe + 2, ye + 1, player));
            }
         }
         // try to find additional defensive moves using patterns
         moves.addAll(
               CheckPattern.getInstance().findPatternMoves(true, ownBoard, element, player));
      }

      // moves against opponent
      s = oppEval.getCritical();

      //iterate over all critical point of opponent
      for (Iterator iter = s.iterator(); iter.hasNext();)
      {
         Evaluation.CritPos element = (Evaluation.CritPos) iter.next();

         int ye = element.getX(); // CAUTION: swapped
         int xe = element.getY();
         xe += (element.isDir() ? 4 : -4);
         ye += ((ye > match.getYsize() / 2 + 3) ? -1 :
               (ye < match.getYsize() / 2 - 3) ? 1 : 0);
         // put pin (player is always yPlayer)
         if (ownBoard.pinAllowed(xe, ye, Board.YPLAYER))
         {
            moves.add(new Move(xe, ye, player));
         }
         // try to find additional defensive moves using patterns
         moves.addAll(
               CheckPattern.getInstance().findPatternMoves(false, oppBoard, element, player));

      }

      //no moves found?
      if (moves.isEmpty())
      {
         moves.add(randomMove(player));
      }

      moveIterator = sortMoves(moves, player).iterator();
      //orderedMoves = new ArrayList(moves);

   }

   /**
    * Find a random legal Move.
    * @return a random Move
    * @param player next player
    */
   private Move randomMove(int player)
   {
      int bx, by;
      Random rand = new Random();
      do
      {
         bx = rand.nextInt(match.getXsize());
         by = rand.nextInt(match.getYsize());
      } while (!match.getBoard(Board.YPLAYER).pinAllowed(bx, by, player));
      return new Move(bx, by);
   }


   /**
    * True, if game is over.
    * @return true if game is over
    */
   public final boolean isGameover()
   {
      return gameover;
   }

   /**
    * Return next move found.
    * @return move or null
    */
   public final Move getMove()
   {
      if (moveIterator.hasNext())
      {
         return (Move) moveIterator.next();
      }
      else
      {
         return null;
      }
   }

   /**
    * Add a move the list.
    * @param move the move
    * @param value its value
    */
   static public void addValuedMove(Move move, int value)
   {
       valuedMoves.add(new ValuedMove(move, value));
   }
}
