package pcd.ass3.sudoku.view.subviews;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import pcd.ass3.sudoku.domain.Pos;
import pcd.ass3.sudoku.utils.Pair;
import static pcd.ass3.sudoku.view.ViewUtilities.makeBorder;

public class CellButton extends JButton {
    private Optional<Pair<String, Color>> nickColor;
    private final Pos pos;

    public CellButton(String buttonText, int row, int col) {
        super(buttonText);
        this.nickColor = Optional.empty();
        this.pos = new Pos(row, col);
        setFont(new Font("Arial", Font.BOLD, 20));
        setFocusPainted(false);                
        unselect();
    }


      @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        nickColor.ifPresent(p -> {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            int x = 0 + 5;
            int y = getHeight() - g2.getFont().getSize();
            g2.setColor(p.y());
            g2.drawString(p.x(), x, y);
            g2.dispose();
        });
    }

    public final void unselect() {
        setBorder(makeBorder(this.pos.row(), this.pos.col()));
        this.nickColor = Optional.empty();
        repaint();
    }

    public final void selected(String nickname, Color usrColor) {
        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, usrColor));
        this.nickColor = Optional.of(new Pair(nickname, usrColor));
        repaint();
    }

}