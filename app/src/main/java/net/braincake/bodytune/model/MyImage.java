package net.braincake.bodytune.model;

public class MyImage {
    private int idMyImage;
    private String urlMyImage;
    private Boolean selectMyImage;

    public MyImage() {
    }

    public MyImage(int idMyImage, String urlMyImage, Boolean selectMyImage) {
        this.idMyImage = idMyImage;
        this.urlMyImage = urlMyImage;
        this.selectMyImage = selectMyImage;
    }

    public int getIdMyImage() {
        return idMyImage;
    }

    public void setIdMyImage(int idMyImage) {
        this.idMyImage = idMyImage;
    }

    public String getUrlMyImage() {
        return urlMyImage;
    }

    public void setUrlMyImage(String urlMyImage) {
        this.urlMyImage = urlMyImage;
    }

    public Boolean getSelectMyImage() {
        return selectMyImage;
    }

    public void setSelectMyImage(Boolean selectMyImage) {
        this.selectMyImage = selectMyImage;
    }
}
