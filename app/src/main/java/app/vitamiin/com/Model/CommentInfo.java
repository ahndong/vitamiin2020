package app.vitamiin.com.Model;

import java.io.Serializable;

/**
 * Created by Puma on 6/28/2016.
 */
public class CommentInfo implements Serializable {
    public int _id;
    public String _mb_id;
    public String _mb_nick;
    public String f_photo;
    public int _review_id;
    public String content;
    public String regdate;
    public int count;
    public int category;
}
