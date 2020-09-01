package com.yr.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 消费者,消息接收端
 *
 * @author linbo
 */
public class Receive {
    // 连接账号
    private String userName = "";
    // 连接密码
    private String password = "";
    // 连接地址
    //private String brokerURL = "failover:(tcp://192.168.1.236:51511,tcp://192.168.1.237:51511,tcp://192.168.1.238:51511,tcp://192.168.1.141:51511,tcp://192.168.1.142:51511,tcp://192.168.1.143:51511)";
    //private String brokerURL = "failover:(tcp://192.168.1.8:61617,tcp://192.168.1.9:61617,tcp://192.168.1.10:61617)";
//	private String brokerURL = "tcp://192.168.1.107:61616";
    //private String brokerURL = "failover:(tcp://192.168.1.118:61617,tcp://192.168.1.118:61618,tcp://192.168.1.118:61619,tcp://192.168.1.119:61617,tcp://192.168.1.119:61618,tcp://192.168.1.119:61619)";
    private String brokerURL = "tcp://192.168.1.234:61616";
    // connection的工厂
    private ConnectionFactory factory;

    private ActiveMQConnectionFactory connectionFactory;
    // 连接对象
    private Connection connection;
    // 一个操作会话
    private Session session;
    // 目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
    private Destination destination;
    // 消费者，就是接收数据的对象
    private MessageConsumer consumer;

    public static void main(String[] args) throws JMSException {
        Receive receive = new Receive();
        receive.start();
        //接收文本消息
       // receive.getTextMessage();
        //接收序列化对象
       // receive.getObjectMessage();
        //接收map
       // receive.getMapMessage();
        //接收byte
        receive.getByteArray();
    }

    public void start() {
        try {
            // 根据用户名，密码，url创建一个连接工厂
            factory = new ActiveMQConnectionFactory(userName, password, brokerURL);

            ((ActiveMQConnectionFactory) factory).setTrustAllPackages(true);
            // 从工厂中获取一个连接
            connection = factory.createConnection();
            // 测试过这个步骤不写也是可以的，但是网上的各个文档都写了
            connection.start();
            // 创建一个session
            // 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
            // 第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
            // Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
            // Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
            // DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // //点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
            Queue qu = session.createQueue("ppjiang");
            // 根据session，创建一个接收者对象
            consumer = session.createConsumer(qu);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    /**
     * 得到txt的消息
     */
    public void getTextMessage() throws JMSException {
        try {
            // 实现一个消息的监听器
            // 实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        // 获取到接收的数据
                        String text = ((TextMessage) message).getText();
                        System.out.println("pp酱提醒你:" + text);
                        message.acknowledge();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取序列化对象
     * @throws JMSException
     */
    public void getObjectMessage() throws JMSException{
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    Student student = (Student) ((ObjectMessage)message).getObject();
                    System.out.println("pp酱提醒您："+student.toString());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取map
     * @throws JMSException
     */
    public void getMapMessage() throws JMSException{
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                MapMessage mapMessage = (MapMessage) message;
                try {
                    System.out.println(mapMessage.getString("name"));
                    System.out.println(mapMessage.getInt("id"));
                    System.out.println(mapMessage.getString("addr"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 接收byte[]
     * @throws JMSException
     */
    public void getByteArray() throws JMSException{
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                BytesMessage bytesMessage = (BytesMessage) message;
                try {
                    FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\a.txt"));
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = bytesMessage.readBytes(bytes)) != -1){
                        out.write(bytes,0,len);
                    }
                    out.close();
                    System.out.println("文件写入成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }





}
