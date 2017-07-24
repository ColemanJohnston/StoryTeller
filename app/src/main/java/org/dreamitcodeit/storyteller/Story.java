package org.dreamitcodeit.storyteller;


import org.parceler.Parcel;

/**
 * Created by neeharmb on 7/12/17.
 */

@Parcel
public class Story {

    String title;
    String storyBody;
    String uID;
    double latitude;
    double longitude;
    String date;
    double rating;
    boolean isCheckedIn;
    String storyId;
    int favCount;

    //private LatLng latLong;

    // TODO - needs to be public for parcelable and private for firebase 😞
    public Story(){}

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Story(String title, String storyBody, String uID, double latitude, double longitude, String date, boolean isCheckedIn, int favCount ){
        this.title = title;
        this.storyBody = storyBody;
        this.uID = uID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.isCheckedIn = isCheckedIn;
        this.favCount = favCount;
    }

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStoryBody() {
        return storyBody;
    }

    public void setStoryBody(String storyBody) {
        this.storyBody = storyBody;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsCheckedIn(){
        return isCheckedIn;
    }

    public void setIsCheckedIn(boolean isCheckedIn){
        this.isCheckedIn = isCheckedIn;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }
}