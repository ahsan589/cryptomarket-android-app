package com.example.cryptomarket.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable {
    private String id;
    private String guid;
    private int published_on;
    private String imageurl;
    private String title;
    private String url;
    private String body;
    private String tags;
    private String lang;
    private String upvotes;
    private String downvotes;
    private String categories;
    private SourceInfo source_info;
    private String source;

    // Default Constructor
    public NewsItem() {
    }

    // Parameterized Constructor
    public NewsItem(String id, String guid, int published_on, String imageurl, String title, String url, String body,
                    String tags, String lang, String upvotes, String downvotes, String categories,
                    SourceInfo source_info, String source) {
        this.id = id;
        this.guid = guid;
        this.published_on = published_on;
        this.imageurl = imageurl;
        this.title = title;
        this.url = url;
        this.body = body;
        this.tags = tags;
        this.lang = lang;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.categories = categories;
        this.source_info = source_info;
        this.source = source;
    }

    // Parcelable Constructor
    protected NewsItem(Parcel in) {
        id = in.readString();
        guid = in.readString();
        published_on = in.readInt();
        imageurl = in.readString();
        title = in.readString();
        url = in.readString();
        body = in.readString();
        tags = in.readString();
        lang = in.readString();
        upvotes = in.readString();
        downvotes = in.readString();
        categories = in.readString();
        source = in.readString();
        source_info = in.readParcelable(SourceInfo.class.getClassLoader());
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(guid);
        dest.writeLong(published_on);
        dest.writeString(imageurl);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(body);
        dest.writeString(tags);
        dest.writeString(lang);
        dest.writeString(upvotes);
        dest.writeString(downvotes);
        dest.writeString(categories);
        dest.writeString(source);
        dest.writeParcelable(source_info, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public int getPublished_on() { return published_on; }
    public void setPublished_on(long published_on) { this.published_on = (int) published_on; }

    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getUpvotes() { return upvotes; }
    public void setUpvotes(String upvotes) { this.upvotes = upvotes; }

    public String getDownvotes() { return downvotes; }
    public void setDownvotes(String downvotes) { this.downvotes = downvotes; }

    public String getCategories() { return categories; }
    public void setCategories(String categories) { this.categories = categories; }

    public SourceInfo getSource_info() { return source_info; }
    public void setSource_info(SourceInfo source_info) { this.source_info = source_info; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    // Nested SourceInfo Class
    public static class SourceInfo implements Parcelable {
        private String name;
        private String img;
        private String lang;

        // Default Constructor
        public SourceInfo() {
        }

        // Parameterized Constructor
        public SourceInfo(String name, String img, String lang) {
            this.name = name;
            this.img = img;
            this.lang = lang;
        }

        // Parcelable Constructor
        protected SourceInfo(Parcel in) {
            name = in.readString();
            img = in.readString();
            lang = in.readString();
        }

        public static final Creator<SourceInfo> CREATOR = new Creator<SourceInfo>() {
            @Override
            public SourceInfo createFromParcel(Parcel in) {
                return new SourceInfo(in);
            }

            @Override
            public SourceInfo[] newArray(int size) {
                return new SourceInfo[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(img);
            dest.writeString(lang);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImg() { return img; }
        public void setImg(String img) { this.img = img; }

        public String getLang() { return lang; }
        public void setLang(String lang) { this.lang = lang; }
    }
}
