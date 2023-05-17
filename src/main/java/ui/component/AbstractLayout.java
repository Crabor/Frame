package ui.component;

import ui.UI;
import ui.struct.AlignType;
import ui.struct.ComponentType;
import ui.struct.ScrollType;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public abstract class AbstractLayout extends AbstractComponent {
    protected AbstractComponent[][] children;
    protected JPanel panel;
    protected JScrollPane scrollPane;
    protected GridBagLayout layout;

    public AbstractLayout(ComponentType type, String id) {
        super(type, id);
    }

    public void setGridSize(int gridWidth, int gridHeight) {
        if (gridWidth <= 0 || gridHeight <= 0 || children != null) {
            logger.error(String.format("%s.setGridSize(%d, %d)", this, gridWidth, gridHeight));
            return;
        }
        logger.info(String.format("%s.setGridSize(%d, %d)", this, gridWidth, gridHeight));
        children = new AbstractComponent[gridHeight][gridWidth];
    }

    private boolean canComponentSet(int gridX, int gridY, int gridWidth, int gridHeight) {
        if (gridX < 0 || gridY < 0 || gridWidth < 0 || gridHeight < 0 ||
                gridX + gridWidth > children[0].length || gridY + gridHeight > children.length) {
            return false;
        }
        AbstractComponent component = children[gridY][gridX];
        for (int i = gridY; i < gridY + gridHeight; i++) {
            for (int j = gridX; j < gridX + gridWidth; j++) {
                if (children[i][j] != component && !isBlankPanel(children[i][j])) {
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
        //print children
//        System.out.printf("before setComponent(%s)%n", component);
//        for (int i = 0; i < children.length; i++) {
//            for (int j = 0; j < children[0].length; j++) {
//                System.out.print(children[i][j] + " ");
//            }
//            System.out.println();
//        }
        if (component.parent != null) {
            component.parent.removeComponent(component, logFlag);
        }

        removeComponent(gridX, gridY, gridWidth, gridHeight, logFlag);
        removeBlank(gridX, gridY, gridWidth, gridHeight);

        component.setParent(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = align.getValue();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.weightx = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weighty = gridHeight;
//        if (component instanceof Table) {
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//        } else
            if (component instanceof Panel) {
            gbc.fill = GridBagConstraints.BOTH;
        }
        //System.out.println("gbc:" + gbc.gridx + " " + gbc.gridy + " " + gbc.gridwidth + " " + gbc.gridheight);
        panel.add(component.getLinkComponent(), gbc);
        if (logFlag) {
            logger.info(String.format("%s.setComponentPosition(%s, %d, %d, %d, %d, %s)",
                    this, component, gridX, gridY, gridWidth, gridHeight, align));
        }

        for (int i = gridY; i < gridY + gridHeight; i++) {
            for (int j = gridX; j < gridX + gridWidth; j++) {
                children[i][j] = component;
            }
        }
        //print children
//        System.out.printf("after setComponent(%s)%n", component);
//        for (int i = 0; i < children.length; i++) {
//            for (int j = 0; j < children[0].length; j++) {
//                System.out.print(children[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    public int[] getComponentPosition(AbstractComponent component) {
        GridBagConstraints constraints = layout.getConstraints(component.getLinkComponent());
        //System.out.println("gbc:" + constraints.gridx + " " + constraints.gridy + " " + constraints.gridwidth + " "
        // + constraints.gridheight);
        return new int[]{constraints.gridx, constraints.gridy, constraints.gridwidth, constraints.gridheight};
    }

    public AlignType getComponentAlign(AbstractComponent component) {
        GridBagConstraints constraints = layout.getConstraints(component.getLinkComponent());
        return AlignType.fromInt(constraints.anchor);
    }

    public void removeComponent(AbstractComponent component) {
        removeComponent(component, true);
    }

    public void removeComponent(AbstractComponent component, boolean logFlag) {
        if (component == null) {
            return;
        }
        //print children
//        System.out.printf("before removeComponent(%s)%n", component);
//        for (int i = 0; i < children.length; i++) {
//            for (int j = 0; j < children[0].length; j++) {
//                System.out.print(children[i][j] + " ");
//            }
//            System.out.println();
//        }
        int[] position = getComponentPosition(component);
        panel.remove(component.getLinkComponent());
        component.setParent(null);
        for (int i = position[1]; i < position[1] + position[3]; i++) {
            for (int j = position[0]; j < position[0] + position[2]; j++) {
                if (isBlankPanel(children[i][j])) {
                    blankCollection.push((Panel) children[i][j]);
                }
                children[i][j] = null;
            }
        }
        setBlank(position[0], position[1], position[2], position[3]);
        if (logFlag && !component.getId().contains(blankPrefix)) {
            logger.info(String.format("%s.removeComponent(%s)", this, component));
        }
        //print children
//        System.out.printf("after removeComponent(%s)%n", component);
//        for (int i = 0; i < children.length; i++) {
//            for (int j = 0; j < children[0].length; j++) {
//                System.out.print(children[i][j] + " ");
//            }
//            System.out.println();
//        }
    }

    public void removeComponent(int gridX, int gridY, int gridWidth, int gridHeight) {
        removeComponent(gridX, gridY, gridWidth, gridHeight, true);
    }

    public void removeComponent(int gridX, int gridY, int gridWidth, int gridHeight, boolean logFlag) {
        for (int i = gridY; i < gridY + gridHeight; i++) {
            for (int j = gridX; j < gridX + gridWidth; j++) {
                removeComponent(children[i][j], logFlag);
            }
        }
    }

    static Stack<Panel> blankCollection = new Stack<>();
    public void setBlank(int gridX, int gridY, int gridWidth, int gridHeight) {
        if (children != null) {
            //print children
//            System.out.printf("before setBlank(%d,%d,%d,%d)%n", gridX, gridY, gridWidth, gridHeight);
//            for (int i = 0; i < children.length; i++) {
//                for (int j = 0; j < children[0].length; j++) {
//                    System.out.print(children[i][j] + " ");
//                }
//                System.out.println();
//            }
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.gridwidth = 1;
            gbc.weighty = 1;
            gbc.gridheight = 1;
            for (int i = gridY; i < gridY + gridHeight; i++) {
                for (int j = gridX; j < gridX + gridWidth; j++) {
                    if (children[i][j] == null) {
                        gbc.gridx = j;
                        gbc.gridy = i;
                        Panel blank;
//                        System.out.println("blankCollection.size():" + blankCollection.size());
                        if (!blankCollection.isEmpty()) {
                            blank = blankCollection.pop();
                        } else {
                            blank = getBlankPanel();
                        }
                        panel.add(blank.linkComponent, gbc);
                        children[i][j] = blank;
                    }
                }
            }
            //print children
//            System.out.printf("after setBlank(%d,%d,%d,%d)%n", gridX, gridY, gridWidth, gridHeight);
//            for (int i = 0; i < children.length; i++) {
//                for (int j = 0; j < children[0].length; j++) {
//                    System.out.print(children[i][j] + " ");
//                }
//                System.out.println();
//            }
        }
    }

    public void removeBlank(int gridX, int gridY, int gridWidth, int gridHeight) {
        if (children != null) {
            //print children
//            System.out.printf("before removeBlank(%d,%d,%d,%d)%n", gridX, gridY, gridWidth, gridHeight);
//            for (int i = 0; i < children.length; i++) {
//                for (int j = 0; j < children[0].length; j++) {
//                    System.out.print(children[i][j] + " ");
//                }
//                System.out.println();
//            }
            for (int i = gridY; i < gridY + gridHeight; i++) {
                for (int j = gridX; j < gridX + gridWidth; j++) {
                    if (isBlankPanel(children[i][j])) {
                        panel.remove(children[i][j].linkComponent);
                        blankCollection.push((Panel) children[i][j]);
                        children[i][j] = null;
                    }
                }
            }
            //print children
//            System.out.printf("after removeBlank(%d,%d,%d,%d)%n", gridX, gridY, gridWidth, gridHeight);
//            for (int i = 0; i < children.length; i++) {
//                for (int j = 0; j < children[0].length; j++) {
//                    System.out.print(children[i][j] + " ");
//                }
//                System.out.println();
//            }
        }
    }

    static boolean gridVisible = false;
    public static void setGridVisible(boolean visible) {
        gridVisible = visible;
    }

    public static String blankPrefix = "blank_";
    static int blankIndex = 1;
    public static Panel getBlankPanel() {
        Panel blankPanel = (Panel) UI.getComponent(ComponentType.PANEL, blankPrefix + blankIndex);
        blankIndex++;
        if (gridVisible) {
            //设置边框
            ((JPanel)blankPanel.linkComponent).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        return blankPanel;
    }

    public static boolean isBlankPanel(AbstractComponent component) {
        return component != null && component.getId().contains(blankPrefix);
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
        } else if (type == ScrollType.NONE) {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
    }

    public void repaint() {
//        if (scrollPane != null) {
//            scrollPane.validate();
//            scrollPane.repaint();
//        }
        panel.validate();
        panel.repaint();
//        Set<AbstractComponent> visited = new HashSet<>();
//        //所有children都要repaint
//        for (int i = 0; i < children.length; i++) {
//            for (int j = 0; j < children[0].length; j++) {
//                if (children[i][j] != null
//                        && children[i][j] instanceof AbstractLayout
//                        && !visited.contains(children[i][j])) {
//                    ((AbstractLayout)children[i][j]).repaint();
//                    visited.add(children[i][j]);
//                }
//            }
//        }
    }
}
