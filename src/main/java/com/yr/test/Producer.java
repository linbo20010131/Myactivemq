package com.yr.test;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 生产者，消息发送者
 *
 * @author linbo
 */
public class Producer {
    /**
     * 用户名
     */
    private static final String USERNAME = "";
    /**
     * 密码
     */
    private static final String PASSWORD = "";
    /**
     * 地址端口
     */
    private static final String BROKEN_URL = "tcp://192.168.1.234:61616";

    /**
     * 连接工厂
     */
    private ConnectionFactory connectionFactory;
    /**
     * 一个连接
     */
    private Connection connection;

    /**
     * 一个会话
     */
    private Session session;

    /**
     * 消息队列，点对点
     */
    private Queue queue;

    /**
     * 生产者
     */
    private MessageProducer producer;

    public static void main(String[] args) throws JMSException {
        String producerName = "ppjiang";
        Producer producer = new Producer();
        producer.producerStart(producerName);
        producer.sendTextMessage();
    }

    /**
     * 开启一个生产者
     *
     * @param queueName
     * @throws JMSException
     */
    public void producerStart(String queueName) throws JMSException {
        // 根据用户名，密码，url创建一个连接工厂
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEN_URL);
        // 从工厂中获取一个连接
        connection = connectionFactory.createConnection();

        connection.start();
        // 创建一个session
        // 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
        // 第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
        // Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
        // Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
        // DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
        session = connection.createSession(true, Session.SESSION_TRANSACTED);

        queue = session.createQueue(queueName);

        producer = session.createProducer(queue);

    }

    /**
     * 发送文本消息
     */
    public void sendTextMessage() throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        for (int i = 0; i < 10000; i++) {
            textMessage.setText("我是平萝莉" + i + "号");
            producer.send(textMessage);
        }
        session.commit();
    }


}
