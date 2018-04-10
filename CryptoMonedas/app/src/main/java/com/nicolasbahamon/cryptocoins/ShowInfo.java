package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class ShowInfo extends Activity {

    private Button backBtn;
    private WebView wewbview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        wewbview = (WebView)findViewById(R.id.webinfo);
        backBtn = (Button)findViewById(R.id.button19);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        wewbview.loadUrl(((Aplicacion) getApplication()).loadUrl);
        wewbview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

    }
}
