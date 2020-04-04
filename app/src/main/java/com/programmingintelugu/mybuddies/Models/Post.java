package com.programmingintelugu.mybuddies.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post implements Serializable {
    String docID;
    String imageURL;
    String title;
    String description;
    @ServerTimestamp
    Date date;

   public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("title",title);
        map.put("imageURL",imageURL);
        map.put("description",description);
        return  map;

    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Post{" +
                "docID='" + docID + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
