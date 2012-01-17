/**
 *
 * Created by IntelliJ IDEA.
 * @author Johannes Schwagereit (mail(at)johannes-schwagereit.de)
 */
package net.schwagereit.t1j;

import java.util.prefs.Preferences;

/**
 * Store and save general parameter, which are not stored as Matchdata.
 * This is a singleton.
 */
public class GeneralSettings
{
   private static final GeneralSettings ourInstance = new GeneralSettings();

   public boolean mdFixedPly = true;

   private static final int DEFAULT_PLY = 5;

   public int mdPly = DEFAULT_PLY; // searchdepth
   public int mdTime = DEFAULT_PLY; // alternatively: time per computer-move

   /**
    * Return the GeneralSettings-Object.
    *
    * @return GeneralSettings-Object
    */
   public static GeneralSettings getInstance()
   {
      return ourInstance;
   }

   /**
    * Constructor - no external instance.
    */
   private GeneralSettings()
   {
      Preferences userPrefs = Preferences.userRoot().node(MatchData.PREFS_PATH);
      mdFixedPly = userPrefs.getBoolean("FixedPly", true);
      mdPly = userPrefs.getInt("Ply", DEFAULT_PLY);
      mdTime = userPrefs.getInt("Time", DEFAULT_PLY);
      
      correct();
   }

   /**
    * Save preferences.
    */
   public void savePreferences()
   {
      Preferences userPrefs = Preferences.userRoot().node(MatchData.PREFS_PATH);
      userPrefs.putBoolean("FixedPly", mdFixedPly);
      userPrefs.putInt("Ply", mdPly);
      userPrefs.putInt("Time", mdTime);
   }

   /**
    * Correct any illegal data.
    */
   public void correct()
   {
      if (mdPly < 1)
      {
         mdPly = 5;
      }
      if (mdTime < 1)
      {
         mdTime = 5;
      }
   }




}
