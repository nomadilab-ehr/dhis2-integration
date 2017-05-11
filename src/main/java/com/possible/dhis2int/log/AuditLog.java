package com.possible.dhis2int.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.possible.dhis2int.Properties;

@Service
public class AuditLog {
	
	private final File logFile;
	
	private PrintWriter writer;
	
	private final String HEADER = "Event,Time,User,Status,DataFile";
	
	private final String FILE_NAME = "'dhis_submission_log' dd-MM-yyyy HH-mm'.csv'";
	
	@Autowired
	public AuditLog(Properties properties) throws IOException {
		logFile = new File(properties.auditLogFileName);
		writer = new PrintWriter(new FileWriter(logFile,true), true);
		ensureHeaderExists();
	}
	
	public String getFileNameTimeStamp(){
		return DateTimeFormat.forPattern(FILE_NAME).print(new DateTime());
	}

	public FileSystemResource getFile() {
		return new FileSystemResource(logFile);
	}
	
	private void ensureHeaderExists() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
		String firstLine = bufferedReader.readLine();
		if(firstLine == null || firstLine.isEmpty()){
			writer.println(HEADER);
		}
		bufferedReader.close();
	}
	
	public void failure(String event, String userId, String dataSent){
		writer.println(new Record(event,new Date(),userId, Status.Failure,dataSent));
	}
	
	public void success(String event, String userId, String dataSent){
		writer.println(new Record(event,new Date(),userId, Status.Success,dataSent));
	}
	
	public enum Status {
		Success,
		Failure
	}
	
	public static class Record {
		String event;
		Date time;
		String userId;
		Status status;
		String dataFile;
		
		public Record(String event, Date time, String userId, Status status, String dataFile) {
			this.event = event;
			this.time = time;
			this.userId = userId;
			this.status = status;
			this.dataFile = dataFile;
		}
		
		@Override
		public String toString() {
			return event + ',' + time + ',' + userId + ',' + status + ',' + dataFile;
		}
	}
}
