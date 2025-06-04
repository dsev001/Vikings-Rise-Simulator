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
        simulate.setNewCombatant(196, 205, 231, 100000,
        "Sephina", "Vali",
        "Silencer", "Odin's Asylum", "Enrage",
        "N/A", "Bloodthirst Gaze", "N/A", 
        "N/A", "N/A");       
        //(166, 174, 198, 200000,
        simulate.setNewEnemyCombatant(196, 205, 231, 100000,
        "N/A", "N/A",
        "N/A", "N/A", "N/A",
        "N/A", "N/A", "N/A", 
        "N/A", "N/A");
        
        //simulate.findTrades(10000);
        simulate.runFights(1);
        //System.out.println("done");
        //simulate.runVerseRound(100000);
        //simulate.getFactorPerSecond();
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
