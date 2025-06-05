package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CombatantInfo {
    // This class is used to store combatants info for passing between combatants
    // round info
    private boolean basicAttackCheck;
    private int round;
    private int activeCounter;
    private int troopChange;
    private int troopHealed;
    // stats of the combatant
    private int troopCount;
    private double rage;
    private double attack;
    private double defense;
    private double health;
    private List<StatusEffect> absorptionList = new ArrayList<>();
    private List<StatusEffect> holderAbsorptionList = new ArrayList<>();
    // properties of the combatant
    private DebuffEffectCollection debuffEffectCollection = new DebuffEffectCollection();
    private HashMap<String,Boolean> uptimeDic = new HashMap<>();
    private double nullification;
    private double counterAttackDamageReduction;
    private double evasion;
    private double retribution;
    private double retributionDamage;
    private double attackBoost;
    private double defenseBoost;
    private double healthBoost;
    // setters
    public void setRound(int round) { this.round = round; }
    public void setActiveCounter(int activeCounter) { this.activeCounter = activeCounter; }
    public void setTroopCount(int troopCount) { this.troopCount = troopCount; }
    public void setRage(double rage) { this.rage = rage; }
    public void setAttack(double attack) { this.attack = attack; }
    public void setDefense(double defense) { this.defense = defense; }
    public void setHealth(double health) { this.health = health; }
    public void setNullification(double nullification) { this.nullification = nullification; }
    public void setCounterAttackDamageReduction(double counterAttackDamageReduction) { this.counterAttackDamageReduction = counterAttackDamageReduction; }
    public void setEvasion(double evasion) { this.evasion = evasion; }
    public void setRetribution(double retribution) { this.retribution = retribution; }
    // getters
    public int getRound() { return round; }
    public int getActiveCounter() { return activeCounter; }
    public int getTroopCount() { return troopCount; }
    public double getRage() { return rage; }
    public double getAttack() { return attack + attackBoost; }
    public double getDefense() { return defense + defenseBoost; }
    public double getHealth() { return health + healthBoost; }
    public double getNullification() { return nullification; }
    public double getCounterAttackDamageReduction() { return counterAttackDamageReduction; }
    public double getEvasion() { return evasion; }

    public boolean getBasicAttackCheck() { return basicAttackCheck; }
    public boolean getMainActive() { return activeCounter == 1 && !debuffEffectCollection.isEffectActive("silence"); }
    public boolean getSecondaryActive() { return activeCounter == 3 && !debuffEffectCollection.isEffectActive("silence"); }
    public boolean checkEvasion() { return evasion != 0; }
    public boolean checkRetribution() { return retribution != 0; }
    public double getRetributionDamage() { double holder = retributionDamage; retributionDamage=0; return holder; }

    // constructor
    public CombatantInfo(int troopCount, double attack, double defense, double health) {
        this.round = 1; // no tick at start
        this.activeCounter = 4;
        this.troopCount = troopCount;
        this.attack = attack;
        this.defense = defense;
        this.health = health;
        this.rage = 0;
        this.retributionDamage = 0;
        this.basicAttackCheck = true;
        this.troopChange = 0;
    }

    // methods
    public void tickRound() {
        // tick the round
        //System.out.println(troopChange);
        //System.out.println(activeCounter);
        round++;
        troopCount+=troopChange;
        List<StatusEffect> expired = new ArrayList<>();
        troopCount+=troopHealed;
        // transfer shields over
        for (StatusEffect newEffect : holderAbsorptionList) {
            boolean replaced = false;
            for (int i = 0; i < absorptionList.size(); i++) {
                StatusEffect existing = absorptionList.get(i);
                if (existing.getName().equals(newEffect.getName())) {
                    absorptionList.set(i, newEffect); // Replace old effect with new one if exists already
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                absorptionList.add(newEffect);
            }
        }
        holderAbsorptionList.clear();

        holderAbsorptionList.clear();
        for (StatusEffect absorptionEffect : absorptionList) {
            absorptionEffect.tick(); // decrease duration
            if (absorptionEffect.isExpired()) {
                expired.add(absorptionEffect);
            }
        }
        absorptionList.removeAll(expired);
        // reset values
        basicAttackCheck = true;
        attackBoost = 0;
        defenseBoost = 0;
        healthBoost = 0;
        retributionDamage = 0;
        troopChange = 0;
        nullification = 0;
        evasion = 0;
        retribution = 0;
        troopHealed = 0;

        debuffEffectCollection.runInfo();
        addDamageTaken(debuffEffectCollection.getTotalDamage()); // so that damage counts the round after like ingame
        rage -= debuffEffectCollection.getRageDamp();
        attackBoost -= debuffEffectCollection.getAttackDamp();
        defenseBoost -= debuffEffectCollection.getDefenseDamp();
        healthBoost -= debuffEffectCollection.getHealthDamp();
        debuffEffectCollection.tickAll();
    }

    public void tickRage() {
        if (rage == 0) { rage+=100; }
        else { rage+=90; }
        if (rage >= 1000) {
            rage = 0;
            activeCounter = 0;
        }
        //if (debuffEffectCollection.isEffectActive("silence")) {System.out.println("Silence active");}
        if (!debuffEffectCollection.isEffectActive("silence")) { activeCounter++; }
    }

    public void addRage (double rage) { this.rage += rage; }
    public void addAttack (double attackBoost) { this.attackBoost += attackBoost; }
    public void addDefense (double defenseBoost) { this.defenseBoost += defenseBoost; }
    public void addHealth (double healthBoost) { this.healthBoost += healthBoost; }
    public void addRetribution (double retribution) { this.retribution += retribution; }
    public void addEvasion (double evasion) { this.evasion += evasion; }
    public void addNullification (double nullification) { this.nullification += nullification; }
    public boolean isEffectActive(String type) { return debuffEffectCollection.isEffectActive(type); }
    public boolean isAbsorptionActive() { return !absorptionList.isEmpty(); }
    public double getRetribution() { return retribution; }
    public int getTroopHealed() { return troopHealed; }

    public void addDamageTaken (double scaledDamage) { 
        scaledDamage /= defense;
        retributionDamage = scaledDamage * retribution; // done after your own defense but not health, and does reflect damage taken by shields, check if helps on statuses
        for (StatusEffect absorption : absorptionList) {
            double holder = absorption.getMagnitude();
            if (holder > 0) {
                if (holder < scaledDamage) {scaledDamage -= holder; absorption.setMagnitude(0);}
                else {absorption.addMagnitude(-scaledDamage); return;}
            }
        }
        scaledDamage /= health;
        troopChange -= scaledDamage;
    }

    public void addDamageTakenPostDefense (double scaledDamagePostDefense) {
        scaledDamagePostDefense /= health;
        //System.out.println(scaledDamagePostDefense);
        troopChange -= scaledDamagePostDefense;
        // check if retribution can be blocked by shields
    }

    public void addAbsorption (StatusEffect scaledAbsorption) {
        // should already be divided by targets defense to scale the magnitude
        //System.out.println(scaledAbsorption.getMagnitude());
        holderAbsorptionList.add(scaledAbsorption);
    }

    public void addHeal (double scaledHealing) {
        // should already be divided by targets defense
        scaledHealing /= health;
        // doesn't add to troop change so heavily wounded pre heal can be checked
        troopHealed += scaledHealing;
    }

    public void addDebuffEffect (int id, StatusEffect statusEffect) {
        debuffEffectCollection.addEffect(id, statusEffect);
    }

    public void addDamageDebuffEffect (int id, StatusEffect statusEffect) {
        // need to scale using defence
        double holder = statusEffect.getMagnitude();
        holder /= defense;
        holder /= health;
        statusEffect.setMagnitude(holder);
        debuffEffectCollection.addEffect(id, statusEffect);
    }

    public void resetRound() {
        debuffEffectCollection = new DebuffEffectCollection();
        round = 1;
        rage = 0; // to hit the 100 r1
        activeCounter=4;
    }
}
