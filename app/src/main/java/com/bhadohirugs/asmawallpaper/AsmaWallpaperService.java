package com.bhadohirugs.asmawallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.RadialGradient;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.Locale;

public class AsmaWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new AsmaEngine();
    }

    private final class AsmaEngine extends Engine {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean visible;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            visible = isVisible;
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacks(drawRunner);
            super.onSurfaceDestroyed(holder);
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    render(canvas);
                }
            } catch (RuntimeException ignored) {
                // The wallpaper surface can disappear during a screen transition.
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 1000L);
            }
        }

        private void render(Canvas canvas) {
            final int width = canvas.getWidth();
            final int height = canvas.getHeight();

            canvas.drawColor(Color.rgb(8, 12, 20));
            paint.setShader(new RadialGradient(
                    width * 0.5f, height * 0.46f, Math.min(width, height) * 0.58f,
                    new int[]{0xFF122334, 0xFF080C14},
                    null, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            Calendar calendar = Calendar.getInstance();
            int secondsToday =
                    calendar.get(Calendar.HOUR_OF_DAY) * 3600
                    + calendar.get(Calendar.MINUTE) * 60
                    + calendar.get(Calendar.SECOND);

            int totalNames = Names.ALL.length;
            int index = Math.min(
                    (int) (secondsToday / 86400.0 * totalNames),
                    totalNames - 1);
            String[] name = Names.ALL[index];

            // Scale a 400-unit design canvas to the actual wallpaper surface.
            final float scale = Math.min(width / 400f, height / 800f);
            final float halfHeight = height / (2f * scale);

            canvas.save();
            canvas.translate(width / 2f, height / 2f);
            canvas.scale(scale, scale);

            // Top gauge: moved down to avoid the lock-screen clock/date.
            final float gaugeY = -halfHeight + 220f;
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            paint.setTextSize(11f);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(0xFF94A3B8);
            canvas.drawText("24H CYCLE", -170f, gaugeY, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setColor(0xFF38BDF8);
            canvas.drawText((index + 1) + " / " + totalNames, 170f, gaugeY, paint);

            final float barTop = gaugeY + 13f;
            paint.setColor(0x44FFFFFF);
            canvas.drawRoundRect(-170f, barTop, 170f, barTop + 6f, 4f, 4f, paint);
            paint.setColor(0xFF10B981);
            canvas.drawRoundRect(
                    -170f, barTop,
                    -170f + 340f * (index + 1) / totalNames,
                    barTop + 6f, 4f, 4f, paint);

            // Soft ambient glow behind the smaller dial.
            paint.setShader(new RadialGradient(
                    0f, 0f, 175f,
                    new int[]{0x2238BDF8, 0x0810B981, 0x00000000},
                    null, Shader.TileMode.CLAMP));
            canvas.drawCircle(0f, 0f, 175f, paint);
            paint.setShader(null);

            // 99 dial marks.
            final float dialRadius = 138f;
            for (int i = 0; i < totalNames; i++) {
                double angle = (i / (double) totalNames) * Math.PI * 2.0 - Math.PI / 2.0;
                float innerRadius = dialRadius - (i % 5 == 0 ? 15f : 9f);

                paint.setColor(i == index
                        ? 0xFF38BDF8
                        : (i % 5 == 0 ? 0x66FFFFFF : 0x2EFFFFFF));
                paint.setStrokeWidth(i == index ? 3.2f : (i % 5 == 0 ? 2f : 1.2f));
                canvas.drawLine(
                        (float) Math.cos(angle) * innerRadius,
                        (float) Math.sin(angle) * innerRadius,
                        (float) Math.cos(angle) * dialRadius,
                        (float) Math.sin(angle) * dialRadius,
                        paint);
            }

            // Rotating cyan pointer.
            canvas.save();
            canvas.rotate(index / (float) totalNames * 360f);
            paint.setColor(0xFF38BDF8);
            paint.setShadowLayer(10f, 0f, 0f, 0xFF38BDF8);
            Path pointer = new Path();
            pointer.moveTo(0f, -dialRadius - 9f);
            pointer.lineTo(-7f, -dialRadius + 5f);
            pointer.lineTo(7f, -dialRadius + 5f);
            pointer.close();
            canvas.drawPath(pointer, paint);
            paint.clearShadowLayer();
            canvas.restore();

            // Center crescent and star.
            paint.setColor(0xFF0A0F18);
            canvas.drawCircle(0f, 0f, 55f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setColor(0x6638BDF8);
            canvas.drawCircle(0f, 0f, 55f, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF38BDF8);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(36f);
            paint.setTypeface(Typeface.DEFAULT);
            canvas.drawText("☪", 0f, 13f, paint);

            // Compact name card: reduced height and raised above bottom shortcuts.
            final float cardTop = Math.max(174f, Math.min(190f, halfHeight - 250f));
            final float cardHeight = 112f;
            RectF card = new RectF(-175f, cardTop, 175f, cardTop + cardHeight);

            paint.setColor(0xE610172A);
            canvas.drawRoundRect(card, 22f, 22f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(0x4438BDF8);
            canvas.drawRoundRect(card, 22f, 22f, paint);
            paint.setStyle(Paint.Style.FILL);

            // Arabic name.
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create("serif", Typeface.BOLD));
            paint.setTextSize(name[0].length() > 14 ? 25f : 32f);
            paint.setColor(Color.WHITE);
            canvas.drawText(name[0], 0f, cardTop + 35f, paint);

            // English transliteration.
            paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            paint.setTextSize(17f);
            paint.setColor(0xFF38BDF8);
            canvas.drawText(name[1], 0f, cardTop + 61f, paint);

            // Meaning.
            paint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            paint.setTextSize(12.5f);
            paint.setColor(0xFFCBD5E1);
            canvas.drawText(name[2], 0f, cardTop + 82f, paint);

            // Each name occupies an equal portion of the local 24-hour day.
            int startMinute = (int) (index * 1440.0 / totalNames);
            int endMinute = (int) ((index + 1) * 1440.0 / totalNames);
            String timeSlot = String.format(
                    Locale.getDefault(), "%02d:%02d — %02d:%02d",
                    startMinute / 60, startMinute % 60,
                    (endMinute / 60) % 24, endMinute % 60);

            paint.setTextSize(10.5f);
            paint.setColor(0xFF94A3B8);
            canvas.drawText(timeSlot, 0f, cardTop + 101f, paint);

            canvas.restore();
        }
    }
}
