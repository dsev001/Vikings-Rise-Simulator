package test;
// should add something for graphing
//import java.io.File;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //File file = new File("demo/src/main/java/test/SkillDatabase.json");
        //System.out.println("Exists? " + file.exists());
        //System.out.println("Absolute path: " + file.getAbsolutePath());

        //need some kind of ui later, should add validation
        Simulator simulate = new Simulator();
    
        simulate.setNewCombatant(166*(5), 174*(4), 198*(2), 200000,
        "Sephina", "Vali",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Soul of Fury", "Strangled Death", 
        "Divine Awe", "Divine Awe");

        simulate.setNewEnemyCombatant(166*(5), 174*(4), 198*(2), 200000,
        "Sephina", "Vali",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Strangled Death", "Soul of Fury", 
        "Icicle Armor", "Agonizing Frost");
        //(166, 174, 198, 200000,


        /*

        "Sephina", "Vali",
        "First Strike", "Odin's Asylum", "Berserk Killing Machine",
        "Rage Leech", "Bloodthirst Gaze", "Strangled Death", 
        "N/A", "N/A");

        simulate.setNewCombatant(166*(5), 174*(4), 198*(2), 100000,
        "Sephina", "Vali",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Soul of Fury", "Strangled Death", 
        "Divine Awe", "Divine Awe");
        //(166, 174, 198, 200000,

        simulate.setNewEnemyCombatant(166*(5), 174*(4), 198*(2), 100000,
        "Sephina", "Vali",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Bloodthirst Gaze", "Strangled Death", 
        "Divine Awe", "Icicle Armor");

        simulate.setNewCombatant(172*(4.6), 196*(4), 169*(2), 100000,
        "Gunnar", "Hilda",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Soul of Fury", "Strangled Death", 
        "Divine Awe", "Divine Awe");
        //(166, 174, 198, 200000,


        simulate.setNewEnemyCombatant(166*(5), 174*(4), 198*(2), 100000,
        "Sephina", "Vali",
        "Disarmament", "Silencer", "Silent Invasion",
        "Einherjar's Oath", "Bloodthirst Gaze", "Strangled Death", 
        "Divine Awe", "Icicle Armor");

        simulate.setNewEnemyCombatant(196*(5), 205*(4), 231*(2), 200000,
        "Sephina", "Vali",
        "Silencer", "Silent Invasion", "Einherjar's Oath",
        "Disarmament", "Bloodthirst Gaze", "Strangled Death", 
        "Agonizing Frost", "Fatal Chomp");

        simulate.setNewEnemyCombatant(196*(5), 205*(4), 231*(2), 100000,
        "Vali", "Sephina",
        "Silencer", "Silent Invasion", "Einherjar's Oath",
        "Disarmament", "Bloodthirst Gaze", "Stinging Tongue", 
        "Divine Awe", "Divine Awe");

        simulate.setNewCombatant(196*(5), 205*(4), 231*(2), 200000,
        "Alf", "Olena", 
        "Einherjar's Oath", "Silent Invasion", "Berserk Killing Machine",
        "Disarmament", "Bloodthirst Gaze", "N/A", 
        "N/A", "N/A");  

        simulate.setNewEnemyCombatant(196*(5), 205*(4), 231*(2), 200000,
        "Vali", "Sephina",
        "Einherjar's Oath", "Silent Invasion", "Berserk Killing Machine",
        "Enrage", "Bloodthirst Gaze", "N/A", 
        "N/A", "N/A"); 
        */
        
        
        simulate.findTrades(100000);
        //simulate.runFights(100000);
        //System.out.println("done");
        //simulate.runVerseRound(100000);
        //simulate.getFactorPerSecond(); // fix fps counter for status effects
        /*
        simulate.setNewEnemyCombatant(300, 200, 100, 100000,
        "Sephina", "N/A",
        "First Strike", "Poison Arrow", "N/A",
        "N/A", "N/A", "N/A", 
        "N/A", "N/A");
        */
        
        
        
        int round = 5;
        double total = 0;
        int repeats = 1500000;
        //simulate.runRound(1);
        //System.out.println(simulate.getFactorPerSecond());
        //groupRoundSimulator(30,simulate);
        //simulate.runVerseRound(1000000);
        //System.out.println(simulate.getFactorPerSecond());

        //System.out.println(simulate.getAllSkills());
    }
}
