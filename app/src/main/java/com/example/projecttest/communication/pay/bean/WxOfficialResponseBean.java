package com.example.projecttest.communication.pay.bean;

import java.io.Serializable;

/**
 * @author Dengzh
 * @date 2020/6/5
 * Description:XW官方支付
 */
public class WxOfficialResponseBean implements Serializable {


    /**
     * sign_type : MD5
     * nonce_str : Hb0okRffGwYh88lV
     * prepay_id : prepay_id=wx050955041589111278337f3e1498684400
     * timestamp : 1591322104
     * sign : 804D5E3E4A695D11EF6D38A6A773EAF3
     * nick : D先生
     * erban_no : 8888888
     * "mchid":1586640511
     */

    /**
     * {"sign_type":"MD5","nonce_str":"kvnPLYenJBUwaTDz","prepay_id":"prepay_id=wx05101007160563f1f49c75cf1255590100","timestamp":"1591323007","sign":"6B1167E40092DAE53070B0EB3D1786AF","nick":"D先生","erban_no":8888888}
     * */

    private String sign_type;
    private String nonce_str;
    private String prepay_id;
    private String timestamp;
    private String sign;
    private String nick;
    private int erban_no;
    private String mchid;

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getErban_no() {
        return erban_no;
    }

    public void setErban_no(int erban_no) {
        this.erban_no = erban_no;
    }
}
