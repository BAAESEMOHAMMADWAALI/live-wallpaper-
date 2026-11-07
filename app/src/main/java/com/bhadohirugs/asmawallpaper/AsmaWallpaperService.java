package com.bhadohirugs.asmawallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AsmaWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine() { return new AsmaEngine(); }

    private final class AsmaEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean visible;
        private final Runnable drawRunner = new Runnable() { @Override public void run() { drawFrame(); } };
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override public void onVisibilityChanged(boolean v) { visible = v; if (v) drawFrame(); else handler.removeCallbacks(drawRunner); }
        @Override public void onSurfaceChanged(SurfaceHolder h, int format, int w, int height) { super.onSurfaceChanged(h, format, w, height); drawFrame(); }
        @Override public void onSurfaceDestroyed(SurfaceHolder h) { visible = false; handler.removeCallbacks(drawRunner); super.onSurfaceDestroyed(h); }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) render(c);
            } catch (RuntimeException ignored) {
            } finally { if (c != null) holder.unlockCanvasAndPost(c); }
            handler.removeCallbacks(drawRunner);
            if (visible) handler.postDelayed(drawRunner, 1000);
        }

        private void render(Canvas c) {
            int w = c.getWidth(), h = c.getHeight();
            c.drawColor(Color.rgb(8, 12, 20));
            p.setShader(new android.graphics.RadialGradient(w*.5f, h*.43f, Math.min(w,h)*.55f,
                    new int[]{0xFF162032,0xFF080C14}, null, android.graphics.Shader.TileMode.CLAMP));
            c.drawRect(0,0,w,h,p); p.setShader(null);
            long now = System.currentTimeMillis();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int seconds = cal.get(java.util.Calendar.HOUR_OF_DAY)*3600 + cal.get(java.util.Calendar.MINUTE)*60 + cal.get(java.util.Calendar.SECOND);
            int idx = Math.min((int)(seconds / 86400.0 * Names.ALL.length), Names.ALL.length-1);
            String[] name = Names.ALL[idx];
            float scale = Math.min(w/400f, h/800f);
            c.save(); c.translate(w/2f, h/2f); c.scale(scale, scale);
            float halfW = 200, halfH = h/(2f*scale);
            // Top 24-hour progress gauge
            p.setColor(0xFF94A3B8); p.setTextSize(11); p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); p.setTextAlign(Paint.Align.LEFT);
            c.drawText("24H CYCLE", -170, -halfH+90, p);
            p.setColor(0xFF38BDF8); p.setTextAlign(Paint.Align.RIGHT); c.drawText((idx+1)+" / 99",170,-halfH+90,p);
            p.setColor(0x33FFFFFF); c.drawRoundRect(-170,-halfH+103,170,-halfH+110,5,5,p);
            p.setColor(0xFF10B981); c.drawRoundRect(-170,-halfH+103,-170+340f*(idx+1)/99f,-halfH+110,5,5,p);
            // Ambient glow
            p.setShader(new android.graphics.RadialGradient(0,0,170,new int[]{0x2238BDF8,0x0810B981,0x00000000},null,android.graphics.Shader.TileMode.CLAMP));
            c.drawCircle(0,0,170,p); p.setShader(null);
            // Dial ticks
            for(int i=0;i<99;i++) {
                double a=(i/99.0)*Math.PI*2-Math.PI/2;
                float r1=154-(i%5==0?16:10), r2=154;
                p.setColor(i==idx?0xFF38BDF8:(i%5==0?0x66FFFFFF:0x2EFFFFFF)); p.setStrokeWidth(i==idx?3.5f:(i%5==0?2f:1.2f));
                c.drawLine((float)Math.cos(a)*r1,(float)Math.sin(a)*r1,(float)Math.cos(a)*r2,(float)Math.sin(a)*r2,p);
            }
            // Rotating pointer
            c.save(); c.rotate(idx/99f*360f); p.setColor(0xFF38BDF8); p.setShadowLayer(12,0,0,0xFF38BDF8);
            android.graphics.Path tri=new android.graphics.Path(); tri.moveTo(0,-166); tri.lineTo(-8,-151); tri.lineTo(8,-151); tri.close(); c.drawPath(tri,p); p.clearShadowLayer(); c.restore();
            // Center meditative icon
            p.setColor(0xFF0A0F18); c.drawCircle(0,0,62,p); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1.5f); p.setColor(0x5538BDF8); c.drawCircle(0,0,62,p); p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF38BDF8); p.setTextAlign(Paint.Align.CENTER); p.setTextSize(40); p.setTypeface(Typeface.DEFAULT); c.drawText("☪",0,14,p);
            // Bottom translucent-style card
            float cardY=Math.min(halfH-220,260);
            p.setColor(0xDD0F172A); c.drawRoundRect(-175,cardY,175,cardY+145,24,24,p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1); p.setColor(0x33FFFFFF); c.drawRoundRect(-175,cardY,175,cardY+145,24,24,p); p.setStyle(Paint.Style.FILL);
            p.setTextAlign(Paint.Align.CENTER); p.setTypeface(Typeface.create("serif",Typeface.BOLD)); p.setTextSize(name[0].length()>14?27:36); p.setColor(Color.WHITE); c.drawText(name[0],0,cardY+43,p);
            p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); p.setTextSize(18); p.setColor(0xFF38BDF8); c.drawText(name[1],0,cardY+76,p);
            p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL)); p.setTextSize(13.5f); p.setColor(0xFFCBD5E1); c.drawText(name[2],0,cardY+100,p);
            int start=(int)(idx*1440.0/99), end=(int)((idx+1)*1440.0/99);
            p.setTextSize(10.5f); p.setColor(0xFF64748B); c.drawText(String.format(Locale.getDefault(),"%02d:%02d — %02d:%02d",start/60,start%60,(end/60)%24,end%60),0,cardY+120,p);
            p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); p.setTextSize(10); p.setColor(0xFF10B981); c.drawText("SAY IT IN YOUR HEART",0,cardY+137,p);
            c.restore();
        }
    }
}
