package config;

import protocol.MySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {
    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getPort() {
        String port = properties.getProperty("server.port");
        if (port == null) {
            return 8080;
        } else {
            return Integer.parseInt(port);
        }
    }

    public static MySerializer.Algorithm getAlgorithm(){
        String algorithm = properties.getProperty("server.algorithm");
        if (algorithm == null) {
            return MySerializer.Algorithm.Java;
        } else {
            return MySerializer.Algorithm.valueOf(algorithm);
        }
    }

}
