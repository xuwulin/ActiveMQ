package com.xwl.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;

import javax.jms.*;
import java.util.UUID;

/**
 * @author xwl
 * @date 2019-08-17 17:12
 * @description 异步投递AsyncSends
 * 异步发送如何确认发送成功？
 * 正确的异步发送方法是需要回调的！！！
 */
public class JmsProduce_AsyncSend {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "nio://192.168.92.129:61608";
    public static final String QUEUE_NAME = "nioauto";

    public static void main(String[] args) throws JMSException {
        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 开启异步投递AsyncSends，可能会有商量消息丢失
        activeMQConnectionFactory.setUseAsyncSend(true);
        // 2、通过连接工厂，获得连接connection并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 3、创建连接会话session
        // connection.createSession(boolean var1, int var2)有两个参数:第一个叫事务，第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4、创建目的地（具体是队列还是主题topic）
        // Destination是一个父接口，他有两个子接口：Queue和Topic
//        Destination destination = session.createQueue(QUEUE_NAME); // 相当于：Collection collection = new ArrayList();
        // 使用子接口Queue
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5、创建消息的生产者
        ActiveMQMessageProducer activeMQMessageProducer = (ActiveMQMessageProducer) session.createProducer(queue);
        // 6、通过使用messageProducer生产3条消息发送到MQ的队列里面
        TextMessage textMessage = null;
        for (int i = 1; i <= 4 ; i++) {
            // 7、创建消息
            // TextMessage
            textMessage = session.createTextMessage("msg---" + i); // 理解为一个字符串
            textMessage.setJMSMessageID(UUID.randomUUID().toString() + "_byXWL");
            String msgID = textMessage.getJMSMessageID();
//            textMessage.setStringProperty("c01", "vip");
            // 8、通过messageProducer发送给mq，并且回调
            activeMQMessageProducer.send(textMessage, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    System.out.println(msgID + "has been send ok");
                }

                @Override
                public void onException(JMSException e) {
                    System.out.println(msgID + "fail to send");
                }
            });

            // MapMessage
            /*MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("k1", "mapMessage---v1");
            messageProducer.send(mapMessage);*/
        }
        // 9、关闭资源
        activeMQMessageProducer.close();
        session.close();
        connection.close();

        System.out.println("*****消息发布到MQ成功*****");
    }
}
