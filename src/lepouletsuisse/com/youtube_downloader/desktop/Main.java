package lepouletsuisse.com.youtube_downloader.desktop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lepouletsuisse.com.youtube_downloader.desktop.util.Config;
import lepouletsuisse.com.youtube_downloader.desktop.util.FxCache;
import lepouletsuisse.com.youtube_downloader.desktop.util.FxLoader;

import static lepouletsuisse.com.youtube_downloader.desktop.util.FxCache.stage;

public class Main extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FxCache.conf = new Config();

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

    public static void main(String[] args) {
        launch(args);
    }
}
