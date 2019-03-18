package com.threathunter.labrador.stream.query;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class UserArchivesQuery extends AbstractTest {
    public static void main(String[] args) throws LabradorException {
        QueryService queryService = new QueryService();
        String pk = "456";
        List<String> variables = Arrays.asList("uid__registration__account__mail__profile", "uid__registration__account__mobile__profile",
                "uid__registration__account__username__profile", "uid__account_register_timestamp__profile",
                "uid__account_token_change_mobile__profile",
                "uid__account_token_change_mail__profile", "uid__account_token_change_mail_timestamp__profile",
                "uid__account_token_change_mobile_timestamp__profile", "uid__account_token_change_mail_count__profile",
                "uid__account_token_change_mobile_count__profile", "uid__account_login_timestamp_last10__profile",
                "uid__account_login_ip_last10__profile", "uid__account_login_geocity_last10__profile",
                "uid__alarm_count__profile", "uid_timestamp__visit_dynamic_count__profile", "uid_geo_city__visit_dynamic_count__profile",
                "uid_useragent__visit_dynamic_count__profile", "uid_did__visit_dynamic_count__profile",
                "uid__visit_dynamic_last_timestamp__profile", "uid__registration__account__ip__profile", "uid__account_login_ip_last__profile");
        Map<String, Object> results = queryService.query(pk, variables);
        System.out.println(results);
    }
}
