package com.first_java_app.smarthrt;

public class UserHelperClassGadget {
    String BtnID, btnValue, btnName;

    public UserHelperClassGadget() { }
    public UserHelperClassGadget(String BtnID, String btnName, String btnValue){
        this.BtnID=BtnID;
        this.btnValue=btnValue;
        this.btnName=btnName;

    }
    public String getBtnID() {
        return BtnID;
    }

    public void setBtnID(String BtnID) {
        this.BtnID = BtnID;
    }
    public String getbtnValue() {
        return btnValue;
    }

    public void setbtnValue(String btnValue) {
        this.btnValue = btnValue;
    }
    public String getbtnName() {
        return btnName;
    }

    public void setbtnName(String btnName) {
        this.btnName = btnName;
    }
}
