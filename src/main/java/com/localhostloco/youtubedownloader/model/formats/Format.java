package com.localhostloco.youtubedownloader.model.formats;

import com.google.gson.JsonObject;
import com.localhostloco.youtubedownloader.model.Itag;
import com.localhostloco.youtubedownloader.model.enums.ExtensionEnum;
import lombok.Data;

@Data
public abstract class Format {

  private final String url;
  private final String mimeType;
  private final ExtensionEnum extension;
  private final Integer bitrate;
  private final Long contentLength;
  private final Long lastModified;
  protected Itag itag;

  protected Format(JsonObject json) {
    try {
      itag = Itag.valueOf("i" + json.get("itag").getAsInt());
    } catch (ExceptionInInitializerError e) {
      e.printStackTrace();
      itag = Itag.unknown;
      itag.setId(json.get("itag").getAsInt());
    }
    url = json.get("url").getAsString().replace("\\u0026", "&");
    mimeType = json.get("type").getAsString();
    bitrate = json.get("bitrate").getAsInt();
    contentLength = json.get("clen").getAsLong();
    lastModified = json.get("lmt").getAsLong();

    if (mimeType.contains(ExtensionEnum.MP4.getValue()))
      extension = ExtensionEnum.MP4;
    else if (mimeType.contains(ExtensionEnum.WEBM.getValue()))
      extension = ExtensionEnum.WEBM;
    else if (mimeType.contains(ExtensionEnum.FLV.getValue()))
      extension = ExtensionEnum.FLV;
    else if (mimeType.contains(ExtensionEnum.HLS.getValue()))
      extension = ExtensionEnum.HLS;
    else if (mimeType.contains(ExtensionEnum.THREEGP.getValue()))
      extension = ExtensionEnum.THREEGP;
    else if (mimeType.contains(ExtensionEnum.M4A.getValue()))
      extension = ExtensionEnum.MP4;
    else
      extension = ExtensionEnum.UNKNOWN;

  }

  public abstract String type();
}
