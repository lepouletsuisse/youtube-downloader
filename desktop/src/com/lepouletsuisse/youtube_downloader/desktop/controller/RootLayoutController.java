package com.lepouletsuisse.youtube_downloader.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.lepouletsuisse.youtube_downloader.desktop.base.Controller;
import com.lepouletsuisse.youtube_downloader.desktop.util.FxCache;
import com.lepouletsuisse.youtube_downloader.desktop.util.FxLoader;

public class RootLayoutController extends Controller {

    private boolean isConfigurationOpen = false;

    @FXML
    private MenuItem mi_configuration;

    @FXML
    private MenuItem mi_close;

    public void initialize() {
        mi_configuration.setOnAction(event -> {
            if (isConfigurationOpen) return;
            BorderPane rootLayout = (BorderPane) FxLoader.load("RootLayout", true).view;
            AnchorPane configuration = (AnchorPane) FxLoader.load("Configuration").view;
            Tab tab = new Tab();
            tab.setContent(configuration);
            tab.setText("Configuration");
            tab.setClosable(true);
            isConfigurationOpen = true;
            ((TabPane) (rootLayout.getCenter())).getTabs().add(tab);
            tab.setOnClosed(closed -> isConfigurationOpen = false);
        });

        mi_close.setOnAction(event -> {
            Stage stage = (Stage) FxCache.stage.getScene().getWindow();
            stage.close();
        });
    }
}
