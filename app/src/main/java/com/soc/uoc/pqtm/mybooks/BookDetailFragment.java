package com.soc.uoc.pqtm.mybooks;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soc.uoc.pqtm.mybooks.helper.ImageDownloaderTask;
import com.soc.uoc.pqtm.mybooks.model.BookContent;

import java.text.SimpleDateFormat;


public class BookDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "id";
    private BookContent.BookItem mItem;
    private FloatingActionButton fab = null;
    private ImageView dimgurl = null;
    private TextView dauthor = null;
    private TextView ddpublish = null;
    private TextView ddesc = null;



    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            int position = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            mItem = BookContent.getBooks().get(position);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.book_detail, container, false);
        SimpleDateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");

        //agafem el boto de l'activity aixi no hem de crear-ne un de nou
        fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.baseline_shopping_cart_black_18dp);
        //agafem tots els camps definits del llibre
        dimgurl = rootView.findViewById(R.id.detail_imgurl);
        dauthor = rootView.findViewById(R.id.detail_author);
        ddpublish = rootView.findViewById(R.id.detail_dpublish);
        ddesc = rootView.findViewById(R.id.detail_desc);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agafem i mostrem la webview
                WebView webView =  rootView.findViewById(R.id.web_view);
                webView.setVisibility(View.VISIBLE);
                //amagem tots els camps del llibre
                dimgurl.setVisibility(View.GONE);
                dauthor.setVisibility(View.GONE);
                ddpublish.setVisibility(View.GONE);
                ddesc.setVisibility(View.GONE);
                webView.requestFocus();
                //carreguem el formulari
                webView.setWebViewClient(new MyBookWebClient());
                webView.loadUrl("file:///android_asset/form.html");
                fab.setVisibility(View.GONE);
            }
        });

        if (mItem != null) {
            new ImageDownloaderTask((ImageView) rootView.findViewById(R.id.detail_imgurl)).execute(mItem.getUrl_image());
            ((TextView) rootView.findViewById(R.id.detail_author)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.detail_desc)).setText(mItem.getDescription());
            ((TextView) rootView.findViewById(R.id.detail_dpublish)).setText(dformat.format(mItem.getPublication_date()));
        }

        return rootView;
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
                Toast.makeText(BookDetailFragment.this.getActivity(), "Llibre Comprat!",
                        Toast.LENGTH_SHORT).show();
                view.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                //tornem a mostrar tots els camps
                dimgurl.setVisibility(View.VISIBLE);
                dauthor.setVisibility(View.VISIBLE);
                ddpublish.setVisibility(View.VISIBLE);
                ddesc.setVisibility(View.VISIBLE);
                return true;
            }
            else{
                Toast.makeText(BookDetailFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
