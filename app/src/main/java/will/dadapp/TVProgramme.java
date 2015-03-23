package will.dadapp;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains information about a tv programme
 * Created by will on 20/12/2014.
 */
public class TVProgramme {

    private String title;
    private String channel;
    private String genre;
    private Date time;
    private String description;

    public TVProgramme(String title, String channel, String genre, Date time, String description) {
        this.title = title;
        this.channel = channel;
        this.genre = genre;
        this.time = time;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getChannel() {
        return channel;
    }

    public String getGenre() {
        return genre;
    }

    public Date getDate() {
        return time;
    }

    public String getDescription() {
        return description;
    }
}
