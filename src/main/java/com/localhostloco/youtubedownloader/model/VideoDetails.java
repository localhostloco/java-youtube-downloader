package com.localhostloco.youtubedownloader.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class VideoDetails {

  private String videoId;
  private String title;
  private int lengthSeconds;
  private List<String> keywords;
  private String shortDescription;
  private List<String> thumbnails;
  private String author;
  private int viewCount;
  private int averageRating;
  private boolean isLiveContent;

  public VideoDetails(String videoId) {
    this.videoId = videoId;
    this.title = videoId;
  }

  public void setDetails(JsonObject json) {
    title = json.get("title").getAsString();
    lengthSeconds = json.get("lengthSeconds").getAsInt();
    List<String> jword = Collections.emptyList();
    if (json.has("keywords")) {
      json.get("keywords").getAsJsonArray().forEach(jsonElement -> jword.add(jsonElement.getAsString()));
    }
    keywords = jword;
    shortDescription = json.get("shortDescription").getAsString();
    JsonArray jsonThumbnails = json.get("thumbnail").getAsJsonObject().get("thumbnails").getAsJsonArray();
    thumbnails = new ArrayList<>(jsonThumbnails.size());
    for (int i = 0; i < jsonThumbnails.size(); i++) {
      JsonObject jsonObject = jsonThumbnails.get(i).getAsJsonObject();
      if (jsonObject.has("url"))
        thumbnails.add(jsonObject.get("url").getAsString());
    }
    averageRating = json.get("averageRating").getAsInt();
    viewCount = json.get("viewCount").getAsInt();
    author = json.get("author").getAsString();
    isLiveContent = json.get("isLiveContent").getAsBoolean();
  }

}
