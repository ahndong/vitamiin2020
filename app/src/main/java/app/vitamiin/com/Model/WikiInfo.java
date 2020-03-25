package app.vitamiin.com.Model;

import java.io.Serializable;

/**
 * Created by Puma on 6/28/2016.
 */
public class WikiInfo implements Serializable {
    public int _id;
    public int _type;
    public String title;
    public String _content;
    public String _imagePath;
    public int _view_cnt;
    public int _comment_cnt;
    public int _like_cnt;

    public int like_wiki;
}
