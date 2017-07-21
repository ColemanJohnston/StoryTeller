package org.dreamitcodeit.storyteller;


import org.parceler.Parcel;

/**
 * Created by neeharmb on 7/12/17.
 */

@Parcel
public class Story {

    String title;
    String storyBody;
    String userName;
    String screenName;
    String uID;
    double latitude;
    double longitude;
    String date;
    boolean isCheckedIn;

    //private LatLng latLong;

    // TODO - needs to be public for parcelable and private for firebase :(
    public Story(){}


    public Story(String title, String storyBody, String userName, String screenName, String uID, double latitude, double longitude, String date, boolean isCheckedIn){
        this.title = title;
        this.storyBody = storyBody;
        this.userName = userName;
        this.screenName = screenName;
        this.uID = uID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.isCheckedIn = isCheckedIn;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
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
}
