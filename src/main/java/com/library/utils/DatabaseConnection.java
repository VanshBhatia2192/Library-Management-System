package com.library.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "database.properties";
    private static DatabaseConnection instance;
    private final BlockingQueue<Connection> connectionPool;
    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private DatabaseConnection() throws SQLException {
        Properties props = new Properties();
        try {
            System.out.println("Looking for database.properties...");
            InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (input == null) {
                System.err.println("Error: " + CONFIG_FILE + " not found in classpath");
                System.err.println("Current classpath: " + System.getProperty("java.class.path"));
                throw new SQLException("Unable to find " + CONFIG_FILE);
            }
            System.out.println("Found database.properties, loading configuration...");
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.poolSize = Integer.parseInt(props.getProperty("db.pool.maxSize", "20"));
            
            System.out.println("Database configuration loaded:");
            System.out.println("URL: " + this.url);
            System.out.println("Username: " + this.username);
            System.out.println("Pool Size: " + this.poolSize);
            
            // Initialize connection pool
            this.connectionPool = new ArrayBlockingQueue<>(poolSize);
            initializePool();
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Failed to load database configuration", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void initializePool() throws SQLException {
        try {
            System.out.println("Initializing connection pool...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < poolSize; i++) {
                Connection conn = createConnection();
                if (conn != null) {
                    connectionPool.offer(conn);
                }
            }
            System.out.println("Connection pool initialized successfully with " + poolSize + " connections");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Error initializing connection pool: " + e.getMessage());
            throw e;
        }
    }

    private Connection createConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully created new database connection");
            return conn;
        } catch (SQLException e) {
            System.err.println("Error creating database connection: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed()) {
                System.out.println("Creating new connection as pool is empty or connection is closed");
                conn = createConnection();
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            throw new SQLException("Failed to get database connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && connectionPool.size() < poolSize) {
                    connectionPool.offer(connection);
                } else {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error releasing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void closeAllConnections() {
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 