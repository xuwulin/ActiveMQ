package com.xwl.activemq.topic.persist;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author xwl
 * @date 2019-08-17 17:12
 * @description 生产者
 * 一定要先运行一次消费者，等于向MQ注册，类似于我订阅了这个主题
 * 然后再运行生产者发送消息，此时，无论消费者是否在线，都会接收到，不在线的话，下次连接的时候，会把没有收到的消息都接收下来
 */
public class JmsProduce_topic_persist {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "tcp://192.168.92.129:61616";
    public static final String TOPIC_NAME = "topic-persist";

    public static void main(String[] args) throws JMSException {
        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2、通过连接工厂，获得连接connection并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        // 3、创建连接会话session
        // connection.createSession(boolean var1, int var2)有两个参数:第一个叫事务，第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4、创建目的地（具体是队列还是主题topic）
        // Destination是一个父接口，他有两个子接口：Queue和Topic
//        Destination destination = session.createQueue(QUEUE_NAME); // 相当于：Collection collection = new ArrayList();
        // 使用子接口Topic
        Topic topic = session.createTopic(TOPIC_NAME);

        // 5、创建消息的生产者
        MessageProducer messageProducer = session.createProducer(topic);
        // 持久化模式
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        connection.start();
        // 6、通过使用messageProducer生产3条消息发送到MQ的队列里面
        for (int i = 1; i <= 4 ; i++) {
            // 7、创建消息
            TextMessage textMessage = session.createTextMessage("***收到持久化topic：msg---" + i); // 理解为一个字符串
            // 8、通过messageProducer发送给mq
            messageProducer.send(textMessage);
        }
        // 9、关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("*****TOPIC_NAME发布到MQ成功*****");
    }
}
