package com.xwl.activemq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;

/**
 * @author xwl
 * @date 2019-08-17 17:12
 * @description 延时和定时投递
 */
public class JmsProduce_DelayAndSchedule {
    // 192.168.92.129为安装有activemq的虚拟机的地址
    public static final String ACTIVEMQ_URL = "nio://192.168.92.129:61608";
    public static final String QUEUE_NAME = "queue-Delay";

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

        // 延时3秒
        long delay = 3 * 1000;
        // 间隔时间4秒
        long period = 4 * 1000;
        // 重复5次
        int repeat = 5;

        // 6、通过使用messageProducer生产3条消息发送到MQ的队列里面
        for (int i = 1; i <= 4 ; i++) {
            // 7、创建消息
            // TextMessage
            TextMessage textMessage = session.createTextMessage("delay msg---" + i);// 理解为一个字符串

            // 设置延时属性
            textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
            textMessage.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);

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
