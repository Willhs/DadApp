package will.dadapp;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * For scraping data from a URL
 * Created by will on 20/12/2014.
 */
public class Scraper {

    private static final String PROGRAMME_CONTAINER_ID = "dnn_ctr825_FM_SearchResults_dgResults";
    private static final String PROGRAMME_TITLE_CLASS = "SearchResult_title";
    private static final String PROGRAMME_SUMMARY_CLASS = "SearchResult_summary";
    private static final String PROGRAMME_TIMETABLE_DIV_CLASS = "timetable_rows";
    private static final String PROGRAMME_TIMETABLE_CLASS = "timetable";

    /**
     * gets fishing programmes from
     * @param skyTVSearchURL : a url to a valid page containing search results for a TV programme
     * @return a list of all the TV programmes found in the page
     */
    public static List<TVProgramme> retrieveAllTVProgrammes(URL skyTVSearchURL){

        String responseString = ""; // the response from the HTTP request

        try {
            HttpURLConnection httpCon = (HttpURLConnection) skyTVSearchURL.openConnection();
            httpCon.addRequestProperty("User-Agent", System.getProperty("http.agent"));

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
                responseString += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return retrieveProgrammesFromSearchResults(responseString);
    }

    /**
     * parses and finds tv programmes in a web page represented as a string
     * @param pageData: data represting the search results page
     * @return all tv programmes in the search results
     */
    private static List<TVProgramme> retrieveProgrammesFromSearchResults(String pageData) {
        Document doc = Jsoup.parse(pageData);

        Element searchResults = doc.getElementById(PROGRAMME_CONTAINER_ID).child(0);
        Elements programmeTableRows = searchResults.children();

        List<TVProgramme> programmes = new ArrayList<TVProgramme>();

        for (Element programmeTR : programmeTableRows) {
            programmes.add(retrieveProgrammeFromTR(programmeTR));
        }
        return programmes;
    }

    /**
     * makes a tv programme by parsing and extracting data from an HTML element
     * @param TB: table row element
     * @return TVProgram parsed from element
     */
    private static TVProgramme retrieveProgrammeFromTR(Element TB) {
        Element tableData = TB.getElementsByTag("td").get(0);
        Element titleSpan = tableData.getElementsByClass(PROGRAMME_TITLE_CLASS).get(0);
        Element summarySpan = tableData.getElementsByClass(PROGRAMME_SUMMARY_CLASS).get(0);
        // time
        Element timeTableDiv = TB.getElementsByClass(PROGRAMME_TIMETABLE_DIV_CLASS).get(0);
        Element dateSpan = timeTableDiv.getElementsByClass(PROGRAMME_TIMETABLE_CLASS).get(0);
        Element timeText = timeTableDiv.getElementsByTag("a").get(0);

        String title = titleSpan.text();

        String[] summaryParts = summarySpan.text().split("Channel:|Genre:|Description:");

        // separate information
        // [0] is empty string
        String channel = summaryParts[1].trim();
        String genre = summaryParts[2].trim();
        String description = summaryParts[3].trim();

        // parse date from date and time strings
        String yearDate = dateSpan.text().contains("Today") // handle "today" instead of day
                ? new SimpleDateFormat("EEE dd MMM").format(new Date())
                : dateSpan.text();

        String time = timeText.text();

        Date date = null;
        // include year because simpledateformat assumes 1970 if no year is provided
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try {

            date = new SimpleDateFormat("EEE dd MMM yyyy - HH:mm")
                    .parse(yearDate + " " + year + " - " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new TVProgramme(title, channel, genre, date, description);
    }
}