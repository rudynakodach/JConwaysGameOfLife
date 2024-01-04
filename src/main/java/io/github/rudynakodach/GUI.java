package io.github.rudynakodach;

import io.github.rudynakodach.JavaxElements.HorizontalComponentContainer;
import io.github.rudynakodach.JavaxElements.NumberTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {
    public static HashMap<Point, JPanel> panels;
    public final JFrame frame;
    private final JPanel gameContainer;
    public final JLabel generationLabel;
    public final JLabel liveCellsLabel;
    public final JLabel deadCellsLabel;

    private void generatePanels() {
        if(!panels.isEmpty()) {
            for(Point p : panels.keySet()) {
                gameContainer.remove(panels.get(p));
                gameContainer.revalidate();
                gameContainer.repaint();
            }
            panels.clear();
        }

        for (int y = 0; y < Main.SIZE; y++) {
            for (int x = 0; x < Main.SIZE; x++) {
                JPanel panel = new JPanel();
                panel.setBackground(new Color(x*(255/Main.SIZE), y*(255/Main.SIZE), 255));
                panel.setBorder(new LineBorder(Color.GRAY, 1));
                panel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(Main.gameStarted) {
                            return;
                        }
                        Point p = new Point(0, 0);
                        for(Map.Entry<Point, JPanel> entry : panels.entrySet()) {
                            if(entry.getValue() == panel) {
                                p = entry.getKey();
                            }
                        }

                        Main.map[(int)p.getX()][(int)p.getY()] = !Main.map[(int)p.getX()][(int)p.getY()];
                        if(Main.map[(int)p.getX()][(int)p.getY()]) {
                            Main.liveCells++;
                        } else {
                            Main.liveCells--;
                        }
                        drawMap(Main.map);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });

                gameContainer.add(panel);
                panels.put(new Point(x, y), panel);
            }
        }
    }

    public GUI() {
        panels = new HashMap<>();
        this.frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(800, 750);
        frame.setPreferredSize(new Dimension(800, 750));

        JPanel contentContainer = new JPanel();
        contentContainer.setSize(800, 750);
        contentContainer.setPreferredSize(new Dimension(800, 750));
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.X_AXIS));

        frame.add(contentContainer);

        gameContainer = new JPanel();
        gameContainer.setLayout(new GridLayout(Main.SIZE, Main.SIZE));

        contentContainer.add(gameContainer);
        generatePanels();

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

        generationLabel = new JLabel();
        JButton startButton = new JButton("Start");
        JButton resetButton = new JButton("Reset");

        HorizontalComponentContainer gameControlsContainer = new HorizontalComponentContainer(startButton, resetButton);

        liveCellsLabel = new JLabel("Live cells: 0");
        deadCellsLabel = new JLabel("Dead cells: 0");

        NumberTextField delayInput = new NumberTextField();
        delayInput.setText(String.valueOf(Main.DELAY));
        delayInput.setMaximumSize(new Dimension(100, 20));
        JLabel delayInputLabel = new JLabel("Delay: ");
        HorizontalComponentContainer delayContainer = new HorizontalComponentContainer(delayInputLabel, delayInput);

        JLabel sizeLabel = new JLabel("Size: ");
        NumberTextField sizeInput = new NumberTextField();
        sizeInput.setMaximumSize(new Dimension(100, 20));

        HorizontalComponentContainer mapSizeContainer = new HorizontalComponentContainer(sizeLabel, sizeInput);

        sizeInput.addActionListener(l -> {
            if(Main.gameStarted) {
                return;
            }

            int newSize = Math.max(5, Math.min(Math.toIntExact(sizeInput.numericValue()), 1000));

            Main.map = new Boolean[newSize][newSize];

            for (Boolean[] barr : Main.map) {
                Arrays.fill(barr, false);
            }

            generatePanels();

        });

        delayInput.addActionListener(e -> {
            String text = delayInput.getText();
            try {
                Main.DELAY = Math.max(50L, Math.min(20000L, Long.parseLong(text)));
                delayInput.setBorder(new LineBorder(Color.black, 1));
            } catch (NumberFormatException ignored) {
                delayInput.setBorder(new LineBorder(Color.red, 2));
            }
        });

        startButton.addMouseListener(new MouseListener() {
            Thread t = new Thread(() -> {
                try {
                    Main.gameLoop();
                } catch (InterruptedException ignored) {}
            });
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!Main.gameStarted) {
                    startButton.setText("Pause");
                    t.start();
                } else {
                    startButton.setText("Resume");
                    Main.scheduledThreadStop = true;
                    t = new Thread(() -> {
                        try {
                            Main.gameLoop();
                        } catch (InterruptedException ignored) {}
                    });
                }

                Main.gameStarted = !Main.gameStarted;
                resetButton.setEnabled(!Main.gameStarted);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        resetButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(Main.gameStarted) {
                    return;
                }

                Main.liveCells = 0;
                Main.generation = 0;
                generationLabel.setText("Generation: 0");
                for(Boolean[] barr : Main.map) {
                    Arrays.fill(barr, false);
                }
                drawMap(Main.map);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        controlsPanel.add(liveCellsLabel);
        controlsPanel.add(deadCellsLabel);
        controlsPanel.add(generationLabel);
        controlsPanel.add(delayContainer);
        controlsPanel.add(mapSizeContainer);
        controlsPanel.add(gameControlsContainer);

        contentContainer.add(controlsPanel);

        frame.setVisible(true);
        frame.pack();
    }

    public void drawMap(Boolean[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if(map[x][y]) {
                    List<Point> liveNeighbors = Main.getNeighbouringCellsNotEqualTo(map, new Point(x, y), false, false);
                    if(liveNeighbors.size() < 2) {
                        panels.get(new Point(x, y)).setBackground(Color.orange);
                    } else if(liveNeighbors.size() > 3) {
                        panels.get(new Point(x, y)).setBackground(Color.red);
                    } else {
                        panels.get(new Point(x, y)).setBackground(Color.green);
                    }
                } else {
                    panels.get(new Point(x, y)).setBackground(Color.white);
                }
            }
        }
    }
}
