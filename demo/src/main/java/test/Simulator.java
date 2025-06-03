package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simulator {
    HashMap<String,Boolean> uptimeDic = new HashMap<>();
    private List<Combatant> combatantList = new ArrayList<>();
    private List<Combatant> enemyCombatantList = new ArrayList<>(); 
    private int idCount = 0;
    private Combatant dummy = new Combatant(100, 100, 100, 200000, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A");

    public void setNewCombatant(double attack, double defense, double health, int troopCount, String commander1Name, String commander2Name, String skill1Name, String skill2Name, String skill3Name, String skill4Name, String mountFirstSlot1Name, String mountFirstSlot2Name, String mountSecondSlot1Name, String mountSecondSlot2Name){
        Combatant combatant = new Combatant(attack, defense, health, troopCount, commander1Name, commander2Name, skill1Name, skill2Name, skill3Name, skill4Name, mountFirstSlot1Name, mountFirstSlot2Name, mountSecondSlot1Name, mountSecondSlot2Name);
        combatant.setCombatantId(idCount);
        combatantList.add(combatant);
        idCount++;
    }

    public void setNewEnemyCombatant(double attack, double defense, double health, int troopCount, String commander1Name, String commander2Name, String skill1Name, String skill2Name, String skill3Name, String skill4Name, String mountFirstSlot1Name, String mountFirstSlot2Name, String mountSecondSlot1Name, String mountSecondSlot2Name){
        Combatant combatant = new Combatant(attack, defense, health, troopCount, commander1Name, commander2Name, skill1Name, skill2Name, skill3Name, skill4Name, mountFirstSlot1Name, mountFirstSlot2Name, mountSecondSlot1Name, mountSecondSlot2Name);
        combatant.setCombatantId(idCount);
        enemyCombatantList.add(combatant);
        idCount++;
    }

    public String getAllSkills() {
        String output = "";
        for (Combatant combatant : combatantList){
            output+=combatant.getAllSkills() + " ";
        }
        return output;      
    }

    public double getFactorPerSecond(){
        double total = 0;
        for (Combatant combatant : combatantList){
            total+=combatant.getFactorPerSecond();
        }
        return total;
    }

    public void runVerseRound(int rounds){     
        setup();   
        for (int i = 0; i < rounds; i++){
            roundCombat();
        }
    }

    public void runFights() {
        if (combatantList.isEmpty() || enemyCombatantList.isEmpty()) {
            throw new IllegalStateException("Combatant lists must be initialized before running fights.");
        }

        setup();

        while (true) {
            System.out.println("Round " + combatantList.get(0).getCombatantInfo().getRound() + 
                             " - Friendly troops: " + combatantList.get(0).getCombatantInfo().getTroopCount());
            System.out.println("Round " + enemyCombatantList.get(0).getCombatantInfo().getRound() + 
                    " - Enemy troops: " + enemyCombatantList.get(0).getCombatantInfo().getTroopCount());
            
            roundCombat();

            // Remove defeated combatants
            for (int iter = combatantList.size() - 1; iter >= 0; iter--) {
                Combatant combatant = combatantList.get(iter);
                if (combatant.getCombatantInfo().getTroopCount() <= 0) {
                    combatantList.remove(iter);
                }
            }

            for (int iter = enemyCombatantList.size() - 1; iter >= 0; iter--) {
                Combatant combatant = enemyCombatantList.get(iter);
                if (combatant.getCombatantInfo().getTroopCount() <= 0) {
                    enemyCombatantList.remove(iter);
                }
            }

            if (combatantList.isEmpty()) {
                System.out.println("Enemy victory!");
                for (Combatant combatant : enemyCombatantList) {
                    System.out.println("Enemy remaining troops: " + combatant.getCombatantInfo().getTroopCount());
                }
                break;
            }

            if (enemyCombatantList.isEmpty()) {
                System.out.println("Friendly victory!");
                for (Combatant combatant : combatantList) {
                    System.out.println("Friendly remaining troops: " + combatant.getCombatantInfo().getTroopCount());
                }
                break;
            }
        }
    }

    private void setup() {
        for (Combatant combatant : combatantList) {
            combatant.reset();
        }

        if (enemyCombatantList.isEmpty()) { 
            dummy.setCombatantId(999);
            enemyCombatantList.add(dummy); 
        }
    }

    private void roundCombat() {
        // Count basic attackers
        int friendlyBasics = 0;
        int enemyBasics = 0;
        
        for (Combatant combatant : combatantList) {
            if (combatant.getCombatantInfo().getBasicAttackCheck()) { 
                friendlyBasics++; 
            }
        }

        for (Combatant combatant : enemyCombatantList) {
            if (combatant.getCombatantInfo().getBasicAttackCheck()) { 
                enemyBasics++; 
            }
        }

        // Set number of attackers
        if (!combatantList.isEmpty()) {
            combatantList.get(0).setNumberEnemyAttackers(enemyBasics);
        }
        if (!enemyCombatantList.isEmpty()) {
            enemyCombatantList.get(0).setNumberEnemyAttackers(friendlyBasics);
        }

        // Round initialization
        for (Combatant combatant : combatantList) {
            combatant.roundInitialisation();
        }
        for (Combatant combatant : enemyCombatantList) {
            combatant.roundInitialisation();
        }

        // Enemy attacks on friendly main (if both sides have combatants)
        if (!combatantList.isEmpty() && !enemyCombatantList.isEmpty()) {
            for (Combatant enemyCombatant : enemyCombatantList) {
                enemyCombatant.startPhase(combatantList.get(0).getCombatantInfo());
            }
        }

        // Friendly attacks on enemy main (if both sides have combatants)
        if (!combatantList.isEmpty() && !enemyCombatantList.isEmpty()) {
            for (Combatant friendlyCombatant : combatantList) {
                friendlyCombatant.startPhase(enemyCombatantList.get(0).getCombatantInfo());
            }
        }

        // Counterattacks - friendly main counterattacking enemies
        if (!combatantList.isEmpty()) {
            for (Combatant enemyCombatant : enemyCombatantList) {
                combatantList.get(0).counterattackPhase(enemyCombatant.getCombatantInfo());
            }
        }

        // Counterattacks - enemy main counterattacking friendlies
        if (!enemyCombatantList.isEmpty()) {
            for (Combatant friendlyCombatant : combatantList) {
                enemyCombatantList.get(0).counterattackPhase(friendlyCombatant.getCombatantInfo());
            }
        }

        // End phase for all combatants
        for (Combatant combatant : combatantList) {
            combatant.endPhase();
        }

        for (Combatant combatant : enemyCombatantList) {
            combatant.endPhase();
        }
    }
}