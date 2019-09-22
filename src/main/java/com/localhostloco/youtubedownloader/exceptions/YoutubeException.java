package com.localhostloco.youtubedownloader.exceptions;

public class YoutubeException extends Exception {
  private YoutubeException(String message) {
    super(message);
  }

  public static class VideoUnavailableException extends YoutubeException {

    public VideoUnavailableException(String message) {
      super(message);
    }
  }

  public static class BadPageException extends YoutubeException {

    public BadPageException(String message) {
      super(message);
    }
  }

  public static class FormatNotFoundException extends YoutubeException {

    public FormatNotFoundException(String message) {
      super(message);
    }

  }

  public static class LiveVideoException extends YoutubeException {

    public LiveVideoException(String message) {
      super(message);
    }

  }
}
