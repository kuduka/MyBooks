package com.soc.uoc.pqtm.mybooks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.soc.uoc.pqtm.mybooks.model.BookContent;

import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {

    private FloatingActionButton fab = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar =  findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lliguem amb la webview, mostrem el form i desactivem el fab button
                WebView webView = findViewById(R.id.web_view);
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new MyBookWebClient());
                webView.loadUrl("file:///android_asset/form.html");
                fab.setVisibility(View.GONE);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_ID));
            ArrayList<BookContent.BookItem> lbooks = (ArrayList<BookContent.BookItem>) getIntent().getSerializableExtra("lbooks");
            arguments.putSerializable("lbooks",lbooks);
            BookDetailFragment fragment = new BookDetailFragment(   );
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    class MyBookWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Verifiquem tots els parámetres via GET
            String name = Uri.parse(url).getQueryParameter("name");
            String num = Uri.parse(url).getQueryParameter("num");
            String date = Uri.parse(url).getQueryParameter("date");

            Boolean error = false;
            String msg = "";
            //cap camp pot estar buit
            if (name == "") {
                msg += "*ERROR*: El nom no pot ser buit.\n";
                error = true;
            }
            if (num == "") {
                msg += "*ERROR*: El número no pot ser buit.\n";
                error = true;
            }
            if (date == "") {
                msg += "*ERROR*: La data no pot ser buida.\n";
                error = true;
            }

            //si hi ha un error mostrem un missatge, si tot està bé, el llibre està comprat
            if (!error) {
                Toast.makeText(BookDetailActivity.this, "Llibre Comprat!",
                        Toast.LENGTH_SHORT).show();
                view.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                return true;
            }
            else{
                Toast.makeText(BookDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

}
