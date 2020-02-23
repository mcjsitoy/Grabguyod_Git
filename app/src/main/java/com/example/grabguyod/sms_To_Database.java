package com.example.grabguyod;

public class sms_To_Database {

    String msg_id;
    String user_phonenumber;
    String user_message;

    public sms_To_Database(String msg_id, String user_phonenumber, String user_message) {
        this.msg_id = msg_id;
        this.user_phonenumber = user_phonenumber;
        this.user_message = user_message;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public String getUser_phonenumber() {
        return user_phonenumber;
    }

    public String getUser_message() {
        return user_message;
    }
}
