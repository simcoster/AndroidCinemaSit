package com.example.bubba.nexttrycinemaviewer;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public ArrayList<String> MovieNames = new ArrayList<>();
    public ArrayList<Cinema> Cinemas = new ArrayList<>();
    WebView webview;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview =(WebView) findViewById(R.id.webView);


        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(false);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webview.loadUrl("javascript:alert(\"it worked!\");");
                if (1==1)
                    return;
                webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("http://www.rav-hen.co.il/cinemas");
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                webview.loadUrl("javascript:(function(){"+
                        "alert(\"it worked!\"})()");

                webview.loadUrl("javascript:(function(){"+
                        "l=$('*[data-site_id=\"1010403\"]');"+
                        "e=document.createEvent('HTMLEvents');"+
                        "e.initEvent('click',true,true);"+
                        "l.dispatchEvent(e);"+
                        "})()");
            }
        });


    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            Document doc = Jsoup.parse(html);
            //  GetMovieTitles(doc);
            Element cinemaSelect = doc.select("div.jspPane").first();

            if (1==1)
            return;

            Elements cinemaElems = doc.select("td.cinema_info");
            for (final Element cinemaElem : cinemaElems) {
                Element cinemaLink = cinemaElem.select("a").first();
                Cinema cinema = new Cinema();
                cinema.Name = cinemaLink.text();
                doc.setBaseUri("http://www.rav-hen.co.il");
                String Url = cinemaLink.attr("abs:href");
                cinema.Url = Url;
                Cinemas.add(cinema);
            }
                //  webview.loadUrl("javascript:getElementById('button_submit').click();");
        }
    }

    private void GetMovieTitles(Document doc) {
        Elements select = doc.select("li.featureItem");
        String movieList = "";
        final TextView textView = (TextView) findViewById(R.id.textView2);
        for (final Element elem :select)
        {
            final String title = elem.select("span.featureTitle").first().text();

            MovieNames.add(title);
        }
    }
}

