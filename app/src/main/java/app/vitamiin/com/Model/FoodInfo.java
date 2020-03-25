package app.vitamiin.com.Model;
import java.io.Serializable;

public class FoodInfo implements Serializable {
    public int _id = 0;
    public String _name;
    public String _imagePath;
    public int _view_cnt = 0;
    public int _like_cnt = 0;
    public int like_wiki = 0;
}
