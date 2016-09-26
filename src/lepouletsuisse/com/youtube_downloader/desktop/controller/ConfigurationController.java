package lepouletsuisse.com.youtube_downloader.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import lepouletsuisse.com.youtube_downloader.desktop.base.Controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import static lepouletsuisse.com.youtube_downloader.desktop.util.FxCache.conf;

public class ConfigurationController extends Controller {

    @FXML
    private GridPane gridpane;

    @FXML
    private Button button_apply;

    @FXML
    private Text text_apply;

    private HashMap<Node, Node> nodes = new HashMap<>();

    private static int nbApply = 0;

    public void initialize() {
        Iterator<ColumnConstraints> cc_iterator = gridpane.getColumnConstraints().iterator();
        cc_iterator.next().setPrefWidth(200); //First column
        cc_iterator.next().setPrefWidth(500); //Second column
        createForm(conf.getAllConfigs());
        button_apply.setOnAction(click -> applyChange());
    }

    private void createForm(HashMap<String, String> options) {
        int x = 0, y = 1;
        for (Entry<String, String> option : options.entrySet()) {
            Text text = new Text(option.getKey());
            TextField textField = new TextField(option.getValue());
            nodes.put(text, textField);
            gridpane.add(text, --y, x);
            gridpane.add(textField, ++y, x++);
        }
    }

    private void applyChange(){
        text_apply.setText("");
        for(Entry<Node, Node> entry : nodes.entrySet()){
            conf.setValue(((Text)entry.getKey()).getText(), ((TextField)entry.getValue()).getCharacters().toString());
        }
        conf.save();
        text_apply.setText("Your changes has been succesfully applied! -> " + ++nbApply + " time(s)!");
    }
}
