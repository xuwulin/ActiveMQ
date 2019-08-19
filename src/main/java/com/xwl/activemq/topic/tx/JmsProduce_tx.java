package com.xwl.activemq.topic.tx;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author xwl
 * @date 2019-08-17 17:12
 * @description 生产者
 */
public class JmsProduce_tx {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "tcp://192.168.92.129:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException {
        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2、通过连接工厂，获得连接connection并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 3、创建连接会话session
        // connection.createSession(boolean var1, int var2)有两个参数:第一个叫事务，第二个叫签收
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
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
            TextMessage textMessage = session.createTextMessage("tx msg---" + i);// 理解为一个字符串
            // 8、通过messageProducer发送给mq
            messageProducer.send(textMessage);
        }
        // 9、关闭资源
        messageProducer.close();
        session.commit(); // 提交事务
        session.close();
        connection.close();

        System.out.println("*****消息发布到MQ成功*****");
    }
}
