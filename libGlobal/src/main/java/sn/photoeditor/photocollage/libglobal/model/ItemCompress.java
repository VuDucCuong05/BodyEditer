package sn.photoeditor.photocollage.libglobal.model;

import java.io.Serializable;

public class ItemCompress implements Serializable {

    private String linkImage;
    private String size;
    private String sizeCompress;
    private long radius;

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public ItemCompress(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSizeCompress() {
        return sizeCompress;
    }

    public void setSizeCompress(String sizeCompress) {
        this.sizeCompress = sizeCompress;
    }
}
