package com.xwl.activemq.topic;

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
public class JmsConsumer_topic {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "tcp://192.168.92.129:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("3号消费者");

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
        // 使用子接口Topic
        Topic topic = session.createTopic(TOPIC_NAME);

        // 5、创建消费者
        MessageConsumer messageConsumer = session.createConsumer(topic);

        /**
         * 方式二：通过监听的方式来消费消息
         * 异步非阻塞方式（监听器onMessage()）
         * 订阅者或接受者通过MessageConsumer的setMessageListener(MessageListener listener)注册一个消息监听器
         * 当消息到达之后，系统自动调用监听器MessageListener的onMessage(Message message)方法
         */
        messageConsumer.setMessageListener(message -> {
            if (message != null && message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println("*****消费者接收到topic消息：" + textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        // 作用：保证控制台不退出，即保证消息被消费完了才关闭连接
        // 因为连接到虚拟机需要一定的时间
        System.in.read(); // （press any key to exit）
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
