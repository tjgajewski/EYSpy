package run;



import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The type Property reader.
 */
public class PropertyReader {

    private Properties properties = new Properties();
    /**
     * The Input stream.
     */
    InputStream inputStream = null;
    /**
     * The Out stream.
     */
    OutputStream outStream=null;

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets properties.
     *
     * @param properties the properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Instantiates a new Property reader.
     *
     * @param prop the prop
     */
    public PropertyReader(String prop) {
        loadProperties(prop);
    }

    /**
     * Loads properties from the given path.
     * @param prop the prop
     */
    private void loadProperties(String prop) {
        try {
            inputStream = new FileInputStream(prop);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dumps properties to the given path. Creates the file if it does not exist.
     * @param map the map containing the properties
     * @param path the file path
     */
    public void dumpProperties(Map<String,String> map,String path){
        try{
            File f = new File(path);
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            f.createNewFile();
            outStream= new FileOutputStream(f);
            Properties properties = new Properties();
            Set<Map.Entry<String,String>> set = map.entrySet();
            for (Map.Entry<String,String> entry : set) {
                properties.put(entry.getKey(), entry.getValue());
            }
            properties.store(outStream,null);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read property string.
     *
     * @param key the key
     * @return the string
     */
    public String readProperty(String key) {
        return properties.getProperty(key);
    }
}
