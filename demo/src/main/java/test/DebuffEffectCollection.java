package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DebuffEffectCollection {
    private HashMap<Integer, List<StatusEffect>> effectsById = new HashMap<>();
    private HashMap<String, Boolean> activeEffectTypes = new HashMap<>();
    double damageTotal;
    double attackDamp;
    double defenseDamp;
    double healthDamp;
    double rageDamp;

    public double getTotalDamage() { return damageTotal; }
    public double getAttackDamp() { return attackDamp; }
    public double getDefenseDamp() { return defenseDamp; }
    public double getHealthDamp() { return healthDamp; }
    public double getRageDamp() { return rageDamp; }
    public boolean isEffectActive(String type) { return activeEffectTypes.getOrDefault(type, false); }

    public void removeEffectRandom() {
        // Collect all active effects into a single list along with their parent ID
        // We'll use a custom small class or just a pair to hold both.
        // For simplicity here, let's collect all effects first, then find their origin.
        List<StatusEffect> allEffects = new ArrayList<>();
        for (List<StatusEffect> effectList : effectsById.values()) {
            allEffects.addAll(effectList);
        }

        // If there are no effects, do nothing
        if (allEffects.isEmpty()) {
            return;
        }

        // Select a random effect
        Random rand = new Random();
        StatusEffect effectToRemove = allEffects.get(rand.nextInt(allEffects.size()));

        // Now, iterate through effectsById to find and remove the chosen effect
        // We need to iterate through the entries to modify the original lists
        // and potentially remove keys from the HashMap.
        Iterator<Integer> idIterator = effectsById.keySet().iterator();
        while (idIterator.hasNext()) {
            Integer id = idIterator.next();
            List<StatusEffect> effectList = effectsById.get(id);
            if (effectList != null) {
                if (effectList.remove(effectToRemove)) { // This attempts to remove the object
                    // If the effect was found and removed from this list,
                    // check if the list is now empty and remove the key.
                    if (effectList.isEmpty()) {
                        idIterator.remove(); // Safely remove the key from the map
                    }
                    break; // Effect found and removed, no need to continue searching
                }
            }
        }

        // Update activeEffectTypes for the removed effect's type
        // Check if any other effect of the same type still exists
        boolean typeStillActive = false;
        for (List<StatusEffect> list : effectsById.values()) {
            for (StatusEffect effect : list) {
                if (effect.getType().equals(effectToRemove.getType())) {
                    typeStillActive = true;
                    break;
                }
            }
            if (typeStillActive) break;
        }
        activeEffectTypes.put(effectToRemove.getType(), typeStillActive);
    }


    public void addEffect(int id, StatusEffect effect) {
        effectsById.computeIfAbsent(id, k -> new ArrayList<>()).add(effect);
        activeEffectTypes.put(effect.getType(), true);
    }

    public void tickAll() {
        damageTotal = 0;
        attackDamp = 0;
        defenseDamp = 0;
        healthDamp = 0;
        rageDamp = 0;
        // We'll track types that might need to be marked inactive
        Set<String> potentiallyInactive = new HashSet<>();

        for (List<StatusEffect> effectList : effectsById.values()) {
            Iterator<StatusEffect> iterator = effectList.iterator();
            while (iterator.hasNext()) {
                StatusEffect effect = iterator.next();
                effect.tick();
                if (effect.isExpired()) {
                    potentiallyInactive.add(effect.getType());
                    iterator.remove();
                }
            }
        }

        // Recalculate effect type status
        for (String type : potentiallyInactive) {
            boolean stillActive = false;
            for (List<StatusEffect> list : effectsById.values()) {
                for (StatusEffect effect : list) {
                    if (effect.getType().equals(type)) {
                        stillActive = true;
                        break;
                    }
                }
                if (stillActive) break;
            }
            activeEffectTypes.put(type, stillActive);
        }
    }

    public void runInfo() {
        for (List<StatusEffect> list : effectsById.values()) {
            for (StatusEffect effect : list) {
                if (SkillDatabase.damageEffectSet.contains(effect.getType())) {
                    damageTotal += effect.getMagnitude();
                }
                else {
                    switch (effect.getType()) {
                        case "attackDamp" -> attackDamp+=effect.getMagnitude();
                        case "defenseDamp" -> defenseDamp+=effect.getMagnitude();
                        case "healthDamp" -> healthDamp+=effect.getMagnitude();
                        case "rageDamp" -> rageDamp+=effect.getMagnitude();
                    }
                }
            }
        }
    }
}
