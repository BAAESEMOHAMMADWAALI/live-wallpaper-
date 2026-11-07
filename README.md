# 99 Names of Allah — 24-Hour Live Wallpaper

Android live-wallpaper project based on your supplied `index.html` design and its 99-name data.

## Build without Android Studio (GitHub Actions)

You can build the APK in GitHub's cloud for free (within GitHub's usage limits); Android Studio is not required.

1. Sign in to GitHub and create a new **public** repository, for example `asma-ul-husna-live-wallpaper`.
2. Extract this ZIP on your phone or computer.
3. Upload the **contents** of the `AsmaUlHusnaLiveWallpaper` folder to the repository's root. Make sure `.github/workflows/build-apk.yml` is included. If uploading from a phone, GitHub's website may be easier in a desktop browser mode; alternatively use a computer.
4. Open the repository's **Actions** tab.
5. Select **Build Android APK** on the left, then tap **Run workflow** (or push to the `main` branch to trigger it).
6. Wait for the run to finish successfully. Open that run and find **Artifacts** at the bottom.
7. Download `AsmaUlHusna-debug-apk`, unzip it, and install `app-debug.apk` on your Android phone. If Android asks, allow installation from that browser/file manager.

Keep the repository public only if you are comfortable making the source code public. You can use a private repository too, subject to your GitHub plan's Actions limits.

## Install and set wallpaper

1. Install the APK.
2. Open **99 Names Live Wallpaper**.
3. Tap **Preview & Set Wallpaper**.
4. In Android's wallpaper preview, choose **Set wallpaper** and select **Home screen and lock screen** if your phone offers that option.

Some phone manufacturers do not allow third-party live wallpapers on the lock screen. The app cannot bypass that device restriction.

## Build locally (optional)

Requires JDK 17, Android SDK Platform 35, and Gradle 8.9. From this project folder run:

```bash
gradle assembleDebug
```

The APK is created at `app/build/outputs/apk/debug/app-debug.apk`.
