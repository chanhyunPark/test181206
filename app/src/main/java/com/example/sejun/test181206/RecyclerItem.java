package com.example.sejun.test181206;

public class RecyclerItem
{
    private String nick;
    private String comment;
    private String date;

    public RecyclerItem(String nick, String comment, String date)
    {
        this.nick = nick;
        this.comment = comment;
        this.date = date;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
