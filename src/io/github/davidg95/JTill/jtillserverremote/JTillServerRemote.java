/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtillserverremote;

import io.github.davidg95.JTill.jtill.ServerConnection;
import io.github.davidg95.JTill.jtillserver.ConnectionAcceptThread;
import io.github.davidg95.JTill.jtillserver.Data;
import io.github.davidg95.JTill.jtillserver.GUI;
import io.github.davidg95.JTill.jtillserver.TillSplashScreen;
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
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author David
 */
public class JTillServerRemote {

    public static Data data;
    public static ServerConnection sc;
    public static GUI g;

    public static String HOST_NAME = "Test";
    public static String SERVER_ADDRESS = "127.0.0.1";
    public static int PORT = 600;

    private ServerSocket s;
    private ConnectionAcceptThread connThread;

    public static Timer updateTimer;
//    public static DatabaseUpdate updateTask;
    public static long updateInterval = 60000L;

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
        sc = new ServerConnection("Test");
        tryConnect();
        data = new Data(sc, g);
        if (!GraphicsEnvironment.isHeadless()) {
            g = new GUI(data, sc);
        }
    }

    public void tryConnect() {
        try {
            sc.connect(HOST_NAME, PORT);
        } catch (IOException ex) {
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
        } else {

        }
        if (connThread != null) {
            connThread.start();
        }
        TillSplashScreen.addBar(50);
        if (!GraphicsEnvironment.isHeadless()) {
            TillSplashScreen.hideSplashScreen();
            g.setVisible(true);
            g.login();
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
        PORT = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter port number", "600"));
    }

}
