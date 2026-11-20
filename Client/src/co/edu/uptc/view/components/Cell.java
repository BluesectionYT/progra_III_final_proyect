package co.edu.uptc.view.components;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public final class Cell {
    private static final int DEFAULT_GAP = 5;

    private final GridBagConstraints constraints = new GridBagConstraints();

    private Cell(int row, int column) {
        constraints.gridy = row;
        constraints.gridx = column;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(DEFAULT_GAP, DEFAULT_GAP, DEFAULT_GAP, DEFAULT_GAP);
    }

    public static Cell at(int row, int column) {
        return new Cell(row, column);
    }

    public Cell span(int columns) {
        constraints.gridwidth = columns;
        return this;
    }

    public Cell growX() {
        constraints.weightx = 1;
        return this;
    }

    public Cell growY() {
        constraints.weighty = 1;
        return this;
    }

    public Cell grow() {
        return growX().growY();
    }

    public Cell east() {
        constraints.anchor = GridBagConstraints.EAST;
        return this;
    }

    public Cell center() {
        constraints.anchor = GridBagConstraints.CENTER;
        return this;
    }

    public Cell insets(int top, int left, int bottom, int right) {
        constraints.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GridBagConstraints build() {
        if (constraints.weightx > 0 && constraints.weighty > 0) {
            constraints.fill = GridBagConstraints.BOTH;
        } else if (constraints.weightx > 0) {
            constraints.fill = GridBagConstraints.HORIZONTAL;
        } else if (constraints.weighty > 0) {
            constraints.fill = GridBagConstraints.VERTICAL;
        } else {
            constraints.fill = GridBagConstraints.NONE;
        }
        return constraints;
    }
}
