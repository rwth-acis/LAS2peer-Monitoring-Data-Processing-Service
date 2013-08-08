package i5.las2peer.services.monitoring.processing.database;

import i5.las2peer.logging.NodeObserver.Event;
import i5.las2peer.logging.monitoring.MonitoringMessage;

import java.sql.Timestamp;

/**
 * 
 * DatabaseInsertStatement.java
 * <br>
 * Helper Class that provides (static) methods to formulate a SQL statement according to the given specifications.
 * The statements are formulated according to the database scheme that can be found in the "scripts" folder provided with this project.
 * @author Peter de Lange
 *
 */
public class DatabaseInsertStatement {
	
	
	/**
	 * 
	 * The entry point to this class.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * @param table the name of the table the query should be inserted to
	 * 
	 * @return a SQL statement
	 * 
	 * @throws Exception if the given information was not correct or sufficient for the desired table entry
	 * 
	 */
	public static String returnInsertStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType, String table) throws Exception{		
	
		if(databaseType == SQLDatabaseType.MySQL){
			
			if(table.equals("MESSAGE")){
				return returnMessageStatement(monitoringMessage, databaseType);
			}
			
			else if(table.equals("AGENT")){
				return returnAgentStatement(monitoringMessage, databaseType);
			}
			
			else if(table.equals("SERVICE")){
				return returnServiceStatement(monitoringMessage, databaseType);
			}
			
			else if(table.equals("NODE")){
				return returnNodeStatement(monitoringMessage, databaseType);
			}
			
			else if(table.equals("REGISTERED_AT")){
				return returnRegisteredAtStatement(monitoringMessage, databaseType);				
			}
			
			else{
				throw new Exception("Don't know table!");
			}
			
		}
		
		else if(databaseType == SQLDatabaseType.DB2){
			throw new Exception("Not implemented yet!");
		}
		
		else{
			throw new Exception("Not supported database type!");
		}
		
	}
	
	
	/**
	 * 
	 * Returns a SQL statement for the message table.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * 
	 * @return a SQL statement
	 * 
	 */
	private static String returnMessageStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType){
		String returnStatement;
		
		String event =  "'" + monitoringMessage.getEvent().toString() + "'";
		String timestamp = ", '" + new Timestamp(monitoringMessage.getTimestamp()).toString() + "'";
		String timespan = "";
		String sourceNode = "";
		String sourceAgentId = "";
		String destinationNode = "";
		String destinationAgentId = "";
		String remarks = "";
		returnStatement = "INSERT INTO MESSAGE (EVENT, TIME_STAMP";
		if(monitoringMessage.getTimespan() != null){
			returnStatement += " ,TIME_SPAN";
			timespan = monitoringMessage.getTimespan().toString();
		}
		if(monitoringMessage.getSourceNode() != null){
			returnStatement += ", SOURCE_NODE";
			sourceNode = ", '" + monitoringMessage.getSourceNode().substring(0, 12) + "'";
		}
		if(monitoringMessage.getSourceAgentId() != null){
			returnStatement += ", SOURCE_AGENT";
			sourceAgentId = ", " + monitoringMessage.getSourceAgentId().toString();
		}
		if(monitoringMessage.getDestinationNode() != null){
			returnStatement += ", DESTINATION_NODE";
			destinationNode = ", '" + monitoringMessage.getDestinationNode().substring(0, 12) + "'";
		}
		if(monitoringMessage.getDestinationAgentId() != null){
			returnStatement += ", DESTINATION_AGENT";
			destinationAgentId = ", " + monitoringMessage.getDestinationAgentId().toString();

		}
		if(monitoringMessage.getRemarks() != null){
			returnStatement += ", REMARKS";
			remarks = ", '" + monitoringMessage.getRemarks() + "'";
		}
		returnStatement += ") VALUES(";
		returnStatement += event + timestamp + timespan + sourceNode + sourceAgentId + destinationNode + destinationAgentId + remarks;
		returnStatement += ");";
		return returnStatement;
	}
	
	
	/**
	 * 
	 * Returns a SQL statement for the Agent table.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * 
	 * @return a SQL statement
	 * 
	 * @throws Exception if the given information was not correct or sufficient for the desired table entry
	 * 
	 */
	private static String returnAgentStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType) throws Exception{
		String returnStatement;
		
		if(monitoringMessage.getSourceNode() == null || monitoringMessage.getSourceAgentId() == null || monitoringMessage.getRemarks() == null)
			throw new Exception("Missing information for persisting agent entity!");
		
		String agentType = null;
		if(monitoringMessage.getEvent() == Event.SERVICE_ADD_TO_MONITORING)
			agentType = "SERVICE";
		else if(monitoringMessage.getEvent() == Event.AGENT_REGISTERED){
			if(monitoringMessage.getRemarks().equals("ServiceAgent")){
				throw new Exception("ServiceAgents are only persisted when added to monitoring!");
			}
			else if(monitoringMessage.getRemarks().equals("UserAgent")){
				agentType = "USER";
			}
			else if(monitoringMessage.getRemarks().equals("GroupAgent")){
				agentType = "GROUP";
			}
			else if(monitoringMessage.getRemarks().equals("MonitoringAgent")){
				agentType = "MONITORING";
			}
			else if(monitoringMessage.getRemarks().equals("Mediator")){
			//Thats right, we treat mediators as agents (as from a monitoring point of view, this is the same)
				agentType = "MEDIATOR";
			}
			else{
				throw new Exception("Unknown remarks entry for persisting agent entity: " + monitoringMessage.getRemarks());
			}
		}
		else{
			throw new Exception("Agent entities will only be persisted if registered at a node!"); 
		}
		returnStatement = "INSERT INTO AGENT(AGENT_ID, TYPE) VALUES(";
		returnStatement += monitoringMessage.getSourceAgentId() + ", '" + agentType + "') ON DUPLICATE KEY UPDATE AGENT_ID=AGENT_ID;";
		return returnStatement;
	}
	
	
	/**
	 * 
	 * Returns a SQL statement for the Service table.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * 
	 * @return a SQL statement
	 * 
	 * @throws Exception if the given information was not correct or sufficient for the desired table entry
	 * 
	 */
	private static String returnServiceStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType) throws Exception {
		String returnStatement;
		
		if(monitoringMessage.getSourceAgentId() == null || monitoringMessage.getRemarks() == null)
			throw new Exception("Missing information for persisting service entity!");
		
		returnStatement = "INSERT INTO SERVICE(AGENT_ID, SERVICE_CLASS_NAME) VALUES(";
		returnStatement += monitoringMessage.getSourceAgentId() + ", '" + monitoringMessage.getRemarks() +"') ON DUPLICATE KEY UPDATE AGENT_ID=AGENT_ID;";
		return returnStatement;
	}
	
	
	/**
	 * 
	 * Returns a SQL statement for the "Registered At" table.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * 
	 * @return a SQL statement
	 * 
	 * @throws Exception if the given information was not correct or sufficient for the desired table entry
	 * 
	 */
	private static String returnRegisteredAtStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType) throws Exception {
		String returnStatement;
		if(monitoringMessage.getTimestamp() == null || monitoringMessage.getSourceNode() == null)
			throw new Exception("Missing information for 'registered at' entity!");
	
		String timestamp = "'" + new Timestamp(monitoringMessage.getTimestamp()).toString() + "'";
		String nodeId = monitoringMessage.getSourceNode().substring(0, 12);
	
		if(monitoringMessage.getEvent() == Event.AGENT_REGISTERED || monitoringMessage.getEvent() == Event.SERVICE_ADD_TO_MONITORING){
			if(monitoringMessage.getSourceAgentId() == null)
				throw new Exception("Missing information for persisting 'registered at' entity!");
			returnStatement = "INSERT INTO REGISTERED_AT(REGISTRATION_DATE, AGENT_ID, RUNNING_AT) VALUES(";
			returnStatement += timestamp + ", " + monitoringMessage.getSourceAgentId() + ", '" + nodeId +"');";
			return returnStatement;
		}
		else if(monitoringMessage.getEvent() == Event.AGENT_REMOVED || monitoringMessage.getEvent() == Event.SERVICE_SHUTDOWN){
			returnStatement = "UPDATE REGISTERED_AT SET UNREGISTRATION_DATE=";
			returnStatement += timestamp;
			returnStatement += " WHERE RUNNING_AT='" + nodeId + "' AND AGENT_ID=" + monitoringMessage.getSourceAgentId() + " AND UNREGISTRATION_DATE IS NULL;";
			return returnStatement;
		}
		else{ //We need to unregister those who have not yet unregistered -> update statement!
			returnStatement = "UPDATE REGISTERED_AT SET UNREGISTRATION_DATE=";
			returnStatement += timestamp;
			returnStatement += " WHERE RUNNING_AT='" + nodeId + "' AND UNREGISTRATION_DATE IS NULL;";
			return returnStatement;
		}
		
	}
	
	
	/**
	 * 
	 * Returns a SQL statement for the Node table.
	 * 
	 * @param monitoringMessage a {@link i5.las2peer.logging.monitoring.MonitoringMessage} that contains the information to be stored
	 * @param databaseType the database type the statement should be formulated for
	 * 
	 * @return a SQL statement
	 * 
	 * @throws Exception if the given information was not correct or sufficient for the desired table entry
	 * 
	 */
	private static String returnNodeStatement(MonitoringMessage monitoringMessage, SQLDatabaseType databaseType) throws Exception {
		String returnStatement;
		if(monitoringMessage.getEvent() == Event.NODE_STATUS_CHANGE){
			if(monitoringMessage.getSourceNode() == null)
				throw new Exception("Missing information for persisting node entity!");
			String nodeId = monitoringMessage.getSourceNode().substring(0, 12);
			int startingLocationPosition = monitoringMessage.getSourceNode().lastIndexOf("/") + 1;
			String nodeLocation = monitoringMessage.getSourceNode().substring(startingLocationPosition);
			returnStatement = "INSERT INTO NODE(NODE_ID, NODE_LOCATION) VALUES(";
			returnStatement += "'" + nodeId +"', '" + nodeLocation + "') ON DUPLICATE KEY UPDATE NODE_ID = NODE_ID;";
			return returnStatement;
		}
		else if(monitoringMessage.getEvent() == Event.NEW_NODE_NOTICE){
			if(monitoringMessage.getRemarks() == null)
				throw new Exception("Missing information for persisting node entity!");
			int nodeIdStart = monitoringMessage.getRemarks().indexOf("<");
			int nodeLocationStart = monitoringMessage.getRemarks().lastIndexOf("/") + 1;
			int nodeLocationEnd = monitoringMessage.getRemarks().indexOf("]");
			String nodeId = monitoringMessage.getRemarks().substring(nodeIdStart, nodeIdStart+12);
			String nodeLocation = monitoringMessage.getRemarks().substring(nodeLocationStart, nodeLocationEnd);
			returnStatement = "INSERT INTO NODE(NODE_ID, NODE_LOCATION) VALUES(";
			//Duplicate can happen because of new node notices

			returnStatement += "'" + nodeId +"', '" + nodeLocation + "') ON DUPLICATE KEY UPDATE NODE_ID = NODE_ID;";
			return returnStatement;
		}
		else{
			throw new Exception("Node persistence only at new node notice or node creation events!");
		}
	}
	
	
}
