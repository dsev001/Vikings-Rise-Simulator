package test;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class SimulatorGUI extends JFrame {
    private JTextArea resultArea;
    private Simulator simulator;
    private List<MarchPanel> marchPanels = new ArrayList<>();

    public SimulatorGUI() {
        setTitle("Vikings Rise Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        simulator = new Simulator();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel marchListPanel = new JPanel();
        marchListPanel.setLayout(new BoxLayout(marchListPanel, BoxLayout.X_AXIS)); // horizontal layout for columns
        JScrollPane marchScrollPane = new JScrollPane(marchListPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JButton addMarchButton = new JButton("Add March");
        addMarchButton.addActionListener(e -> {
            MarchPanel mp = new MarchPanel(marchListPanel);
            marchPanels.add(mp);
            marchListPanel.add(mp);
            marchListPanel.revalidate();
            marchListPanel.repaint();
        });
        // Add one march by default
        MarchPanel mp = new MarchPanel(marchListPanel);
        marchPanels.add(mp);
        marchListPanel.add(mp);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addMarchButton, BorderLayout.NORTH);
        topPanel.add(marchScrollPane, BorderLayout.CENTER);

        // Add simulation mode options
        String[] simModes = {"Trades", "Plot Fights", "Group Round Sim"};
        JComboBox<String> simModeBox = new JComboBox<>(simModes);
        simModeBox.setSelectedIndex(0);
        simModeBox.setAlignmentX(CENTER_ALIGNMENT);

        // Add input fields for rounds/fights and group round sim
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        JLabel roundsLabel = new JLabel("Rounds (Trades):");
        JTextField roundsField = new JTextField("1000000", 8);
        JLabel fightsLabel = new JLabel("Fights (Plot):");
        JTextField fightsField = new JTextField("30000", 8);
        // Group Round Sim specific fields
        JLabel groupLengthLabel = new JLabel("Fight Length (intervals for 1-n rounds ran):");
        JTextField groupLengthField = new JTextField("10", 6);
        JLabel groupNumLabel = new JLabel("Fight Length (Rounds per interval):");
        JTextField groupNumField = new JTextField("100000", 6);
        // Add all, but hide group round sim fields by default
        inputPanel.add(roundsLabel);
        inputPanel.add(roundsField);
        inputPanel.add(fightsLabel);
        inputPanel.add(fightsField);
        inputPanel.add(groupLengthLabel);
        inputPanel.add(groupLengthField);
        inputPanel.add(groupNumLabel);
        inputPanel.add(groupNumField);
        groupLengthLabel.setVisible(false);
        groupLengthField.setVisible(false);
        groupNumLabel.setVisible(false);
        groupNumField.setVisible(false);

        JButton simulateButton = new JButton("Run");
        simulateButton.setAlignmentX(CENTER_ALIGNMENT);
        resultArea = new JTextArea(10, 60);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(simModeBox);
        bottomPanel.add(inputPanel);
        bottomPanel.add(simulateButton);
        bottomPanel.add(scrollPane);

        // Show/hide input fields based on sim mode
        simModeBox.addActionListener(e -> {
            String mode = (String) simModeBox.getSelectedItem();
            boolean isTrades = mode.equals("Trades");
            boolean isPlot = mode.equals("Plot Fights");
            boolean isGroup = mode.equals("Group Round Sim");
            // Only show the relevant fields for each mode
            roundsLabel.setVisible(isTrades);
            roundsField.setVisible(isTrades);
            fightsLabel.setVisible(isPlot);
            fightsField.setVisible(isPlot);
            groupLengthLabel.setVisible(isGroup);
            groupLengthField.setVisible(isGroup);
            groupNumLabel.setVisible(isGroup);
            groupNumField.setVisible(isGroup);
            inputPanel.revalidate();
            inputPanel.repaint();
        });
        // Set initial visibility
        simModeBox.setSelectedIndex(0);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        simulateButton.addActionListener(e -> {
            String mode = (String) simModeBox.getSelectedItem();
            if (mode.equals("Trades")) {
                int rounds = 1000000;
                try { rounds = Integer.parseInt(roundsField.getText().trim()); } catch (Exception ignored) {}
                runSimulationThreaded(rounds);
            } else if (mode.equals("Plot Fights")) {
                int fights = 30000;
                try { fights = Integer.parseInt(fightsField.getText().trim()); } catch (Exception ignored) {}
                runPlotFightsThreaded(fights);
            } else if (mode.equals("Group Round Sim")) {
                int fightLength = 10;
                int fightsPerLength = 1000;
                try { fightLength = Integer.parseInt(groupLengthField.getText().trim()); } catch (Exception ignored) {}
                try { fightsPerLength = Integer.parseInt(groupNumField.getText().trim()); } catch (Exception ignored) {}
                runGroupRoundSimThreaded(fightsPerLength, fightLength);
            }
        });
    }
    // Run group round simulator in a background thread
    private void runGroupRoundSimThreaded(int totalTradeSamples, int maxRounds) {
        resultArea.setText("Running group round simulation, please wait...");
        new Thread(() -> {
            try {
                simulator = new Simulator();
                for (MarchPanel mp : marchPanels) {
                    if (!mp.isValidMarch()) continue;
                    if (mp.isFriendly()) {
                        simulator.setNewCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    } else {
                        simulator.setNewEnemyCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    }
                }
                List<CombatRecord> results = simulator.groupRoundSimulator(totalTradeSamples, maxRounds);
                StringBuilder sb = new StringBuilder();
                sb.append("Group Round Simulation Results (Rounds: TradesPreHeal, TradesPostHeal):\n");
                for (int i = 0; i < results.size(); i++) {
                    CombatRecord rec = results.get(i);
                    sb.append(String.format("%2d: %.3f, %.3f\n", (i+1), rec.getTradesPreHeal(), rec.getTradesPostHeal()));
                }
                SwingUtilities.invokeLater(() -> resultArea.setText(sb.toString()));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultArea.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }


    private void runSimulationThreaded(int rounds) {
        resultArea.setText("Running simulation, please wait...");
        new Thread(() -> {
            try {
                simulator = new Simulator();
                for (MarchPanel mp : marchPanels) {
                    if (!mp.isValidMarch()) continue;
                    if (mp.isFriendly()) {
                        simulator.setNewCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    } else {
                        simulator.setNewEnemyCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    }
                }
                CombatRecord record = simulator.findTrades(rounds, false);
                SwingUtilities.invokeLater(() -> resultArea.setText("Simulation complete!\n" +
                    "Trades Pre-Heal: " + record.getTradesPreHeal() + "\n" +
                    "Trades Post-Heal: " + record.getTradesPostHeal()));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultArea.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    // Run histogram plotting in a background thread
    private void runPlotFightsThreaded(int fights) {
        resultArea.setText("Running fight simulations and plotting, please wait...");
        new Thread(() -> {
            try {
                simulator = new Simulator();
                for (MarchPanel mp : marchPanels) {
                    if (!mp.isValidMarch()) continue;
                    if (mp.isFriendly()) {
                        simulator.setNewCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    } else {
                        simulator.setNewEnemyCombatant(mp.getHp(), mp.getAtk(), mp.getDef(), mp.getTroopSize(),
                            mp.getPrimaryCommander(), mp.getSecondaryCommander(),
                            mp.getSkill(0), mp.getSkill(1), mp.getSkill(2), mp.getSkill(3),
                            mp.getSkill(4), mp.getSkill(5), mp.getSkill(6), mp.getSkill(7));
                    }
                }
                simulator.runFights(fights);
                SwingUtilities.invokeLater(() -> resultArea.setText("Fight histogram plotted (see new window)."));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> resultArea.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    // MarchPanel: a single column for a march
    private class MarchPanel extends JPanel {
        private JComboBox<String> primaryCommander, secondaryCommander;
        private JTextField hpField, atkField, defField, troopField;
        private JComboBox<String>[] skills = new JComboBox[8];
        private JComboBox<String> teamBox;
        public MarchPanel(JPanel parentPanel) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            // Commander dropdowns
            java.util.List<String> commanderNames = new java.util.ArrayList<>();
            commanderNames.add("N/A");
            try {
                for (String s : test.SkillDatabase.commanderNamesSet) commanderNames.add(s);
            } catch (Exception e) {
                commanderNames.add("Error loading commanders");
            }
            JLabel lblPrimary = new JLabel("Primary Commander");
            lblPrimary.setAlignmentX(CENTER_ALIGNMENT);
            add(lblPrimary);
            primaryCommander = new JComboBox<>(commanderNames.toArray(new String[0]));
            add(primaryCommander);
            JLabel lblSecondary = new JLabel("Secondary Commander");
            lblSecondary.setAlignmentX(CENTER_ALIGNMENT);
            add(lblSecondary);
            secondaryCommander = new JComboBox<>(commanderNames.toArray(new String[0]));
            add(secondaryCommander);
            // Stats
            JLabel lblHp = new JLabel("Health");
            lblHp.setAlignmentX(CENTER_ALIGNMENT);
            add(lblHp);
            hpField = new JTextField("830");
            add(hpField);
            JLabel lblAtk = new JLabel("Attack");
            lblAtk.setAlignmentX(CENTER_ALIGNMENT);
            add(lblAtk);
            atkField = new JTextField("696");
            add(atkField);
            JLabel lblDef = new JLabel("Defense");
            lblDef.setAlignmentX(CENTER_ALIGNMENT);
            add(lblDef);
            defField = new JTextField("396");
            add(defField);
            JLabel lblTroops = new JLabel("Troops");
            lblTroops.setAlignmentX(CENTER_ALIGNMENT);
            add(lblTroops);
            troopField = new JTextField("300000");
            add(troopField);
            // Skill dropdowns
            java.util.List<String> slot1Skills = new java.util.ArrayList<>();
            java.util.List<String> slot2Skills = new java.util.ArrayList<>();
            java.util.List<String> mountSkills = new java.util.ArrayList<>();
            slot1Skills.add("N/A");
            slot2Skills.add("N/A");
            mountSkills.add("N/A");
            try {
                for (String s : test.SkillDatabase.skillNamesSet) slot1Skills.add(s);
                for (String s : test.SkillDatabase.firstSlotMountSkillNamesSet) mountSkills.add(s);
                for (String s : test.SkillDatabase.secondSlotMountSkillNamesSet) slot2Skills.add(s);
            } catch (Exception e) {
                slot1Skills.add("Error loading skills");
                slot2Skills.add("Error loading skills");
                mountSkills.add("Error loading skills");
            }
            JLabel lblSkills = new JLabel("Skills");
            lblSkills.setAlignmentX(CENTER_ALIGNMENT);
            add(lblSkills);
            for (int i = 0; i < 4; i++) {
                skills[i] = new JComboBox<>(slot1Skills.toArray(new String[0]));
                add(skills[i]);
            }
            JLabel lblMount1 = new JLabel("Slot 1 Mount Skills");
            lblMount1.setAlignmentX(CENTER_ALIGNMENT);
            add(lblMount1);
            for (int i = 4; i < 6; i++) {
                skills[i] = new JComboBox<>(mountSkills.toArray(new String[0]));
                add(skills[i]);
            }
            JLabel lblMount2 = new JLabel("Slot 2 Mount Skills");
            lblMount2.setAlignmentX(CENTER_ALIGNMENT);
            add(lblMount2);
            for (int i = 6; i < 8; i++) {
                skills[i] = new JComboBox<>(slot2Skills.toArray(new String[0]));
                add(skills[i]);
            }
            JLabel lblTeam = new JLabel("Team");
            lblTeam.setAlignmentX(CENTER_ALIGNMENT);
            add(lblTeam);
            teamBox = new JComboBox<>(new String[]{"Friendly", "Enemy"});
            add(teamBox);
            JButton removeButton = new JButton("Remove");
            removeButton.setAlignmentX(CENTER_ALIGNMENT);
            removeButton.addActionListener(e -> {
                marchPanels.remove(this);
                parentPanel.remove(this);
                parentPanel.revalidate();
                parentPanel.repaint();
            });
            add(removeButton);
            setBorder(BorderFactory.createTitledBorder("March"));
        }
        public boolean isFriendly() { return teamBox.getSelectedItem().equals("Friendly"); }
        public boolean isValidMarch() {
            try {
                Integer.parseInt(hpField.getText());
                Integer.parseInt(atkField.getText());
                Integer.parseInt(defField.getText());
                Integer.parseInt(troopField.getText());
                return true;
            } catch (Exception e) { return false; }
        }
        public int getHp() { return Integer.parseInt(hpField.getText()); }
        public int getAtk() { return Integer.parseInt(atkField.getText()); }
        public int getDef() { return Integer.parseInt(defField.getText()); }
        public int getTroopSize() { return Integer.parseInt(troopField.getText()); }
        public String getPrimaryCommander() { return (String) primaryCommander.getSelectedItem(); }
        public String getSecondaryCommander() { return (String) secondaryCommander.getSelectedItem(); }
        public String getSkill(int i) { return (String) skills[i].getSelectedItem(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimulatorGUI().setVisible(true);
        });
    }
}
