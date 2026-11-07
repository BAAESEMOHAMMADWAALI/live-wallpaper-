package com.bhadohirugs.asmawallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(0xFF080C14);
        TextView title = new TextView(this);
        title.setText("99 Names of Allah\n24-Hour Live Wallpaper");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("The displayed name changes automatically according to your local time.");
        subtitle.setTextColor(0xFFCBD5E1);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2);
        sp.setMargins(0, 22, 0, 30);
        root.addView(subtitle, sp);
        Button set = new Button(this);
        set.setText("Preview & Set Wallpaper");
        set.setOnClickListener(v -> {
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, AsmaWallpaperService.class));
            startActivity(intent);
        });
        root.addView(set);
        setContentView(root);
    }
}
