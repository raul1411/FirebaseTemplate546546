package com.example.firebasetemplate.model;

import java.util.Date;
import java.util.HashMap;

public class Comments {
    public String authorName;
    public String text;
    public HashMap<String, Boolean> likes = new HashMap<>();
    public Date day;
}
