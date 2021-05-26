package com.cherry.commons.tmplates;

import com.cherry.commons.properties.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

;

public class SmsTemplate {

    private SmsProperties smsProperties;

    public SmsTemplate(SmsProperties smsProperties){
        this.smsProperties=smsProperties;
    }


    public void sendSms(String phoneNumbers,String code) {
        try{

            Credential cred = new Credential(smsProperties.getSecret(), smsProperties.getAccessKey());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(smsProperties.getEndpoint());

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SmsClient client = new SmsClient(cred, smsProperties.getRegion(), clientProfile);

            SendSmsRequest req = new SendSmsRequest();

            String[] phoneNumberSet1 = {smsProperties.getCountryCode()+phoneNumbers};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId(smsProperties.getSdkAppId());
            req.setSignName(smsProperties.getSignName());
            req.setTemplateId(smsProperties.getTemplateCode());

            String[] templateParamSet1 = {code};
            req.setTemplateParamSet(templateParamSet1);


            SendSmsResponse resp = client.SendSms(req);

            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }

    }

}
