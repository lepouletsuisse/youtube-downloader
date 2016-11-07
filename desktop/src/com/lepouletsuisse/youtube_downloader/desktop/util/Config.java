package com.lepouletsuisse.youtube_downloader.desktop.util;

import com.lepouletsuisse.youtube_downloader.desktop.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class Config {

    public Config(String projectPath) {
        setProjectPath(projectPath);
        load();
    }

    private HashMap<String, String> configs = new HashMap<>();

    private File configPath;
    private Properties prop = new Properties();
    private String osName;
    private String osVersion;
    private String projectPath;
    private String executablesPath;

    private void load() throws RuntimeException {
        try {
            this.configPath = new File(projectPath + File.separator + "config" + File.separator + "config.properties");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Chargement du fichier de configuration en " + configPath + "...");

        osName = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");

        try (InputStream input = new FileInputStream(configPath)){
            prop.load(input);
            configs.put("dest_directory", prop.getProperty("dest_directory").replace("\\", "/"));
            configs.put("pref_mp3", prop.getProperty("pref_mp3"));
            configs.put("pref_thumbnails", prop.getProperty("pref_thumbnails"));
            configs.put("pref_playlist", prop.getProperty("pref_playlist"));
        }
        catch(FileNotFoundException notFound){
            System.err.println("Unable to load config file " + configPath);
            throw new RuntimeException(notFound);
        }
        catch(Exception e){
            System.err.println("Error! " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("Fichier de configuration chargé!");
    }

    public String getValue(String configName){
        return configs.get(configName);
    }

    public void setValue(String key, String newValue){
        prop.setProperty(key, newValue);
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String path) {
        projectPath = path;
    }

    public String getExecutablesPath() {
        return executablesPath;
    }

    public void setExecutablesPath(String path) {
        executablesPath = path;
    }

    public HashMap<String, String> getAllConfigs(){
        return configs;
    }

    public void save(){
        FileOutputStream fileOut = null;
        try{
            fileOut = new FileOutputStream(configPath);
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
}
