package com.xwl.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author xwl
 * @date 2019-08-17 17:12
 * @description 生产者 集群测试
 */
public class JmsProduce_queue_cluster {
    // 安装有activemq集群的虚拟机的地址
    public static final String ACTIVEMQ_URL = "failover:(tcp://192.168.92.102:61616,tcp://192.168.92.103:61616,tcp://192.168.92.104:61616)?randomize=false";
    public static final String QUEUE_NAME = "queue-cluster";

    public static void main(String[] args) throws JMSException {
        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
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
        MessageProducer messageProducer = session.createProducer(queue);
        // 6、通过使用messageProducer生产3条消息发送到MQ的队列里面
        for (int i = 1; i <= 4 ; i++) {
            // 7、创建消息
            // TextMessage
            TextMessage textMessage = session.createTextMessage("queue-cluster---" + i);// 理解为一个字符串
//            textMessage.setStringProperty("c01", "vip");
            // 8、通过messageProducer发送给mq
            messageProducer.send(textMessage);

            // MapMessage
            /*MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("k1", "mapMessage---v1");
            messageProducer.send(mapMessage);*/
        }
        // 9、关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("*****消息发布到MQ成功*****");
    }
}
