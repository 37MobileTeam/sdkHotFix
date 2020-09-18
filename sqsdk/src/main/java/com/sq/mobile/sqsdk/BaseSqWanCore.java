package com.sq.mobile.sqsdk;

import com.sq.mobile.commonpluhostandsqsdk.SQSdkInterface;
import com.sq.mobile.features1.pay.PayManager;
import com.sq.mobile.features2.login.LoginMaganer;


public class BaseSqWanCore implements SQSdkInterface {

    @Override
    public void init() {

    }

    @Override
    public void login() {
        new LoginMaganer().login();
    }

    @Override
    public void pay() {
        new PayManager().pay();
    }

}
