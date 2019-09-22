package com.localhostloco.youtubedownloader.model;

/*-
 * #
 * Java youtube video and audio downloader
 *
 * Copyright (C) 2019 Igor Kiulian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;

import java.util.ArrayList;
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
//        keywords = json.containsKey("keywords") ? json.getJsonArray("keywords").stream().collect( () -> {}); : Collections.emptyList();
    shortDescription = json.get("shortDescription").getAsString();
    JsonArray jsonThumbnails = json.get("thumbnail").getAsJsonObject().get("thumbnails").getAsJsonArray();
    thumbnails = new ArrayList<>(jsonThumbnails.size());
    for (int i = 0; i < jsonThumbnails.size(); i++) {
      JsonObject JsonObject = jsonThumbnails.get(i).getAsJsonObject();
      if (JsonObject.has("url"))
        thumbnails.add(JsonObject.get("url").getAsString());
    }
    averageRating = json.get("averageRating").getAsInt();
    viewCount = json.get("viewCount").getAsInt();
    author = json.get("author").getAsString();
    isLiveContent = json.get("isLiveContent").getAsBoolean();
  }

}
