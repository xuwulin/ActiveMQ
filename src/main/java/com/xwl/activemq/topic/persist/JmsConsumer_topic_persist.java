package com.xwl.activemq.topic.persist;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author xwl
 * @date 2019-08-18 21:05
 * @description 消费者
 * topic消息，一定要先启动消费再启动生产者，如果先启动生产者后启动消费者，则消息会成为废消息
 *
 */
public class JmsConsumer_topic_persist {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "tcp://192.168.92.129:61616";
    public static final String TOPIC_NAME = "topic-persist";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("*****z3");

        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2、通过连接工厂，获得连接connection并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.setClientID("z3");

        // 3、创建连接会话session
        // connection.createSession(boolean var1, int var2)有两个参数:第一个叫事务，第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4、创建目的地（具体是队列还是主题topic）
        // Destination是一个父接口，他有两个子接口：Queue和Topic
//        Destination destination = session.createQueue(QUEUE_NAME); // 相当于：Collection collection = new ArrayList();
        // 使用子接口Topic
        Topic topic = session.createTopic(TOPIC_NAME);
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "remark...");
        connection.start();

        Message message = topicSubscriber.receive();
        while (message != null) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("收到的持久化消息topic：" + textMessage.getText());
            message = topicSubscriber.receive(1000L);
        }


        session.close();
        connection.close();
    }
}
