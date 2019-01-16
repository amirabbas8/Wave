package net.saoshyant.wave.app.data;

public class FeedItem_my_wave {

    private int id;
    private String userid, name, profilePic,voice,timeStamp,nlike,mylike,  lprog, oprog, pprog,text;


    public FeedItem_my_wave() {
    }


    public FeedItem_my_wave(int id, String name, String userid, String nlike,
                            String profilePic,String voice,String timeStamp,
                            String mylike,  String lprog, String oprog,String pprog,String text) {
        super();
        this.id = id;
        this.name = name;
        this.userid = userid;
        this.profilePic = profilePic;
        this.voice = voice;
        this.timeStamp = timeStamp;
        this.nlike = nlike;
        this.mylike = mylike;
        this.lprog = lprog;
        this.oprog = oprog;
        this.text = text;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getUserId() {
        return userid;
    }


    public void setUserId(String userid) {
        this.userid = userid;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }






    public String getProfilePic() {
        return profilePic;
    }


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public String getVoice() {
        return voice;
    }


    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }



    public String getNLike() {
        return nlike;
    }


    public void setNLike(String nlike) {
        this.nlike = nlike;
    }



    public String getMylike() {
        return mylike;
    }


    public void setMylike(String mylike) {
        this.mylike = mylike;
    }



    public String getlprog() {
        return lprog;
    }


    public void setlprog(String lprog) {
        this.lprog = lprog;
    }


    public String getoprog() {
        return oprog;
    }


    public void setoprog(String oprog) {
        this.oprog = oprog;
    }


    public String getPprog() {
        return pprog;
    }

    public void setPprog(String pprog) {
        this.pprog = pprog;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
