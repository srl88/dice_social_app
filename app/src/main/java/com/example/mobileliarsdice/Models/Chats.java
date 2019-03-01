package com.example.mobileliarsdice.Models;

public class Chats extends UserInfo {

    private Boolean newChat;

    public Chats(){
        super();
    }

    public Chats(String id ,String userName, String url, Boolean newChat) {
        //Note that the chat id is the "friend" id, since the parent node is the current user id.
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
