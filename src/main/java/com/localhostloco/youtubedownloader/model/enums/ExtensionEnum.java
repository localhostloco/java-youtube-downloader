package com.localhostloco.youtubedownloader.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExtensionEnum {

  MP4("mp4"), WEBM("webm"), THREEGP("3gp"),
  FLV("flv"),
  HLS("hls"),
  M4A("m4a"),
  UNKNOWN("unknown");

  private String value;
}
