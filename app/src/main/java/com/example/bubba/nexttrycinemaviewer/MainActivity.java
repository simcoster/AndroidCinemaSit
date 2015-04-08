package com.example.bubba.nexttrycinemaviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkViewInternal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final XWalkView viewById = (XWalkView) findViewById(R.id.xwalkWebView);
        viewById.setResourceClient(new ResourceClient(viewById));
        viewById.setVisibility(View.VISIBLE);

        final Button buttonnn = (Button) findViewById(R.id.button);

        final WebView webView = (WebView) findViewById(R.id.webView);

        final TextView textView = (TextView) findViewById(R.id.textView2);

        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        ChangeLoadImages(true);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>',document.URL);");
            }

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon)
            {
                    Log.e("Started loading", url);
            }
        });
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        textView.setMovementMethod(new ScrollingMovementMethod());

        buttonnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("ggegege","gegegege");
              //  viewById.load("http://tickets.yesplanet.co.il/ypa?key=1025", null);
                ClearText();
                ChangeAddress("http://tickets.yesplanet.co.il/ypa?key=1025");
            }
        });
    }

    public void ChangeAddress(final String url)
    {
        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.post(new Runnable() {
            public void run() {
                Log.e("Change adress to:  ",url);
                CookieManager cookieManager = CookieManager.getInstance();
                webView.loadUrl(url);
            }
        });
    }

    public void AddText(final String text)
    {
        final TextView textView = (TextView) findViewById(R.id.textView2);
        textView.post(new Runnable() {
            public void run() {
                textView.append(text);
            }
        });
    }

    public void ClearText()
    {
        final TextView textView = (TextView) findViewById(R.id.textView2);
        textView.post(new Runnable() {
            public void run() {
                textView.setText("");
            }
        });
    }

    public void ChangeLoadImages(final Boolean IsloadImages)
    {
        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.post(new Runnable() {
            public void run() {
                webView.getSettings().setLoadsImagesAutomatically(IsloadImages);
            }
        });
    }

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


    class MyJavaScriptInterface {
        final TextView textView = (TextView) findViewById(R.id.textView2);
        public List ScreeningUrlsJavscript = new ArrayList<String>();
        public List ScreeningUrlsAbsolute = new ArrayList<String>();
        String ScreeningIndexURl;
        Boolean isInitilized = false;
        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(final String html, String Url) {

            if (Url.contains("TicketingTodaysEventsPage")) {
                ScreeningIndexURl= Url;
                if (!isInitilized) {
                    Document parse = Jsoup.parse(html);
                    Elements viewingLinks = parse.select("td.CinemaSelectEventPage_Events_Table_DateTimeCell_General > a");
                    for (Element item : viewingLinks) {
                        ScreeningUrlsJavscript.add(item.attr("href"));
                    }
                   // ScreeningUrlsJavscript = new ArrayList<String>(ScreeningUrlsJavscript.subList(1, 4));
                    isInitilized =true;
                }
                if (ScreeningUrlsJavscript.size()>0)
                {
                    String SitsUrl = (String) ScreeningUrlsJavscript.get(0);
                    ScreeningUrlsJavscript.remove(0);
                    ChangeAddress(SitsUrl);
                }
                else
                {
                    AddText("DONE!!!");
                    Log.e("Notice!", "DONE!!!");
                }
            }

            else if (Url.contains("SelectSeatPage2"))
            {
                new Thread()
                {
                    public void run() {
                        StringBuilder seatLocations = new StringBuilder();
                        seatLocations.append('\n');
                        Document sitPage = Jsoup.parse(html);
                        AddText("Name:" + sitPage.select("a.General_Result_Text").first().text());
                        AddText("Date:"+ sitPage.select("span.SessionInformation_Label_DateTime").first().text());
                        AddText("Location:" + sitPage.select("span.SessionInformation_Label_Site").first().text());
                        Elements select = sitPage.select("div.seat");
                        int RowNum = 1;
                        for (Element item : select) {
                            Matcher cssMatcher = Pattern.compile("_Seat_(.*)_").matcher(item.attr("id"));
                            if (cssMatcher.find())
                            {
                                int currRowNum = Integer.parseInt(cssMatcher.group(1));
                                if (currRowNum != RowNum)
                                {
                                    seatLocations.append('\n');
                                    RowNum =currRowNum;
                                }
                            }
                            if (item.hasAttr("onclick"))
                            {
                                seatLocations.append('O');
                            }
                            else
                            {
                                seatLocations.append('X');
                            }
                        }
                        AddText(seatLocations.toString());
                    }
                }.start();
                ChangeAddress(ScreeningIndexURl);

            }
        }
    }
}

    class ScreeningInfo
    {
        String MovieTitle;
        String Time;
        SeatStatus [][] SeatStatus;
    }

    enum SeatStatus
    {
        Avaialable,
        Occupied,
        Handicapped_Avaialable,
        Handicapped_Occupied,
        Missing
    }
class ResourceClient extends XWalkResourceClient {

    public ResourceClient(XWalkView xwalkView) {
        super(xwalkView);
    }

    public void onLoadStarted(XWalkView view, String url) {
        super.onLoadStarted(view, url);
        Log.d("blah", "Load Started:" + url);
    }

    public void onLoadFinished(XWalkView view, String url) {
        super.onLoadFinished(view, url);
        Log.d("blah", "Load Finished:" + url);
    }

    public boolean shouldOverrideUrlLoading(XWalkView view, String url){
        Log.e("should shoudld",url);
        if (url.contains("jpg"))
        {
            Log.e("should intercept"," should intercept");
            return true;
        }
        return false;
    }

}

