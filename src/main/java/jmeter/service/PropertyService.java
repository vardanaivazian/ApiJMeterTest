package jmeter.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public enum PropertyService {
    INSTANCE;
    
    private Properties prop;
    private final Logger logger = Logger.getLogger(PropertyService.class.getName());
    private static final String CONFIG_PATH = "src/main/resources/config.properties";

    PropertyService() {
        init();
    }
    
    public String get( String name ) {
        return prop.getProperty(name);
    }
    
    private void init() {

        logger.info( "Initializing configuration: " + CONFIG_PATH );
        
        try ( InputStream input = new FileInputStream(CONFIG_PATH)) {

            prop = new Properties();
            prop.load(input);

        } catch ( IOException ex) {
            logger.severe( ex.getMessage() );
        }
        
    }
}
