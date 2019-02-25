package com.example.mobileliarsdice.Models;

public class Chats extends UserInfo {

    private Boolean newChat;

    public Chats(){
        super();
    }

    public Chats(String id ,String userName, String url, Boolean newChat) {
        super(id, userName, url);
        this.newChat = newChat;
    }

    public Boolean getNewChat() {
        return newChat;
    }

    public void setNewChat(Boolean newChat) {
        this.newChat = newChat;
    }

}
