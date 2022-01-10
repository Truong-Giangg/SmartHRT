package com.first_java_app.smarthrt;

public class UserHelperClassGadget {
    String BtnID, btnValue, btnName,widType, gestureT, espPin;

    public UserHelperClassGadget() { }
    public UserHelperClassGadget(String BtnID, String btnName, String btnValue, String widType,String gestureT, String espPin){
        this.BtnID=BtnID;
        this.btnValue=btnValue;
        this.btnName=btnName;
        this.widType=widType;
        this.gestureT=gestureT;
        this.espPin=espPin;

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
    public String getWidType() {
        return widType;
    }
    public void setWidType(String widType) {
        this.widType = widType;
    }

    public String getGestureT() {
        return gestureT;
    }
    public void setGestureT(String gestureT) {
        this.gestureT = gestureT;
    }

    public String getEspPin() {
        return espPin;
    }
    public void setEspPin(String espPin) {
        this.espPin = espPin;
    }
}
