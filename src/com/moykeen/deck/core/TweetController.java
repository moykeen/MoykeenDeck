package com.moykeen.deck.core;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;

import com.mcdermottroe.apple.OSXKeychain;
import com.mcdermottroe.apple.OSXKeychainException;
import com.moykeen.deck.gui.JTextAreaWithEmacsKeybind;
import com.moykeen.deck.gui.MoykeenDeckFrame;

import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;



/**
 * This class handles doing tweets.  
 * @author makoto
 *
 */
public class TweetController {
	
	/**
	 * データ解析用
	 */
	static public final String APPLICATION_SUPPORT_DIRECTORY_NAME = System.getProperty("user.home") + "/Library/Application Support/MoykeenDeck";
	static public final String DATA_FILE_NAME = "downloaedData_";
	
	/**
	 * The twitter object 
	 */
	private Twitter twitter;
	
	/**
	 * 解析用にダウンロードしたステータスのリスト
	 */
	private List<Status> statuses = null;
	
	/**
	 * 解析したユーザ名
	 */
	private String analyzedUserName;
	
	/**
	 * a request token 
	 */
	private transient RequestToken requestTokenSaved;
	
	/**
	 * 認証が済んでいるかのフラグ
	 */
	private boolean authenticationDone = false;
	
	/**
	 * ステータス表示用デリゲート
	 */
	private StatusIndicator statusIndicator = null;
	
	/**
	 * a key chain controller
	 */
	private KeychainController keychainController = null;
	
	/**
	 * プログレスバーに値を伝えるために使う
	 */
	private transient int progressCount;
	
	
	public TweetController() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(MoykeenDeckApp.OAUTH_CONSUMER_KEY, MoykeenDeckApp.OAUTH_CONSUMER_SECRET);
		keychainController = new KeychainController();
	}
	
	/**
	 * 認証サイトのURL取得
	 */
	public String getAuthorizationSiteURLString(){
		String retStr = null;
		
		if(authenticationDone)
			return "**認証URL無効**   あなたのアカウントはすでに認証済みです。別のアカウントでログインしたい場合は、開発者にお問い合わせ下さい。";
		
		try {
			RequestToken requestToken = twitter.getOAuthRequestToken();
			requestTokenSaved = requestToken;
			retStr = requestToken.getAuthenticationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
			retStr = "(authrization currently unavailable..)";
		} finally {
			//statusIndicator.finishTask();
		}
		
		return retStr;
	}
	

	/**
	 * handle account registration
	 * @param pinField
	 * @return
	 */
	public ActionListener getAccountRegistrationActionListener(final JTextField pinField, final JButton okButton) {
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				
				//PIN code typed by user.
				String pin = pinField.getText();
				
				AccessToken accessToken = null;
				try {
					accessToken = twitter.getOAuthAccessToken(requestTokenSaved, pin);
				} catch (TwitterException te) {
					if(401 == te.getStatusCode()){
						statusIndicator.displayMessage("アクセストークンを取得できませんでした。");
						//System.out.println("Unable to get the access token.");
					}
					te.printStackTrace();
					return;
				}
				
				statusIndicator.displayMessage("認証完了。つぶやいていいですよ。");
				authenticationDone = true;
				okButton.setEnabled(false);
				//System.out.println("Got access token.");
				
				//keyChainに保存する
				keychainController.addAccessToken(accessToken);
				
				
//				
//				try {
//					//すでに存在している場合は、一度消してから新たに保存する必要がある。
//					OSXKeychain keychain = OSXKeychain.getInstance();
//					keychain.addGenericPassword("MoykeenDeck", "theSingleAccount", accessToken.getToken() + " " + accessToken.getTokenSecret());
//					
//				} catch (OSXKeychainException e2) {
//					e2.printStackTrace();
//				}
				
			}
		};
	}


	/**
	 * get an action that handles submission of a tweet
	 * @return an action 
	 */
	public ActionListener getSubmitTweetActionListener(final JTextArea textArea) {
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				//まだ認証が済んでいないとき
				if(!authenticationDone){
					statusIndicator.displayMessage("アカウント設定がまだのようです。");
					return;
				}
				
				String tweetText = textArea.getText();
				//空文字の時
				if(tweetText.equals("")){
					statusIndicator.displayMessage("つぶやきが入力されていません。");
					return;
				}
				
				
				
				
				try {
					
					//TODO インターネット通信を含む処理なので、別スレッドを立ち上げた方が良いかな。
					
					boolean mFlag = false;
					if(tweetText.indexOf('も') >= 0){
						mFlag = true;
					}
				    System.out.println(tweetText);
					//System.out.println("Successfully updated the status to [" + status.getText() + "].");
					
					//開発中なのでつぶやきの投稿はしない if(false)にしないと、TwitterExceptionという例外がnever thrownと怒られる
					//if(false){
						Status status = twitter.updateStatus(tweetText);
				//	}

										
				    
					if(mFlag)
						statusIndicator.displayMessage("つぶやきを投稿しました。とてもモイキーンなつぶやきですね・・ニヤニヤ");
					else
						statusIndicator.displayMessage("つぶやきを投稿しました。");
					
					//テキストフィールドの文字をクリアする
					textArea.setText("");
					
							
				    
				    
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	
	/**
	 * スタットグラフを投稿
	 */
	public ActionListener getSubmitGraphActionListener(final JTextArea textArea, final ChartPanel cp) {
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				//まだ認証が済んでいないとき
				if(!authenticationDone){
					statusIndicator.displayMessage("アカウント設定がまだのようです。");
					return;
				}
				
				//グラフ無い
				if(cp.getChart() == null){
					statusIndicator.displayMessage("グラフがありません。");
					return;
				}
				
				String tweetText = textArea.getText();
				//空文字の時
				//if(tweetText.equals("")){
			//		statusIndicator.displayMessage("なんか考察を入れましょう。");
				//	return;
				//}
				
				
				
				try {
					
					//TODO インターネット通信を含む処理なので、別スレッドを立ち上げた方が良いかな。
					
					File f = File.createTempFile("stats_graph", ".png");
					//File f = new File("/Users/makoto/Desktop/test.png");
					ChartUtilities.saveChartAsPNG(f, cp.getChart(), 1128, 800);
					final StatusUpdate st = new StatusUpdate(tweetText);
					st.media(f);
					
				    //System.out.println(tweetText);
					//System.out.println("Successfully updated the status to [" + status.getText() + "].");
					
					//開発中なのでつぶやきの投稿はしない if(false)にしないと、TwitterExceptionという例外がnever thrownと怒られる
					//if(false){
						Status status = twitter.updateStatus(st);;
					//}

										
					statusIndicator.displayMessage("つぶやきを投稿しました。");
					
					//テキストフィールドの文字をクリアする
					textArea.setText("");							
				    
				    
				}catch (IOException ie){
					ie.printStackTrace();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		};
		
	}
	
	
	

	/**
	 * create date stat chart
	 * @param username: null for yourself. 
	 * @return
	 */
	public JFreeChart createDateStatChart(){
		TimeSeries ts = new TimeSeries("tweet count vs. date");
		Day day, prevDay = null;
		
		
		//ツイート回数vs日付のデータ
		for (Status status : statuses) {
			day = new Day(status.getCreatedAt());
			//System.out.println(status.getUser().getName() + ":" + status.getText() + ":" + status.getCreatedAt());
			
			if(ts.getItemCount() >= 1){
				Day d = (Day)ts.getTimePeriod(0);
				
				//System.out.println(day + " " + d);
				
				try{
					if(day.equals(d)){
						int c = ts.getValue(0).intValue() + 1;
						ts.update(0, c);						
					}else{
						ts.add(day, 1);
					}
				}catch(org.jfree.data.general.SeriesException se){
					//なんか、ときどき、時系列に並んでないデータがあるっぽい
					ts.delete(0, 0); //消す
				}
			}else{
				ts.add(day, 1);
			}
		}
		
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		tsc.addSeries(ts);
		return ChartFactory.createTimeSeriesChart("", "Day", "ツイート回数", tsc, false, false, false);
		
	}
	
	/**
	 * create hour stat chart
	 * @param username: null for yourself. 
	 * @return
	 */
	public JFreeChart createHourStatChart(){
		
		TimeSeries ts = new TimeSeries("tweet count vs. hour");
		Hour hour;
		int hours [] = new int [24];
		int total = 0;
		
		//集計
		for (Status status : statuses) {
			hour = new Hour(status.getCreatedAt());
			//System.out.println(status.getUser().getName() + ":" + status.getText() + ":" + status.getCreatedAt());
			
			hours[hour.getHour()]++;
			total++;
		}	
		
		for(int i = 0; i < hours.length; i++){
			ts.add(new Hour(i, 1, 1, 2000), hours[i]);
		}

		TimeSeriesCollection tsc = new TimeSeriesCollection();
		tsc.addSeries(ts);
		return ChartFactory.createTimeSeriesChart("つぶやいた時間帯", "時刻", "ツイート回数", tsc, false, false, false);
		
	}
	
	/**
	 * create hour stat chart
	 */
	public JFreeChart createWeekDayStatChart(){
		Calendar cal = Calendar.getInstance();
		
		int weekdays [] = new int [7];
		int total = 0;
		
		
		//集計
		for (Status status : statuses) {
			Date d = status.getCreatedAt();
			cal.setTime(d);
			weekdays[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]++;
			total++;
			//System.out.println(status.getSource() + "  " + status.getText());			
		}	
		
		
		double[][] data = new double[][] {{weekdays[0], weekdays[1], weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6]}};
		
		// CategoryDatasetオブジェクトの作成
		CategoryDataset cData = DatasetUtilities.createCategoryDataset(new String [] {"RowKey"}, new String [] {"日", "月", "火", "水", "木", "金", "土"}, data);
		return ChartFactory.createBarChart ("曜日とつぶやき回数の関係", "曜日", "つぶやいた回数", cData, PlotOrientation.VERTICAL, false, false, true);
		
	}
	
	/**
	 * ツイート種類(通常、リプライ、リツイート)の割合
	 */
	public JFreeChart createTweetTypeStatChart(){
		int types[] = new int [3];
		int total = 0;
		
		//集計
		for (Status status : statuses) {
			
			
			total++;
			if(status.isRetweet()){
				types[2]++;
				continue;
			}
			
			if(status.getInReplyToScreenName() != null){
				types[1]++;
				continue;
			}
			
			types[0]++;
		}
		
		DefaultKeyedValues kv = new DefaultKeyedValues();
		kv.addValue("通常のツイート" + "  " + types[0] + "回", types[0]);
		kv.addValue("リプライ" + "  " + types[1] + "回", types[1]);
		kv.addValue("リツイート" + "  " + types[2] + "回", types[2]);
		 
		return ChartFactory.createPieChart3D("ツイートの種類", new DefaultPieDataset(kv), false, false, true);
		
	}
	
	/**
	 * リプライの統計
	 */
	public JFreeChart createReplyChart(){
		
		//collectStatuses(null);
		//saveStatusList();
		
		int total = 0;
		
		
		//ハッシュ
		HashMap h = new HashMap();
		
		//集計
		for (Status status : statuses) {
			String s = status.getInReplyToScreenName();
			if(s == null)
				continue;
			
			total++;
			if(!h.containsKey(s)){
				h.put(s, 1);
			}else{
				int n = ((Integer)h.get(s)).intValue();
				h.put(s, n + 1);
			}
		}
		
		DefaultKeyedValues kv = new DefaultKeyedValues();
		Iterator itr = h.entrySet().iterator();
		
		
		int sonota = 0;
		while (itr.hasNext()) {
			Map.Entry pairs = (Map.Entry)itr.next();
			int count = ((Integer)pairs.getValue()).intValue();
			
			if((float)count / total >= 0.03) //10%以上なら、入れる
				kv.addValue((String)pairs.getKey() + "  " + count + "回" , count);
			else{
				sonota += count;
			}
			
		}
		kv.sortByValues(org.jfree.util.SortOrder.DESCENDING);
		kv.addValue("その他" + "  " + sonota + "回", sonota);
		 
		return ChartFactory.createPieChart3D("よく会話をする人", new DefaultPieDataset(kv), false, false, true);

	}
	
	/**
	 * 文字数のヒストグラム
	 */
	public JFreeChart createWordCountChart(){
		SimpleHistogramDataset hist = new SimpleHistogramDataset("word count");
		hist.setAdjustForBinSize(false);
		final int tic = 5;
		for(int i = 0; i < 140; i += tic){
			if(i == 0)
				hist.addBin(new SimpleHistogramBin(i, i + tic, true, true)); //i==0の場合、lower boundを含む
			else
				hist.addBin(new SimpleHistogramBin(i, i + tic, false, true));
		}
		
		//集計
		for (Status status : statuses) {
			String s = status.getText();
			hist.addObservation(s.length());
		}
		
		return ChartFactory.createXYBarChart("文字の長さの分布", "文字の長さ", false, "回数", hist, PlotOrientation.VERTICAL, false, false, true);
		
	}
	
	/**
	 * 使用アプリの集計
	 */
	public JFreeChart createUsedAppChart(){

		//collectStatuses("kimura6");
		//saveStatusList();
		
		
		int total = 0;
				
		//ハッシュ
		HashMap h = new HashMap();
		
		//集計
		for (Status status : statuses) {
			String s = status.getSource();
			
			
			total++;
			if(!h.containsKey(s)){
				h.put(s, 1);
			}else{
				int n = ((Integer)h.get(s)).intValue();
				h.put(s, n + 1);
			}
		}
		
		DefaultKeyedValues kv = new DefaultKeyedValues();
		Iterator itr = h.entrySet().iterator();
		
		
		while (itr.hasNext()) {
			Map.Entry pairs = (Map.Entry)itr.next();
			int count = ((Integer)pairs.getValue()).intValue();
			
			String appInfo = (String)pairs.getKey();
			String appName = null;
			
			Pattern pattern = Pattern.compile(">(.+)<");
			Matcher matcher = pattern.matcher(appInfo);
			if (matcher.find()) {
				appName = matcher.group(1);
			} else {
				appName = appInfo;
			}
			
			kv.addValue(appName + "  " + count + "回" , count);			
		}
		kv.sortByValues(org.jfree.util.SortOrder.DESCENDING);
				 
		return ChartFactory.createPieChart3D("よく使うアプリ", new DefaultPieDataset(kv), false, false, true);

	}
	
	
	/**
	 * statusのリストをダウンロードする
	 */
	public int collectStatuses(String username, final JProgressBar progressBar) {
		statuses = new ArrayList<Status>();
		
		final int nPerPage = 200;
		final int n = 350;
		int total = 0;
		
		analyzedUserName = username;
		
		for(progressCount = 0; progressCount < n; progressCount++){
			List<Status> part;
			Paging p = new Paging(progressCount + 1, nPerPage);
			
			try{
				if(username == null)
					part = twitter.getUserTimeline(p);
				else
					part = twitter.getUserTimeline(username, p);
				
				statuses.addAll(part);
				
			}catch(TwitterException e){
				e.printStackTrace();
				break;
			}
			
			total += part.size();
			if(part.size() == 0)
				break;
			
			
			
			//上手く動かない。。
			if(progressBar != null){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						System.out.println(progressCount);
						//progressBar.setValue((int)((double)progressCount / n * 100));
						progressBar.setValue((int)((double)progressCount * 10));
					}
				});
			}
						
			//System.out.println(progressCount + "  " + total);
			
		}
		
		if(progressBar != null){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBar.setValue(100);
				}
			});
		}
		
		return total;
		
	}
	
	/**
	 * statusのリストを保存する
	 */
	public void saveStatusList() {
		File applicationSupportDirectory = new File(APPLICATION_SUPPORT_DIRECTORY_NAME);
		File dataFile;
		
		
		if(!applicationSupportDirectory.exists()){
			applicationSupportDirectory.mkdir();
		}
		
		if(analyzedUserName == null){
			dataFile = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + "default");
		}else{
			dataFile = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + analyzedUserName);
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));
			oos.writeObject(statuses);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * statusのリストを読み出す
	 */
	@SuppressWarnings("unchecked")
	public void loadStatusList(String username) {
		analyzedUserName = username;
		
		File dataFile;
		
		if(analyzedUserName == null){
			dataFile = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + "default");
		}else{
			dataFile = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + analyzedUserName);
		}
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));
			statuses = (ArrayList<Status>)ois.readObject();
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * Dataをダウンロード このメソッド現在使ってない
	 * @return
	 */
	public ActionListener getDataDownloadActionListener(final JProgressBar progressBar) {
		return new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				collectStatuses(null, null);	
				saveStatusList();
				
				
			}
		};
		
	}
	
	
	/**
	 * Twitterを準備
	 */
	public void prepareForTwitter() {
		//keyChainからaccessTokenを読み取ってくる
		
		AccessToken accessToken = keychainController.findAccessToken();
		
		if(accessToken != null){
			twitter.setOAuthAccessToken(accessToken);
			authenticationDone = true;
		}
			
		
//		
//		try {
//			OSXKeychain keychain = OSXKeychain.getInstance();
//			String sTemp = keychain.findGenericPassword("MoykeenDeck", "theSingleAccount");
//			
//			
//			System.out.println(sTemp);
//			if(sTemp.equals("")){//まだアカウントが設定されてない場合
//				return;
//			}
//			
//			String [] strs = sTemp.split(" "); //空白でtokenとtoken secretを分けている
//			
//			//twitter.setOAuthConsumer(MoykeenDeckApp.OAUTH_CONSUMER_KEY, MoykeenDeckApp.OAUTH_CONSUMER_SECRET);
//			twitter.setOAuthAccessToken(new AccessToken(strs[0], strs[1]));
//			
//			authorizationDone = true;
//			
//		} catch (OSXKeychainException e2) {
//			e2.printStackTrace();
//		}
		
				
	}

	/**
	 * 
	 * @param statusIndicator
	 */
	public void setStatusIndicator(StatusIndicator statusIndicator) {
		this.statusIndicator = statusIndicator;

	}
	
	/**
	 * 
	 */
	public StatusIndicator getStatusIndicator(){
		return statusIndicator;
	}
	
	/**
	 * 
	 */
	public boolean getAuthorizationDone() {
		return this.authenticationDone;
	}

	/**
	 * すでに取得したデータがあるか
	 * @return
	 */
	public boolean isStatusListAvailable() {
		
		File f = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + "default");
		return f.exists();		
		
	}

	
	/**
	 * 解析の準備ができているか 
	 */
	public boolean isStatsPrepared(){
		return statuses != null;
	}

	
	
	
	

	
	
}
