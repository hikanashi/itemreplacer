package com.jamasoftware.services.itemreplacer;

import javax.swing.SwingUtilities;

import com.jamasoftware.services.itemreplacer.window.MainGUI;


public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                final MainGUI mainGUI = new MainGUI();
                mainGUI.setVisible(true);
            }
        });
    }
}
