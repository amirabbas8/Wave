package net.saoshyant.wave.app.data;

public class FeedItem_geyhan {


    private String userid, name, profilePic,nlike,mylike,lprog, oprog, pprog;


    public FeedItem_geyhan() {
    }


    public FeedItem_geyhan( String name, String userid, String nlike,
                           String profilePic,
                           String mylike, String lprog, String oprog, String pprog) {
        super();
        this.name = name;
        this.userid = userid;
        this.profilePic = profilePic;
        this.nlike = nlike;
        this.mylike = mylike;
        this.lprog = lprog;
        this.oprog = oprog;
        this.pprog = pprog;
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
}
