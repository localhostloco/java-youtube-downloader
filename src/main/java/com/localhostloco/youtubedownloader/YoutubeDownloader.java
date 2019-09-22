package com.localhostloco.youtubedownloader;

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
import com.google.gson.JsonParser;
import com.localhostloco.youtubedownloader.model.Itag;
import com.localhostloco.youtubedownloader.model.VideoDetails;
import com.localhostloco.youtubedownloader.model.YoutubeVideo;
import com.localhostloco.youtubedownloader.model.formats.AudioFormat;
import com.localhostloco.youtubedownloader.model.formats.AudioVideoFormat;
import com.localhostloco.youtubedownloader.model.formats.Format;
import com.localhostloco.youtubedownloader.model.formats.VideoFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class YoutubeDownloader {

  public static final char[] ILLEGAL_FILENAME_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
  private static final String AUDIO = "audio";
  private static final String VIDEO = "video";
  private static final String CONFIG_START = "ytplayer.config = ";
  private static final String CONFIG_END = ";ytplayer.load";
  private static final String ERROR = "\"status\":\"ERROR\",\"reason\":\"";

  public static YoutubeVideo getVideo(String videoId) throws YoutubeException, IOException {
    String page = loadPage("https://www.youtube.com/watch?v=" + videoId);

    VideoDetails videoDetails = new VideoDetails(videoId);

    int start = page.indexOf(CONFIG_START);
    int end = page.indexOf(CONFIG_END);

    if (start == -1 || end == -1) {
      int errorIndex = page.indexOf(ERROR);
      if (errorIndex != -1) {
        String reason = page.substring(errorIndex + ERROR.length(), page.indexOf("\"", errorIndex + ERROR.length() + 1));
        throw new YoutubeException.VideoUnavailableException(reason);
      } else {
        throw new YoutubeException.BadPageException("Could not parse web page");
      }
    }
    String cfg = page.substring(start + CONFIG_START.length(), end);

    JsonArray jsonAdaptiveFormats;

    String adaptive_fmts;
    String url_encoded_fmt_stream_map;
    try {
      JsonObject config = new JsonParser().parse(cfg).getAsJsonObject();
      JsonObject args = config.getAsJsonObject("args");

      url_encoded_fmt_stream_map = args.get("url_encoded_fmt_stream_map").getAsString();

      adaptive_fmts = args.get("adaptive_fmts").getAsString();
      jsonAdaptiveFormats = parseAdaptiveFormats(adaptive_fmts);
      JsonObject player_response = new JsonParser().parse(args.get("player_response").getAsString()).getAsJsonObject();
      if (player_response.has("videoDetails"))
        videoDetails.setDetails(player_response.get("videoDetails").getAsJsonObject());
    } catch (Exception e) {
      throw new YoutubeException.BadPageException("Could not parse web page");
    }

    List<Format> formats = new ArrayList<>(jsonAdaptiveFormats.size() + 1);

    try {
      formats.add(new AudioVideoFormat(splitQuery(url_encoded_fmt_stream_map)));
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (int i = 0; i < jsonAdaptiveFormats.size(); i++) {
      JsonObject json = jsonAdaptiveFormats.get(i).getAsJsonObject();
      try {
        Itag itag = Itag.valueOf("i" + json.get("itag").getAsInt());

        if (itag.isVideo() && itag.isAudio())
          formats.add(new AudioVideoFormat(json));
        else if (itag.isVideo())
          formats.add(new VideoFormat(json));
        else if (itag.isAudio())
          formats.add(new AudioFormat(json));
      } catch (IllegalArgumentException e) {
        System.err.println("Unknown itag " + json.get("itag").getAsInt());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return new YoutubeVideo(videoDetails, formats);
  }

  private static String loadPage(String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
    connection.setRequestProperty("Accept-Language", "en-US,en;");

    BufferedReader in = new BufferedReader(new InputStreamReader(
            connection.getInputStream()));

    StringBuilder sb = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null)
      sb.append(inputLine);
    in.close();

    return sb.toString();
  }

  private static JsonArray parseAdaptiveFormats(String adaptive_fmts) {
    JsonArray array = new JsonArray();

    String splitBy = adaptive_fmts.substring(0, adaptive_fmts.indexOf("=") + 1);
    Pattern pattern = Pattern.compile("&" + splitBy + "|^" + splitBy + "|," + splitBy);
    for (String s : pattern.split(adaptive_fmts)) {
      if (!s.isEmpty()) {
        JsonObject params = splitQuery(splitBy + s);
        if (params.has("url"))
          array.add(params);
      }

    }

    return array;
  }

  private static JsonObject splitQuery(String requestString) {
    JsonObject query_pairs = new JsonObject();
    try {
      if (requestString != null) {
        String[] pairs = requestString.split("&");

        for (String pair : pairs) {
          String[] commaPairs = pair.split(",");
          for (String commaPair : commaPairs) {
            int idx = commaPair.indexOf("=");
            query_pairs.addProperty(URLDecoder.decode(commaPair.substring(0, idx), "UTF-8"), URLDecoder.decode(commaPair.substring(idx + 1), "UTF-8"));

          }
        }
      }
    } catch (Exception e) {
      System.err.println(requestString);
      e.printStackTrace();
    }
    return query_pairs;
  }

  public interface DownloadCallback {

    void onDownloading(int progress);

    void onFinished(File file);

    void onError(Throwable throwable);
  }

}
