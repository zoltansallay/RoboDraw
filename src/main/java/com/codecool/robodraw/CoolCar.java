/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codecool.robodraw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author zoz
 */
public class CoolCar extends JPanel {

    private double x = 0.0;
    private double y = 0.0;
    private double direction = 0.0;
    private int pen = 0;
    private int penStyle = 0;
    private double oneChangeStep = Double.MAX_VALUE;
    
    private final BufferedImage image;
    private final Graphics2D g;

    private final int CANVASWIDTH = 1420;
    private final int CANVASHEIGHT = 400;
    private final int ORIGO_X = 30;
    private final int ORIGO_Y = 300;
    private final double ONE_STEP = 0.5;
    
    private final Logger logger = Logger.getLogger(RoboCar.class.getName());
    
    CoolCar() {
        setPreferredSize(new Dimension(CANVASWIDTH, CANVASHEIGHT));
        image = new BufferedImage(CANVASWIDTH, CANVASHEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, CANVASWIDTH, CANVASHEIGHT);
        g.setColor(Color.BLACK);
        g.drawLine(0, ORIGO_Y, CANVASWIDTH, ORIGO_Y);
        g.drawLine(ORIGO_X, 0, ORIGO_X, CANVASHEIGHT);
        g.setColor(Color.BLUE);
        repaint();
    }
    
    private void drawArc(double distanceToGo, double radius) {
        double distanceDone = 0.0;
        double distanceSinceChange = 0.0;
        g.setColor(Color.BLUE);
        g.fillOval(ORIGO_X + (int) x - (pen / 2), ORIGO_Y - (int) y - (pen / 2), pen, pen);
        while (distanceDone < distanceToGo) {
            step(ONE_STEP);
            if (radius > 0)
                direction += ONE_STEP / radius;
            distanceDone += ONE_STEP;
            distanceSinceChange += ONE_STEP;
            if (distanceSinceChange > oneChangeStep) {
                if (penStyle == 1) {
                    if (g.getColor() == Color.BLUE) {
                        g.setColor(Color.WHITE);
                    } else {
                        g.setColor(Color.BLUE);
                    }
                }
                if (penStyle == 2) {
                    if (g.getColor() == Color.BLUE) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.BLUE);
                    }
                }
                distanceSinceChange = 0.0;
            }
            g.fillOval(ORIGO_X + (int) x - (pen / 2), ORIGO_Y - (int) y - (pen / 2), pen, pen);
            repaint();
            try {
                Thread.sleep(1L);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void drawLine(double distanceToGo) {
        drawArc(distanceToGo, 0);
    }
    
    private void step(double dx) {
        x += dx * Math.cos(direction);
        y += dx * Math.sin(direction);
    }
    
    public void run(String fileName) throws IOException {
        logger.log(Level.INFO, "Initial position of roboCar: {0}, {1}", new Object[]{x, y});
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] words = line.split("\\s+");
                String command1 = words[0];
                String command2 = words[1];
                double number1 = Double.valueOf(words[2]);
                double number2 = 0.0;
                if (words.length > 3) {
                    number2 = Double.valueOf(words[3]);
                }
                if (command1.contentEquals("SET")) {
                    if (command2.contentEquals("X")) {
                        x = number1;
                    } else if (command2.contentEquals("Y")) {
                        y = number1;
                    } else if (command2.contentEquals("DIRECTION")) {
                        direction = number1 / 180.0 * Math.PI;
                    } else if (command2.contentEquals("PEN")) {
                        pen = (int) number1;
                    } else if (command2.contentEquals("STYLE")) {
                        penStyle = (int) number1;
                        if (penStyle == 1) {
                            oneChangeStep = 8.0;
                        } else if (penStyle == 2) {
                            oneChangeStep = 63.0;
                        }
                    } else {
                        logger.log(Level.SEVERE, "Syntax error in the script file!");
                    }
                }
                if (command1.contentEquals("TURN")) {
                    if (command2.contentEquals("LEFT")) {
                        direction += number1 / 180.0 * Math.PI;
                    } else if (command2.contentEquals("RIGHT")) {
                        direction -= number1 / 180.0 * Math.PI;
                    } else {
                        logger.log(Level.SEVERE, "Syntax error in the script file!");
                    }
                }
                if (command1.contentEquals("GO")) {
                    if (command2.contentEquals("STRAIGHT")) {
                        double distanceToGo = number1;
                        drawLine(distanceToGo);
                    } else if (command2.contentEquals("CIRCLE")) {
                        double distanceToGo = number1 / 180.0 * Math.PI * number2;
                        drawArc(distanceToGo, number2);
                    } else {
                        logger.log(Level.SEVERE, "Syntax error in the script file!");
                    }
                }
            }
        }
        logger.log(Level.INFO, "Terminal position of roboCar: {0}, {1}", new Object[]{x, y});
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        graphics.drawImage(image, 0, 0, this);
    }
}
