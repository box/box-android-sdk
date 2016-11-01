package com.box.androidsdk.content.models;

import com.eclipsesource.json.JsonObject;


/**
 * An object containing Mobile Device Management data that may be required to login with certain enterprise and api key combinations.
 */
public class BoxMDMData extends BoxJsonObject {

    public static final String BOX_MDM_DATA = "box_mdm_data";
    public static final String BUNDLE_ID = "bundle_id";
    public static final String MANAGEMENT_ID = "management_id";
    public static final String PUBLIC_ID = "public_id";
    public static final String BILLING_ID = "billing_id";
    public static final String EMAIL_ID = "email_id";


    public BoxMDMData() {
        super();
    }

    public BoxMDMData(JsonObject jsonObject) {
        super(jsonObject);
    }

    public void setValue(final String key, final String value){
        set(key, value);
    }

    public void setBundleId(final String bundleId){
        setValue(BUNDLE_ID, bundleId);
    }

    public void setPublicId(final String publicId){
        setValue(PUBLIC_ID, publicId);
    }

    public void setManagementId(final String managementId){
        setValue(MANAGEMENT_ID, managementId);
    }

    public void setEmailId(final String emailId){
        setValue(EMAIL_ID, emailId);
    }

    public void setBillingId(final String billingId){
        setValue(BILLING_ID, billingId);
    }

    public String getBundleId(){
        return getPropertyAsString(PUBLIC_ID);
    }

    public String getPublicId(){
        return getPropertyAsString(PUBLIC_ID);
    }

    public String getManagementId(){
        return getPropertyAsString(MANAGEMENT_ID);
    }

    public String getEmailId(){
        return getPropertyAsString(EMAIL_ID);
    }

    public String getBillingIdId(){
        return getPropertyAsString(BILLING_ID);
    }


}
