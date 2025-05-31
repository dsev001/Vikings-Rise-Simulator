package test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Combatant {
    private HashMap<String, String> setupNames = new HashMap<>();
    Set<String> triggeredSet = new HashSet<>();
    //sent out 
    CombatantInfo combatantInfo;
    CombatantInfo enemyCombatant;
    private List<Skill> allSkills = new ArrayList<>();
    HashMap<String, Boolean> uptimeDic = new HashMap<>();
    // friendly vars
    private TotalCounter totalCounter = new TotalCounter();
    private List<StatusEffect> buffEffects = new ArrayList<>();
    private double basicAttackDamage;
    private double counterAttackDamage;
    private double dealtIncrease;
    private double healDealtIncrease;
    private double absorptionDealtIncrease;
    private double commandDealtIncrease;
    private double cooperationDealtIncrease;
    private double passiveDealtIncrease;
    private double counterattackDealtIncrease;
    private double burnDamageIncrease;
    private double poisonDamageIncrease;
    private double bleedDamageIncrease;
    private double lacerateDamageIncrease;
    private int numberEnemyAttackers;
    private int combatantId;

    // Constructor
    public Combatant(double attack, double defense, double health, int troopCount, String commander1Name, String commander2Name, String skill1Name, String skill2Name, String skill3Name, String skill4Name, String mountFirstSlot1Name, String mountFirstSlot2Name, String mountSecondSlot1Name, String mountSecondSlot2Name) {
        combatantInfo = new CombatantInfo(troopCount, attack, defense, health);
        setupNames.put("commander1", commander1Name);
        setupNames.put("commander2", commander2Name);
        setupNames.put("skill1", skill1Name);
        setupNames.put("skill2", skill2Name);
        setupNames.put("skill3", skill3Name);
        setupNames.put("skill4", skill4Name);
        setupNames.put("mountFirstSlot1", mountFirstSlot1Name);
        setupNames.put("mountFirstSlot2", mountFirstSlot2Name);
        setupNames.put("mountSecondSlot1", mountSecondSlot1Name);
        setupNames.put("mountSecondSlot2", mountSecondSlot2Name);
        createSkillsList();
        internalReset();
    }

    public void setCombatantId(int combatantId) { this.combatantId = combatantId; }

    private void internalReset() {
        combatantInfo.setRage(0);
        combatantInfo.setRound(1);
        combatantInfo.setActiveCounter(4);
        uptimeDic.put("burn",false);
        uptimeDic.put("bleed",false);
        uptimeDic.put("poison",false);
        uptimeDic.put("lacerate",false);
        uptimeDic.put("heal",false);
        uptimeDic.put("absorption",false);
        uptimeDic.put("shieldGranted",false);
        uptimeDic.put("heal",false);
        uptimeDic.put("lessUnits",false);
        uptimeDic.put("moreUnits",false);
        uptimeDic.put("Silence",false);
        totalCounter.reset();
        //roundEndReset(); // resets all values
    }

    public void reset() {
        internalReset();
    }
    
    private void createSkillsList() {
        //long startTime = System.nanoTime();
        allSkills.clear();

        // Use pre-loaded skill lookup from SkillDatabase
        Map<String, Map<String, String>> skillLookup = SkillDatabase.skillLookup;

        List<String> intermediaryBreakingStage = new ArrayList<>();
        boolean com1 = false;
        boolean com2 = false;
        boolean com1check = false;
        boolean com2check = false;
        String commander1active = "";
        String commander2active = "";

        // Resolve setupNames into base skill components
        for (Map.Entry<String, String> entry : setupNames.entrySet()) {
            String targetName = entry.getValue();
            if (targetName.equalsIgnoreCase("N/A")) continue;

            Map<String, String> skillEntry = skillLookup.get(targetName.toLowerCase());
            boolean match = false;

            if (skillEntry != null) {
                for (Map.Entry<String, String> field : skillEntry.entrySet()) {
                    String key = field.getKey();
                    String value = field.getValue();
                    if (key.equals("name") && value.equals(setupNames.get("commander1"))) {
                        com1 = true;
                    }
                    if (key.equals("name") && !value.equals(setupNames.get("commander1"))) {
                        com1 = false;
                    }
                    if (key.equals("name") && value.equals(setupNames.get("commander2"))) {
                        com2 = true;
                    }
                    if (key.equals("name") && !value.equals(setupNames.get("commander2"))) {
                        com2 = false;
                    }
                    if (!key.equals("name") && !"N/A".equalsIgnoreCase(value)) {
                        intermediaryBreakingStage.add(value);
                        match = true;
                    }
                    if (key.equals("awakenedSkill") && com1) {
                        commander1active = value;
                    }
                    if (key.equals("awakenedSkill") && com2) {
                        commander2active = value;
                    }
                }
            }

            if (!match) {
                System.out.println("No match found for " + targetName);
                System.out.println("check for error");
            }
        }
        // Break down skills into further components and filters
        for (String targetName : intermediaryBreakingStage) {
            Map<String, String> skillEntry = skillLookup.get(targetName.toLowerCase());
            boolean match = false;

            if (skillEntry != null) {
                for (Map.Entry<String, String> field : skillEntry.entrySet()) {
                    String key = field.getKey();
                    String value = field.getValue();
                    if (key.equalsIgnoreCase("name") && value.equalsIgnoreCase(commander1active)) {
                        com1check = true;
                    }
                    if (key.equalsIgnoreCase("name") && !value.equalsIgnoreCase(commander1active)) {
                        com1check = false;
                    }
                    if (key.equalsIgnoreCase("name") && value.equalsIgnoreCase(commander2active)) {
                        com2check = true;
                    }
                    if (key.equalsIgnoreCase("name") && !value.equalsIgnoreCase(commander2active)) {
                        com2check = false;
                    }

                    if (!key.equals("name") && !"N/A".equalsIgnoreCase(value)) {
                        Skill skill = Skill.loadFromJsonByName(value);
                        if (com1check) {
                            skill.setDependent("activeMain");
                        }
                        if (com2check) {
                            skill.setDependent("activeSecondary");
                        }
                        allSkills.add(skill);
                        match = true;
                    }
                }
            }

            if (!match) {
                Skill skill = Skill.loadFromJsonByName(targetName);
                allSkills.add(skill);
            }
        }

    //long endTime = System.nanoTime();
    //System.out.println((endTime - startTime) / 1_000_000 + " milliseconds");
    }

    //setters and getters
    public void setCombatantInfo(CombatantInfo combatantInfo) { this.combatantInfo = combatantInfo; }
    public double getFactorPerSecond(){ 
        System.out.println(totalCounter.getReductionFactorTotal()/(combatantInfo.getRound()-1));
        return totalCounter.getAccumulatedFactorTotal() / ((double)combatantInfo.getRound()-1);
    }
    public List<Skill> getAllSkills() { return allSkills; }
    public int getRound() { return combatantInfo.getRound(); }
    public CombatantInfo getCombatantInfo() { return combatantInfo; }
    public void setNumberEnemyAttackers(int numberEnemyAttackers) { this.numberEnemyAttackers = numberEnemyAttackers; }
    public CombatantInfo getEnemyCombatant() { return enemyCombatant; }
    // methods
    /*
    private double computeDamage() {

    }
    */

    // runs buff effects for your troops, then exchange phase
    public void roundInitialisation() {
        numberEnemyAttackers = 0; // for counterattack scaling logic
        //runBuffEffects();
    }

    //Start Phase ticks rounds, adds actives and triggered set
    public void startPhase(CombatantInfo enemyCombatant) {
        //runBuffEffects();
        this.enemyCombatant = enemyCombatant;
        // find uptimes, runs buffs and the bases for triggered set
        for (String damageEffectUptime : SkillDatabase.damageEffectSet) {
            uptimeDic.put(damageEffectUptime, enemyCombatant.isEffectActive(damageEffectUptime));
            //if (combatantId == 0) { System.out.println(damageEffectUptime + " " + enemyCombatant.isEffectActive(damageEffectUptime)); }
        }
        uptimeDic.put("absorption", combatantInfo.isAbsorptionActive());
        if (combatantInfo.getTroopCount() > enemyCombatant.getTroopCount()) { uptimeDic.put("moreUnits",true); }
        if (combatantInfo.getTroopCount() < enemyCombatant.getTroopCount()) { uptimeDic.put("moreUnits",true); }
        if (combatantInfo.getMainActive()) { triggeredSet.add("activeMain");triggeredSet.add("active"); }
        if (combatantInfo.getSecondaryActive()) { triggeredSet.add("activeSecondary");triggeredSet.add("active"); }

        double enemyEvasion = enemyCombatant.getEvasion(); // evasion prevents damage but not triggers
        if (Math.random() > enemyEvasion) {
            totalCounter.addDamageFactor((basicAttackDamage+1)*200);
            enemyCombatant.addDamageTaken(Scaler.scale((basicAttackDamage+1-enemyCombatant.getNullification())*200,combatantInfo.getAttack(),combatantInfo.getTroopCount()));
        }
        triggeredSet.add("basicAttack");
        triggeredSet.add("counterAttack");
        for (Skill skill : allSkills){
            if (skill.shouldTrigger(combatantInfo.getRound(), uptimeDic, triggeredSet)) {
                //scuffed but shouldn't error since its read in order
                triggeredSet.add(skill.getName()); // reuse triggered set to send to the target

                // break early if evasion, still needs triggered set to be added to though and it doesn't break not self buffs
                if (enemyEvasion != 0) {
                    if (Math.random() < enemyEvasion && !SkillDatabase.baseTypeSet.contains(skill.getEffectType())) { continue; }
                }

                if (skill.getEffectType().equalsIgnoreCase("directDamage")) {
                    switch (skill.getEffectType()) {
                        case "directDamage" -> countDamageFactor(skill);
                        case "absorption" -> countAbsorptionFactor(skill);
                        case "heal" -> countHealFactor(skill);
                    }
                }
                else if (SkillDatabase.baseTypeSet.contains(skill.getEffectType())) {
                    buffEffects.add(new StatusEffect(skill.getName(), skill.getEffectType(), skill.getDuration(), skill.getMagnitude()));
                }
                else {
                    StatusEffect debuff = new StatusEffect(skill.getName(), skill.getEffectType(), skill.getDuration(), skill.getMagnitude());
                    if (SkillDatabase.damageEffectSet.contains(debuff.getType())) {
                        // this is a damage debuff
                        double holder = debuff.getMagnitude();
                        switch (debuff.getType()) {

                            case "burnDamage" -> holder *= (1 + burnDamageIncrease);
                            case "poisonDamage" -> holder *= (1 + poisonDamageIncrease);
                            case "bleedDamage" -> holder *= (1 + bleedDamageIncrease);
                            case "lacerateDamage" -> holder *= (1 + lacerateDamageIncrease);
                        }
                        debuff.setMagnitude(Scaler.scale(holder,combatantInfo.getAttack(),combatantInfo.getTroopCount()));
                        totalCounter.addStatusFactor(holder*debuff.getDuration()); //adds status factors, done all at once since status will still do damage after changing target
                        enemyCombatant.addDamageDebuffEffect(combatantId,debuff);
                        continue;
                    }
                    enemyCombatant.addDebuffEffect(combatantId,debuff);
                }
            }
        }
    }

    public void counterattackPhase(CombatantInfo enemyCombatant) {
        this.enemyCombatant = enemyCombatant;
        double damage = 1 + counterAttackDamage - enemyCombatant.getNullification() - enemyCombatant.getCounterAttackDamageReduction();
        damage *= 200;
        //if (combatantId == 0) {System.out.println(damage);}
        double enemyEvasion = enemyCombatant.getEvasion();
        if (enemyEvasion != 0) {
            if (Math.random() < enemyEvasion) { return; }
        }
        if (damage > 0) {
            totalCounter.addDamageFactor(damage);
            enemyCombatant.addDamageTaken(Scaler.scale(damage, combatantInfo.getAttack(), combatantInfo.getTroopCount()));
        }
    }

    public void endPhase() {
        combatantInfo.tickRound();
        List<StatusEffect> expired = new ArrayList<>();
        for (StatusEffect buffEffect : buffEffects) {
            buffEffect.tick(); // decrease duration
            if (buffEffect.isExpired()) {
                expired.add(buffEffect);
            }
        }
        buffEffects.removeAll(expired);
        //for (StatusEffect buff : buffEffects) { System.out.print(buff.getName() + " "); }
        //System.out.println(" ");
        endRoundReset();
    }

    private void countAbsorptionFactor(Skill skill) {
        StatusEffect shield = new StatusEffect(skill.getName(), skill.getEffectType(), skill.getDuration(), Scaler.scale((skill.getMagnitude()*(1+absorptionDealtIncrease)),combatantInfo.getAttack(),combatantInfo.getTroopCount()));
        totalCounter.addAbsorptionFactor(skill.getMagnitude()*(1+absorptionDealtIncrease));
        combatantInfo.addAbsorption(shield);
    }

    private void countHealFactor(Skill skill) {
        StatusEffect heal = new StatusEffect(skill.getName(), skill.getEffectType(), skill.getDuration(), skill.getMagnitude()*(1+healDealtIncrease));
        totalCounter.addHealFactor(skill.getMagnitude()*(1+healDealtIncrease));
        buffEffects.add(heal);
    }

    private void endRoundReset() {
        triggeredSet.clear();
        basicAttackDamage=0;
        counterAttackDamage=0;
        dealtIncrease=0;
        healDealtIncrease=0;
        absorptionDealtIncrease=0;
        commandDealtIncrease=0;
        cooperationDealtIncrease=0;
        passiveDealtIncrease=0;
        counterattackDealtIncrease=0;
        burnDamageIncrease=0;
        poisonDamageIncrease=0;
        bleedDamageIncrease=0;
        lacerateDamageIncrease=0;
        numberEnemyAttackers=0;
    }

    private void runBuffEffects() {
        System.out.println(combatantInfo.getRound());
        uptimeDic.put("heal",false);
        for (StatusEffect effect : buffEffects) {
            switch (effect.getType()) {
                case "heal" -> { combatantInfo.addHeal(Scaler.scale(effect.getMagnitude(),combatantInfo.getAttack(),combatantInfo.getTroopCount())); uptimeDic.put("heal",true);}
                case "rage" -> combatantInfo.addRage(effect.getMagnitude());
                case "burnDealtIncrease" -> burnDamageIncrease += effect.getMagnitude();
                case "poisonDealtIncrease" -> poisonDamageIncrease += effect.getMagnitude();
                case "bleedDealtIncrease" -> bleedDamageIncrease += effect.getMagnitude();
                case "lacerateDealtIncrease" -> lacerateDamageIncrease += effect.getMagnitude();
                case "attackBoost" -> combatantInfo.addAttack(effect.getMagnitude());
                case "defenseBoost" -> combatantInfo.addDefense(effect.getMagnitude());
                case "healthBoost" -> combatantInfo.addHealth(effect.getMagnitude());
                case "retribution" -> {combatantInfo.addRetribution(effect.getMagnitude()); totalCounter.addRetribution(effect.getMagnitude());}
                case "evasion" -> {combatantInfo.addEvasion(effect.getMagnitude()); totalCounter.addEvasion(effect.getMagnitude());}
                case "nullificationIncrease" -> {combatantInfo.addNullification(effect.getMagnitude()); totalCounter.addNullification(effect.getMagnitude());}
                case "dealtIncrease" -> dealtIncrease += effect.getMagnitude();
                case "commandDealtIncrease" -> commandDealtIncrease += effect.getMagnitude();
                case "cooperationDealtIncrease" -> cooperationDealtIncrease += effect.getMagnitude();
                case "passiveDealtIncrease" -> passiveDealtIncrease += effect.getMagnitude();
                case "counterattackDealtIncrease" -> counterattackDealtIncrease += effect.getMagnitude();
                case "healDealtIncrease" -> healDealtIncrease += effect.getMagnitude();
                case "absorptionDealtIncrease" -> absorptionDealtIncrease += effect.getMagnitude();
                case "basicAttackDamage" -> basicAttackDamage += effect.getMagnitude();
                case "counterAttackDamage" -> counterAttackDamage += effect.getMagnitude();
                default -> System.out.println("error, type " + effect.getType() + " not found");
            }
        }
    }

    private void countDamageFactor(Skill skill) {
        double damage = skill.getMagnitude();
        double additionalDealt = 0;
        switch (skill.getCategorySpecification()) {
            case "command" -> additionalDealt += commandDealtIncrease;
            case "cooperation" -> additionalDealt += cooperationDealtIncrease;
            case "passive" -> additionalDealt += passiveDealtIncrease;
            case "counterattack" -> additionalDealt += counterattackDealtIncrease;
            case "active" -> additionalDealt += 0; // add in active damage increases
            case "N/A" -> {}
            default -> System.out.println("Unknown category specification: " + skill.getCategorySpecification());
        }
        damage *= (1 + dealtIncrease + additionalDealt - enemyCombatant.getNullification());
        totalCounter.addDamageFactor(damage);
        enemyCombatant.addDamageTaken(Scaler.scale(damage, combatantInfo.getAttack(), combatantInfo.getTroopCount()));
    }
}   
