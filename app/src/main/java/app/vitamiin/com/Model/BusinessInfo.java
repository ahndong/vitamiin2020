package app.vitamiin.com.Model;

import java.io.Serializable;

/**
 * Created by Puma on 6/28/2016.
 */
public class BusinessInfo implements Serializable {
    public int _id;
    public String _business;
    public int _good_cnt;
    public String _imagePath;

    public boolean isCheck;
}
