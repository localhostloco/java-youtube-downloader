package com.localhostloco.youtubedownloader.model.formats;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class VideoFormat extends Format {

  private final int fps;
  private final String qualityLabel;
  private final Integer width;
  private final Integer height;

  public VideoFormat(JsonObject json) {
    super(json);
    fps = json.get("fps").getAsInt();
    qualityLabel = json.get("quality_label").getAsString();
    if (json.has("size")) {
      String[] split = json.get("size").getAsString().split("x");
      width = Integer.parseInt(split[0]);
      height = Integer.parseInt(split[1]);
    } else {
      width = json.get("width").getAsInt();
      height = json.get("height").getAsInt();
    }
  }

  @Override
  public String type() {
    return "video";
  }

}
