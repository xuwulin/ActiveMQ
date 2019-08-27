package com.xwl.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @author xwl
 * @date 2019-08-18 21:05
 * @description 消费者 集群测试
 *
 */
public class JmsConsumer_queue_cluster {
    // 安装有activemq集群的虚拟机的地址
    public static final String ACTIVEMQ_URL = "failover:(tcp://192.168.92.102:61616,tcp://192.168.92.103:61616,tcp://192.168.92.104:61616)?randomize=false";
    public static final String QUEUE_NAME = "queue-cluster";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("3号消费者");

        // 1、创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2、通过连接工厂，获得连接connection并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 3、创建连接会话session
        // connection.createSession(boolean var1, int var2)有两个参数:第一个叫事务，第二个叫签收
        // 如果开启事务，则一定要提交事务，即要在末尾写上：session.commit();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4、创建目的地（具体是队列还是主题topic）
        // Destination是一个父接口，他有两个子接口：Queue和Topic
//        Destination destination = session.createQueue(QUEUE_NAME); // 相当于：Collection collection = new ArrayList();
        // 使用子接口Queue
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5、创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        /**
         * 方式一：同步阻塞方式（receive()）
         * 订阅者或者接受者调用MessageConsumer的receive()方法来接收消息，receive方法能够在接收到消息之前（或超时之前）将一直阻塞
         */
        /*while (true) {
            // 生产者生产什么消息，消费者就要消费什么消息
            // 需要强制类型转换
//            TextMessage textMessage = (TextMessage) messageConsumer.receive(); // 会一直等待
            TextMessage textMessage = (TextMessage) messageConsumer.receive(4000L); // 等待指定毫秒数
            if (textMessage != null) {
                System.out.println("*****消费者接收到消息：" + textMessage.getText());
            } else {
                break;
            }
        }
        messageConsumer.close();
        session.close();
        connection.close();*/

        /**
         * 方式二：通过监听的方式来消费消息
         * 异步非阻塞方式（监听器onMessage()）
         * 订阅者或接受者通过MessageConsumer的setMessageListener(MessageListener listener)注册一个消息监听器
         * 当消息到达之后，系统自动调用监听器MessageListener的onMessage(Message message)方法
         */
        messageConsumer.setMessageListener(message -> {
            // TextMessage
            if (message != null && message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println("*****消费者接收到消息：" + textMessage.getText());
//                    System.out.println("*****消费者接收到消息属性：" + textMessage.getStringProperty("c01"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            // MapMessage
            /*if (message != null && message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                try {
                    System.out.println("*****消费者接收到消息：" + mapMessage.getString("k1"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }*/
        });
        // 作用：保证控制台不退出，即保证消息被消费完了才关闭连接
        // 因为连接到虚拟机需要一定的时间
        System.in.read(); // （press any key to exit）
//        session.commit(); // 如果开启了事务，则一定要提交，否则会导致消息重发，默认重发6次（共发送7次），然后会被分配到死信队列中
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
