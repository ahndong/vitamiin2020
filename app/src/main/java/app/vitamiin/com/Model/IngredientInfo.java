package app.vitamiin.com.Model;

import java.io.Serializable;
/**
 * Created by dong8 on 2017-02-19.
 */

public class IngredientInfo implements Serializable{
    public int _id;
    public int _mat_id;
    public int _type;
    public String _name="";
    public String _name_kor="";
    public String _name_eng="";
    public String _functionality="";
    public String _definition="";
    public String _daily="";
    public String _caution="";
    public String _class="";
    public int _ewg=0;
    public Boolean _ewg12=false;
    public Boolean _harmful=false;
}
