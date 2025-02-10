package it.bancomatpay.sdk.manager.task.model;

import java.io.Serializable;

public class BcmLocation implements Serializable {

    private double latitude;
    private double longitude;

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

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @return Distance in Meters
     */
    public double distanceTo(BcmLocation lastLocation) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lastLocation.getLatitude() - this.getLatitude());
        double lonDistance = Math.toRadians(lastLocation.getLongitude() - this.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.getLatitude())) * Math.cos(Math.toRadians(lastLocation.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0.0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
