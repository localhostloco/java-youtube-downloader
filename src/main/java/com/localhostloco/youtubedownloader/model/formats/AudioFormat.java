package com.localhostloco.youtubedownloader.model.formats;

import com.google.gson.JsonObject;
import com.localhostloco.youtubedownloader.model.quality.AudioQuality;


public class AudioFormat extends Format {

  private final Integer audioSampleRate;

  public AudioFormat(JsonObject json) {
    super(json);
    audioSampleRate = json.get("audio_sample_rate").getAsInt();
  }

  @Override
  public String type() {
    return "audio";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AudioFormat)) return false;
    AudioFormat obj = (AudioFormat) o;
    return (obj.getExtension().equals(this.getExtension()) && obj.getUrl().equals(this.getUrl()) && obj.getContentLength().equals(this.getContentLength()));
  }

  @Override
  public int hashCode() {
    return audioSampleRate.hashCode();
  }

  public AudioQuality audioQuality() {
    return itag.audioQuality();
  }

  public Integer audioSampleRate() {
    return audioSampleRate;
  }
}
