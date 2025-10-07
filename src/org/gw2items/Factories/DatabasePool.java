/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.Factories;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author Michael
 */
public class DatabasePool {

    private static ComboPooledDataSource cpds;
    final static int MAX_POOL_SIZE = 10;
    final static int MIN_POOL_SIZE = 3;
    final static int ACCOMMODATION = 5;

    /**
     * create a pool of database connections
     */
    public static void makePool() {
        final Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("database.properties")) {
            props.load(in);
        } catch (IOException ex) {
            showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        final String url = props.getProperty("db.url");
        final String db = props.getProperty("db.database");
        final String user = props.getProperty("db.user");
        final String passwd = props.getProperty("db.password");

        try {
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl(url.concat(db));
            cpds.setUser(user);
            cpds.setPassword(passwd);
            cpds.setMaxPoolSize(MAX_POOL_SIZE);
            cpds.setMinPoolSize(MIN_POOL_SIZE);
            cpds.setAcquireIncrement(ACCOMMODATION);
            cpds.setIdleConnectionTestPeriod(300);
        } catch (PropertyVetoException ex) {
            showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     *
     * @return mysql connection from pool
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }
}
