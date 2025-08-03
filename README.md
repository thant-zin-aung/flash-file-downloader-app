# Flash File Downloader (YouTube + Direct File Support)

A Java-based downloader application with a clean GUI, capable of:

- Downloading and merging YouTube videos using `yt-dlp` and `ffmpeg`
- Downloading any file from the internet that supports the `Accept-Ranges` HTTP header (multi-threaded)

This app supports a Chrome Extension integration for one-click downloads and real-time GUI progress tracking.

---

## 📥 Download

Download the latest version of the app from the [Releases](https://github.com/thant-zin-aung/flash-file-downloader-app/releases) page, or directly:

👉 **[Download FlashFileDownloaderSetup.exe](https://github.com/thant-zin-aung/flash-file-downloader-app/releases/download/v1.0.0-installer/Flash.File.Downloader.Setup.exe)**

---

## 🚀 Features

🎨 JavaFX-based GUI  
🎥📂 YouTube video/audio format selection  
🔗🎞️ Merging via `ffmpeg`  
⏳📊 Real-time download progress with speed, ETA, and filename  
⚙️🧵 Custom multi-threaded downloader for direct HTTP/HTTPS files  
👻🔕 Background mode with taskbar icon hidden  
🧩🌐 Chrome Extension for download triggering  
✍️🧹 Auto filename sanitization  
🌍🔒 CORS-compatible backend for extension  

---

## 🧰 Tech Stack

- Java 17+
- JavaFX
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)
- [ffmpeg](https://ffmpeg.org/)
- Gson (for JSON parsing)
- Chrome Extension (optional)

---

## 📦 Requirements

- Java 17+  
- `yt-dlp` binary (set in `YtDlpManager`)  
- `ffmpeg` binary (set in `YtDlpManager`)  
- Internet connection  
- For direct file downloads: file server must support `Accept-Ranges: bytes`

---

## 📂 Project Structure

```
src/
├── controllers/
│ └── DownloadController.java
├── downloader/
│ └── MultiThreadedDownloader.java
├── yt_dlp/
│ └── YoutubeUtility.java
├── server/
│ └── LocalHttpServer.java (CORS-enabled)
├── utils/
│ ├── ObservableValue.java
│ └── ObjectUtil.java
└── Main.java
```


---

## 💻 How to Use

### ▶️ YouTube Downloads
1. Paste a YouTube URL.
2. Fetch available formats.
3. Choose your preferred video+audio format.
4. Click **Download**.
5. Watch real-time download stats (progress %, speed, ETA, etc.).
6. File auto-merges via `ffmpeg`.

### 📥 Direct File Downloads
1. Paste any valid HTTP/HTTPS file URL.
2. If the file supports `Accept-Ranges`, the app will:
   - Split download into chunks
   - Download in parallel
   - Merge them on disk
   - Track progress, speed, ETA

---

## 🔌 Chrome Extension Integration (Optional)

You can use the Chrome Extension to:
- Detect YouTube, Facebook, or generic downloadable files
- Trigger download using your local Java server via:
```
http://localhost:12345/formats?url=ENCODED_URL
http://localhost:12345/download?url=ENCODED_URL
```


Ensure your backend includes this CORS header:
```java
exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
```

## 📌 Notes
 - Direct downloads rely on Content-Length and Accept-Ranges.
YouTube videos with DASH format will be merged (video + audio) automatically.

- All filenames are sanitized for cross-platform compatibility.

- Extension supports POST or GET to your Java local server.

- Files are saved to your default Downloads/ folder.

## 📄 License
MIT License © 2025 - Thant Zin Aung (tza.personal.dev@gmail.com)


---

Let me know if you want:

- Example screenshots in the Markdown  
- Instructions for bundling `yt-dlp` and `ffmpeg`  
- Windows startup integration (task scheduler)  
- Packaging the app as `.exe` or `.msi` installer
