package com.threathunter.labrador.stream.query;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.QueryService;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/11/23.
 */
public class DerivativeQuery extends AbstractTest {
    QueryService queryService = new QueryService();

    public static void main(String[] args) throws LabradorException {
        QueryService queryService = new QueryService();
        String pk = "did123";
//        List<String> variables = Arrays.asList("uid__account_login_distinct_did__1h_profile");
//        List<String> variables = Arrays.asList("uid__account_login_distinct_count_did_succ__1d__profile");
        List<String> variables = Arrays.asList("did__visit_distinct_ip__1d__profile");
        Map<String, Object> results = queryService.query(pk, variables);
        System.out.println(results);
    }

    @Test
    public void uid__order_submit_count__1h__profile() throws LabradorException {
        String pk = "567";
        List<String> variables = Arrays.asList("uid__order_submit_count__hourly_profile");
        Map<String,Object> results = queryService.query(pk ,variables);
        System.out.println(results);
    }

    @Test
    public void uid__order_submit_distinct_product_location__1h_profile() throws LabradorException {
        String pk = "uid1802051001";
        List<String> variables = Arrays.asList("uid__order_cancel_count__1h__profile");
        Map<String,Object> results = queryService.query(pk ,variables);
        System.out.println(results);
    }
}
