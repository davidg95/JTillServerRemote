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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author David
 */
public class JTillServerRemote {

    /**
     * The connection to the server.
     */
    public static ServerConnection sc;
    /**
     * The GUI.
     */
    public static GUI g;

    /**
     * Host name for the connection.
     */
    public static String HOST_NAME = "RemoteAppConnection";
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new JTillServerRemote().start();
    }

    public JTillServerRemote() {
        icon = new javax.swing.ImageIcon(getClass().getResource("/io/github/davidg95/JTill/resources/tillIcon.png")).getImage();
        sc = new ServerConnection();
        if (!GraphicsEnvironment.isHeadless()) {
            g = new GUI(sc, true, icon);
        }
        sc.setGUI(g);
        loadProperties();
        tryConnect();
    }

    public void tryConnect() {
        try {
            sc.connect(SERVER_ADDRESS, PORT, HOST_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
            int opt = JOptionPane.showOptionDialog(null, "Error connecting to server " + SERVER_ADDRESS + " on port " + PORT + "\nTry again?", "Connection Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/io/github/davidg95/JTill/resources/tillIcon.png")), null, null);
            if (opt == JOptionPane.YES_OPTION) {
                initialSetup();
                saveProperties();
                tryConnect();
            } else {
                System.exit(0);
            }
        }
    }

    public void start() {
        if (!GraphicsEnvironment.isHeadless()) {
            TillSplashScreen.showSplashScreen();
            g.databaseLogin();
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

    public static void loadProperties() {
        properties = new Properties();
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);

            HOST_NAME = properties.getProperty("host");
            SERVER_ADDRESS = properties.getProperty("address", SERVER_ADDRESS);
            PORT = Integer.parseInt(properties.getProperty("port", Integer.toString(PORT)));

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            initialSetup();
            saveProperties();
        } catch (IOException ex) {
        }
    }

    public static void saveProperties() {
        properties = new Properties();
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            HOST_NAME = InetAddress.getLocalHost().getHostName();

            properties.setProperty("host", HOST_NAME);
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

}
