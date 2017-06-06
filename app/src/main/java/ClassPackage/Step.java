package ClassPackage;

import java.io.Serializable;

/**
 * Created by Gimlibéta on 26/05/2017.
 */

public class Step  implements Serializable {

    private String id;
    private String urlPicture;
    private String gpsData;
    private String description;

    public Step(String id, String urlPicture, String gpsData, String description) {
        this.id = id;
        this.urlPicture = urlPicture;
        this.gpsData = gpsData;
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

    public String getGpsData() {
        return gpsData;
    }

    public void setGpsData(String gpsData) {
        this.gpsData = gpsData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
