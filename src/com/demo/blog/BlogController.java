package com.demo.blog;

import java.util.List;
import java.util.Map.Entry;

import com.demo.common.config.AwsConfig;
import com.demo.common.model.Blog;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * BlogController 所有 sql 与业务逻辑写在 Model 或 Service 中，不要写在 Controller
 * 中，养成好习惯，有利于大型项目的开发与维护
 */
@Before(BlogInterceptor.class)
public class BlogController extends Controller {

	

	public void index() {
		render("blog.html");
	}

	/*
	 * 创建队列
	 */
	public void creating() {
		try {
			String myQueue = getPara("queue_name");
			AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
			Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
			sqs.setRegion(usWest2);
			System.out.println("Creating a new SQS queue called MyQueue.\n");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
			String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			sqs.shutdown();
			renderText("创建队列成功:" + myQueueUrl);
		} catch (Exception e) {
			renderText("创建失败," + e.getMessage());
		}
	}

	/*
	 * 获取队列列表
	 */

	public void list() {
		String result = "";
		AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
		Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
		sqs.setRegion(usWest2);
		System.out.println("Listing all queues in your account.\n");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			result = result + queueUrl + "<br/>";
		}
		sqs.shutdown();
		renderText("队列列表信息:<br/>" + result);
	}

	/*
	 * 发送消息
	 */
	public void send() {
		String myQueue = getPara("queue_name");
		String text = getPara("queue_context");
		AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
		Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
		sqs.setRegion(usWest2);
		System.out.println("Creating a new SQS queue called MyQueue.\n");
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		sqs.sendMessage(new SendMessageRequest(myQueueUrl, text));
		sqs.shutdown();
		renderText("消息发送成功");
	}

	public void addsend() {
		render("sendsqs.html");
	}

	/*
	 * 获取消息
	 */
	public void getReceive() {
		String myQueue = getPara("queue_name");
		AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
		Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
		sqs.setRegion(usWest2);
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		StringBuilder sb = new StringBuilder();
		for (Message message : messages) {

			sb.append("  Message");
			sb.append("<br/>");
			sb.append("    MessageId:     " + message.getMessageId());
			sb.append("<br/>");
			sb.append("    ReceiptHandle: " + message.getReceiptHandle());
			sb.append("<br/>");
			sb.append("    MD5OfBody:     " + message.getMD5OfBody());
			sb.append("<br/>");
			sb.append("    Body:          " + message.getBody());
			sb.append("<br/>");
		}
		String messageReceiptHandle = messages.get(0).getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
		sqs.shutdown();
		renderText(sb.toString());
	}

	public void addReceive() {
		render("receivesqs.html");
	}

	public void delReceive() {
		try {
			String myQueue = getPara("queue_name");
			AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
			Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
			sqs.setRegion(usWest2);
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
			String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
			sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
			renderText("删除成功");
		} catch (Exception e) {
			renderText("删除失败," + e.getMessage());
		}

	}

}
