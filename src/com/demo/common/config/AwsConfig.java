package com.demo.common.config;

import com.jfinal.kit.PropKit;

public class AwsConfig {
	public static  String AWS_KEY = PropKit.get("AWS_KEY");
	public static String AWS_SECRET =  PropKit.get("AWS_SECRET");
	
}
