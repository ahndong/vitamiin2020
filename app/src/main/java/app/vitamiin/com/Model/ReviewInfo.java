package app.vitamiin.com.Model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Puma on 6/28/2016.
 */
public class ReviewInfo implements Serializable {
    public int _id;
    public String title; //good name for review or title for sikdan, exper

    public String isok;
    public String _mb_id;
    public String _mb_nick;
    public String f_photo;
    public int _mb_age;
    public int _mb_sex;

    //공통
    public ArrayList<Integer> _good_id = new ArrayList<>();
    public ArrayList<String> _good_photo_urls = new ArrayList<>();
    public ArrayList<String> _good_business = new ArrayList<>();

    public ArrayList<String> _good_name = new ArrayList<>();
    public ArrayList<File> _good_photo_files = new ArrayList<>();
    public ArrayList<String> photos = new ArrayList<>();
    public ArrayList<String> photos_id = new ArrayList<>();
    public ArrayList<File> photosFile = new ArrayList<>();

    public String content;
    public String hash_tag;
    public int category;
    public int period;
    public String regdate;
    public int count;
    public int comment_cnt;
    public int view_cnt;
    public int like_cnt;
    public int like_review;
    public int fallow_user;
    public String _name;

    //review에만
    public int Author_pregnant = 1;
    public int Author_examinee = 1;
    public int Author_lactating = 1;
    public int Author_climacterium = 1;
    public String content2;
    public String content3;
    public int buy_path;
    public int price;
    public int retake;
    public int person;
    public double rate;
    public int func_rate;
    public int price_rate;
    public int take_rate;
    public int taste_rate;

    //exper에만
    public ArrayList<String> knowhow = new ArrayList<>();

    public int disease;
    public String business; //good business
    public String good_photo; //good photo
}
