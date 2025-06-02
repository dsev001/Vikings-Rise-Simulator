package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// this class is called by main to run a number of rounds & create combatants, should have logic to pass uptime data between combatant instances
public class Simulator {
    HashMap<String,Boolean> uptimeDic = new HashMap<>();
    private List<Combatant> combatantList = new ArrayList<>();
    private List<Combatant> enemyCombatantList = new ArrayList<>(); 
    private List<CombatantInfo> friendlyCombatantsInfo = new ArrayList<>();
    private List<CombatantInfo> enemyCombatantsInfo = new ArrayList<>();
    private int idCount = 0; // for managing status effects
    private Combatant dummy = new Combatant(100, 100, 100, 200000, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A");
    //not sure if I'll have multiple enemies, gives me some room

    //add new combatants to the list
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

    //setters + getters
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
        //List<Double> friendlyDamageList = new ArrayList<>();
        //List<Double> enemyDamageList = new ArrayList<>();
        int friendlyBasics;
        int enemyBasics;
        for (Combatant combatant : combatantList) {
            combatant.reset(); //resets for when simulators called multiple times,
        }

        if (enemyCombatantList.isEmpty()) { 
            dummy.setCombatantId(999);
            enemyCombatantList.add(dummy); 
        }

        friendlyBasics = 0;
        enemyBasics = 0;
        for (Combatant combatant: combatantList){
            CombatantInfo holderCombatantInfo = combatant.getCombatantInfo();
            if (holderCombatantInfo.getBasicAttackCheck()) { friendlyBasics+=1; }
            friendlyCombatantsInfo.add(holderCombatantInfo);
        }

        for (Combatant combatant: enemyCombatantList){
            CombatantInfo holderCombatantInfo = combatant.getCombatantInfo();
            if (holderCombatantInfo.getBasicAttackCheck()) { enemyBasics+=1; }
            enemyCombatantsInfo.add(holderCombatantInfo);
        }
        
        for (int i = 0; i < rounds; i++){

            // number of attackers will default to 0 so no need to worry about flankers
            // need to modify this slightly, for counterattacks dealt and basics recieved are dif

            combatantList.get(0).setNumberEnemyAttackers(enemyBasics);
            enemyCombatantList.get(0).setNumberEnemyAttackers(friendlyBasics);

            for (Combatant friendlyCombatant : combatantList) {
                friendlyCombatant.roundInitialisation();
            }
            for (Combatant enemyCombatant : enemyCombatantList) {
                enemyCombatant.roundInitialisation();
            }
            // does basic attacks hitting the friendly main
            for (Combatant enemyCombatant : enemyCombatantList) {
                enemyCombatant.startPhase(friendlyCombatantsInfo.get(0));
                friendlyCombatantsInfo.set(0, enemyCombatant.getEnemyCombatant());
            }

            // does basic attacks hitting the enemy main
            for (Combatant friendlyCombatant : combatantList) {
                friendlyCombatant.startPhase(enemyCombatantsInfo.get(0));
                enemyCombatantsInfo.set(0, friendlyCombatant.getEnemyCombatant());
            }

            // does the friendly main counterattacking flankers
            for (int iter = 0; iter < enemyCombatantsInfo.size(); iter++) {
                CombatantInfo combatantInfo = enemyCombatantsInfo.get(iter);
                combatantList.get(0).counterattackPhase(combatantInfo);
                enemyCombatantsInfo.set(iter, combatantList.get(0).getEnemyCombatant());
            }
            // runs counters
            for (int iter = 0; iter < friendlyCombatantsInfo.size(); iter++) {
                CombatantInfo combatantInfo = friendlyCombatantsInfo.get(iter);
                enemyCombatantList.get(0).counterattackPhase(combatantInfo);
                friendlyCombatantsInfo.set(iter, enemyCombatantList.get(0).getEnemyCombatant());
            }
            //setting
            for (int iter = 0; iter < combatantList.size(); iter++) {
                combatantList.get(iter).setCombatantInfo(friendlyCombatantsInfo.get(iter));
                combatantList.get(iter).endPhase();
            }

            for (int iter = 0; iter < enemyCombatantList.size(); iter++) {
                enemyCombatantList.get(iter).setCombatantInfo(enemyCombatantsInfo.get(iter));
                enemyCombatantList.get(iter).endPhase();
            }
        }
    }

    private void roundCombat() {

    }
}
