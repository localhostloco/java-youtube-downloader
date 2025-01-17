package com.localhostloco.youtubedownloader.model.formats;

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

import com.google.gson.JsonObject;
import com.localhostloco.youtubedownloader.model.quality.AudioQuality;


public class AudioFormat extends Format {

  private final Integer audioSampleRate;

  public AudioFormat(JsonObject json) throws Exception {
    super(json);
    audioSampleRate = json.get("audio_sample_rate").getAsInt();
  }

  @Override
  public String type() {
    return "audio";
  }

  public AudioQuality audioQuality() {
    return itag.audioQuality();
  }

  public Integer audioSampleRate() {
    return audioSampleRate;
  }
}
