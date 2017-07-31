package org.dreamitcodeit.storyteller.models;


import org.parceler.Parcel;

import java.util.List;

/**
 * Created by mariadeangelis on 7/21/17.
 */

@Parcel
public class User {

    String uid;
    List<String> favoriteIDs;
    List<String> userStoryIDs;
    String fbName;
    String fbUserID;
    String bio;
    String location;

    public User(){

    }

    public User(String uid, List<String> userStoryIDs, List<String> favoriteIDs, String fbName, String fbUserID, String bio, String location) {
        this.uid = uid;
        this.favoriteIDs = favoriteIDs;
        this.fbName = fbName;
        this.fbUserID = fbUserID;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getFavoriteIDs() {
        return favoriteIDs;
    }//TODO: figure out how to make ArrayList instead of list

    public void setFavoriteIDs(List<String> favoriteIDs) {
        this.favoriteIDs = favoriteIDs;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbUserID() {
        return fbUserID;
    }

    public void setFbUserID(String fbUserID) {
        this.fbUserID = fbUserID;
    }

    public List<String> getUserStoryIDs() {
        return userStoryIDs;
    }

    public void setUserStoryIDs(List<String> userStoryIDs) {
        this.userStoryIDs = userStoryIDs;
    }


}