package cn.baizhi;

import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootTest
public class RabbitTest {
    //生产者
    @Test
    void testP() throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory con = new ConnectionFactory();
        //rabbitmq服务ip地址
        con.setHost("192.168.91.131");
        //设置rabbimq端口
        con.setPort(5672);

        //账户和密码
        con.setUsername("eon");
        con.setPassword("123");

        //设置一个超时时间
        con.setHandshakeTimeout(50000);

        //rabbitmq虚拟主机
        con.setVirtualHost("2101");

        //获取连接对象
        Connection connection = con.newConnection();
        //获取通道对象
        Channel channel = connection.createChannel();

        /**设置队列参数
         * @param queue 队列名称  如果这个队列不存在，将会被创建
         * @param durable 持久性：用来定义队列是否要持久化  true:持久化  false:不持久化
         * @param exclusive 是否只能由创建者使用，其他连接不能使用。 true:独占队列  false:不独占队列
         * @param autoDelete 是否自动删除（没有连接自动删除） true:自动删除   false:不自动删除
         * @param arguments 队列的其他属性(构造参数)
         */

        channel.queueDeclare("rabbitMQ-message1", false, false, false, null);

        /**发布消息
         * @param exchange 消息交换机名称,空字符串将使用直接交换器模式，发送到默认的Exchange=amq.direct。此状态下，RoutingKey默认和Queue名称相同
         * @param queueName 队列名称
         * @param BasicProperties  设置消息持久化：MessageProperties.PERSISTENT_TEXT_PLAIN是持久化；MessageProperties.TEXT_PLAIN是非持久化。
         * @param body 消息对象转换的byte[]
         */

        channel.basicPublish("", "rabbitMQ-message1", null, "这是我们的第一条消息".getBytes());
        //关闭连接
        channel.close();
        connection.close();

    }

    //开通消费者
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接
        ConnectionFactory con = new ConnectionFactory();
        //rabbitmq服务ip地址
        con.setHost("192.168.91.131");
        //设置rabbimq端口
        con.setPort(5672);

        //账户和密码
        con.setUsername("eon");
        con.setPassword("123");

        //设置一个超时时间
        con.setHandshakeTimeout(50000);

        //rabbitmq虚拟主机
        con.setVirtualHost("2101");

        Connection connection = con.newConnection();
        Channel channel = connection.createChannel();


        /**消费者消费消息
         * @param queue 队列名称
         * @param autoAck 是否自动应答。false表示consumer在成功消费过后必须要手动回复一下服务器，如果不回复，服务器就将认为此条消息消费失败，继续分发给其他consumer。
         * @param callback 回调方法类，一般为自己的Consumer类
         */

        channel.queueDeclare("rabbitMQ-message1", false, false, false, null);

        channel.basicConsume("rabbitMQ-message1",true , new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //super.handleDelivery(consumerTag, envelope, properties, body);
                String s = new String(body);
                System.out.println("队列中的消息：" + s);
            }
        });
    }

}
