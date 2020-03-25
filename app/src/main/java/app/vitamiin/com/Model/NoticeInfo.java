package app.vitamiin.com.Model;

import java.io.Serializable;

/**
 * Created by Puma on 6/28/2016.
 */
public class NoticeInfo implements Serializable {
    public int _id;
    public String _subject;
    public String _content;
    public String regdate;

    public boolean view;
}
