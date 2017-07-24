package org.dreamitcodeit.storyteller;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by mariadeangelis on 7/21/17.
 */

@Parcel
public class User {

    String uid;
    List<String> favoriteIDs;
    List<String> storyIDs;
    String fbName;
    String fbPictureID;

    public User(){

    }

    public User(String uid, List<String> favoriteIDs, List<String> storyIDs, String fbName, String fbPictureID) {
        this.uid = uid;
        this.favoriteIDs = favoriteIDs;
        this.storyIDs = storyIDs;
        this.fbName = fbName;
        this.fbPictureID = fbPictureID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getFavoriteIDs() {
        return favoriteIDs;
    }

    public void setFavoriteIDs(List<String> favoriteIDs) {
        this.favoriteIDs = favoriteIDs;
    }

    public List<String> getStoryIDs() {
        return storyIDs;
    }

    public void setStoryIDs(List<String> storyIDs) {
        this.storyIDs = storyIDs;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbPictureID() {
        return fbPictureID;
    }

    public void setFbPictureID(String fbPictureID) {
        this.fbPictureID = fbPictureID;
    }
}
