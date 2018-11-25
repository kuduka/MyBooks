package com.soc.uoc.pqtm.mybooks;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.vistrav.ask.Ask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BaseActivity extends AppCompatActivity {

    protected Drawer drawer;
    protected  void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        //creem els submenus del menu lateral

        SecondaryDrawerItem subItem1 = new SecondaryDrawerItem().withIdentifier(1).withName("Share to other Apps");
        SecondaryDrawerItem subItem2 = new SecondaryDrawerItem().withIdentifier(2).withName("Copy to clipboard");
        SecondaryDrawerItem subItem3 = new SecondaryDrawerItem().withIdentifier(3).withName("Share in Whatsapp");

        //creem el profile
        IProfile profile = new ProfileDrawerItem()
                .withName("Marc Fite")
                .withEmail("marc@fite.su")
                .withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460"))
                .withIdentifier(1);


        //creem la capcalera del menu lateral

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(false)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //creem el menu lateral
        this.drawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(false)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        subItem1,
                        new DividerDrawerItem(),
                        subItem2,
                        new DividerDrawerItem(),
                        subItem3,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        int option = ((int) drawerItem.getIdentifier());
                        if(option == 1){
                            shareApps();
                        } else if (option == 2){
                            copyClipboard();
                        } else if (option == 3){
                            shareWhatsapp();
                        }
                        return true;
                    }
                })
                .build();

    }

    private void shareApps() {
        //share image/text via apps
        Intent intent = createIntent();
        checkPerms();
        startActivity(Intent.createChooser(intent, "Choose app:"));
    }

    private void copyClipboard() {
        //get clipboard service and copy content
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("MyBooks", "Aplicació Android sobre llibres.");
        clipboard.setPrimaryClip(clip);
        //warn user that text has been copied
        Toast.makeText(BaseActivity.this, "Contingut copiat al portapapers",
                Toast.LENGTH_SHORT).show();
    }
    private void shareWhatsapp() {
        //share image/text via whatsapp
        checkPerms();
        Intent intent = createIntent();
        //set default app to whatsapp
        intent.setPackage("com.whatsapp");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            //show error if not installed
            Toast.makeText(BaseActivity.this, "WhatsApp not installed!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void checkPerms() {
        //change policy for reading files and ask permission to write files to  storage
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //grant permissions if needed
        Ask.on(this)
                .id(1) // in case you are invoking multiple time Ask from same activity or fragment
                .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("In order to share images through your apps we need your permission")
                .go();

    }

    private Intent createIntent() {
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.icon);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/MyBooks.jpg";
        OutputStream out = null;
        File file=new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path=file.getPath();
        Uri bmpUri = Uri.parse("file://"+path);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        String shareBodyText = "Aplicació Android sobre llibres";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP MyBooks");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        return intent;
    }
}
