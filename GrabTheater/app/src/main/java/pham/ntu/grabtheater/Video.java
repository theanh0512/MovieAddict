package pham.ntu.grabtheater;

import java.io.Serializable;

/**
 * Created by Administrator PC on 3/31/2016.
 */
public class Video implements Serializable{
    String id;
    String iso_639_1;
    String key;
    String name;
    String site;
    int size;
    String type;

    public Video(String id,String iso_639_1,String key,String name,String site,int size,String type){
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public String getId() {
        return id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }
}
