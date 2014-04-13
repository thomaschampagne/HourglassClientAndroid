package com.thomaschampagne.hourglass.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.thomaschampagne.hourglass.library.asynctasks.DownloadAsyncTask;
import com.thomaschampagne.hourglass.library.callback.ISynchronizationListener;
import com.thomaschampagne.hourglass.library.model.SyncConfig;
import com.thomaschampagne.hourglass.library.unmashaller.UnMarshaller;

public class SyncServices {

	/**
	 * 
	 * @param activity
	 * @param syncHandler
	 * @param syncConfig
	 * @throws Exception
	 */
	public static void checkout(Activity activity, ISynchronizationListener syncHandler, SyncConfig syncConfig)
			throws Exception {

			DownloadAsyncTask synchronizeCheckoutTaskAsync = new DownloadAsyncTask(activity, syncHandler, syncConfig);
			String requestUrl = generateQueryCheckout(
                    syncConfig.getServerUrl(),
                    syncConfig.getFilterRegexInclude(),
                    syncConfig.getFilterRegexExclude(),
                    syncConfig.getSyncOnTestResources());

			synchronizeCheckoutTaskAsync.execute(requestUrl);

	}

	/**
	 * 
	 * @param activity
	 * @param syncHandler
	 * @param currentClientVersion
	 * @param syncConfig
	 * @throws Exception
	 */
	public static void updateFromRevision(Activity activity, ISynchronizationListener syncHandler,
			Integer currentClientVersion, SyncConfig syncConfig)
			throws Exception {
		
			DownloadAsyncTask synchronizeFromRevisionTaskAsync = new DownloadAsyncTask(activity, syncHandler, syncConfig);
			String requestUrl = generateQueryPullFromRevision(
                    syncConfig.getServerUrl(),
                    currentClientVersion,
                    syncConfig.getFilterRegexInclude(),
                    syncConfig.getFilterRegexExclude(),
                    syncConfig.getSyncOnTestResources());

			synchronizeFromRevisionTaskAsync.execute(requestUrl);

	}

	/**
	 * 
	 * @param context
	 * @param syncConfig
	 * @return
	 * @throws Exception
	 */
	public static Integer getServerRevNumberCall(Context context,
			SyncConfig syncConfig) throws Exception {

		String jsonResponse = webServiceCall(generateQueryGetServerRevNumber(syncConfig.getServerUrl(), syncConfig.getSyncOnTestResources()), syncConfig.getConnectionTimeoutMillisOnRemoteRevisionCheck());
		return UnMarshaller.serverSimpleJsonResponseToInteger(context, jsonResponse);
	}

	/**
	 * 
	 * @param context
	 * @param syncConfig
	 * @return
	 * @throws Exception
	 */
	public static Integer getServerRevNumberDateCall(Context context,
			SyncConfig syncConfig) throws Exception {
		
		String jsonResponse = webServiceCall(generateQueryGetServerRevNumberDate(syncConfig.getServerUrl(), syncConfig.getSyncOnTestResources()), syncConfig.getConnectionTimeoutMillisOnRemoteRevisionCheck()); 
		return UnMarshaller.serverSimpleJsonResponseToInteger(context, jsonResponse);
	}
	
	public static String webServiceCall(String url, int timeoutMillis) throws IOException,
			ClientProtocolException, URISyntaxException, IllegalStateException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet();
		get.setURI(new URI(url));
		httpclient.getParams().setIntParameter("http.connection.timeout", timeoutMillis); 

		Log.d("webServiceCall", get.getURI().toString());

		HttpResponse response = httpclient.execute(get);
		StatusLine statusLine = response.getStatusLine();

		String responseString = null;

		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			responseString = out.toString();

		} else {

			response.getEntity().getContent().close();
			throw new IOException(statusLine.getReasonPhrase());
		}
		return responseString;
	}

	public static String generateQueryCheckout(String serverUrl,
                                               String filterRegexInclude, String filterRegexExclude,
                                               Boolean activeTestingResources) throws Exception {

		return (serverUrl + "?q=" + URLEncoder.encode(
				"{\"method\":\"checkout\","
						+ "\"params\":{\"simulateArchive\":" + "false,"
						+ "\"filterWithRegex\":\"" + filterRegexInclude
						+ "\",\"filterWithoutRegex\":\"" + filterRegexExclude
						+ "\"}," + "\"test\":"
						+ activeTestingResources.toString() + "}", "UTF-8"));
	}

	public static String generateQueryPullFromRevision(
            String serverUrl, Integer currentClientVersion,
            String filterRegexInclude, String filterRegexExclude,
            Boolean useTestsResources) throws Exception {

		return (serverUrl + "?q=" + URLEncoder.encode(
				"{\"method\":\"pullFromRevision\","
						+ "\"params\":{\"revision\":" + currentClientVersion
						+ ",\"simulateArchive\":" + "false,"
						+ "\"filterWithRegex\":\"" + filterRegexInclude
						+ "\",\"filterWithoutRegex\":\"" + filterRegexExclude
						+ "\"}," + "\"test\":" + useTestsResources.toString()
						+ "}", "UTF-8"));
	}

	public static String generateQueryGetServerRevNumber(String serverUrl,
			Boolean activeTestingResources) throws Exception {
		return (serverUrl + "?q=" + URLEncoder.encode(
				"{\"method\":\"getRevNumber\",\"test\":"
						+ activeTestingResources.toString() + "}", "UTF-8"));
	}
	
	public static String generateQueryGetServerRevNumberDate(String serverUrl,
			Boolean activeTestingResources) throws Exception {
		return (serverUrl + "?q=" + URLEncoder.encode(
				"{\"method\":\"getRevNumberDate\",\"test\":"
						+ activeTestingResources.toString() + "}", "UTF-8"));
	}

}