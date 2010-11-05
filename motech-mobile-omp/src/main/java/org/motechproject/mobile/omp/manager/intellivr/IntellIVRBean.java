/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.mobile.omp.manager.intellivr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.omp.manager.GatewayManager;
import org.motechproject.mobile.omp.manager.GatewayMessageHandler;
import org.motechproject.mobile.omp.manager.utils.MessageStatusStore;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central class for handling the interaction with the Intell IVR system.  Provides several functions.
 *  
 * {@link GatewayManager} to handle the bundling and sending of {@link MessageRequest} to users.
 * {@link GetIVRConfigRequest} to handle requests from the IVR system for content for inbound callers
 * {@link IVRCallRequester} to handle placing requests for calls with the ivr system.  
 * {@link IVRCallSessionProcessor} to handle the initial lifecycle of an {@link IVRCallSession} up to the point the call is requested.
 * {@link ReportHandler} to handle reports from the ivr system about completed calls.    
 * {@link IVRCallStatsProvider} to provider data to be used for operational reporting.
 * @author fcbrooks
 *
 */
public class IntellIVRBean implements GatewayManager, GetIVRConfigRequestHandler, IVRCallRequester, IVRCallSessionProcessor, IVRCallStatsProvider, ReportHandler {

	private GatewayMessageHandler messageHandler;
	protected String reportURL;
	private String apiID;
	private String method;
	private String defaultLanguage;
	private String defaultTree;
	private String defaultReminder;
	private IntellIVRServer ivrServer;
	private MessageStatusStore statusStore;
	protected Map<Long, IVRNotificationMapping> ivrNotificationMap;
	protected Map<String, Long> ivrReminderIds;
	private long bundlingDelay;
	private int retryDelay;
	private int maxAttempts;
	private int maxDays;
	private int availableDays;
	private int callCompletedThreshold;
	private int preReminderDelay;
	private boolean accelerateRetries;
	private String noPendingMessagesRecordingName;
	private String welcomeMessageRecordingName;
	private Resource mappingResource;
	private CoreManager coreManager;
	private RegistrarService registrarService;
	private IVRDAO ivrDao;
	private IVRCallRequester ivrCallRequester;

	private Log log = LogFactory.getLog(IntellIVRBean.class);
	private Log reportLog = LogFactory.getLog(IntellIVRBean.class.getName() + ".reportlog");
	private Log callLog = LogFactory.getLog(IntellIVRBean.class.getName() + ".calllog");

	@Transactional
	public void init() {

		ivrNotificationMap = new HashMap<Long, IVRNotificationMapping>();
		ivrReminderIds = new HashMap<String, Long>();

		try {

			File f = mappingResource.getFile();

			log.debug("Looking for Notification to IVR entity mappings in " + f.getName());

			BufferedReader br = new BufferedReader(new FileReader(f));

			Pattern p = Pattern.compile("([0-9]+)=([IiRr]{1}),(.+)");
			Matcher m;

			String line = "";

			while ( (line = br.readLine()) != null ) {

				m = p.matcher(line);

				if ( m.matches() ) {

					long mapID = Long.parseLong(m.group(1));
					String ivrType = m.group(2).toUpperCase();
					String ivrEntity = m.group(3);

					log.debug("Found IVR entity mapping: " + mapID + " => " + ivrType + "," + ivrEntity);

					IVRNotificationMapping i = new IVRNotificationMapping();
					i.setId(mapID);
					i.setType(ivrType);
					i.setIvrEntityName(ivrEntity);
					ivrNotificationMap.put(mapID, i);

					ivrReminderIds.put(ivrEntity, mapID);

				}

			}

		} catch (IOException e) {
			log.error("IOException creating IVR to Notification Map - default tree and message will be used");
		}

		if ( accelerateRetries ) {
			log.warn("Using accelerated retries.  Configured retry intervals will be ignored.");
			retryDelay = 1;
		}

	}

	public void cleanUp() {}

	public String getMessageStatus(GatewayResponse response) {
		log.debug("Returning " + statusStore.getStatus(response.getGatewayMessageId()) + " for " + response.getId());
		return statusStore.getStatus(response.getGatewayMessageId());
	}

	public MStatus mapMessageStatus(GatewayResponse response) {
		log.debug("Returning " + messageHandler.lookupStatus(response.getResponseText()) + " for " + response.getId());
		return messageHandler.lookupStatus(response.getResponseText());
	}

	/**
	 * Method for the core mobile server to request a message be delivered to a user of the IVR system.
	 * 
	 * Messages will take at least as long as the {@link #getBundlingDelay()} to be sent after a call to this method.  
	 * 
	 * When a request is received for a user at a particular phone number, the bean will wait up to the bundling
	 * delay for other messages for that user at that phone number before triggering a call from the IVR system.  This 
	 * is to compensate for the lack of message bundling in the underlying system.
	 * 
	 */
	@Transactional
	public Set<GatewayResponse> sendMessage(GatewayRequest gatewayRequest) {
		Set<GatewayResponse> responses = null;
		try {
			log.debug("Received GatewayRequest:" + gatewayRequest);
	
			String recipientID = gatewayRequest
				.getMessageRequest()
				.getRecipientId();
	
			String phone = gatewayRequest
				.getRecipientsNumber();
	
			String status = StatusType.OK.value();
			if ( recipientID == null 
					|| phone == null 
					|| gatewayRequest.getMessageRequest().getMessageType() == MessageType.TEXT ) {
				status = StatusType.ERROR.value();
			} else {
	
				String phoneType = gatewayRequest
				.getMessageRequest()
				.getPhoneNumberType();
	
				if ( phoneType.equalsIgnoreCase("PERSONAL") || phoneType.equalsIgnoreCase("HOUSEHOLD") ) {
					
					Language language = gatewayRequest
					.getMessageRequest()
					.getLanguage();
					
					Integer[] openState = { IVRCallSession.OPEN };
					List<IVRCallSession> existingSessions = ivrDao.loadIVRCallSessions(recipientID, phone, language.getName(), openState, 0, 0, IVRCallSession.OUTBOUND);
					
					if ( existingSessions.size() == 0 ) {
						Date now = new Date();
						Date bundlingExpiration = addToDate(now, GregorianCalendar.MILLISECOND, (int)bundlingDelay);
						IVRCallSession session = new IVRCallSession(recipientID, phone, language.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, now, bundlingExpiration);						
						session.getMessageRequests().add(gatewayRequest.getMessageRequest());
						ivrDao.saveIVRCallSession(session);
						log.debug("Created session " + session);
					} else {
						existingSessions.get(0).getMessageRequests().add(gatewayRequest.getMessageRequest());
						log.debug("Using session " + existingSessions.get(0));
					}
					
				} else {
					log.debug("GatewayRequest " + gatewayRequest.getId() + " has phone type " +
							gatewayRequest.getMessageRequest().getPhoneNumberType() +
							".  Call will not be made and message will remain pending.");
				}
	
			}
	
			responses = messageHandler
				.parseMessageResponse(gatewayRequest, status);
	
			for ( GatewayResponse response : responses )
				statusStore.updateStatus(response.getGatewayMessageId(),
						response.getResponseText());
			
		} catch (Exception e) {
			log.error("Error scheduling intellIVR call", e);
		}
		return responses;
	}

	/**
	 * Queries database for the {@link IVRCallSession} in OPEN state with nextAttempt before 
	 * the current time.  Changes them to SEND_WAIT state.
	 */
	@Transactional
	public void processOpenSessions() {
		
		log.debug("Start processing OPEN IVRCallSessions");
		
		Integer[] states = { IVRCallSession.OPEN };
		
		List<IVRCallSession> sessions = ivrDao.loadIVRCallSessionsByStateNextAttemptBeforeDate(states, new Date());
		
		for ( IVRCallSession session : sessions) {
			log.debug("Changing to SEND_WAIT state session " + session);
			session.setState(IVRCallSession.SEND_WAIT);
		}
		
		log.debug("End processing OPEN IVRCallSessions");
		
	}

	/**
	 * Queries database for {@link IVRCallSession} in SEND_WAIT state with nextAttempt 
	 * before the current time.  Passed request to the {@link IVRCallRequester} interface
	 * implementation.
	 */
	@Transactional
	public void processWaitingSessions() {
		
		log.debug("Start processing SEND_WAIT IVRCallSessions");
		
		Integer[] states = { IVRCallSession.SEND_WAIT };
		
		List<IVRCallSession> sessions = ivrDao.loadIVRCallSessionsByStateNextAttemptBeforeDate(states, new Date());

		for ( IVRCallSession session : sessions )
			ivrCallRequester.requestCall(session, UUID.randomUUID().toString());
		
		log.debug("End processing SEND_WAIT IVRCallSessions");
		
	}

	/**
	 * Places request with {@link IntellIVRServer} to place the call.  If the server returns
	 * a success code the {@link IVRCallSession} is placed into a REPORT_WAIT to await the 
	 * report of the completed call.  If the server returns an error the {@link IVRCallSession}
	 * is set to a CLOSED state
	 */
	@Transactional
	public void requestCall(IVRCallSession session, String externalId) {
		
		log.debug("Received request to place call for session id " + session.getId() + " using external id " + externalId);
		
		try {

			log.debug("Requesting call for session: " + session);
			
			//format the request
			RequestType request = createIVRRequest(session, externalId);
			
			//request the call
			ResponseType response = ivrServer.requestCall(request);
			
			//parse the response
			String status = response.getStatus() == StatusType.OK ? StatusType.OK.value() : response.getErrorCode().value();
			
			//update the local status store
			for (MessageRequest messageRequest : session.getMessageRequests())
				statusStore.updateStatus(messageRequest.getId().toString(), status);

			//if response was OK wait for report, if not close the session and take no further action
			if ( response.getStatus() == StatusType.OK ) {
				session.setState(IVRCallSession.REPORT_WAIT);
				session.setAttempts(session.getAttempts()+1);
				IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
				session.getCalls().add(call);
			} else {
				session.setState(IVRCallSession.CLOSED);
				session.setAttempts(session.getAttempts()+1);
				IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.APIERROR, response.getErrorCode().name() + "(" + response.getErrorCode().value() + ")", session);
				session.getCalls().add(call);
			}
			
			/*
			 * format and write log message
			 */
			StringBuilder requestIds = new StringBuilder();
			StringBuilder notificationIDs = new StringBuilder();
			boolean firstRequest = true;
			
			for ( MessageRequest mRequest : session.getMessageRequests() ) {
				if ( firstRequest )
					firstRequest = false;
				else {
					requestIds.append("|");
					notificationIDs.append("|");
				}
				requestIds.append(mRequest.getId());
				notificationIDs.append(mRequest.getNotificationType().getId().toString());
			}
			
			StringBuilder reminders = new StringBuilder();
			boolean firstReminder = true;
			
			for ( Object o : request.getVxml().getPrompt().getAudioOrBreak() ) {
				if ( o instanceof AudioType ) {
					if ( firstReminder )
						firstReminder = false;
					else
						reminders.append("|");
					reminders.append(((AudioType)o).getSrc());
				}
			}
			
			callLog.info("OUT," +
					session.getPhone() + "," +
					session.getUserId() + "," +
					status + "," +
					externalId + "," +
					requestIds.toString() + "," +
					notificationIDs.toString() + "," +
					request.getTree() + "," + 
					reminders.toString());
			
		} catch (Exception e) {
			log.error("Error sending intellIVR call", e);
		}
		
	}
	
	/**
	 * Method to request a {@link RequestType} instance based on the content of the 
	 * {@link IVRCallSession} instance and the provided externalId
	 * @param session
	 * @param externalId
	 * @return
	 */
	public RequestType createIVRRequest(IVRCallSession session, String externalId) {
		
		Set<MessageRequest> messageRequests = session.getMessageRequests();

		log.debug("Creating IVR Request for " + messageRequests);

		RequestType ivrRequest = new RequestType();

		/*
		 * These first three values are fixed
		 */
		ivrRequest.setApiId(apiID);
		ivrRequest.setMethod(method);
		ivrRequest.setReportUrl(reportURL);
		
		/*
		 * recipient's phone number
		 */
		ivrRequest.setCallee(session.getPhone());

		/*
		 * Set language
		 */
		String language = session.getLanguage();
		ivrRequest.setLanguage(language != null ? language : defaultLanguage);

		/*
		 * Private id
		 */
		ivrRequest.setPrivate(externalId);
		
		/*
		 * Create the content
		 */
		MessageRequest infoRequest = null;
		List<String> reminderMessages = new ArrayList<String>();
		for (MessageRequest messageRequest : messageRequests) {

			long notificationId = messageRequest.getNotificationType().getId();

			if ( !ivrNotificationMap.containsKey(notificationId) ) {
				log.debug("No IVR Notification mapping found for " + notificationId);
			} else {

				IVRNotificationMapping mapping = ivrNotificationMap.get(notificationId);

				if ( mapping.getType().equalsIgnoreCase(IVRNotificationMapping.INFORMATIONAL)) {
					if ( infoRequest == null )
						infoRequest = messageRequest;
					else {
						GregorianCalendar currDateFrom = new GregorianCalendar();
						currDateFrom.setTime(infoRequest.getDateFrom());
						GregorianCalendar possibleDateFrom = new GregorianCalendar();
						possibleDateFrom.setTime(messageRequest.getDateFrom());
						if ( currDateFrom.before(possibleDateFrom) )
							infoRequest = messageRequest;
					}

				} else {
					reminderMessages.add(mapping.getIvrEntityName());
				}

			}

		}

		if ( infoRequest != null ) {
			IVRNotificationMapping infoMapping = ivrNotificationMap
			.get(infoRequest
					.getNotificationType()
					.getId());
			ivrRequest.setTree(infoMapping.getIvrEntityName());
		}

		RequestType.Vxml vxml = new RequestType.Vxml();
		vxml.setPrompt(new RequestType.Vxml.Prompt());

		/*
		 * add a break element if the preReminderDelay is > 0
		 */
		if ( preReminderDelay > 0 ) {
			BreakType breakType = new BreakType();
			breakType.setTime(Integer.toString(preReminderDelay) + "s");
			vxml.getPrompt()
				.getAudioOrBreak()
				.add(breakType);
		}
		
		/*
		 * add a welcome message if an outbound call and a recording name has been configured
		 */
		if ( session.getCallDirection().equalsIgnoreCase(IVRCallSession.OUTBOUND) 
				&& welcomeMessageRecordingName != null
				&& welcomeMessageRecordingName.trim().length() > 0 ) {
			AudioType welcome = new AudioType();
			welcome.setSrc(welcomeMessageRecordingName);
			vxml.getPrompt()
				.getAudioOrBreak()
				.add(welcome);
		}

		/*
		 * add the reminder messages
		 */
		for (String fileName : reminderMessages) {
			AudioType audio = new AudioType();
			audio.setSrc(fileName);
			vxml.getPrompt()
				.getAudioOrBreak()
				.add(audio);
		}
		ivrRequest.setVxml(vxml);

		return ivrRequest;
		
	}
	
	@Transactional
	public ResponseType handleRequest(GetIVRConfigRequest request) {
		return handleRequest(request, UUID.randomUUID().toString());
	}

	/**
	 * Handles the request from the IVR system for content for incoming callers
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public ResponseType handleRequest(GetIVRConfigRequest request, String externalId) {

		ResponseType r = new ResponseType();
		String userId = request.getUserid();
	
		log.info("Received ivr config request for id " + userId);

		try {

			String[] enrollments = registrarService.getPatientEnrollments(Integer.parseInt(userId));

			if ( enrollments == null || enrollments.length == 0 ) {
				callLog.info("IN,," + request.getUserid() + ",UNENROLLED");
				r.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
				r.setErrorString("Unenrolled user");
				r.setStatus(StatusType.ERROR);
			} else {

				MessageRequestDAO<MessageRequest> mrDAO = coreManager.createMessageRequestDAO();

				Date endDate = new Date();
				Date startDate = addToDate(endDate, GregorianCalendar.DAY_OF_MONTH, (int)availableDays*-1);
				
				List<MessageRequest> pendingMessageRequests = mrDAO.getMsgRequestByRecipientDateFromBetweenDates(request.getUserid(), startDate, endDate);

				IVRCallSession session = new IVRCallSession(userId, null, null, IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, new Date(), null);

				IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Client called IVR system", session);
				session.getCalls().add(call);
				
				if ( pendingMessageRequests.size() == 0 ) {
					log.debug("No pending messages found for " + request.getUserid());
					callLog.info("IN,," + request.getUserid() + ",NO_PENDING");
					r.setStatus(StatusType.OK);
					RequestType.Vxml vxml = new RequestType.Vxml();
					vxml.setPrompt(new RequestType.Vxml.Prompt());
					AudioType a = new AudioType();
					a.setSrc(noPendingMessagesRecordingName.trim());
					vxml.getPrompt().getAudioOrBreak().add(a);
					r.setVxml(vxml);
					r.setReportUrl(reportURL);
					r.setPrivate(externalId);
				} else {

					log.debug("Found pending messages for " + request.getUserid() + ": " + pendingMessageRequests);

					for (MessageRequest messageRequest : pendingMessageRequests ) {
						
						session.getMessageRequests().add(messageRequest);

						statusStore.updateStatus(messageRequest.getId().toString(), StatusType.OK.value());

					}

					/*
					 * ResponseType fields are a subset of the RequestType fields
					 * Can create a RequestType based on this criteria and use
					 * only the fields that are needed to create the ResponseType
					 */
					RequestType requestType = createIVRRequest(session,externalId);

					r.setPrivate(requestType.getPrivate());
					r.setReportUrl(requestType.getReportUrl());
					r.setStatus(StatusType.OK);
					r.setTree(requestType.getTree());
					r.setVxml(requestType.getVxml());

					StringBuilder notificationIDs = new StringBuilder();
					boolean firstRequest = true;
					
					for ( MessageRequest messageRequest : session.getMessageRequests() ) {
						if ( firstRequest )
							firstRequest = false;
						else 
							notificationIDs.append("|");
						notificationIDs.append(messageRequest.getNotificationType().getId().toString());
					}
					
					StringBuilder reminders = new StringBuilder();
					boolean firstReminder = true;
					
					for ( Object o : r.getVxml().getPrompt().getAudioOrBreak() ) {
						if ( o instanceof AudioType ) {
							if ( firstReminder )
								firstReminder = false;
							else
								reminders.append("|");
							reminders.append(((AudioType)o).getSrc());
						}
					}
					
					callLog.info("IN,," +
									request.getUserid() + "," +
									StatusType.OK.value() + "," +
									call.getExternalId() + ",," +
									notificationIDs.toString() + "," +
									r.getTree() + "," + 
									reminders.toString());

				}

				ivrDao.saveIVRCallSession(session);

			}

		} catch (NumberFormatException e) {
			log.error("Invalid user id: id must be numeric");
			callLog.info("IN,," + request.getUserid() + "," + ErrorCodeType.MOTECH_INVALID_USER_ID.name());
			r.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
			r.setErrorString("Invalid user id: id must be numeric");
			r.setStatus(StatusType.ERROR);
		} catch (ValidationException e) {
			log.error("Invalid user id: no such id '" + userId + "' on server");
			callLog.info("IN,," + request.getUserid() + "," + ErrorCodeType.MOTECH_INVALID_USER_ID.name());
			r.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
			r.setErrorString("Invalid user id: no such id '" + userId + "' on server");
			r.setStatus(StatusType.ERROR);
		} finally {
			
		}

		return r;
	}

	/**
	 * Handles reports detailing the results of calls placed or received by the IVR system
	 * {@link IVRCallSession} and {@link IVRCall} instances are updated based on the data
	 * received. 
	 */
	@Transactional
	public ResponseType handleReport(ReportType report) {
		log.info("Received call report: " + report.toString());

		List<String> messages = formatReportLogMessages(report);
		for ( String message : messages )
			reportLog.info(message);

		//private field contains the external id specified in the original request
		String externalId = report.getPrivate();

		if ( externalId == null )
			log.error("Unable to identify call in report: " + externalId);
		else {
			
			//look up the call
			IVRCall call = ivrDao.loadIVRCallByExternalId(externalId);

			if ( call == null ) {
				log.error("Unable to find IVRCall with external id " + externalId);
			} else {

				//update the call's fields
				call.setConnected(toDate(report.getConnectTime()));
				call.setDisconnected(toDate(report.getDisconnectTime()));
				call.setDuration(report.getDuration());
				call.setStatus(toIvrCallStatus(report.getStatus()));
				call.setStatusReason("");
				
				//add menu entries to the database
				for ( IvrEntryType entry : report.getINTELLIVREntry() ) {
					IVRMenu menu = new IVRMenu(
									entry.getMenu() 		== null ? null : entry.getMenu(), 
									entry.getEntrytime()	== null ? null : toDate(entry.getEntrytime()), 
									entry.getDuration(), 
									entry.getKeypress() 	== null ? null : entry.getKeypress(), 
									entry.getFile() 		== null ? null : entry.getFile()
											);
					call.getMenus().add(menu);
				}
				
				String status = report.getStatus().value();
				IVRCallSession session = call.getSession();
				
				if ( session == null ) {
					log.error("Unable to find IVRCallSession for IVRCall with external id " + externalId);
				} else {

					/*
					 * Retry if necessary
					 */
					if ( report.getStatus() == ReportStatusType.COMPLETED && callExceedsThreshold(session,report) ) {
						//Success.  Call was complete and over the required call time threshold.
						session.setState(IVRCallSession.CLOSED);
					} else {

						if ( session.getCallDirection().equalsIgnoreCase(IVRCallSession.INBOUND) ) {
							//can't retry or update status for failed inbound calls.  
							status = null;
							session.setState(IVRCallSession.CLOSED);
						} else {
							
							//set to SEND_WAIT to be retried
							session.setState(IVRCallSession.SEND_WAIT);
							
							//if the status is completed it must have been below the call time threshold
							if ( report.getStatus() == ReportStatusType.COMPLETED ) {
								call.setStatus(IVRCallStatus.BELOWTHRESHOLD);
								status = "BELOWTHRESHOLD";
							}

							//check the number of attempts today
							if ( session.getAttempts() < this.maxAttempts ) {
								//try again after the retry delay
								session.setNextAttempt(addToDate(session.getNextAttempt(), GregorianCalendar.MINUTE, retryDelay));
							} else {

								//all attempts for this day have been exhausted
								session.setDays(session.getDays() + 1);
								session.setAttempts(0);

								//check the number of days attempted
								if ( session.getDays() < this.maxDays ) {

									//have not exhausted days.  try again tomorrow
									//acceletateRetries is a debug option to speed up next day retries to the same day
									if ( accelerateRetries )
										session.setNextAttempt(addToDate(new Date(), GregorianCalendar.MINUTE, 1));
									else
										session.setNextAttempt(addToDate(session.getCreated(), GregorianCalendar.DAY_OF_MONTH, 1));
									
								} else {
									//all attempts for all days have been exhausted
									session.setState(IVRCallSession.CLOSED);
									status = "MAXATTEMPTS";
								}

							}

						}

					}

					/*
					 * Update message status
					 */
					if ( status != null ) {
						Collection<MessageRequest> requests = session.getMessageRequests();
						for (MessageRequest messageRequest : requests) {

							log.debug("Updating Message Request "
									+ messageRequest.getId().toString()
									+ " to " + status);
							statusStore.updateStatus(messageRequest
									.getId()
									.toString()
									, status);

						}

					}

				}
				
			}

		}

		ResponseType r = new ResponseType();
		r.setStatus(StatusType.OK);
		return r;
	}

	/**
	 * Contains logic for a call being over the threshold for a completed calls.
	 * Basically if the first non-reminder message is greater than the callCompleteThreshold value
	 * it is considered complete.  
	 * @param session
	 * @param report
	 * @return
	 */
	protected boolean callExceedsThreshold(IVRCallSession session, ReportType report) {

		int effectiveCallTime = 0;
		int reminderCount = 0;
		boolean shouldHaveInformationalMessage = false;

		//get the message request and determine if we should expect to find a informational message in the report
		//if there is no informational message then the first non-reminder logic does not apply
		for ( MessageRequest request : session.getMessageRequests() ) {
			long notificationId = request.getNotificationType().getId();
			if ( ivrNotificationMap.containsKey(notificationId) )
				if ( ivrNotificationMap.get(notificationId).getType().equalsIgnoreCase(IVRNotificationMapping.INFORMATIONAL) )
					shouldHaveInformationalMessage = true;
		}

		//to hold a reference to the first non-reminder
		IvrEntryType firstInfoEntry = null;

		//get the list of recording that were heard
		List<IvrEntryType> entries = report.getINTELLIVREntry();

		//iterate.  increment counter for reminder messages.  identify the first non-reminder 
		for (IvrEntryType entry : entries)
			if ( ivrReminderIds.containsKey(entry.getMenu()) || entry.getMenu().equalsIgnoreCase("break") || entry.getMenu().equalsIgnoreCase(welcomeMessageRecordingName) )
				reminderCount++;
			else
				if ( firstInfoEntry == null && ( session.getCallDirection().equalsIgnoreCase(IVRCallSession.OUTBOUND) || reminderCount > 0) )
					firstInfoEntry = entry;

		if ( firstInfoEntry == null )//did not find first non-reminder
			if ( shouldHaveInformationalMessage )//there should have been one
				effectiveCallTime = 0;//insure it is below threshold
			else
				if ( reminderCount > 0 )//no info message expected but no reminders play either
					effectiveCallTime = callCompletedThreshold;//say it was over threshold
				else
					effectiveCallTime = report.getDuration();//fall back to the duration of the entire call
		else
			effectiveCallTime = firstInfoEntry.getDuration();//duration of the first non-reminder

		return effectiveCallTime >= callCompletedThreshold;
	}
	
	@Transactional
	public int getCountIVRCallSessions() {
		return ivrDao.countIVRCallSesssions();
	}
	
	@Transactional
	public int getCountIVRSessionsInLastMinutes(int minutes) {
		minutes = minutes < 0 ? 0 : minutes;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.MINUTE, (int)minutes*(-1));
		return ivrDao.countIVRCallSessionsCreatedBetweenDates(start, end);
	}

	@Transactional
	public int getCountIVRCallSessionsInLastHours(int hours) {
		hours = hours < 0 ? 0 : hours;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.HOUR_OF_DAY, (int)hours*(-1));
		return ivrDao.countIVRCallSessionsCreatedBetweenDates(start, end);
	}

	@Transactional
	public int getCountIVRCallSessionsInLastDays(int days) {
		days = days < 0 ? 0 : days;
		GregorianCalendar end = new GregorianCalendar();
		GregorianCalendar lastMidnight = new GregorianCalendar(
				end.get(GregorianCalendar.YEAR), 
				end.get(GregorianCalendar.MONTH),
				end.get(GregorianCalendar.DAY_OF_MONTH));
		Date start = addToDate(lastMidnight.getTime(), GregorianCalendar.DAY_OF_MONTH, (int)(days-1)*(-1));
		return ivrDao.countIVRCallSessionsCreatedBetweenDates(start, end.getTime());
	}
	
	@Transactional
	public int getCountIVRCalls() {
		return ivrDao.countIVRCalls();
	}
	
	@Transactional
	public int getCountIVRCallsInLastMinutes(int minutes) {
		minutes = minutes < 0 ? 0 : minutes;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.MINUTE, (int)minutes*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDates(start, end);
	}
	
	@Transactional
	public int getCountIVRCallsInLastHours(int hours) {
		hours = hours < 0 ? 0 : hours;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.HOUR_OF_DAY, (int)hours*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDates(start, end);
	}
	
	@Transactional
	public int getCountIVRCallsInLastDays(int days) {
		days = days < 0 ? 0 : days;
		GregorianCalendar end = new GregorianCalendar();
		GregorianCalendar lastMidnight = new GregorianCalendar(
				end.get(GregorianCalendar.YEAR), 
				end.get(GregorianCalendar.MONTH),
				end.get(GregorianCalendar.DAY_OF_MONTH));
		Date start = addToDate(lastMidnight.getTime(), GregorianCalendar.DAY_OF_MONTH, (int)(days-1)*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDates(start, end.getTime());
	}
	
	@Transactional
	public int getCountIVRCallsWithStatus(IVRCallStatus status) {
		return ivrDao.countIVRCallsWithStatus(status);
	}
	
	@Transactional
	public int getCountIVRCallsInLastMinutesWithStatus(int minutes, IVRCallStatus status) {
		minutes = minutes < 0 ? 0 : minutes;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.MINUTE, (int)minutes*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDatesWithStatus(start, end, status);
	}
	
	@Transactional
	public int getCountIVRCallsInLastHoursWithStatus(int hours, IVRCallStatus status) {
		hours = hours < 0 ? 0 : hours;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.HOUR_OF_DAY, (int)hours*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDatesWithStatus(start, end, status);
	}
	
	@Transactional
	public int getCountIVRCallsInLastDaysWithStatus(int days, IVRCallStatus status) {
		days = days < 0 ? 0 : days;
		GregorianCalendar end = new GregorianCalendar();
		GregorianCalendar lastMidnight = new GregorianCalendar(
				end.get(GregorianCalendar.YEAR), 
				end.get(GregorianCalendar.MONTH),
				end.get(GregorianCalendar.DAY_OF_MONTH));
		Date start = addToDate(lastMidnight.getTime(), GregorianCalendar.DAY_OF_MONTH, (int)(days-1)*(-1));
		return ivrDao.countIVRCallsCreatedBetweenDatesWithStatus(start, end.getTime(), status);
	}
	
	@Transactional
	public List<IVRRecordingStat> getIVRRecordingStats() {
		return ivrDao.getIVRRecordingStats();
	}
	
	@Transactional
	public List<IVRCallStatusStat> getIVRCallStatusStats() {
		return ivrDao.getIVRCallStatusStats();
 	}
	
	@Transactional
	public List<IVRCallStatusStat> getIVRCallStatusStatsFromLastMinutes(int minutes) {
		minutes = minutes < 0 ? 0 : minutes;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.MINUTE, (int)minutes*(-1));
		return ivrDao.getIVRCallStatusStatsBetweenDates(start, end);
	}
	
	@Transactional
	public List<IVRCallStatusStat> getIVRCallStatusStatsFromLastHours(int hours) {
		hours = hours < 0 ? 0 : hours;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.HOUR_OF_DAY, (int)hours*(-1));
		return ivrDao.getIVRCallStatusStatsBetweenDates(start, end);
	}
	
	@Transactional
	public List<IVRCallStatusStat> getIVRCallStatusStatsFromLastDays(int days) {
		days = days < 0 ? 0 : days;
		GregorianCalendar end = new GregorianCalendar();
		GregorianCalendar lastMidnight = new GregorianCalendar(
				end.get(GregorianCalendar.YEAR), 
				end.get(GregorianCalendar.MONTH),
				end.get(GregorianCalendar.DAY_OF_MONTH));
		Date start = addToDate(lastMidnight.getTime(), GregorianCalendar.DAY_OF_MONTH, (int)(days-1)*(-1));
		return ivrDao.getIVRCallStatusStatsBetweenDates(start, end.getTime());
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessions() {
		return ivrDao.loadIVRCallSessions();
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessionsInLastMinutes(int minutes) {
		minutes = minutes < 0 ? 0 : minutes;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.MINUTE, (int)minutes*(-1));
		return ivrDao.loadIVRCallSessionsCreatedBetweenDates(start, end);
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessionsInLastHours(int hours) {
		hours = hours < 0 ? 0 : hours;
		Date end = new Date();
		Date start = addToDate(end, GregorianCalendar.HOUR_OF_DAY, (int)hours*(-1));
		return ivrDao.loadIVRCallSessionsCreatedBetweenDates(start, end);
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessionsInLastDays(int days) {
		days = days < 0 ? 0 : days;
		GregorianCalendar end = new GregorianCalendar();
		GregorianCalendar lastMidnight = new GregorianCalendar(
				end.get(GregorianCalendar.YEAR), 
				end.get(GregorianCalendar.MONTH),
				end.get(GregorianCalendar.DAY_OF_MONTH));
		Date start = addToDate(lastMidnight.getTime(), GregorianCalendar.DAY_OF_MONTH, (int)(days-1)*(-1));
		return ivrDao.loadIVRCallSessionsCreatedBetweenDates(start, end.getTime());
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessionsForUser(String user) {
		return ivrDao.loadIVRCallSessionsByUser(user);
	}
	
	@Transactional
	public List<IVRCallSession> getIVRCallSessionsForPhone(String phone) {
		return ivrDao.loadIVRCallSessionsByPhone(phone);
	}
	
	public void setMessageHandler(GatewayMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public GatewayMessageHandler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * The URL to which the IVR system will be requested to post call reports
	 * @return reportURL
	 */
	public String getReportURL() {
		return reportURL;
	}

	/**
	 * Set URL to which the IVR system will be requested to post call reports
	 * @param reportURL
	 */
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}

	/**
	 * The API key for the IntellIVR server
	 * @return
	 */
	public String getApiID() {
		return apiID;
	}

	/**
	 * Set the API key for the IntellIVR server
	 * @param apiID
	 */
	public void setApiID(String apiID) {
		this.apiID = apiID;
	}

	/**
	 * The implementation of the {@link IntellIVRServer} interface being used
	 * @return
	 */
	public IntellIVRServer getIvrServer() {
		return ivrServer;
	}

	/**
	 * Set the implementation of the {@link IntellIVRServer} interface being used
	 * @param ivrServer
	 */
	public void setIvrServer(IntellIVRServer ivrServer) {
		this.ivrServer = ivrServer;
	}

	/**
	 * The method being used for IntellIVR server
	 * @return
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Set the method being used for IntellIVR server.  Generally 'ivroriginate'.
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * The default language to use if not otherwise specified.
	 * @return
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * Set the default language to use if not otherwise specified.
	 * @param defaultLanguage
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	/**
	 * The default tree.  Not used.
	 * @return
	 */
	public String getDefaultTree() {
		return defaultTree;
	}

	/**
	 * Set the default tree.  Not used.
	 * @param defaultTree
	 */
	public void setDefaultTree(String defaultTree) {
		this.defaultTree = defaultTree;
	}

	/**
	 * The default reminder.  Not used.
	 * @return
	 */
	public String getDefaultReminder() {
		return defaultReminder;
	}

	/**
	 * Set the default reminder.  Not used.
	 * @param defaultReminder
	 */
	public void setDefaultReminder(String defaultReminder) {
		this.defaultReminder = defaultReminder;
	}

	/**
	 * The {@link MessageStatusStore} used to store request statuses.
	 * @return
	 */
	public MessageStatusStore getStatusStore() {
		return statusStore;
	}

	/**
	 * Set the {@link MessageStatusStore} used to store request statuses.
	 * @param statusStore
	 */
	public void setStatusStore(MessageStatusStore statusStore) {
		this.statusStore = statusStore;
	}

	/**
	 * Delay to bundle additional messages for a user before sending
	 * See {@link #sendMessage(GatewayRequest, MotechContext)} for more details. 
	 * @return
	 */
	public long getBundlingDelay() {
		return bundlingDelay;
	}

	/**
	 * Set delay in milliseconds to bundle additional messages for a user before sending
	 * See {@link #sendMessage(GatewayRequest, MotechContext)} for more details. 
	 * @param bundlingDelay
	 */
	public void setBundlingDelay(long bundlingDelay) {
		this.bundlingDelay = bundlingDelay;
	}

	/**
	 * Delay in minutes to wait before retrying after a failed message delivery.
	 * @return
	 */
	public int getRetryDelay() {
		return retryDelay;
	}

	/**
	 * Set delay in minutes to wait before retrying after a failed message delivery.
	 * @param retryDelay
	 */
	public void setRetryDelay(int retryDelay) {
		this.retryDelay = retryDelay;
	}

	/**
	 * Max attempts to try a deliver a message each day.
	 * @return
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * Set max attempts to try a deliver a message each day.
	 * @param maxAttempts
	 */
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Max days to retry message delivery
	 * @return
	 */
	public int getMaxDays() {
		return maxDays;
	}

	/**
	 * Set max days to retry message delivery
	 * @param maxDays
	 */
	public void setMaxDays(int maxDays) {
		this.maxDays = maxDays;
	}

	/**
	 * Number of days a message should remain available to replayed
	 * @return
	 */
	public int getAvailableDays() {
		return availableDays;
	}

	/**
	 * Set number of days a message should remain available to replayed
	 * @param availableDays
	 */
	public void setAvailableDays(int availableDays) {
		this.availableDays = availableDays;
	}

	/**
	 * Seconds of the first primary informational message that the user
	 * must have listened to to consider the message delivered.
	 * @return
	 */
	public int getCallCompletedThreshold() {
		return callCompletedThreshold;
	}

	/**
	 * Set seconds of the first primary informational message that the user
	 * must have listened to to consider the message delivered.
	 * @param callCompletedThreshold
	 */
	public void setCallCompletedThreshold(int callCompletedThreshold) {
		this.callCompletedThreshold = callCompletedThreshold;
	}

	/**
	 * Seconds of silence that is pre-pended to beginning of each call. 
	 * @return
	 */
	public int getPreReminderDelay() {
		return preReminderDelay;
	}

	/**
	 * Set seconds of silence that is pre-pended to beginning of each call.
	 * @param preReminderDelay
	 */
	public void setPreReminderDelay(int preReminderDelay) {
		this.preReminderDelay = preReminderDelay;
	}

	/**
	 * If true, the next day retries are tried immediately.  For testing.
	 * @return
	 */
	public boolean isAccelerateRetries() {
		return accelerateRetries;
	}

	/**
	 * Enables/disables accelerated retries
	 * @param accelerateRetries
	 */
	public void setAccelerateRetries(boolean accelerateRetries) {
		this.accelerateRetries = accelerateRetries;
	}

	/**
	 * Name of recording to play in the event a user has no pending messages
	 * @return
	 */
	public String getNoPendingMessagesRecordingName() {
		return noPendingMessagesRecordingName;
	}

	/**
	 * Set the name of recording to play in the event a user has no pending messages
	 * @param noPendingMessagesRecordingName
	 */
	public void setNoPendingMessagesRecordingName(
			String noPendingMessagesRecordingName) {
		this.noPendingMessagesRecordingName = noPendingMessagesRecordingName;
	}

	/**
	 * Name of a recording of a welcome message to be played before all other messages
	 * @return
	 */
	public String getWelcomeMessageRecordingName() {
		return welcomeMessageRecordingName;
	}

	/**
	 * Set the name of a recording of a welcome message to be played before all other messages
	 * @param welcomeMessageRecordingName
	 */
	public void setWelcomeMessageRecordingName(String welcomeMessageRecordingName) {
		this.welcomeMessageRecordingName = welcomeMessageRecordingName;
	}

	/**
	 * Name of file resource that contains the mapping between {@link NotificationType} ids
	 * and file names on the IVR server.  Each line should match the following expression:
	 * 
	 * [0-9]+=[IiRr]{1},.+
	 * 
	 * @return
	 */
	public Resource getMappingResource() {
		return mappingResource;
	}

	/**
	 * Set the file resource for mapping.  See {@link #getMappingResource()}.
	 * @param mappingsFile
	 */
	public void setMappingResource(Resource mappingsFile) {
		this.mappingResource = mappingsFile;
	}

	/**
	 * For access to core motech mobile services
	 * @return
	 */
	public CoreManager getCoreManager() {
		return coreManager;
	}

	/**
	 * Set the {@link CoreManager}.
	 * @param coreManager
	 */
	public void setCoreManager(CoreManager coreManager) {
		this.coreManager = coreManager;
	}

	/**
	 * Interface the to the Motech Server.  
	 * @return
	 */
	public RegistrarService getRegistrarService() {
		return registrarService;
	}

	/**
	 * Set the {@link RegistrarService}.
	 * @param registrarService
	 */
	public void setRegistrarService(RegistrarService registrarService) {
		this.registrarService = registrarService;
	}

	public IVRDAO getIvrDao() {
		return ivrDao;
	}

	public void setIvrDao(IVRDAO ivrDao) {
		this.ivrDao = ivrDao;
	}
	
	public IVRCallRequester getIvrCallRequester() {
		return ivrCallRequester;
	}

	public void setIvrCallRequester(IVRCallRequester ivrCallRequester) {
		this.ivrCallRequester = ivrCallRequester;
	}

	private Date toDate(XMLGregorianCalendar time) {
		return time == null ? null : time.toGregorianCalendar().getTime();
	}
	
	private Date addToDate(Date start, int field, int amount) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.add(field, amount);
		return cal.getTime();
	}
	
	private IVRCallStatus toIvrCallStatus(ReportStatusType status) {
		if (status == null)
			return null;
		if (status.name() == null)
			return null;
		return IVRCallStatus.valueOf(status.name());
	}
	
	private List<String> formatReportLogMessages(ReportType report) {

		List<String> result = new ArrayList<String>();

		StringBuilder common = new StringBuilder();
		common.append(report.getCallee());
		common.append("," + report.getDuration());
		common.append("," + report.getINTELLIVREntryCount());
		common.append("," + report.getPrivate());
		common.append("," + report.getConnectTime());
		common.append("," + report.getDisconnectTime());
		common.append("," + report.getStatus().value());

		result.add(common.toString());
		
		for ( IvrEntryType entry : report.getINTELLIVREntry() ) {

			StringBuilder message = new StringBuilder();
			message.append(common.toString());
			message.append("," + entry.getFile());
			message.append("," + entry.getKeypress());
			message.append("," + entry.getMenu());
			message.append("," + entry.getDuration());
			message.append("," + entry.getEntrytime());

			result.add(message.toString());

		}

		return result;
	}
	
}
