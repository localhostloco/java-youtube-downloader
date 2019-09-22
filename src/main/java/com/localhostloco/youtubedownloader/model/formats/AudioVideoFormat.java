package com.localhostloco.youtubedownloader.model.formats;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class AudioVideoFormat extends Format {

  private final String qualityLabel;
  private final Integer width;
  private final Integer height;

  private Integer audioSampleRate;

  public AudioVideoFormat(JsonObject json) {
    super(json);
    qualityLabel = json.get("qualityLabel").getAsString();
    width = json.get("width").getAsInt();
    height = json.get("height").getAsInt();
    audioSampleRate = json.get("audio_sample_rate").getAsInt();
  }

  @Override
  public String type() {
    return "audio/video";
  }

}
