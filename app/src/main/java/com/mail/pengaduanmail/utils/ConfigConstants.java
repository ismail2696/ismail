package com.mail.pengaduanmail.utils;

/**
 * Created by fandis on 20/01/2018.
 */

public class ConfigConstants {
    // web service url constants
    public class ServiceType {
        public static final String BASE_URL = "http://aduan.lauwba.xyz/pengaduan_mail/";
        public static final String LOGIN = BASE_URL + "simple_login.php";
        public static final String REGISTER =  BASE_URL + "simple_register.php";

    }
    // webservice key constants
    public class Params {

        public static final String NAME = "name";
        public static final String HOBBY = "hobby";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }
}

