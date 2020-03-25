package app.vitamiin.com.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class GoodInfo implements Serializable {
    public int _id;
    public int _pf_id;
    public String _name;
    public double _rate;

    public String manufacturer;
    public String import_company;

    public String _business;
    public int _business_id;
    public String _report;
    public String _regdate;
    public String _expiredate;
    public String _ikon;
    public String _amount_per_intake;
    public String _unit_amount_per_intake;
    public String _intake;
    public String _preserve;
    public String _warning;
    public String _kind;
    public int _view_cnt;
    public int _review_cnt;
    public int _like_cnt;
    public String _imagePath = "";

    public int like_good;

    public boolean isCheck;
}
