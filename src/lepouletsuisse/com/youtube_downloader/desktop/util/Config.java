package lepouletsuisse.com.youtube_downloader.desktop.util;

import lepouletsuisse.com.youtube_downloader.desktop.Main;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class Config {

    public Config() {
        load();
    }

    private HashMap<String, String> configs = new HashMap<>();

    private String configPath;
    private String currentPath;
    private Properties prop = new Properties();
    private String osName;
    private String osVersion;

    private void load() throws RuntimeException {
        Path currentRelativePath = Paths.get("");
        currentPath = currentRelativePath.toAbsolutePath().toString();
        this.configPath = Main.class.getClassLoader().getResource("config/config.properties").getPath();
        System.out.println("Chargement du fichier de configuration en " + configPath + "...");

        osName = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");

        InputStream input = null;
        try{
            input = new FileInputStream(configPath);
            prop.load(input);
            String ffmpeg = Main.class.getClassLoader().getResource("executables").getPath();
            if(ffmpeg.charAt(0) == '/'){
                ffmpeg = ffmpeg.substring(1, ffmpeg.length());
            }
            configs.put("ffmpeg_path", ffmpeg);
            configs.put("dest_directory", prop.getProperty("dest_directory"));
            configs.put("pref_mp3", prop.getProperty("pref_mp3"));
            configs.put("pref_thumbnails", prop.getProperty("pref_thumbnails"));
            configs.put("pref_playlist", prop.getProperty("pref_playlist"));

            /* Get all the options in the config file
            Enumeration<?> e = prop.propertyNames();
            while(e.hasMoreElements()){
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                System.out.println("Key : " + key + ", Value : " + value);
            }*/
        }catch(FileNotFoundException notFound){
            throw new RuntimeException(notFound);
        }
        catch(Exception e){
            System.err.println("Error! " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally{
            try{
                input.close();
            }catch(Exception e){
                System.err.println("Error! " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Fichier de configuration chargé!");
    }

    public String getValue(String configName){
        return configs.get(configName);
    }

    public void setValue(String key, String newValue){
        prop.setProperty(key, newValue);
    }

    public HashMap<String, String> getAllConfigs(){
        return configs;
    }

    public void save(){
        FileOutputStream fileOut = null;
        try{
            File file = new File(configPath);
            fileOut = new FileOutputStream(file);
            prop.store(fileOut, "Configuration for Youtube Downloader");
            load();
        }catch(Exception e){
            System.err.println("Error! " + e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                fileOut.close();
            }catch(Exception e){
                System.err.println("Error! " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String getCurrentPath(){
        return currentPath;
    }
}