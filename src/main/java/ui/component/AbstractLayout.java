package ui.component;

import ui.struct.AlignType;
import ui.struct.ComponentType;
import ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractLayout extends AbstractComponent {
    protected int[][] blankGrid;
    protected JPanel panel;
    protected JScrollPane scrollPane;
    protected GridBagLayout layout;

    public AbstractLayout(ComponentType type, String id) {
        super(type, id);
    }

    public void setGridSize(int gridWidth, int gridHeight) {
        if (gridWidth <= 0 || gridHeight <= 0 || blankGrid != null) {
            logger.error(String.format("%s.setGridSize(%d, %d)", this, gridWidth, gridHeight));
            return;
        }
        logger.info(String.format("%s.setGridSize(%d, %d)", this, gridWidth, gridHeight));
        blankGrid = new int[gridHeight][gridWidth];
    }

    private boolean canComponentSet(int gridX, int gridY, int gridWidth, int gridHeight) {
        if (gridX < 0 || gridY < 0 || gridWidth < 0 || gridHeight < 0 ||
                gridX + gridWidth > blankGrid[0].length || gridY + gridHeight > blankGrid.length) {
            return false;
        }
        for (int i = gridY; i < gridY + gridHeight; i++) {
            for (int j = gridX; j < gridX + gridWidth; j++) {
                if (blankGrid[i][j] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setComponent(AbstractComponent component, int gridX, int gridY, int gridWidth, int gridHeight,
                             AlignType align) {
        setComponent(component, gridX, gridY, gridWidth, gridHeight, align, true);
    }

    public void setComponent(AbstractComponent component, int gridX, int gridY, int gridWidth, int gridHeight,
                             AlignType align, boolean logFlag) {
        //边界条件
        if (!canComponentSet(gridX, gridY, gridWidth, gridHeight)) {
            if (logFlag) {
                logger.error(String.format("%s.setComponentPosition(%s, %d, %d, %d, %d, %s)",
                        this, component, gridX, gridY, gridWidth, gridHeight, align));
            }
            return;
        }

        component.setParent(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = align.getValue();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.weightx = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weighty = gridHeight;
        if (component instanceof Table) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        } else if (component instanceof Panel) {
            gbc.fill = GridBagConstraints.BOTH;
        }
        //System.out.println("gbc:" + gbc.gridx + " " + gbc.gridy + " " + gbc.gridwidth + " " + gbc.gridheight);
        panel.add(component.getBaseComponent(), gbc);
        if (logFlag) {
            logger.info(String.format("%s.setComponentPosition(%s, %d, %d, %d, %d, %s)",
                    this, component, gridX, gridY, gridWidth, gridHeight, align));
        }

        for (int i = gridY; i < gridY + gridHeight; i++) {
            for (int j = gridX; j < gridX + gridWidth; j++) {
                blankGrid[i][j] = 1;
            }
        }
        //print blankGrid
//        for (int i = 0; i < blankGrid.length; i++) {
//            for (int j = 0; j < blankGrid[0].length; j++) {
//                System.out.print(blankGrid[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    public int[] getComponentPosition(AbstractComponent component) {
        GridBagConstraints constraints = layout.getConstraints(component.getBaseComponent());
        //System.out.println("gbc:" + constraints.gridx + " " + constraints.gridy + " " + constraints.gridwidth + " "
        // + constraints.gridheight);
        return new int[]{constraints.gridx, constraints.gridy, constraints.gridwidth, constraints.gridheight};
    }

    public AlignType getComponentAlign(AbstractComponent component) {
        GridBagConstraints constraints = layout.getConstraints(component.getBaseComponent());
        return AlignType.fromInt(constraints.anchor);
    }

    public void removeComponent(AbstractComponent component) {
        removeComponent(component, true);
    }

    public void removeComponent(AbstractComponent component, boolean logFlag) {
        int[] position = getComponentPosition(component);
        panel.remove(component.getBaseComponent());
        component.setParent(null);
        for (int i = position[1]; i < position[1] + position[3]; i++) {
            for (int j = position[0]; j < position[0] + position[2]; j++) {
                blankGrid[i][j] = 0;
            }
        }
        if (logFlag) {
            logger.info(String.format("%s.removeComponent(%s)", this, component));
        }
        //print blankGrid
//        for (int i = 0; i < blankGrid.length; i++) {
//            for (int j = 0; j < blankGrid[0].length; j++) {
//                System.out.print(blankGrid[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    public void paintBlank() {
        if (blankGrid != null) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.gridwidth = 1;
            gbc.weighty = 1;
            gbc.gridheight = 1;
            for (int i = 0; i < blankGrid.length; i++) {
                for (int j = 0; j < blankGrid[0].length; j++) {
                    if (blankGrid[i][j] == 0) {
                        JPanel blankPanel = new JPanel();
                        blankPanel.setBackground(Color.WHITE);
                        gbc.gridx = j;
                        gbc.gridy = i;
                        panel.add(blankPanel, gbc);
//                        AbstractComponent blankPanel = UI.getComponent(ComponentType.PANEL,
//                                getId() + "_blank_" + j + "_" + i);
////                        blankPanel.setProperty(JSONObject.parseObject("{\"background\":\"green\"}"));
//                        setComponentPosition(blankPanel, j, i, 1, 1);
                    }
                }
            }
        }
    }

    public void setScrollBar(ScrollType type) {
        if (type == ScrollType.VERTICAL) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else if (type == ScrollType.HORIZONTAL) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else if (type == ScrollType.BOTH) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
    }
}
