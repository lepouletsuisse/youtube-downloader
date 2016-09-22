package desktop.util;

import desktop.Main;
import desktop.base.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Classe permettant de charger le controlleur et la vue d'une interface graphique
 *
 */
public class FxLoader {

    // Controller/view cached
    private static HashMap<String, Result<? extends Controller, ? extends Node>> controllerCache = new HashMap<>();

    /**
     * Récupère l'URL d'une ressource
     *
     * @param resource La ressource dont l'URL doit être récupérer
     *
     * @return L'URL de la ressource
     */
    public static URL getUrl(String resource) {
        return Main.class.getResource(resource);
    }

    /**
     * Charge la paire vue-controleur
     *
     * @param view      Vue à charger
     * @param isCached  Définit si elle doit être mis en cache ou non
     * @param <C>       Type de controleur
     * @param <V>       Type de vue
     *
     * @return Objet Result contenant la vue et le controleur demandé
     */
    public static <C extends Controller, V extends Node> Result<C, V> load(String view, boolean isCached) {
        if (isCached && controllerCache.containsKey(view)) {
            return (Result<C, V>) controllerCache.get(view);
        } else {
            try {
                // Load root layout from fxml file.
                FXMLLoader loader = new FXMLLoader();
                URL url = getUrl("view/" + view + ".fxml");
                loader.setLocation(url);
                V fxml = loader.load();
                C controller = loader.getController();
                Result<C, V> res = new Result<>(controller, fxml);
                if (isCached) {
                    controllerCache.put(view, res);
                }
                return res;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load view " + view, e);
            }
        }
    }

    /**
     * @see #load(String, boolean)
     */
    public static <C extends Controller, V extends Node> Result<C, V> load(String view) {
        return load(view, false);
    }

    public static class Result<C extends Controller, V extends Node> {

        public final C controller;
        public final V view;

        public Result(C controller, V view) {
            this.controller = controller;
            this.view = view;
        }
    }
}
