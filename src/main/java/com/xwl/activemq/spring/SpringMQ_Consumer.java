package com.xwl.activemq.spring;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xwl
 * @date 2019-08-19 16:13
 * @description
 */
@Service
public class SpringMQ_Consumer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        // spring的构造注入，类名首字母小写
        SpringMQ_Consumer consumer = (SpringMQ_Consumer) ctx.getBean("springMQ_Consumer");
        String retValue = (String) consumer.jmsTemplate.receiveAndConvert();
        System.out.println("*********消费者收到的消息：" + retValue);
    }


}
