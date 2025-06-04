package test;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    // This HashMap seems unused in the provided code snippet.
    // HashMap<String,Boolean> uptimeDic = new HashMap<>();
    private List<Combatant> combatantList = new ArrayList<>();
    private List<Combatant> enemyCombatantList = new ArrayList<>();
    private int idCount = 0;
    // The dummy combatant should be handled carefully. If it's meant to be a permanent
    // placeholder when enemyList is empty, it might need to be part of the initial setup
    // of the main enemyCombatantList, or added to the *copy* of the list in singleFight.
    // For this fix, we assume enemyCombatantList is populated with actual enemies for runFights.
    private Combatant dummy = new Combatant(100, 100, 100, 200000, "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A");

    /**
     * Adds a new friendly combatant to the simulation.
     * Sets the combatant's ID and its initial troop count.
     */
    public void setNewCombatant(double attack, double defense, double health, int troopCount, String commander1Name, String commander2Name, String skill1Name, String skill2Name, String skill3Name, String skill4Name, String mountFirstSlot1Name, String mountFirstSlot2Name, String mountSecondSlot1Name, String mountSecondSlot2Name){
        Combatant combatant = new Combatant(attack, defense, health, troopCount, commander1Name, commander2Name, skill1Name, skill2Name, skill3Name, skill4Name, mountFirstSlot1Name, mountFirstSlot2Name, mountSecondSlot1Name, mountSecondSlot2Name);
        combatant.setCombatantId(idCount);
        combatantList.add(combatant);
        idCount++;
    }

    /**
     * Adds a new enemy combatant to the simulation.
     * Sets the combatant's ID and its initial troop count.
     */
    public void setNewEnemyCombatant(double attack, double defense, double health, int troopCount, String commander1Name, String commander2Name, String skill1Name, String skill2Name, String skill3Name, String skill4Name, String mountFirstSlot1Name, String mountFirstSlot2Name, String mountSecondSlot1Name, String mountSecondSlot2Name){
        Combatant combatant = new Combatant(attack, defense, health, troopCount, commander1Name, commander2Name, skill1Name, skill2Name, skill3Name, skill4Name, mountFirstSlot1Name, mountFirstSlot2Name, mountSecondSlot1Name, mountSecondSlot2Name);
        combatant.setCombatantId(idCount);
        enemyCombatantList.add(combatant);
        idCount++;
    }

    /**
     * Concatenates all skills from friendly combatants.
     * @return A string containing all skills.
     */
    public String getAllSkills() {
        String output = "";
        for (Combatant combatant : combatantList){
            output+=combatant.getAllSkills() + " ";
        }
        return output;
    }

    /**
     * Calculates the total factor per second for all friendly combatants.
     * @return The sum of factor per second for friendly combatants.
     */
    public double getFactorPerSecond(){
        double total = 0;
        for (Combatant combatant : combatantList){
            total+=combatant.getFactorPerSecond();
        }
        return total;
    }

    /**
     * Runs a single verse round simulation for a specified number of rounds.
     * Note: This method will modify the main combatant lists. If it also needs to be reusable
     * without modifying the main lists, it would require similar modifications to `runFights`.
     * @param rounds The number of rounds to simulate.
     */
    public void runVerseRound(int rounds){
        setup(); // Resets the main combatant lists to their initial state
        for (int i = 0; i < rounds; i++){
            // This calls the overloaded roundCombat that operates on the main lists.
            roundCombat(combatantList, enemyCombatantList);
        }
    }

    /**
     * Runs multiple fight simulations. Each fight starts with combatants in their initial state.
     * @param fights The number of fights to simulate.
     * @throws IllegalStateException If combatant lists are empty.
     */
    public void runFights(int fights) {
        if (combatantList.isEmpty() || enemyCombatantList.isEmpty()) {
            throw new IllegalStateException("Combatant lists must be initialized before running fights.");
        }
        int wins = 0;
        int loss = 0;

        for (int i = 0; i < fights; i++) {
            // Before each fight, reset all combatants in the main lists to their initial state.
            setup();

            // Create new ArrayLists (shallow copies) for the current fight.
            // This ensures that removing defeated combatants in singleFight doesn't affect
            // the original combatantList and enemyCombatantList for subsequent fights.
            List<Combatant> currentFriendlyCombatants = new ArrayList<>(combatantList);
            List<Combatant> currentEnemyCombatants = new ArrayList<>(enemyCombatantList);

            // Run a single fight with the copies.
            if (singleFight(currentFriendlyCombatants, currentEnemyCombatants)) {
                wins++;
            } else {
                loss++;
            }   
        }

        System.out.println("Friendly Wins: " + wins);
        System.out.println("Friendly Losses: " + loss);
    }

    /**
     * Simulates a single fight between two lists of combatants.
     * This method operates on the provided lists (which should be copies) and modifies them.
     * @param currentFriendlyCombatants The list of friendly combatants for this fight.
     * @param currentEnemyCombatants The list of enemy combatants for this fight.
     * @return true if friendly combatants win, false otherwise.
     */
    private boolean singleFight(List<Combatant> currentFriendlyCombatants, List<Combatant> currentEnemyCombatants) {
        //System.out.println(currentEnemyCombatants.get(0).getCombatantInfo().getTroopCount());
        //System.out.println(currentFriendlyCombatants.get(0).getCombatantInfo().getTroopCount());
        while (true) {
            // Perform one round of combat using the current friendly and enemy combatants.
            roundCombat(currentFriendlyCombatants, currentEnemyCombatants);

            // Remove defeated friendly combatants from the *current* fight's list.
            // Iterate backwards to avoid ConcurrentModificationException when removing elements.
            for (int iter = currentFriendlyCombatants.size() - 1; iter >= 0; iter--) {
                Combatant combatant = currentFriendlyCombatants.get(iter);
                if (combatant.getCombatantInfo().getTroopCount() <= 0) {
                    currentFriendlyCombatants.remove(iter);
                }
            }

            // Remove defeated enemy combatants from the *current* fight's list.
            for (int iter = currentEnemyCombatants.size() - 1; iter >= 0; iter--) {
                Combatant combatant = currentEnemyCombatants.get(iter);
                if (combatant.getCombatantInfo().getTroopCount() <= 0) {
                    currentEnemyCombatants.remove(iter);
                }
            }

            // Check for victory conditions based on the *current* fight's lists.
            if (currentFriendlyCombatants.isEmpty()) {
                return false; // Enemy victory
            }

            if (currentEnemyCombatants.isEmpty()) {
                return true; // Friendly victory
            }
        }
    }

    /**
     * Resets all combatants in the main `combatantList` and `enemyCombatantList`
     * to their initial troop counts and other default states.
     * This method is called before each fight simulation to ensure a fresh start.
     */
    private void setup() {
        // Reset friendly combatants
        for (Combatant combatant : combatantList) {
            combatant.reset(); // Calls resetTroops and other state resets
        }

        // Reset enemy combatants
        for (Combatant combatant : enemyCombatantList) {
            combatant.reset(); // Calls resetTroops and other state resets
        }

        // The original dummy logic:
        // if (enemyCombatantList.isEmpty()) { dummy.setCombatantId(999); enemyCombatantList.add(dummy); }
        // This line permanently adds the dummy to the class's enemyCombatantList.
        // For `runFights` to work repeatedly with original lists, this should be avoided here.
        // If a dummy is needed when the enemy list is empty, it should be added to the *copy*
        // of the list within `singleFight` if `currentEnemyCombatants` becomes empty.
        // For this solution, we assume `enemyCombatantList` is populated before `runFights` is called.
    }

    /**
     * Performs one round of combat logic between two provided lists of combatants.
     * This overloaded method allows `roundCombat` to operate on temporary lists
     * without modifying the main class-level lists.
     * @param friendlyCombatants The list of friendly combatants for this round.
     * @param enemyCombatants The list of enemy combatants for this round.
     */
    private void roundCombat(List<Combatant> friendlyCombatants, List<Combatant> enemyCombatants) {
        // Count basic attackers
        int friendlyBasics = 0;
        int enemyBasics = 0;

        for (Combatant combatant : friendlyCombatants) {
            if (combatant.getCombatantInfo().getBasicAttackCheck()) {
                friendlyBasics++;
            }
        }

        for (Combatant combatant : enemyCombatants) {
            if (combatant.getCombatantInfo().getBasicAttackCheck()) {
                enemyBasics++;
            }
        }

        // Set number of attackers on the combatant info of the first combatant in each list.
        // This assumes the first combatant represents the "main" target/attacker for the group.
        if (!friendlyCombatants.isEmpty()) {
            friendlyCombatants.get(0).setNumberEnemyAttackers(enemyBasics);
        }
        if (!enemyCombatants.isEmpty()) {
            enemyCombatants.get(0).setNumberEnemyAttackers(friendlyBasics);
        }

        // Round initialization for all combatants
        for (Combatant combatant : friendlyCombatants) {
            combatant.roundInitialisation();
        }
        for (Combatant combatant : enemyCombatants) {
            combatant.roundInitialisation();
        }

        // Enemy attacks on friendly main (if both sides have combatants)
        if (!friendlyCombatants.isEmpty() && !enemyCombatants.isEmpty()) {
            for (Combatant enemyCombatant : enemyCombatants) {
                enemyCombatant.startPhase(friendlyCombatants.get(0).getCombatantInfo());
            }
        }

        // Friendly attacks on enemy main (if both sides have combatants)
        if (!friendlyCombatants.isEmpty() && !enemyCombatants.isEmpty()) {
            for (Combatant friendlyCombatant : friendlyCombatants) {
                friendlyCombatant.startPhase(enemyCombatants.get(0).getCombatantInfo());
            }
        }

        // Counterattacks - friendly main counterattacking enemies
        if (!friendlyCombatants.isEmpty()) {
            for (Combatant enemyCombatant : enemyCombatants) {
                friendlyCombatants.get(0).counterattackPhase(enemyCombatant.getCombatantInfo());
            }
        }

        // Counterattacks - enemy main counterattacking friendlies
        if (!enemyCombatants.isEmpty()) {
            for (Combatant friendlyCombatant : friendlyCombatants) {
                enemyCombatants.get(0).counterattackPhase(friendlyCombatant.getCombatantInfo());
            }
        }

        // End phase for all combatants
        for (Combatant combatant : friendlyCombatants) {
            combatant.endPhase();
        }

        for (Combatant combatant : enemyCombatants) {
            combatant.endPhase();
        }
    }

    /**
     * Original roundCombat method. It now calls the overloaded version,
     * operating on the class's main combatant lists.
     * This is kept for compatibility with `runVerseRound`.
     */
    private void roundCombat() {
        roundCombat(combatantList, enemyCombatantList);
    }
}
