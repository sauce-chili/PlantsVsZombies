package vstu.oop;

import vstu.oop.model.core.Game;
import vstu.oop.model.core.level.Level;
import vstu.oop.model.core.level.Level1;
import vstu.oop.view.GameView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game(new Level1());

            new GameView(game);
        });
    }
}