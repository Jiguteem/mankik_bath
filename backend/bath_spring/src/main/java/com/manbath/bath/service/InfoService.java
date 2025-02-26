
package com.manbath.bath.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.manbath.bath.entitiy.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.nashorn.api.scripting.URLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manbath.bath.DTO.ControlPostDTO;
import com.manbath.bath.DTO.HistoryPostDTO;
import com.manbath.bath.DTO.HistroyGetDTO;
import com.manbath.bath.DTO.InfoPostDTO;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.ControlRepository;
import com.manbath.bath.repository.InfoRepository;
import com.manbath.bath.repository.UserRepository;

import jakarta.persistence.TypedQuery;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Log4j2
@Service
public class InfoService {
	Map<String,Long> MapMap =new HashMap<>();

	@Autowired
	private InfoRepository infoRepository;
	
	@Autowired
	private BathRepository bathRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public Info findByBathid(String id){
		log.info("info findByBathid id :" + id );


		try {
			return infoRepository.getfindByBathid(bathRepository.findByBathid(id)).get(0);
		}catch (IndexOutOfBoundsException e){
			return new Info();
		}

	}
	
	@Transactional
	public Info saveByBathid (String id,String user_id,InfoPostDTO infodto) {
		log.info("info saveByBathid id :" + id );
		log.info("info saveByBathid user_id :" + user_id);
		infodto.setBath_id(id);
		//System.out.println(MapMap.get(infodto.getBath_id()) != infodto.getUID());

		System.out.println("*********************************************************************");
		System.out.println("**********************에기서 부터 api 작업 시작**************************");
		System.out.println("*********************************************************************");
		if(MapMap.get(infodto.getBath_id()) == null){

			System.out.println("*********************************************************************");
			System.out.println("**********************UID 최초 없을 때 동작**************************");
			System.out.println("*********************************************************************");
			//상황맞춰서 API 전달
			System.out.println("13233223232");
			String apiUrl = "https://mzoin.com/sMankik/mk.saveUserLog";
			String apiUrl_2 = "https://mzoin.com/sMankik/mk.saveContentsAndSendPush";
			HttpURLConnection urlConnection = null;
			HttpURLConnection urlConnection_2 = null;
			System.out.println("13233223232");
			try{
				System.out.println("13233223232");
				URL url = new URL(apiUrl);
				urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setConnectTimeout(500);
				urlConnection.setReadTimeout(100);
				urlConnection.setRequestProperty("Content-Type", "application/json;");
				urlConnection.setDoOutput(true);
				urlConnection.setInstanceFollowRedirects(true);

				OutputStream outputStream = urlConnection.getOutputStream();

				URL url_2 = new URL(apiUrl_2);
				urlConnection_2 = (HttpURLConnection)url_2.openConnection();
				urlConnection_2.setRequestMethod("POST");
				urlConnection_2.setConnectTimeout(500);
				urlConnection_2.setReadTimeout(100);
				urlConnection_2.setRequestProperty("Content-Type", "application/json;");
				urlConnection_2.setDoOutput(true);
				urlConnection_2.setInstanceFollowRedirects(true);

				OutputStream outputStream_2 = urlConnection_2.getOutputStream();

				User user = userRepository.getfindByUserid(user_id);


				if(infodto.getFan()== 3){
					System.out.println("fan number" + infodto.getFan());
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
//					bufferedWriter.write("{ \"userLog\":{\n" +
//							"        \"logType\":\"BCLEANS_\",\n" +
//							"        \"targetKey\":\"1\",\n" +
//							"        \"targetKind\":\"A\",\n" + user.getUserid() +
//							"        \"frUserKey\":\" " +
//							"         \",\n" +
//							"        \"frUserText\":\"" + user.getUsername() +
//							"         \"\n" +
//							"    }" +
//							"}");

					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCLEANS_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 4) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCLEANE_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 5) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCAPO___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 6) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCAPC___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 7) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCOLDO__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 8) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCOLDC__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 9) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BHOTO___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 10) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BHOTC___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 11) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BREGIST_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 12) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHS__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);

					bufferedWriter.flush();

					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 준비 시작\",\n" +
									"        \"bodyFullStr\":\"지금 목욕 준비를 시작 해요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 13) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHF__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();

					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 준비 시작\",\n" +
									"        \"bodyFullStr\":\"지금 목욕 준비를 시작 해요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 14) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHE__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 종료 \",\n" +
									"        \"bodyFullStr\":\"지금 목욕이 종료 되었어요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 15) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCANCEL_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}


			}catch (Exception e){
				System.out.println(e.toString());
			}
			try {
				System.out.println(urlConnection.getResponseCode());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//UID 변경
			MapMap.put(infodto.getBath_id(), infodto.getUID());
		}else if(!(MapMap.get(infodto.getBath_id())==infodto.getUID())){
			//상황맞춰서 API 전달
			System.out.println("*********************************************************************");
			System.out.println("**********************UID 기존이랑 다를때 동작**************************");
			System.out.println("*********************************************************************");
			System.out.println("13233223232");
			String apiUrl = "https://mzoin.com/sMankik/mk.saveUserLog";
			HttpURLConnection urlConnection = null;

			String apiUrl_2 = "https://mzoin.com/sMankik/mk.saveContentsAndSendPush";
			HttpURLConnection urlConnection_2 = null;
			System.out.println("13233223232");
			try{
				System.out.println("13233223232");
				URL url = new URL(apiUrl);
				urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setConnectTimeout(500);
				urlConnection.setReadTimeout(100);
				urlConnection.setRequestProperty("Content-Type", "application/json;");
				urlConnection.setDoOutput(true);
				urlConnection.setInstanceFollowRedirects(true);

				OutputStream outputStream = urlConnection.getOutputStream();

				URL url_2 = new URL(apiUrl_2);
				urlConnection_2 = (HttpURLConnection)url_2.openConnection();
				urlConnection_2.setRequestMethod("POST");
				urlConnection_2.setConnectTimeout(500);
				urlConnection_2.setReadTimeout(100);
				urlConnection_2.setRequestProperty("Content-Type", "application/json;");
				urlConnection_2.setDoOutput(true);
				urlConnection_2.setInstanceFollowRedirects(true);

				OutputStream outputStream_2 = urlConnection_2.getOutputStream();

				User user = userRepository.getfindByUserid(user_id);
				if(infodto.getFan()== 3){
					System.out.println("fan number" + infodto.getFan());
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
//					bufferedWriter.write("{ \"userLog\":{\n" +
//							"        \"logType\":\"BCLEANS_\",\n" +
//							"        \"targetKey\":\"1\",\n" +
//							"        \"targetKind\":\"A\",\n" + user.getUserid() +
//							"        \"frUserKey\":\" " +
//							"         \",\n" +
//							"        \"frUserText\":\"" + user.getUsername() +
//							"         \"\n" +
//							"    }" +
//							"}");

					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCLEANS_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 4) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCLEANE_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 5) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCAPO___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 6) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCAPC___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 7) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCOLDO__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 8) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCOLDC__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 9) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BHOTO___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 10) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BHOTC___\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 11) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BREGIST_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}else if (infodto.getFan() == 12) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHS__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();

					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 준비 시작\",\n" +
									"        \"bodyFullStr\":\"지금 목욕 준비를 시작 해요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 13) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHF__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 준비 시작\",\n" +
									"        \"bodyFullStr\":\"지금 목욕 준비를 시작 해요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 14) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BBATHE__\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
					BufferedWriter bufferedWriter_2 = new BufferedWriter(new OutputStreamWriter(outputStream_2,"UTF-8"));
					bufferedWriter_2.write(
							"{\n" +
									"    \"contents\":{\n" +
									"        \"title\":\"목욕 종료 \",\n" +
									"        \"bodyFullStr\":\"지금 목욕이 종료 되었어요\",\n" +
									"        \"actorList\":[{\"accessKind\":\"U\",\"accessKey\":\""+user.getUserid() +"\"}],\n" +
									"        \"creAppKey\":\"1\",\n" +
									"        \"creUserKey\":\""+user.getUserid()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter_2.flush();
				}else if (infodto.getFan() == 15) {
					BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
					bufferedWriter.write(
							"{\n" +
									"    \"userLog\":{\n" +
									"        \"logType\":\"BCANCEL_\",\n" +
									"        \"targetKey\":\"1\",\n" +
									"        \"targetKind\":\"A\",\n" +
									"        \"frUserKey\":\""+user.getUserid() +"\",\n" +
									"        \"frUserText\":\""+user.getUsername()+"\"\n" +
									"    }\n" +
									"}"
					);
					bufferedWriter.flush();
				}
			}catch (Exception e){
				System.out.println(e.toString());
			}
			try {
				System.out.println(urlConnection.getResponseCode());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			//UID 변경
			MapMap.put(infodto.getBath_id(), infodto.getUID());
		}else{
			System.out.println("*********************************************************************");
			System.out.println("**********************UID 기존이랑 같을때 동작**************************");
			System.out.println("*********************************************************************");
		}

		Info info = new Info();

		Bath bath = bathRepository.findByBathid(infodto.getBath_id());


		info.setBathid(bath);
		info.setTemp(infodto.getTemp());
		info.setLevel(infodto.getLevel());
		info.setCap(infodto.getCap());
		info.setH_valve(infodto.getH_valve());
		info.setC_valve(infodto.getC_valve());
		info.setFan(infodto.getFan());
		info.setHeat(infodto.getHeat());
		info.setClean_valve(infodto.getClean_valve());
		info.setState(infodto.getState());

		bath.setTemp(infodto.getTemp());
		bath.setLevel(infodto.getLevel());
		bath.setState(infodto.getState());
		bath.setCap(infodto.getCap());
		bath.setC_valve(infodto.getC_valve());
		bath.setH_valve(infodto.getH_valve());
		bath.setClean_valve(infodto.getClean_valve());


		bathRepository.save(bath);

		return infoRepository.save(info);
	}


}
