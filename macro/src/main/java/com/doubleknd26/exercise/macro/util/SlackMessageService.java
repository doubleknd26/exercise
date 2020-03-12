package com.doubleknd26.exercise.macro.util;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class SlackMessageService {
	private static final Logger logger = LogManager.getLogger();
	private String webHookUrl;
	private String channel;

	public SlackMessageService(String webHookUrl, String channel, String initMessage) {
		this.webHookUrl = webHookUrl;
		this.channel = channel;
		noti(initMessage);
	}

	public void noti(String message) {
		noti(message, "");
	}

	public void noti(String message, String to) {
		HttpClient client = new HttpClient();
		JsonObject json = new JsonObject();
		json.addProperty("channel", channel);
		json.addProperty("text", Strings.isNullOrEmpty(to) ? message : "<!" + to +"> " + message);
		json.addProperty("icon_emoji", ":ghost:");

		PostMethod post = new PostMethod(webHookUrl);
		try {
			post.addParameter("payload", json.toString());
			post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			int responseCode = client.executeMethod(post);
			if (responseCode != HttpStatus.SC_OK) {
				System.out.println("Response Code: " + responseCode);
			}
			logger.info(message);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
	}

	public static class Builder {
		private String webHookUrl = null;
		private String channel = null;
		private String initMessage = "Message service is started.";

		public Builder setWebHookUrl(String webHookUrl) {
			this.webHookUrl = webHookUrl;
			return this;
		}

		public Builder setChannel(String channel) {
			this.channel = channel;
			return this;
		}

		public Builder setInitMessage(String initMessage) {
			this.initMessage = initMessage;
			return this;
		}

		public SlackMessageService build() {
			return new SlackMessageService(webHookUrl, channel, initMessage);
		}
	}
}
