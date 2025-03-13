class printDoc {
    private String str;
    private int senderID;

    void setStr(String s, int id) {
        str = s;
        senderID = id;
    }

    String getStr() {
        return str;
    }

    int getSender() {
        return senderID;
    }

    printDoc(String s, int id) {
        str = s;
        senderID = id;
    }
}