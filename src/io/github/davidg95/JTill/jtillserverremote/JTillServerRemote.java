/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtillserverremote;

import io.github.davidg95.JTill.jtill.*;
import io.github.davidg95.JTill.jtillserver.*;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.TrayIcon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author David
 */
public class JTillServerRemote implements JTill {

    private static final Logger LOG = Logger.getLogger(JTillServerRemote.class.getName());

    /**
     * The GUI.
     */
    public static GUI g;
    
    /**
     * Server address.
     */
    public static String SERVER_ADDRESS;
    /**
     * Server port number.
     */
    public static int PORT = 52341;

    /**
     * Default port number 52341.
     */
    public static final int DEFAULT_PORT = 52341;

    public static Image icon;
    public static TrayIcon trayIcon;

    private static Properties properties;

    private final String propertiesFile = System.getenv("APPDATA") + "\\JTill Server Remote\\remote.properties";
    
    private final ServerConnection sc;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new JTillServerRemote().start();
    }

    public JTillServerRemote() {
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOG.log(Level.WARNING, "Windows look and feel not supported on this system");
        }
        icon = new javax.swing.ImageIcon(getClass().getResource("/io/github/davidg95/JTill/resources/tillIcon.png")).getImage();
        sc = ServerConnection.getInstance();
        DataConnect.set(sc);
        createAppDataFolder();
        LogFileHandler handler = new LogFileHandler(System.getenv("APPDATA") + "\\JTill Server Remote\\");
        LOG.addHandler(handler);
        loadProperties();
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                g = GUI.create(this, true, icon);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        DataConnect.get().setGUI(g);
        tryConnect();
    }

    private void createAppDataFolder() {
        File file = new File(System.getenv("APPDATA") + "\\JTill Server Remote\\");
        if (!file.exists()) {
            LOG.log(Level.WARNING, "Creating appdata folder JTill Server Remote");
            if (file.mkdir()) {
                new File(System.getenv("APPDATA") + "\\JTill Server Remote\\logs\\").mkdir();
                LOG.log(Level.INFO, "Created folder " + file);
            } else {
                LOG.log(Level.SEVERE, "Error creating " + file);
            }
        }
    }

    public final void tryConnect() {
        try {
            ServerConnection.getInstance().connectAsRemote(SERVER_ADDRESS, PORT);
            saveProperties();
        } catch (IOException ex) {
            int opt = JOptionPane.showOptionDialog(null, "Error connecting to server " + SERVER_ADDRESS + " on port " + PORT + "\nTry again?", "Connection Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/io/github/davidg95/JTill/resources/tillIcon.png")), null, null);
            if (opt == JOptionPane.YES_OPTION) {
                initialSetup();
                tryConnect();
            } else {
                System.exit(0);
            }
        }
    }

    public void start() {
        if (!GraphicsEnvironment.isHeadless()) {
            TillSplashScreen.showSplashScreen();
            TillSplashScreen.addBar(50);
            if (!GraphicsEnvironment.isHeadless()) {
                TillSplashScreen.hideSplashScreen();
                g.setVisible(true);
                g.login();
            } else {

            }
        } else {

        }
    }

    public static Image getIcon() {
        return icon;
    }

    public void loadProperties() {
        properties = new Properties();
        InputStream in;

        try {
            in = new FileInputStream(propertiesFile);

            properties.load(in);
            
            SERVER_ADDRESS = properties.getProperty("address", SERVER_ADDRESS);
            PORT = Integer.parseInt(properties.getProperty("port", Integer.toString(PORT)));
            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            initialSetup();
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public void saveProperties() {
        properties = new Properties();
        OutputStream out;

        try {
            out = new FileOutputStream(propertiesFile);
            
            properties.setProperty("address", SERVER_ADDRESS);
            properties.setProperty("port", Integer.toString(PORT));

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    public static void initialSetup() {
        SERVER_ADDRESS = (String) JOptionPane.showInputDialog(null, "Enter JTill Server IP address", "Initial Setup", JOptionPane.PLAIN_MESSAGE, null, null, SERVER_ADDRESS);
        PORT = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter port number", "" + PORT));
    }

    @Override
    public DataConnect getDataConnection() {
        return sc;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
