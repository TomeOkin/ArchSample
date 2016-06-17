/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.archsample.data.model;

//{
//    "_id": "5720370967765974fbfcf992",
//    "createdAt": "2016-04-27T11:50:33.254Z",
//    "desc": "\u5728\u7ebf JSON \u8f6c POJO \uff0c\u8d85\u7b80\u5355\u5b9e\u7528",
//    "publishedAt": "2016-04-27T12:04:15.19Z",
//    "source": "chrome",
//    "type": "Android",
//    "url": "https://github.com/joelittlejohn/jsonschema2pojo",
//    "used": true,
//    "who": "mthli"
//}

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 主要的资讯的数据模型，格式如上
 */
public class NewsData implements Parcelable {
  public String _id; // 消息 id
  public String createdAt; // 创建时间
  public String desc; // 描述
  public String publishedAt; // 发布时间
  public String source; // 来源平台
  public String type; // 内容类型
  public String url; // 消息地址
  public boolean used;
  public String who;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this._id);
    dest.writeString(this.createdAt);
    dest.writeString(this.desc);
    dest.writeString(this.publishedAt);
    dest.writeString(this.source);
    dest.writeString(this.type);
    dest.writeString(this.url);
    dest.writeByte(used ? (byte) 1 : (byte) 0);
    dest.writeString(this.who);
  }

  public NewsData() {
  }

  protected NewsData(Parcel in) {
    this._id = in.readString();
    this.createdAt = in.readString();
    this.desc = in.readString();
    this.publishedAt = in.readString();
    this.source = in.readString();
    this.type = in.readString();
    this.url = in.readString();
    this.used = in.readByte() != 0;
    this.who = in.readString();
  }

  public static final Parcelable.Creator<NewsData> CREATOR = new Parcelable.Creator<NewsData>() {
    @Override
    public NewsData createFromParcel(Parcel source) {
      return new NewsData(source);
    }

    @Override
    public NewsData[] newArray(int size) {
      return new NewsData[size];
    }
  };
}
