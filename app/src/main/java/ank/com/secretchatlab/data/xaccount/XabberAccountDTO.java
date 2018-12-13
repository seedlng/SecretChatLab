package ank.com.secretchatlab.data.xaccount;

import java.util.List;

/**
 * Created by valery.miller on 17.07.17.
 */

public class XabberAccountDTO {

    private int id;
    private String account_status;
    private String username;
    private String first_name;
    private String last_name;
    private String registration_date;
    private String language;
    private String phone;
    private boolean need_to_verify_phone;
    private List<XMPPUserDTO> xmpp_users;
    private List<EmailDTO> email_list;
    private List<SocialBindingDTO> social_bindings;

    public XabberAccountDTO(int id, String account_status, String username, String first_name,
                            String last_name, String registration_date, String language, String phone,
                            boolean need_to_verify_phone, List<XMPPUserDTO> xmpp_users,
                            List<EmailDTO> email_list, List<SocialBindingDTO> social_bindings) {
        this.id = id;
        this.account_status = account_status;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.registration_date = registration_date;
        this.language = language;
        this.phone = phone;
        this.need_to_verify_phone = need_to_verify_phone;
        this.xmpp_users = xmpp_users;
        this.email_list = email_list;
        this.social_bindings = social_bindings;
    }

    public String getAccountStatus() {
        return account_status;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getRegistrationDate() {
        return registration_date;
    }

    public String getLanguage() {
        return language;
    }

    public List<XMPPUserDTO> getXmppUsers() {
        return xmpp_users;
    }

    public List<EmailDTO> getEmails() {
        return email_list;
    }

    public List<SocialBindingDTO> getSocialBindings() {
        return social_bindings;
    }

    public boolean isNeedToVerifyPhone() {
        return need_to_verify_phone;
    }

    public String getPhone() {
        return phone;
    }
}

