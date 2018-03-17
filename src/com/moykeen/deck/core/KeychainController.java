package com.moykeen.deck.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import twitter4j.auth.AccessToken;

/**
 * A class to access the OSX Keychain.
 * osx-keychain-java���AJarBundler��.app�������Ƃ��ɓ����Ȃ������̂ŁA
 * ���̃N���X(����сA�ʌɍ�����l�C�e�B�u�c�[��KeyChainContol)���g����Keychain�ւ̃A�N�Z�X���s���B
 * 
 * @author makoto 
 */
public class KeychainController {
	
	/**
	 * The path to the KeyChainControll
	 */
	private static final String toolPath;
	
	/**
	 * The single account Name
	 */
	private static final String SINGLE_ACCOUNT_NAME = "theSingleAccount";
	
	static{
		String pathA = "./KeyChainControl/KeyChainControl";
		String pathB = "./KeyChainControl/build/Debug/KeyChainControl";
		if(new File(pathA).exists()){
			toolPath = pathA;
		}else if(new File(pathB).exists()){
			toolPath = pathB;
		}else{
			toolPath = null;
			System.out.println("cannot find KeyChainControl. curent dir is " + new File(".").getAbsolutePath());
			System.exit(0);
		}
	}
	
	public KeychainController() {
	}
	

	/**
	 * add an access token.
	 */
	public void addAccessToken(AccessToken accessToken){
		try{
			Runtime rt = Runtime.getRuntime();
			Process process = null;
			String [] cmd = {toolPath, "-a", SINGLE_ACCOUNT_NAME, accessToken.getToken(), accessToken.getTokenSecret()};			
			process = rt.exec(cmd);
			
			//���O�̏o��
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			
			//wait for the process getting done
			process.waitFor();
			
			if(process.exitValue() != 0){//�ُ�I�������΂���
				throw new IOException();
			}
			
		}catch(InterruptedException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * remove the registered access token.
	 */
	public void removeAccessToken(){
		try{
			Runtime rt = Runtime.getRuntime();
			Process process = null;
			String [] cmd = {toolPath, "-r", SINGLE_ACCOUNT_NAME};			
			process = rt.exec(cmd);
			
			//���O�̏o��
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			
			//wait for the process getting done
			process.waitFor();
			
			if(process.exitValue() != 0){//�ُ�I�������΂���
				throw new IOException();
			}
			
		}catch(InterruptedException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * find a registered access token.
	 */
	public AccessToken findAccessToken(){
		AccessToken accessToken = null;
		try{
			Runtime rt = Runtime.getRuntime();
			Process process = null;
			String [] cmd = {toolPath, "-f", SINGLE_ACCOUNT_NAME};			
			process = rt.exec(cmd);
			
			//�W���o�͂ɃA�N�Z�X�g�[�N�����o�͂����̂ŁA�ߑ�����
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			
			//��s�ڂ����邱�ƂŁA�����ł������ǂ����킩��
			line = br.readLine();
			if(line.charAt(0) == '0'){
				process.waitFor();
				return null;
			}
				
			
			//���̍s�ɃA�N�Z�X�g�[�N���������Ă���
			line = br.readLine();
			String [] strs = line.split(" "); //�󔒂�token��token secret�𕪂��Ă���
			accessToken = new AccessToken(strs[0], strs[1]);
			
			//wait for the process getting done
			process.waitFor();
			
			if(process.exitValue() != 0){//�ُ�I�������΂���
				throw new IOException();
			}
			
		}catch(InterruptedException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return accessToken;
	}
	
	
	
}
