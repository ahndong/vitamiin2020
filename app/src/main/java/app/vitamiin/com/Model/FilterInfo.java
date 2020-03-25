package app.vitamiin.com.Model;

import java.io.Serializable;

/**
 * Created by Puma on 6/28/2016.
 */
public class FilterInfo implements Serializable {
    public int _id;
    public String _string;

    public String _imgPath = "";
    public int _good_cnt = 0;

    public boolean isCheck;
}
