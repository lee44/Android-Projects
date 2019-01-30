package jlee.app.cal.wampfirebasemessaging;

class UserMessage
{
    public final static int MSG_TYPE_SENT = 1;
    public final static int MSG_TYPE_RECEIVED = 2;
    private String msgContent;
    private long createdAt;
    private String sender;
    private int userID;
    private int msgType;

    public UserMessage(int msgType, String msgContent)
    {
        this.msgType = msgType;
        this.msgContent = msgContent;
    }

    public String getMessage()
    {
        return msgContent;
    }
    public long getCreatedAt()
    {
        return createdAt;
    }
    public String getSender()
    {
        return sender;
    }
    public int getDeviceToken()
    {
        return userID;
    }
    public int getMessageType()
    {
        return msgType;
    }
}