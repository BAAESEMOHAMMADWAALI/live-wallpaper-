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
            final int w = c.getWidth();
            final int h = c.getHeight();
            c.drawColor(Color.rgb(8, 12, 20));
            p.setShader(new android.graphics.RadialGradient(w * .5f, h * .46f,
                    Math.min(w, h) * .62f,
                    new int[]{0xFF10202B, 0xFF080C14}, null,
                    android.graphics.Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);
            p.setShader(null);

            // Keep the original 24-hour / 99-name calculation unchanged.
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int seconds = cal.get(java.util.Calendar.HOUR_OF_DAY) * 3600
                    + cal.get(java.util.Calendar.MINUTE) * 60
                    + cal.get(java.util.Calendar.SECOND);
            int idx = Math.min((int) (seconds / 86400.0 * Names.ALL.length), Names.ALL.length - 1);
            String[] name = Names.ALL[idx];

            float scale = Math.min(w / 400f, h / 800f);
            c.save();
            c.translate(w / 2f, h / 2f);
            c.scale(scale, scale);
            float halfH = h / (2f * scale);

            // TOP SAFE AREA: place the gauge below the system clock/date zone.
            float gaugeY = -halfH + 250f;
            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextSize(11f);
            p.setColor(0xFF94A3B8);
            p.setTextAlign(Paint.Align.LEFT);
            c.drawText("24H CYCLE", -170f, gaugeY, p);
            p.setColor(0xFF38BDF8);
            p.setTextAlign(Paint.Align.RIGHT);
            c.drawText((idx + 1) + " / 99", 170f, gaugeY, p);
            p.setColor(0x33FFFFFF);
            c.drawRoundRect(-170f, gaugeY + 13f, 170f, gaugeY + 20f, 5f, 5f, p);
            p.setColor(0xFF10B981);
            c.drawRoundRect(-170f, gaugeY + 13f,
                    -170f + 340f * (idx + 1) / 99f, gaugeY + 20f, 5f, 5f, p);

            // CENTER DIAL: slightly smaller and vertically balanced.
            final float dialY = -10f;
            final float dialRadius = 140f;
            c.save();
            c.translate(0f, dialY);
            p.setShader(new android.graphics.RadialGradient(0f, 0f, 165f,
                    new int[]{0x2238BDF8, 0x0810B981, 0x00000000}, null,
                    android.graphics.Shader.TileMode.CLAMP));
            c.drawCircle(0f, 0f, 165f, p);
            p.setShader(null);

            // 99 fixed tick marks around the dial.
            for (int i = 0; i < Names.ALL.length; i++) {
                double angle = (i / (double) Names.ALL.length) * Math.PI * 2 - Math.PI / 2;
                float inner = dialRadius - (i % 5 == 0 ? 15f : 9f);
                float outer = dialRadius;
                p.setColor(i == idx ? 0xFF38BDF8
                        : (i % 5 == 0 ? 0x66FFFFFF : 0x2EFFFFFF));
                p.setStrokeWidth(i == idx ? 3.2f : (i % 5 == 0 ? 1.8f : 1.1f));
                c.drawLine((float) Math.cos(angle) * inner,
                        (float) Math.sin(angle) * inner,
                        (float) Math.cos(angle) * outer,
                        (float) Math.sin(angle) * outer, p);
            }

            // Pointer still advances through all 99 positions over 24 hours.
            c.save();
            c.rotate(idx / (float) Names.ALL.length * 360f);
            p.setColor(0xFF38BDF8);
            p.setShadowLayer(10f, 0f, 0f, 0xFF38BDF8);
            android.graphics.Path pointer = new android.graphics.Path();
            pointer.moveTo(0f, -152f);
            pointer.lineTo(-7f, -139f);
            pointer.lineTo(7f, -139f);
            pointer.close();
            c.drawPath(pointer, p);
            p.clearShadowLayer();
            c.restore();

            // Center crescent emblem.
            p.setColor(0xFF0A0F18);
            c.drawCircle(0f, 0f, 56f, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.5f);
            p.setColor(0x6638BDF8);
            c.drawCircle(0f, 0f, 56f, p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF38BDF8);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(36f);
            p.setTypeface(Typeface.DEFAULT);
            c.drawText("☪", 0f, 13f, p);
            c.restore();

            // COMPACT NAME CARD: sits below the dial and above fingerprint/shortcuts.
            float cardY = Math.max(dialY + dialRadius + 24f,
                    Math.min(halfH - 300f, 170f));
            float cardTop = cardY;
            float cardBottom = cardTop + 116f;
            p.setColor(0xE610172A);
            c.drawRoundRect(-175f, cardTop, 175f, cardBottom, 22f, 22f, p);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1f);
            p.setColor(0x4438BDF8);
            c.drawRoundRect(-175f, cardTop, 175f, cardBottom, 22f, 22f, p);
            p.setStyle(Paint.Style.FILL);

            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.create("serif", Typeface.BOLD));
            p.setTextSize(name[0].length() > 14 ? 29f : 33f);
            p.setColor(Color.WHITE);
            drawCenteredFit(c, name[0], 0f, cardTop + 34f, p, 310f);

            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextSize(17f);
            p.setColor(0xFF38BDF8);
            drawCenteredFit(c, name[1], 0f, cardTop + 61f, p, 310f);

            p.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            p.setTextSize(12f);
            p.setColor(0xFFCBD5E1);
            drawCenteredFit(c, name[2], 0f, cardTop + 81f, p, 310f);

            int start = (int) (idx * 1440.0 / Names.ALL.length);
            int end = (int) ((idx + 1) * 1440.0 / Names.ALL.length);
            p.setTextSize(9.5f);
            p.setColor(0xFF8493A8);
            String timeSlot = String.format(Locale.getDefault(), "%02d:%02d — %02d:%02d",
                    start / 60, start % 60, (end / 60) % 24, end % 60);
            c.drawText(timeSlot, 0f, cardTop + 99f, p);

            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextSize(9f);
            p.setColor(0xFF10B981);
            c.drawText("SAY IT IN YOUR HEART", 0f, cardTop + 111f, p);
            c.restore();
        }

        private void drawCenteredFit(Canvas canvas, String text, float x, float y,
                                     Paint paint, float maxWidth) {
            float originalSize = paint.getTextSize();
            float measured = paint.measureText(text);
            if (measured > maxWidth && measured > 0f) {
                paint.setTextSize(originalSize * maxWidth / measured);
            }
            canvas.drawText(text, x, y, paint);
            paint.setTextSize(originalSize);
        }
    }
                }
