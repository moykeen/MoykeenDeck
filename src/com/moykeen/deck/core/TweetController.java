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
	 * �f�[�^��͗p
	 */
	static public final String APPLICATION_SUPPORT_DIRECTORY_NAME = System.getProperty("user.home") + "/Library/Application Support/MoykeenDeck";
	static public final String DATA_FILE_NAME = "downloaedData_";
	
	/**
	 * The twitter object 
	 */
	private Twitter twitter;
	
	/**
	 * ��͗p�Ƀ_�E�����[�h�����X�e�[�^�X�̃��X�g
	 */
	private List<Status> statuses = null;
	
	/**
	 * ��͂������[�U��
	 */
	private String analyzedUserName;
	
	/**
	 * a request token 
	 */
	private transient RequestToken requestTokenSaved;
	
	/**
	 * �F�؂��ς�ł��邩�̃t���O
	 */
	private boolean authenticationDone = false;
	
	/**
	 * �X�e�[�^�X�\���p�f���Q�[�g
	 */
	private StatusIndicator statusIndicator = null;
	
	/**
	 * a key chain controller
	 */
	private KeychainController keychainController = null;
	
	/**
	 * �v���O���X�o�[�ɒl��`���邽�߂Ɏg��
	 */
	private transient int progressCount;
	
	
	public TweetController() {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(MoykeenDeckApp.OAUTH_CONSUMER_KEY, MoykeenDeckApp.OAUTH_CONSUMER_SECRET);
		keychainController = new KeychainController();
	}
	
	/**
	 * �F�؃T�C�g��URL�擾
	 */
	public String getAuthorizationSiteURLString(){
		String retStr = null;
		
		if(authenticationDone)
			return "**�F��URL����**   ���Ȃ��̃A�J�E���g�͂��łɔF�؍ς݂ł��B�ʂ̃A�J�E���g�Ń��O�C���������ꍇ�́A�J���҂ɂ��₢���킹�������B";
		
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
						statusIndicator.displayMessage("�A�N�Z�X�g�[�N�����擾�ł��܂���ł����B");
						//System.out.println("Unable to get the access token.");
					}
					te.printStackTrace();
					return;
				}
				
				statusIndicator.displayMessage("�F�؊����B�Ԃ₢�Ă����ł���B");
				authenticationDone = true;
				okButton.setEnabled(false);
				//System.out.println("Got access token.");
				
				//keyChain�ɕۑ�����
				keychainController.addAccessToken(accessToken);
				
				
//				
//				try {
//					//���łɑ��݂��Ă���ꍇ�́A��x�����Ă���V���ɕۑ�����K�v������B
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
				//�܂��F�؂��ς�ł��Ȃ��Ƃ�
				if(!authenticationDone){
					statusIndicator.displayMessage("�A�J�E���g�ݒ肪�܂��̂悤�ł��B");
					return;
				}
				
				String tweetText = textArea.getText();
				//�󕶎��̎�
				if(tweetText.equals("")){
					statusIndicator.displayMessage("�Ԃ₫�����͂���Ă��܂���B");
					return;
				}
				
				
				
				
				try {
					
					//TODO �C���^�[�l�b�g�ʐM���܂ޏ����Ȃ̂ŁA�ʃX���b�h�𗧂��グ�������ǂ����ȁB
					
					boolean mFlag = false;
					if(tweetText.indexOf('��') >= 0){
						mFlag = true;
					}
				    System.out.println(tweetText);
					//System.out.println("Successfully updated the status to [" + status.getText() + "].");
					
					//�J�����Ȃ̂łԂ₫�̓��e�͂��Ȃ� if(false)�ɂ��Ȃ��ƁATwitterException�Ƃ�����O��never thrown�Ɠ{����
					//if(false){
						Status status = twitter.updateStatus(tweetText);
				//	}

										
				    
					if(mFlag)
						statusIndicator.displayMessage("�Ԃ₫�𓊍e���܂����B�ƂĂ����C�L�[���ȂԂ₫�ł��ˁE�E�j���j��");
					else
						statusIndicator.displayMessage("�Ԃ₫�𓊍e���܂����B");
					
					//�e�L�X�g�t�B�[���h�̕������N���A����
					textArea.setText("");
					
							
				    
				    
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	
	/**
	 * �X�^�b�g�O���t�𓊍e
	 */
	public ActionListener getSubmitGraphActionListener(final JTextArea textArea, final ChartPanel cp) {
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				//�܂��F�؂��ς�ł��Ȃ��Ƃ�
				if(!authenticationDone){
					statusIndicator.displayMessage("�A�J�E���g�ݒ肪�܂��̂悤�ł��B");
					return;
				}
				
				//�O���t����
				if(cp.getChart() == null){
					statusIndicator.displayMessage("�O���t������܂���B");
					return;
				}
				
				String tweetText = textArea.getText();
				//�󕶎��̎�
				//if(tweetText.equals("")){
			//		statusIndicator.displayMessage("�Ȃ񂩍l�@�����܂��傤�B");
				//	return;
				//}
				
				
				
				try {
					
					//TODO �C���^�[�l�b�g�ʐM���܂ޏ����Ȃ̂ŁA�ʃX���b�h�𗧂��グ�������ǂ����ȁB
					
					File f = File.createTempFile("stats_graph", ".png");
					//File f = new File("/Users/makoto/Desktop/test.png");
					ChartUtilities.saveChartAsPNG(f, cp.getChart(), 1128, 800);
					final StatusUpdate st = new StatusUpdate(tweetText);
					st.media(f);
					
				    //System.out.println(tweetText);
					//System.out.println("Successfully updated the status to [" + status.getText() + "].");
					
					//�J�����Ȃ̂łԂ₫�̓��e�͂��Ȃ� if(false)�ɂ��Ȃ��ƁATwitterException�Ƃ�����O��never thrown�Ɠ{����
					//if(false){
						Status status = twitter.updateStatus(st);;
					//}

										
					statusIndicator.displayMessage("�Ԃ₫�𓊍e���܂����B");
					
					//�e�L�X�g�t�B�[���h�̕������N���A����
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
		
		
		//�c�C�[�g��vs���t�̃f�[�^
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
					//�Ȃ񂩁A�Ƃ��ǂ��A���n��ɕ���łȂ��f�[�^��������ۂ�
					ts.delete(0, 0); //����
				}
			}else{
				ts.add(day, 1);
			}
		}
		
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		tsc.addSeries(ts);
		return ChartFactory.createTimeSeriesChart("", "Day", "�c�C�[�g��", tsc, false, false, false);
		
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
		
		//�W�v
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
		return ChartFactory.createTimeSeriesChart("�Ԃ₢�����ԑ�", "����", "�c�C�[�g��", tsc, false, false, false);
		
	}
	
	/**
	 * create hour stat chart
	 */
	public JFreeChart createWeekDayStatChart(){
		Calendar cal = Calendar.getInstance();
		
		int weekdays [] = new int [7];
		int total = 0;
		
		
		//�W�v
		for (Status status : statuses) {
			Date d = status.getCreatedAt();
			cal.setTime(d);
			weekdays[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]++;
			total++;
			//System.out.println(status.getSource() + "  " + status.getText());			
		}	
		
		
		double[][] data = new double[][] {{weekdays[0], weekdays[1], weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6]}};
		
		// CategoryDataset�I�u�W�F�N�g�̍쐬
		CategoryDataset cData = DatasetUtilities.createCategoryDataset(new String [] {"RowKey"}, new String [] {"��", "��", "��", "��", "��", "��", "�y"}, data);
		return ChartFactory.createBarChart ("�j���ƂԂ₫�񐔂̊֌W", "�j��", "�Ԃ₢����", cData, PlotOrientation.VERTICAL, false, false, true);
		
	}
	
	/**
	 * �c�C�[�g���(�ʏ�A���v���C�A���c�C�[�g)�̊���
	 */
	public JFreeChart createTweetTypeStatChart(){
		int types[] = new int [3];
		int total = 0;
		
		//�W�v
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
		kv.addValue("�ʏ�̃c�C�[�g" + "  " + types[0] + "��", types[0]);
		kv.addValue("���v���C" + "  " + types[1] + "��", types[1]);
		kv.addValue("���c�C�[�g" + "  " + types[2] + "��", types[2]);
		 
		return ChartFactory.createPieChart3D("�c�C�[�g�̎��", new DefaultPieDataset(kv), false, false, true);
		
	}
	
	/**
	 * ���v���C�̓��v
	 */
	public JFreeChart createReplyChart(){
		
		//collectStatuses(null);
		//saveStatusList();
		
		int total = 0;
		
		
		//�n�b�V��
		HashMap h = new HashMap();
		
		//�W�v
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
			
			if((float)count / total >= 0.03) //10%�ȏ�Ȃ�A�����
				kv.addValue((String)pairs.getKey() + "  " + count + "��" , count);
			else{
				sonota += count;
			}
			
		}
		kv.sortByValues(org.jfree.util.SortOrder.DESCENDING);
		kv.addValue("���̑�" + "  " + sonota + "��", sonota);
		 
		return ChartFactory.createPieChart3D("�悭��b������l", new DefaultPieDataset(kv), false, false, true);

	}
	
	/**
	 * �������̃q�X�g�O����
	 */
	public JFreeChart createWordCountChart(){
		SimpleHistogramDataset hist = new SimpleHistogramDataset("word count");
		hist.setAdjustForBinSize(false);
		final int tic = 5;
		for(int i = 0; i < 140; i += tic){
			if(i == 0)
				hist.addBin(new SimpleHistogramBin(i, i + tic, true, true)); //i==0�̏ꍇ�Alower bound���܂�
			else
				hist.addBin(new SimpleHistogramBin(i, i + tic, false, true));
		}
		
		//�W�v
		for (Status status : statuses) {
			String s = status.getText();
			hist.addObservation(s.length());
		}
		
		return ChartFactory.createXYBarChart("�����̒����̕��z", "�����̒���", false, "��", hist, PlotOrientation.VERTICAL, false, false, true);
		
	}
	
	/**
	 * �g�p�A�v���̏W�v
	 */
	public JFreeChart createUsedAppChart(){

		//collectStatuses("kimura6");
		//saveStatusList();
		
		
		int total = 0;
				
		//�n�b�V��
		HashMap h = new HashMap();
		
		//�W�v
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
			
			kv.addValue(appName + "  " + count + "��" , count);			
		}
		kv.sortByValues(org.jfree.util.SortOrder.DESCENDING);
				 
		return ChartFactory.createPieChart3D("�悭�g���A�v��", new DefaultPieDataset(kv), false, false, true);

	}
	
	
	/**
	 * status�̃��X�g���_�E�����[�h����
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
			
			
			
			//��肭�����Ȃ��B�B
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
	 * status�̃��X�g��ۑ�����
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
	 * status�̃��X�g��ǂݏo��
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
	 * Data���_�E�����[�h ���̃��\�b�h���ݎg���ĂȂ�
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
	 * Twitter������
	 */
	public void prepareForTwitter() {
		//keyChain����accessToken��ǂݎ���Ă���
		
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
//			if(sTemp.equals("")){//�܂��A�J�E���g���ݒ肳��ĂȂ��ꍇ
//				return;
//			}
//			
//			String [] strs = sTemp.split(" "); //�󔒂�token��token secret�𕪂��Ă���
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
	 * ���łɎ擾�����f�[�^�����邩
	 * @return
	 */
	public boolean isStatusListAvailable() {
		
		File f = new File(APPLICATION_SUPPORT_DIRECTORY_NAME, DATA_FILE_NAME + "default");
		return f.exists();		
		
	}

	
	/**
	 * ��͂̏������ł��Ă��邩 
	 */
	public boolean isStatsPrepared(){
		return statuses != null;
	}

	
	
	
	

	
	
}
