package test;
// should add something for graphing
//import java.io.File;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //File file = new File("demo/src/main/java/test/SkillDatabase.json");
        //System.out.println("Exists? " + file.exists());
        //System.out.println("Absolute path: " + file.getAbsolutePath());

        //need input validation
        Simulator simulate = new Simulator();
        simulate.setNewCombatant(300, 200, 100, 100000,
        "Alf", "Olena",
        "Fiery Rage", "Bone Corroding Arrow", "Poison Arrow",
        "First Strike", "Bloodthirst Gaze", "N/A", 
        "N/A", "N/A");

        
        simulate.setNewEnemyCombatant(300, 200, 100, 100000,
        "Sephina", "N/A",
        "First Strike", "Poison Arrow", "N/A",
        "N/A", "N/A", "N/A", 
        "N/A", "N/A");

        simulate.setNewEnemyCombatant(300, 200, 100, 100000,
        "Sephina", "N/A",
        "First Strike", "Poison Arrow", "N/A",
        "N/A", "N/A", "N/A", 
        "N/A", "N/A");
        
        
        
        int round = 5;
        double total = 0;
        int repeats = 1500000;
        //simulate.runRound(1);
        //System.out.println(simulate.getFactorPerSecond());
        //groupRoundSimulator(30,simulate);
        simulate.runVerseRound(1000000);
        System.out.println(simulate.getFactorPerSecond());

        //System.out.println(simulate.getAllSkills());
    }

    /*
    private static void groupRoundSimulator(int maxRounds, Simulator simulate) {
        List<Double> totalList = new ArrayList<>();
        long startTime = System.nanoTime();
        int availableThreads = Runtime.getRuntime().availableProcessors();
        availableThreads = 1; //parallel computing seems to slow it down even after optimising json read?
        ExecutorService executor = Executors.newFixedThreadPool(availableThreads);
        for (int rounds = 1; rounds <= maxRounds; rounds++) {
            final int currentRounds = rounds; // <- effectively final copy
            int totalRepeats = 500000 / currentRounds; //2 mil rounds is getting ~ +-1
            int chunkSize = totalRepeats / availableThreads;
            List<Future<Double>> futures = new ArrayList<>();

            for (int t = 0; t < availableThreads; t++) {
                int start = t * chunkSize;
                int end = (t == availableThreads - 1) ? totalRepeats : start + chunkSize;

                futures.add(executor.submit(() -> {
                    double threadTotal = 0;
                    Simulator localSim = new Simulator();
                    localSim.setNewCombatant(100, 50, 200, 10,
                            "Sephina", "Olena",
                            "Fiery Rage", "Poison Arrow", "Bone Corroding Arrow",
                            "First Strike", "N/A", "N/A",
                            "N/A", "N/A");
                    // actives trigger every round
                    for (int i = start; i < end; i++) {
                        localSim.runVerseRound(currentRounds);
                        threadTotal += localSim.getFactorPerSecond();
                    }
                    return threadTotal;
                }));
            }

            double total = 0;
            for (Future<Double> f : futures) {
                try {
                    total += f.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            double avg = total / totalRepeats;
            totalList.add(avg);
            System.out.printf("Rounds: %d -> Avg FPS: %.4f%n", currentRounds, avg);
        }
        executor.shutdown();
        long endTime = System.nanoTime();
        double durationMillis = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Simulation completed in %.2f ms (%.2f seconds)%n", durationMillis, durationMillis / 1000.0);
    }
    */
}
