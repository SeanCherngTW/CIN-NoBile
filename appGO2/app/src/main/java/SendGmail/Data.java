package SendGmail;

//資料庫裡的每個資料；getAllSpots():抓出所有資料
public class Data {
    private int id;
    //private String account;
    //private String password;
    private String receiver1;
    private String receiver2;
    private String receiver3;

    //public Data(String account, String password, String receiver1, String receiver2, String receiver3) {
    //    this(0, account, password, receiver1, receiver2, receiver3);
    //}

    public Data(String receiver1, String receiver2, String receiver3) {
        this(0, receiver1, receiver2, receiver3);
    }

    public Data(int id, String receiver1, String receiver2, String receiver3) {
        this.id = id;
        //this.account = account;
        //this.password = password;
        this.receiver1 = receiver1;
        this.receiver2 = receiver2;
        this.receiver3 = receiver3;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    //public void setAccount(String account) {
    //    this.account = account;
    //}

    //public String getAccount() {
    //    return account;
    //}

    //public void setPassword(String password) {
    //    this.password = password;
    //}

    //public String getPassword() {
    //    return password;
    //}

    public void setReceiver1(String receiver1) {
        this.receiver1 = receiver1;
    }

    public String getReceiver1() {
        return receiver1;
    }

    public void setReceiver2(String receiver2) {
        this.receiver2 = receiver2;
    }

    public String getReceiver2() {
        return receiver2;
    }

    public void setReceiver3(String receiver3) {
        this.receiver3 = receiver3;
    }

    public String getReceiver3() {
        return receiver3;
    }
}

