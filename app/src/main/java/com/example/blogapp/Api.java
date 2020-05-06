package com.example.blogapp;

public class Api {
    public static final String URL = "http://192.168.1.8:8000/";
    public static final String HOME = URL + "api";
    public static final String LOGIN = HOME + "/login";
    public static final String REGISTER = HOME + "/register";
    public static final String SAVE_USER_INFO = HOME + "/save_user_info";
    public static final String POST =HOME + "/posts";
    public static final String ADD_POST = POST + "/create";
}
