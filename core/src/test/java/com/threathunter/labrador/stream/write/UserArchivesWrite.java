package com.threathunter.labrador.stream.write;

import com.threathunter.labrador.AbstractTest;
import com.threathunter.labrador.core.exception.DataTypeNotMatchException;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.service.StreamSendService;
import com.threathunter.labrador.core.transform.SpecialVariableExtract;
import com.threathunter.model.Event;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.aerospike.client.util.Util.sleep;

/**
 * Created by wanbaowang on 17/11/16.
 * 用户档案
 */
public class UserArchivesWrite extends AbstractTest {

    //用户登录成功次数[urlVisit]
    @Test
    public void uid__account_login_count_succ__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        ImmutableMap kv = ImmutableMap.of("uid", "456", "result", "T");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_count_succ__profile"));
        sendService.process(event);
    }

    //账号登陆成功did按小时去重列表
    @Test
    public void uid__account_login_distinct_did__1h_profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        ImmutableMap kv = ImmutableMap.of("uid", "456", "did", "did146", "result", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_distinct_did__1h_profile"));
        sendService.process(event);
    }

    //账号最近10条登录城市
    @Test
    public void uid__account_login_geocity_last10__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("c_ip", "122.144.218.13");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_geocity_last10__profile"));
        sendService.process(event);
    }


    //用户最近10次登陆ip
    @Test
    public void uid__account_login_ip_last10__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("c_ip", "2.3.4.5");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_ip_last10__profile"));
        sendService.process(event);
    }

    //用户最近10次登陆时间
    @Test
    public void uid__account_login_timestamp_last10__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_timestamp_last10__profile"));
        sendService.process(event);
    }

    //用户修改手机号
    @Test
    public void uid__account_token_change_mobile__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "mobile");
        kv.put("new_token", "13049502039");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mobile__profile"));
        sendService.process(event);
    }

    //用户修改邮箱次数
    @Test
    public void uid__account_token_change_mail_count__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "email");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mail_count__profile"));
        sendService.process(event);
    }


    @Test
    public void uid__account_token_change_mail_last_timestamp__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(12324677);
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "email");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mail_last_timestamp__profile"));
        sendService.process(event);
    }

    //用户修改手机次数
    @Test
    public void uid__account_token_change_mobile_count__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "mobile");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mobile_count__profile"));
        sendService.process(event);
    }

    @Test
    public void uid__transaction_withdraw_sum_withdraw_amount__1h__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("TRANSACTION_WITHDRAW");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("did", "did685");
        kv.put("withdraw_amount", 89L);
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__transaction_withdraw_sum_withdraw_amount__1h__profile"));
        sendService.process(event);
    }


    @Test
    public void uid_did__account_login_count_succ__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("did", "did4564558");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid_did__account_login_count_succ__profile"));
        sendService.process(event);
    }

    @Test
    public void uid__account_register_timestamp__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_register_timestamp__profile"));
        sendService.process(event);
    }


    @Test
    public void uid__registration__account__mail__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("register_verification_token_type", "email");
        kv.put("register_verification_token", "wanbao.wang@threathunter.cn");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__registration__account__mail__profile"));
        sendService.process(event);
    }

    @Test
    public void uid__registration__account__mobile__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("register_verification_token_type", "mobile");
        kv.put("register_verification_token", "18521316077");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__registration__account__mobile__profile"));
        sendService.process(event);
    }


    @Test
    public void uid__account_token_change_mobile_timestamp__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("token_type", "mobile");
        kv.put("new_token", "18521316077");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mobile_timestamp__profile"));
        sendService.process(event);
    }

    //用户修改邮箱
    @Test
    public void uid__account_token_change_mail__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "email");
        kv.put("new_token", "51198641@qq.com");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mail__profile"));
        sendService.process(event);
    }

    //用户注册时的用户名
    @Test
    public void uid__registration__account__username__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("user_name", "wangwanbao");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__registration__account__username__profile"));
        sendService.process(event);
    }

    //ip每小时登陆的uid
    @Test
    public void ip__visit_distinct_uid__1h__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("did", "did4564558");
        kv.put("result", "T");
        String ip = randomIp();
        kv.put("c_ip", ip);
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("ip__visit_distinct_uid__1h__profile"));
        sendService.process(event);
    }

    //用户最后登陆成功的ip
    @Test
    public void uid__account_login_ip_last__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "456", "did", "did4564558", "result", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_ip_last__profile"));
        sendService.process(event);
    }

    //用户最后登陆成功的时间
    @Test
    public void uid__account_login_succ_timestamp__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_LOGIN");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "456", "did", "did4564558", "result", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_login_succ_timestamp__profile"));
        sendService.process(event);
    }



    //帐号修改的邮箱的时间
    @Test
    public void uid__account_token_change_mail_timestamp__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = ImmutableMap.of("uid", "456", "token_type", "email", "result", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mail_timestamp__profile"));
        sendService.process(event);
    }


    //用户注册时的ip
    @Test
    public void uid__registration__account__ip__profile() throws DataTypeNotMatchException, LabradorException {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "456", "reuslt", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__registration__account__ip__profile"));
        sendService.process(event);
    }

    @Test
    public void uid__order_submit_count__hourly_profile() throws Exception {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ORDER_SUBMIT");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "567", "reuslt", "T", "c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__order_submit_count__hourly_profile"));
        sendService.process(event);
    }

    @Test
    public void uid__order_submit_distinct_merchant__1h_profile() throws Exception {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ORDER_SUBMIT");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "567", "reuslt", "T", "c_ip", randomIp(), "result", "F", "merchant", "merchant_2");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__order_submit_count__hourly_profile"));
        sendService.process(event);
    }

    @Test
    public void uid__order_cancel_count__hourly__profile() throws Exception {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ORDER_CANCEL");
        event.setTimestamp(System.currentTimeMillis());
        Map<String,Object> kv = ImmutableMap.of("uid", "uid1802051003", "reuslt", "T", "c_ip", randomIp(), "result", "F");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__order_cancel_count__hourly__profile"));
        sendService.process(event);

        sleep(1000000);
    }

    @Test
    public void testOnlineException() {
        Event event = new Event();
        event.setApp("nebula");
        event.setName("ACCOUNT_LOGIN");
    }

       /*

    //用户修改邮箱时间
    @Test
    public void uid__account_token_change_mail_timestamp__profile() {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_TOKEN_CHANGE");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("result", "T");
        kv.put("token_type", "email");
        kv.put("new_token", "51198641@qq.com");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__account_token_change_mail_timestamp__profile"));
        sendService.process(event);
    }


    //用户修改手机时间




    //用户注册时的ip
    @Test
    public void uid__registration__account__ip__profile() {
        Event event = new Event();
        event.setId(randomId());
        event.setName("ACCOUNT_REGISTRATION");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        kv.put("user_name", "wangwanbao");
        kv.put("result", "T");
        kv.put("c_ip", randomIp());
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__registration__account__ip__profile"));
        sendService.process(event);
    }



        @Test
    public void uid__transaction_deposit_count__1d__profile() {
        Event event = new Event();
        event.setId(randomId());
        event.setName("TRANSACTION_DEPOSIT");
        event.setTimestamp(System.currentTimeMillis());
        Map<String, Object> kv = new HashMap<>();
        kv.put("uid", "456");
        event.setPropertyValues(kv);
        StreamSendService sendService = new StreamSendService(new SpecialVariableExtract("uid__transaction_deposit_count__1d__profile"));
        sendService.process(event);
    }
     //注册时间

    */

    @After
    public void tearDown() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
