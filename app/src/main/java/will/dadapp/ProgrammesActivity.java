package will.dadapp;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Immediately requests fishing programmes information and then displays it
 */
public class ProgrammesActivity extends ListActivity{

    private ProgrammeArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // no title bar
        super.onCreate(savedInstanceState);

        // set the adapter
        listAdapter = new ProgrammeArrayAdapter(this, R.layout.list);
        setListAdapter(listAdapter);

        ProgrammesAsyncTask task = new ProgrammesAsyncTask(this);

        URL skyTVFishingURL = null;
        try {
            skyTVFishingURL = new URL("http://www.skytv.co.nz/tv-guide-search.aspx?category=Programs&search=fishing");
        } catch (MalformedURLException e) { e.printStackTrace(); }
        // start the request for fishing programmes
        task.execute(skyTVFishingURL);

        // TODO: test this out
        // Create a progress bar to display while the list loads
        /*ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true); // shows infinite loading time (because time is not known)
        getListView().setEmptyView(progressBar);*/
    }

    /**
     * populates the list of programmes.
     * @param programmes
     */
    private void populateList(List<TVProgramme> programmes) {
        // sort programmes by date
        Collections.sort(programmes, new Comparator<TVProgramme>() {
            @Override
            public int compare(TVProgramme tvProgramme, TVProgramme tvProgramme2) {
                return tvProgramme.getDate().compareTo(tvProgramme2.getDate());
            }
        });

        // add programmes to the list
        listAdapter.addAll(programmes);
        listAdapter.notifyDataSetChanged();
    }

    private class ProgrammeArrayAdapter extends ArrayAdapter<TVProgramme> {

        public ProgrammeArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.programme_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView channel = (TextView) convertView.findViewById(R.id.channel);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView description = (TextView) convertView.findViewById(R.id.description);

            TVProgramme programme = getItem(position);

            title.setText(programme.getTitle());
            channel.setText(programme.getChannel());
            time.setText(new SimpleDateFormat("EEE dd/MM 'at' h:mm a").format(programme.getDate()));
            description.setText(programme.getDescription());

            return convertView;
        }

    }

    private class ProgrammesAsyncTask extends AsyncTask<URL, Void, List<TVProgramme>>{

        private ProgressDialog progressDialog;

        public ProgrammesAsyncTask(Activity parent){
            this.progressDialog = new ProgressDialog(parent);
        }

        @Override
        protected void onPreExecute(){
            SpannableString ss1 =  new SpannableString("Retrieving fishing shows");
            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss1.length(), 0);

            progressDialog.setMessage(ss1);
            progressDialog.show();
        }

        @Override
        protected List<TVProgramme> doInBackground(URL... urls) {
            return Scraper.retrieveAllTVProgrammes(urls[0]);
        }

        @Override
        protected void onPostExecute(List<TVProgramme> programmes){
            populateList(programmes);

            // to prevent crash..
            try {
                if ((this.progressDialog != null) && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

}
