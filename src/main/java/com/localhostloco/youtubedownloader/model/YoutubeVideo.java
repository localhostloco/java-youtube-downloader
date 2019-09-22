package com.localhostloco.youtubedownloader.model;

import com.localhostloco.youtubedownloader.exceptions.YoutubeException;
import com.localhostloco.youtubedownloader.model.enums.ExtensionEnum;
import com.localhostloco.youtubedownloader.model.formats.AudioFormat;
import com.localhostloco.youtubedownloader.model.formats.Format;
import com.localhostloco.youtubedownloader.model.formats.VideoFormat;
import com.localhostloco.youtubedownloader.model.quality.AudioQuality;
import com.localhostloco.youtubedownloader.model.quality.VideoQuality;
import com.localhostloco.youtubedownloader.runtime.YoutubeDownloader;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class YoutubeVideo {

  private VideoDetails videoDetails;
  private List<Format> formats;

  public YoutubeVideo(VideoDetails videoDetails, List<Format> formats) {
    this.videoDetails = videoDetails;
    this.formats = formats;
  }

  public VideoDetails details() {
    return videoDetails;
  }

  public List<Format> formats() {
    return formats;
  }

  public Optional<Format> findFormatByItag(int itag) {
    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format.getItag().id() == itag)
        return Optional.of(format);
    }
    return Optional.empty();
  }

  public List<VideoFormat> videoFormats() {
    List<VideoFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof VideoFormat)
        find.add((VideoFormat) format);
    }
    return find;
  }

  public List<VideoFormat> findVideoWithQuality(VideoQuality videoQuality) {
    List<VideoFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof VideoFormat && ((VideoFormat) format).getQualityLabel().equals(videoQuality.name()))
        find.add((VideoFormat) format);
    }
    return find;
  }

  public List<VideoFormat> findVideoWithExtension(ExtensionEnum extension) {
    List<VideoFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof VideoFormat && format.getExtension().equals(extension))
        find.add((VideoFormat) format);
    }
    return find;
  }

  public List<AudioFormat> audioFormats() {
    List<AudioFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof AudioFormat)
        find.add((AudioFormat) format);
    }
    return find;
  }

  public List<AudioFormat> findAudioWithQuality(AudioQuality audioQuality) {
    List<AudioFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof AudioFormat && ((AudioFormat) format).audioQuality() == audioQuality)
        find.add((AudioFormat) format);
    }
    return find;
  }

  public List<AudioFormat> findAudioWithExtension(ExtensionEnum extension) {
    List<AudioFormat> find = new LinkedList<>();

    for (int i = 0; i < formats.size(); i++) {
      Format format = formats.get(i);
      if (format instanceof AudioFormat && format.getExtension() == extension)
        find.add((AudioFormat) format);
    }
    return find;
  }

  public File download(Format format, File outDir) throws IOException, YoutubeException {
    if (videoDetails.isLiveContent())
      throw new YoutubeException.LiveVideoException("Can not download live stream");

    if (!outDir.exists()) {
      boolean mkdirs = outDir.mkdirs();
      if (!mkdirs)
        throw new IOException("Could not create output directory: " + outDir);
    }

    String fileName = videoDetails.getTitle() + "." + format.getExtension().getValue();
    File outputFile = new File(outDir, cleanFilename(fileName));

    URL url = new URL(format.getUrl());
    try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
      try (FileOutputStream fis = new FileOutputStream(outputFile)) {
        byte[] buffer = new byte[4096];
        int count = 0;
        while ((count = bis.read(buffer, 0, 4096)) != -1) {
          fis.write(buffer, 0, count);
        }
        return outputFile;
      }
    }
  }

  public void downloadAsync(Format format, File outDir, YoutubeDownloader.DownloadCallback callback) throws IOException, YoutubeException {
    if (videoDetails.isLiveContent())
      throw new YoutubeException.LiveVideoException("Can not download live stream");

    if (!outDir.exists()) {
      boolean mkdirs = outDir.mkdirs();
      if (!mkdirs)
        throw new IOException("Could not create output directory: " + outDir);
    }

    URL url = new URL(format.getUrl());

    String fileName = videoDetails.getTitle() + "." + format.getExtension().getValue();
    File outputFile = new File(outDir, cleanFilename(fileName));

    int i = 1;
    while (outputFile.exists()) {
      fileName = videoDetails.getTitle() + "(" + i++ + ")" + "." + format.getExtension().getValue();
      outputFile = new File(outDir, cleanFilename(fileName));
    }

    File finalOutputFile = outputFile;

    Thread thread = new Thread(() -> {
      try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
        buffer(finalOutputFile, bis, format, callback);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }

  private String cleanFilename(String filename) {
    for (char c : YoutubeDownloader.getILLEGAL_FILENAME_CHARACTERS()) {
      filename = filename.replace(c, '_');
    }
    return filename;
  }

  private void buffer(File finalOutputFile, BufferedInputStream bis, Format format, YoutubeDownloader.DownloadCallback callback) {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(finalOutputFile))) {
      double total = 0;
      byte[] buffer = new byte[4096];
      int count = 0;
      int progress = 0;
      while ((count = bis.read(buffer, 0, 4096)) != -1) {
        bos.write(buffer, 0, count);
        total += count;
        int newProgress = (int) ((total / format.getContentLength()) * 100);
        if (newProgress > progress) {
          progress = newProgress;
          callback.onDownloading(progress);
        }
      }

      callback.onFinished(finalOutputFile);
    } catch (IOException e) {
      callback.onError(e);
    }
  }

}
