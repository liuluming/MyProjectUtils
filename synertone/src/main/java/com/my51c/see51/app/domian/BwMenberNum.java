package com.my51c.see51.app.domian;

public class BwMenberNum {

    public int bwNumber;
    public String name;
    public String intfer;
    public String metric;
    public String weight;
    private boolean MemberTag;

    public boolean isMemberTag() {
        return MemberTag;
    }

    public void setMemberTag(boolean memberTag) {
        MemberTag = memberTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBwNumber() {
        return bwNumber;
    }

    public void setBwNumber(int bwNumber) {
        this.bwNumber = bwNumber;
    }

    public String getIntfer() {
        return intfer;
    }

    public void setIntfer(String intfer) {
        this.intfer = intfer;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


}
