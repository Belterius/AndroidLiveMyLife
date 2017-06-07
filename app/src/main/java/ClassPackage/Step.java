package ClassPackage;

import java.io.Serializable;

/**
 * Created by Gimlibéta on 26/05/2017.
 */

public class Step  implements Serializable {

    private String id;
    private String urlPicture;
    private String gpsLongitude;
    private String gpsLatitude;
    private String description;

    public Step(String id, String urlPicture, String gpsLongitude,String gpsLatitude, String description) {
        this.id = id;
        this.urlPicture = urlPicture;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
