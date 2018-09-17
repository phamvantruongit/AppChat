package vn.com.phamvantruongit.appchat.model;

public class Message {
    private int usersId;
    private String message;
    private String sentAt;
    private String name;

    public Message(int usersId, String message, String sentAt, String name) {
        this.usersId = usersId;
        this.message = message;
        this.sentAt = sentAt;
        this.name = name;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
