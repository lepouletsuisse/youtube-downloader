package com.lepouletsuisse.youtube_downloader.desktop;

import com.sapher.youtubedl.YoutubeDL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.lepouletsuisse.youtube_downloader.desktop.util.Config;
import com.lepouletsuisse.youtube_downloader.desktop.util.FxCache;
import com.lepouletsuisse.youtube_downloader.desktop.util.FxLoader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;

import static com.lepouletsuisse.youtube_downloader.desktop.util.FxCache.stage;

public class Main extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initRessources();

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Youtube Downloader");
        this.primaryStage.setResizable(true);

        stage = this.primaryStage;

        initRootLayout();

        stage.setX(400);
        stage.setY(0);
        stage.setWidth(800);
        stage.setHeight(800);

        AnchorPane library = (AnchorPane) FxLoader.load("Downloader").view;
        Tab tab = new Tab();
        tab.setContent(library);
        tab.setText("Downloader");
        tab.setClosable(false);

        ((TabPane) (rootLayout.getCenter())).getTabs().add(tab);
    }

    private void initRootLayout() {
        rootLayout = (BorderPane) FxLoader.load("RootLayout", true).view;
        TabPane tabs = ((TabPane) (rootLayout.getCenter()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        if (rootLayout != null) {
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            FxCache.scene = scene;
        }
    }

    private void initRessources(){
        System.out.println("Initialisation of the ressources...");
        String projectPath = System.getProperty("user.home") + File.separator + "Youtube-downloader";
        ArrayList<String> ressourcesNames = new ArrayList<>();
        ressourcesNames.add("config/config.properties");
        ressourcesNames.add("bin/ffmpeg.exe");
        ressourcesNames.add("bin/ffplay.exe");
        ressourcesNames.add("bin/ffprobe.exe");
        ressourcesNames.add("bin/youtube-dl.exe");

        copyFiles(ressourcesNames, projectPath);

        FxCache.conf = new Config(projectPath);
        System.out.println("Initialisation of the ressources done!");
    }

    private void copyFiles(ArrayList<String> ressourceNames, String projectPath){
        for(String ressource : ressourceNames){
            copyFile(ressource, projectPath);
        }
    }

    private void copyFile(String fileRessourceName, String projectPath){
        File destFolder = new File(projectPath + File.separator + fileRessourceName.substring(0, fileRessourceName.lastIndexOf('/')));
        File destFile = new File(destFolder.getPath() + File.separator + fileRessourceName.substring(fileRessourceName.lastIndexOf('/')));
        System.out.println("Trying to open a InputStream with " + fileRessourceName + " as value...");
        InputStream configFileIS = Main.class.getResourceAsStream(fileRessourceName);
        //System.out.println("DEBUG: \ndestFolder: " + destFolder + "\ndestFile: " + destFile + "\nressource: " + fileRessourceName);
        try{
            if (destFolder.exists()) {
                System.out.println("Ressource folder already exists! (" + fileRessourceName + ") Skip creating folder");
            } else if (!destFolder.mkdirs()) {
                throw new RuntimeException("Creation of a ressource destination folder failed (" + destFolder.getPath() + ")! Abort...");
            }
            if(!destFile.createNewFile()){
                System.out.println("Ressource file already exists! (" + fileRessourceName + ") Skip creating file");
            }else{
                try{
                    Files.copy(configFileIS, Paths.get(destFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Ressource " + fileRessourceName + " successfully copied!");
                }catch(Exception e){
                    e.printStackTrace();
                    throw new RuntimeException("Copy of a ressource failed (" + fileRessourceName + ")! Abort...");
                }
            }
            configFileIS.close();
        }catch(java.io.IOException IOe){
            IOe.printStackTrace();
            throw new RuntimeException("Probleme with IO");
        }
        if(fileRessourceName.contains("youtube-dl.exe")){
            //Set the executable path for Youtube-DL
            try {
                System.out.println("Set executable youtube-dl path...");
                YoutubeDL.setExecutablePath(destFolder.getPath() + File.separator + "youtube-dl.exe");
                System.out.println("New path: " + YoutubeDL.getExecutablePath());
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
