/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codecool.robodraw;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 *
 * @author zoz
 */
public class RoboDraw extends JApplet {

    public static void main(final String[] args) {

        RoboCar roboCar = new RoboCar();

        JFrame frame = new JFrame("RoboCar @ Codecool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(50,50);
        frame.setContentPane(roboCar);
        frame.pack();
        frame.setVisible(true);        

        try {
            roboCar.run("data/codecool.txt");
        } catch (IOException ex) {
            Logger.getLogger(RoboCar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
