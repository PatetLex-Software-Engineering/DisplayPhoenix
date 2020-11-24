package net.displayphoenix.blockly.ui;

import net.displayphoenix.Application;
import net.displayphoenix.blockly.elements.workspace.Field;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.event.IBlocklyListener;
import net.displayphoenix.exception.AppNotCreatedException;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ListHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author TBroski
 */
public class BlocklyDependencyPanel extends JPanel {

    private static Random rand = new Random();

    private Map<String, Integer> colorCache = new HashMap<>();
    private List<String> addedProvisions = new ArrayList<>();
    private BlocklyPanel blocklyPanel;
    private JPanel dependencyPanel;

    /**
     * BlocklyPanel with dependencies and provisions
     *
     * @see BlocklyPanel
     */
    public BlocklyDependencyPanel() {
        this.setLayout(new BorderLayout());
        this.blocklyPanel = new BlocklyPanel();
        this.dependencyPanel = new JPanel(new BorderLayout());

        this.dependencyPanel.setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.dependencyPanel.setOpaque(true);
        this.blocklyPanel.addBlocklyEventListener(new IBlocklyListener() {
            @Override
            public void onBlocklyEvent(BlocklyEvent event) {
                updateDependencies();
            }
        });
        this.blocklyPanel.queueOnLoad(() -> {
            updateDependencies();
        });

        this.add(PanelHelper.westAndCenterElements(this.blocklyPanel, this.dependencyPanel));
    }

    /**
     * @return BlocklyPanel of panel
     */
    public BlocklyPanel getBlocklyPanel() {
        return blocklyPanel;
    }

    private void updateDependencies() {
        this.dependencyPanel.removeAll();

        JLabel dependencyLabel = new JLabel(Localizer.translate("blockly.dependency.text"));
        ComponentHelper.themeComponent(dependencyLabel);
        ComponentHelper.deriveFont(dependencyLabel, 20);
        JPanel dependPanel = PanelHelper.join(dependencyLabel);
        dependPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        JPanel dependencyList = getDependencyPanel();

        JLabel provisionsLabel = new JLabel(Localizer.translate("blockly.provision.text"));
        ComponentHelper.themeComponent(provisionsLabel);
        ComponentHelper.deriveFont(provisionsLabel, 20);
        JPanel providePanel = PanelHelper.join(provisionsLabel);
        providePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        JPanel provisionList = getProvisionsPanel();

        this.dependencyPanel.add("North", PanelHelper.northAndCenterElements(providePanel, provisionList));
        JPanel dependPanelWithBorder = PanelHelper.northAndCenterElements(dependPanel, dependencyList);
        dependPanelWithBorder.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        this.dependencyPanel.add("South", dependPanelWithBorder);
        this.dependencyPanel.revalidate();
        this.dependencyPanel.repaint();
    }

    /**
     * @return List of unsatisfied dependencies
     */
    public List<String> getUnsatisfiedDependencies() {
        // Getting all dependencies
        List<String> dependencies = getDependencies();

        // Iterating provisions
        for (String provision : getProvisions()) {

            // Checking if dependencies contain a provision
            if (dependencies.contains(provision)) {

                // Removes satisfied dependency
                dependencies.remove(provision);
            }
        }

        for (ImplementedBlock implementedBlock : getBlocklyPanel().getWorkspace()) {
            List<String> blockDependencies = new Object() {
                public List<String> getBlockStatementDependencies(ImplementedBlock implementedBlock, List<String> statementProvisions) {
                    List<String> blockDependencies = new ArrayList<>();
                    for (String statement : implementedBlock.getStatementBlocks().keySet()) {
                        for (String provision : implementedBlock.getBlock().getStatementProvisions(statement)) {
                            statementProvisions.add(provision);
                        }
                        for (ImplementedBlock statementBlock : implementedBlock.getStatementBlocks().get(statement)) {
                            for (String dependency : getBlockStatementDependencies(statementBlock, statementProvisions)) {
                                blockDependencies.add(dependency);
                            }
                        }
                    }
                    for (String value : implementedBlock.getValueBlocks().keySet()) {
                        for (ImplementedBlock valueBlock : implementedBlock.getValueBlocks().get(value)) {
                            for (String dependency : getBlockStatementDependencies(valueBlock, statementProvisions)) {
                                blockDependencies.add(dependency);
                            }
                        }
                    }
                    if (implementedBlock.isDeletable() && implementedBlock.isMovable() && implementedBlock.getBlock().getStatementDependencies() != null) {
                        for (String dependency : implementedBlock.getBlock().getStatementDependencies()) {
                            if (!statementProvisions.contains(dependency)) {
                                blockDependencies.add(dependency);
                            }
                        }
                    }
                    blockDependencies = ListHelper.removeDuplicates(blockDependencies);
                    return blockDependencies;
                }
            }.getBlockStatementDependencies(implementedBlock, new ArrayList<>());
            for (String blockDependency : blockDependencies) {
                dependencies.add(blockDependency);
            }
        }

        dependencies = ListHelper.removeDuplicates(dependencies);
        return dependencies;
    }

    /**
     * @see BlocklyDependencyPanel#getUnsatisfiedDependencies()
     *
     * @return All dependencies
     */
    public List<String> getDependencies() {
        List<String> dependencies = new ArrayList<>();

        // Iterating dependencies of all blocks
        for (ImplementedBlock implementedBlock : getBlocklyPanel().getWorkspace()) {

            // Iterating all dependencies
            for (String dependency : getDependenciesFromBlock(implementedBlock)) {

                // Adding dependency
                dependencies.add(dependency);
            }
        }

        // Removing duplicates
        dependencies = ListHelper.removeDuplicates(dependencies);
        return dependencies;
    }

    /**
     * Adds a provision to panel
     *
     * @param provision Provision to add
     */
    public void addProvision(String provision) {
        this.addedProvisions.add(provision);
    }

    /**
     * Returns a list of all provisions
     *
     * @see BlocklyDependencyPanel#getUnsatisfiedDependencies()
     *
     * @return All provisions
     */
    public List<String> getProvisions() {
        List<String> provisions = new ArrayList<>();

        // Iterating all blocks
        for (ImplementedBlock implementedBlock : getBlocklyPanel().getWorkspace()) {

            // Adding all provisions
            for (String provision : getProvisionsFromBlock(implementedBlock)) {
                provisions.add(provision);
            }
        }

        // Adding manual provisions
        for (String provision : this.addedProvisions) {
            provisions.add(provision);
        }

        // Removing duplicates
        provisions = ListHelper.removeDuplicates(provisions);
        return provisions;
    }

    private JPanel getDependencyPanel() {
        JPanel dependencyList = PanelHelper.join();
        for (String dependency : getDependencies()) {
            JLabel label = new JLabel(dependency);
            ComponentHelper.themeComponent(label);
            ComponentHelper.deriveFont(label, 17);
            if (!this.colorCache.containsKey(dependency))
                this.colorCache.put(dependency, rand.nextInt(360));
            float hue = this.colorCache.get(dependency);
            label.setForeground(Color.getHSBColor(hue / 360F, 0.45F, 0.65F));
            JPanel labelPanel = PanelHelper.join(label);
            labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            dependencyList = PanelHelper.northAndCenterElements(dependencyList, labelPanel);
        }
        return dependencyList;
    }
    private JPanel getProvisionsPanel() {
        JPanel provisionList = PanelHelper.join();
        for (String provision : getProvisions()) {
            JLabel label = new JLabel(provision);
            ComponentHelper.themeComponent(label);
            ComponentHelper.deriveFont(label, 17);
            if (!this.colorCache.containsKey(provision))
                this.colorCache.put(provision, rand.nextInt(360));
            float hue = this.colorCache.get(provision);
            label.setForeground(Color.getHSBColor(hue / 360F, 0.45F, 0.65F));
            JPanel labelPanel = PanelHelper.join(label);
            labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            provisionList = PanelHelper.northAndCenterElements(provisionList, labelPanel);
        }
        return provisionList;
    }

    private List<String> getDependenciesFromBlock(ImplementedBlock implementedBlock) {
        List<String> dependencies = new ArrayList<>();
        if (implementedBlock.getBlock().getLocalDependencies() != null) {
            for (String dependency : implementedBlock.getBlock().getLocalDependencies()) {
                if (!dependencies.contains(dependency)) {
                    dependencies.add(dependency);
                }
            }
        }
        for (Field field : implementedBlock.getFields()) {
            if (implementedBlock.getBlock().getFieldDependencies(field.getKey()) != null && implementedBlock.getBlock().getFieldDependencies(field.getKey()).get(field.getValue()) != null) {
                for (String dependency : implementedBlock.getBlock().getFieldDependencies(field.getKey()).get(field.getValue())) {
                    if (!dependencies.contains(dependency)) {
                        dependencies.add(dependency);
                    }
                }
            }
        }
        for (String statement : implementedBlock.getStatementBlocks().keySet()) {
            for (ImplementedBlock statementBlock : implementedBlock.getStatementBlocks().get(statement)) {
                for (String dependency : getDependenciesFromBlock(statementBlock)) {
                    if (!dependencies.contains(dependency)) {
                        dependencies.add(dependency);
                    }
                }
            }
        }
        for (String value : implementedBlock.getValueBlocks().keySet()) {
            for (ImplementedBlock valueBlock : implementedBlock.getValueBlocks().get(value)) {
                for (String dependency : getDependenciesFromBlock(valueBlock)) {
                    if (!dependencies.contains(dependency)) {
                        dependencies.add(dependency);
                    }
                }
            }
        }
        return dependencies;
    }
    private List<String> getProvisionsFromBlock(ImplementedBlock implementedBlock) {
        List<String> provisions = new ArrayList<>();
        if (implementedBlock.getBlock().getLocalProvisions() != null) {
            for (String provision : implementedBlock.getBlock().getLocalProvisions()) {
                if (!provisions.contains(provision)) {
                    provisions.add(provision);
                }
            }
        }
        for (Field field : implementedBlock.getFields()) {
            if (implementedBlock.getBlock().getFieldProvisions(field.getKey()) != null && implementedBlock.getBlock().getFieldProvisions(field.getKey()).get(field.getValue()) != null) {
                for (String provision : implementedBlock.getBlock().getFieldProvisions(field.getKey()).get(field.getValue())) {
                    if (!provisions.contains(provision)) {
                        provisions.add(provision);
                    }
                }
            }
        }
        for (String statement : implementedBlock.getStatementBlocks().keySet()) {
            for (ImplementedBlock statementBlock : implementedBlock.getStatementBlocks().get(statement)) {
                for (String provision : getProvisionsFromBlock(statementBlock)) {
                    if (!provisions.contains(provision)) {
                        provisions.add(provision);
                    }
                }
            }
        }
        for (String value : implementedBlock.getValueBlocks().keySet()) {
            for (ImplementedBlock valueBlock : implementedBlock.getValueBlocks().get(value)) {
                for (String provision : getProvisionsFromBlock(valueBlock)) {
                    if (!provisions.contains(provision)) {
                        provisions.add(provision);
                    }
                }
            }
        }
        return provisions;
    }
}
