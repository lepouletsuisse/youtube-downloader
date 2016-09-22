package desktop.base;

import javafx.scene.control.CheckBox;

public class YoutubeSong {
    public CheckBox cb;
    public int placeInPlaylist;

    public YoutubeSong(CheckBox checkBox, int place){
        cb = checkBox;
        placeInPlaylist = place;
    }
}
