package com.lepouletsuisse.youtube_downloader.desktop.controller;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import com.lepouletsuisse.youtube_downloader.desktop.Main;
import com.lepouletsuisse.youtube_downloader.desktop.base.Controller;
import com.lepouletsuisse.youtube_downloader.desktop.base.YoutubeSong;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import static com.lepouletsuisse.youtube_downloader.desktop.util.FxCache.conf;

public class DownloaderController extends Controller {

    private ArrayList<YoutubeSong> allSongs = new ArrayList<>();

    @FXML
    private Button button_dl;

    @FXML
    private TextField input_url;

    @FXML
    private CheckBox cb_mp3Only;

    @FXML
    private CheckBox cb_downloadThumbnail;

    @FXML
    private CheckBox cb_playlist;

    @FXML
    private Text info_destDirectory;

    @FXML
    private ListView lw;

    @FXML
    private Group group_playlist;

    @FXML
    private Text text_playlistName;

    @FXML
    private Button button_select;

    @FXML
    private Button button_unselect;

    public void initialize() {
        group_playlist.setDisable(true);
        cb_downloadThumbnail.setDisable(true);

        if (conf.getValue("pref_playlist").equals("true")) {
            cb_playlist.setSelected(true);
            group_playlist.setVisible(true);
        }
        if (conf.getValue("pref_mp3").equals("true")){
            cb_mp3Only.setSelected(true);
            cb_downloadThumbnail.setDisable(false);
        }
        if (conf.getValue("pref_thumbnails").equals("true")) cb_downloadThumbnail.setSelected(true);

        cb_playlist.setOnAction(event -> {
            if (cb_playlist.isSelected()) {
                getPlaylistList();
                lw.setDisable(false);
            } else {
                lw.setDisable(true);
            }
        });

        input_url.setOnKeyReleased(event -> {
            // Playlist: https://www.youtube.com/watch?v=P_BePbpCbw4&index=2&list=PLlo6p7qhezRh-4x_2-PRbwkNpfOAEYbTi
            // No Playlist: https://www.youtube.com/watch?v=uqsNttghlWI
            if (input_url.getCharacters().toString().contains("&list") || input_url.getCharacters().toString().contains("?list")) {
                if(group_playlist.isDisable()) group_playlist.setDisable(false);
                if (cb_playlist.isSelected()) getPlaylistList();
            } else{
                group_playlist.setDisable(true);
                lw.getItems().clear();
            }
        });

        button_dl.setOnAction(event -> downloadMP3(input_url.getCharacters().toString(), parseOptions()));

        info_destDirectory.setText("Your playlist will be download in " + conf.getValue("dest_directory"));

        cb_mp3Only.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                cb_downloadThumbnail.setDisable(false);
            } else {
                cb_downloadThumbnail.setDisable(true);
            }
        }));

        button_select.setOnAction(event -> {
            for(YoutubeSong song : allSongs){
                song.cb.setSelected(true);
                updateCB(song.cb);
            }
        });

        button_unselect.setOnAction(event -> {
            for(YoutubeSong song : allSongs){
                song.cb.setSelected(false);
                updateCB(song.cb);
            }
        });
    }

    private HashMap<String, String> parseOptions(){
        String songIndexs = "";
        for(YoutubeSong song : allSongs){
            if(song.cb.isSelected()){
                songIndexs += song.placeInPlaylist + ",";
            }
        }
        if(songIndexs.length() >= 1) songIndexs = songIndexs.substring(0, songIndexs.length() - 1);
        System.out.println(songIndexs);
        HashMap<String, String> options = new HashMap<>();
        options.put("output", "%(title)s.%(ext)s");
        options.put("retries", "10");
        options.put("no-part", null);
        options.put("newline", null);
        options.put("verbose", null);
        if (cb_playlist.isSelected() && cb_playlist.isVisible()) {
            options.put("yes-playlist", null);
            options.put("playlist-items", songIndexs);
        } else {
            options.put("no-playlist", null);
        }
        if (cb_mp3Only.isSelected()) {
            options.put("extract-audio", null);
            options.put("audio-format", "mp3");
            if (cb_downloadThumbnail.isSelected()) {
                options.put("embed-thumbnail", null);
            }
        }
        return options;
    }

    private void downloadMP3(String videoUrl, HashMap<String, String> options) {
        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, conf.getValue("dest_directory"));
        for (Map.Entry<String, String> option : options.entrySet()) {
            if (option.getValue() == null) {
                request.setOption(option.getKey());
            } else {
                request.setOption(option.getKey(), option.getValue());
            }
        }
        try {
            System.out.println("You tried to download the URL \"" + request.getUrl() + "\" with the following options:");
            for (Map.Entry<String, String> option : request.getOption().entrySet()) {
                System.out.println(option.getKey() + (option.getValue() == null ? "" : ": " + option.getValue()));
            }

            // Make request and return response
            YoutubeDLResponse response = YoutubeDL.execute(request);
            // Response
            System.out.println("Here it is the command used: " + response.getCommand());
            System.out.println(response.getOut()); // Executable output

        } catch (Exception e) {
            System.err.println("Error!! " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getPlaylistList() {
        lw.getItems().clear();
        lw.setVisible(true);
        allSongs.clear();

        String inputUrl = input_url.getCharacters().toString();
        int begin = inputUrl.indexOf("list=") + 5;
        int end = inputUrl.indexOf("&index=");
        if(end == -1 || end <= begin) end = inputUrl.length();
        String playlistUrl = "https://www.youtube.com/playlist?list=" + inputUrl.substring(begin, end);
        try{
            Document doc = Jsoup.connect(playlistUrl).get();
            Elements newsHeadlines = doc.select("a.pl-video-title-link");
            Elements playlistTitle = doc.select("h1.pl-header-title");
            text_playlistName.setText(playlistTitle.text());
            int i = 1;
            for(Element element : newsHeadlines){
                CheckBox cb = new CheckBox(element.text());
                cb.setSelected(true);
                cb.getStylesheets().add(Main.class.getResource("view/style.css").toString());
                cb.setOnAction(event -> updateCB(cb));
                updateCB(cb);
                YoutubeSong song = new YoutubeSong(cb, i++);
                allSongs.add(song);
                lw.getItems().add(cb);
            }
        }catch(Exception e){
            System.err.println("Error! " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCB(CheckBox cb){
        cb.getStyleClass().removeAll("green-cb", "red-cb");
        if(cb.isSelected()){
            cb.getStyleClass().add("green-cb");
        }else{
            cb.getStyleClass().add("red-cb");
        }
    }
}
