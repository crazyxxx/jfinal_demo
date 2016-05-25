package com.demo.aws;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.demo.common.config.AwsConfig;

/*
 * S3的demo
 */
public class AwsController extends Controller {

	 

	public void index() {

		render("index.html");
	}

	/*
	 * 创建桶
	 */
	public void add() {
		try {
			String bucketName = getPara("name");
			AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
			Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
			s3.setRegion(usWest2);
			s3.createBucket(bucketName);

			renderText("创建桶成功");
		} catch (Exception e) {
			renderText("创建桶失败：" + e.getMessage());
		}

	}

	/*
	 * 列表
	 */
	public void list() {
		try {
			String result = "";
			AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
			Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
			s3.setRegion(usWest2);
			for (Bucket bucket : s3.listBuckets()) {
				result = result + bucket.getName() + "<br/>";
			}
			renderText("列表信息:<br/>" + result);
		} catch (Exception e) {
			renderText("失败：" + e.getMessage());
		}
	}

	public void upload() {

		render("upload.html");

	}

	public void uploadAdd() {

		try {
			UploadFile upload = getFile("file");
			String bucketName = getPara("bucketName");
			String key = getPara("key");

			AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(AwsConfig.AWS_KEY, AwsConfig.AWS_SECRET));
			Region usWest2 = Region.getRegion(Regions.EU_CENTRAL_1);
			s3.setRegion(usWest2);
			s3.putObject(new PutObjectRequest(bucketName, key, upload.getFile()));
			renderText("成功 ");
		} catch (Exception e) {
			renderText("失败：" + e.getMessage());
		}

	}

}
