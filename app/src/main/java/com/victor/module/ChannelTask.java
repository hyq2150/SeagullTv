package com.victor.module;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.victor.data.Category;
import com.victor.data.Channel;
import com.victor.util.Constant;
import com.victor.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ChannelTask {
	private String TAG = "ChannelTask";
	private int requestCount;
	private Context mContext;

	public ChannelTask(Context context) {
		mContext = context;
	}

	public void requestChannelData() {
		requestCount++;
		Log.e(TAG, "requestCount=" + requestCount);
		if(requestCount > 5){
			return;
		}
		new ChannelInfoTask().execute(Constant.CHANNEL_URL);
//		new ChannelInfoTask().execute("http://128.0.16.83:8080/datas/channels_json.txt");
	}

	class ChannelInfoTask extends AsyncTask<String, Integer, Bundle>{

		@Override
		protected Bundle doInBackground(String... params) {
			// TODO Auto-generated method stub
			int status = 0;
			Bundle responseData = new Bundle();
			if (HttpUtil.isNetEnable(mContext)){
				String result = null;
				try {
					result = HttpUtil.HttpGetRequest(params[0]);
					Log.e(TAG,"result = " + result);
					if (!TextUtils.isEmpty(result)) {
						status = Constant.Msg.REQUEST_SUCCESS;
						List<Category> channels = parseChannels(result);
						responseData.putSerializable(Constant.CHANNEL_DATA_KEY, (Serializable) channels);
					} else {
						status = Constant.Msg.REQUEST_FAILED;
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					status = Constant.Msg.SOCKET_TIME_OUT;
				}
			} else {
				status = Constant.Msg.NETWORK_ERROR;
			}
			responseData.putInt(Constant.STATUS_KEY, status);
			responseData.putInt(Constant.REQUEST_MSG_KEY, Constant.Msg.CHANNEL_REQUEST);

			return responseData;
		}

		protected void onPostExecute(Bundle result) {
			if(result != null){
				DataObservable.getInstance().setData(result);
			}else{
				requestChannelData();
			}
		}
	}

	private List<Category> parseChannels (String datas) {
		Log.e(TAG,"parseChannels()...datas = " + datas);
		List<Category> categories = new ArrayList<>();
		try{
			JSONArray result = new JSONArray(datas);
			for (int i=0;i<result.length();i++) {
				JSONObject info = result.getJSONObject(i);

				Category category = new Category();
				category.category = info.optString("channel_category").trim();
				category.gravity = Gravity.CENTER_HORIZONTAL;

				List<Channel> channels = new ArrayList<>();
				JSONArray channelArry = info.getJSONArray("channels");
				for (int j=0;j<channelArry.length();j++) {
					JSONObject item = channelArry.getJSONObject(j);
					Channel channel = new Channel();
					channel.category = i;
					channel.name = item.getString("channel_name").trim();
					channel.epg = item.getString("epg").trim();
					channel.icon = item.getString("icon").trim();
					JSONArray playUrlArry = item.getJSONArray("play_urls");
					String[] playUrls = new String[playUrlArry.length()];
					for (int k=0;k<playUrlArry.length();k++) {
						playUrls[k] = playUrlArry.getJSONObject(k).getString("play_url");
					}
					channel.playUrls = playUrls;
					channels.add(channel);
				}
				category.channels = channels;
				categories.add(category);
			}

		}catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e(TAG,"parseChannels()...categories.size() = " + categories.size());
		return categories;
	}

}