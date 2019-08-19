package com.xwl.activemq.spring;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author xwl
 * @date 2019-08-19 16:03
 * @description
 */
@Service
public class SpringMQ_Produce {
    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        // spring的构造注入，类名首字母小写
        SpringMQ_Produce produce = (SpringMQ_Produce) ctx.getBean("springMQ_Produce");

       /* produce.jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("*****spring和activemq的整合case*****");
                return textMessage;
            }
        });*/
        // 使用lambda表达式
        produce.jmsTemplate.send(session -> {
            TextMessage textMessage = session.createTextMessage("*****spring和activemq的整合case1*****");
            return textMessage;
        });
        System.out.println("********send task over*******");
    }
}
