package ca.carleton.gcrc.couch.command.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public class SchemaDefinition {
	
	static public SchemaDefinition fronJson(JSONObject jsonDef) throws Exception {
		String groupName = jsonDef.getString("group");
		String schemaId = jsonDef.getString("id");
		
		SchemaDefinition def = new SchemaDefinition(groupName, schemaId);

		// label
		{
			String label = jsonDef.optString("label");
			if( null != label ){
				def.setLabel(label);
			}
		}
		
		// Attributes
		JSONArray attributes = jsonDef.optJSONArray("attributes");
		if( null != attributes ) {
			for(int i=0,e=attributes.length(); i<e; ++i){
				JSONObject jsonAttr = attributes.getJSONObject(i);
				SchemaAttribute attribute = SchemaAttribute.fromJson(jsonAttr);
				def.addAttribute(attribute);
			}
		}
		
		return def;
	}
	
	private String groupName;
	private String schemaId;
	private String label;
	private List<SchemaAttribute> attributes = new Vector<SchemaAttribute>();
	
	public SchemaDefinition(String groupName, String schemaId){
		this.groupName = groupName;
		this.schemaId = schemaId;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public List<SchemaAttribute> getAttributes(){
		return attributes;
	}

	public void addAttribute(SchemaAttribute attribute){
		attributes.add(attribute);
	}
	
	public String getDocumentIdentifier(){
		return "schema."+groupName+"_"+schemaId;
	}
	
	public String getSchemaName(){
		return groupName+"_"+schemaId;
	}
	
	public String getSchemaLabel(){
		if( null == label ){
			return getSchemaName();
		}
		
		return label;
	}
	
	public void saveToDocsDir(File parentDir) throws Exception {
		
		// Create directory
		File schemaDir = new File(parentDir, getDocumentIdentifier());
		if( false == schemaDir.exists() ){
			boolean created = schemaDir.mkdir();
			if( !created ){
				throw new Exception("Unable to create schema directory");
			}
		}
		
		// definition.json
		{
			File file = new File(schemaDir, "definition.json");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			JSONObject obj = toJson();
			osw.write( obj.toString(3) );
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		saveToSchemaDir(schemaDir);
	}
	
	public void saveToSchemaDir(File schemaDir) throws Exception {
		
		// _id.txt
		{
			File file = new File(schemaDir, "_id.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(getDocumentIdentifier());
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// nunaliit_type.txt
		{
			File file = new File(schemaDir, "nunaliit_type.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write("schema");
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// nunaliit_schema.txt
		{
			File file = new File(schemaDir, "nunaliit_schema.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write("schema");
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// isRootSchema.json
		{
			File file = new File(schemaDir, "isRootSchema.json");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write("true");
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// name.txt
		{
			File file = new File(schemaDir, "name.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write( getSchemaName() );
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// label.txt
		{
			File file = new File(schemaDir, "label.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write( getSchemaName() );
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// relatedSchemas.json
		{
			File file = new File(schemaDir, "relatedSchemas.json");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			JSONArray arr = new JSONArray();
			osw.write( arr.toString(3) );
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// create.json
		{
			File file = new File(schemaDir, "create.json");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			JSONObject jsonCreate = computeCreateField();
			osw.write( jsonCreate.toString(3) );
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// brief.txt
		{
			File file = new File(schemaDir, "brief.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			printBrief(pw);
			pw.flush();
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// display.txt
		{
			File file = new File(schemaDir, "display.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			printDisplay(pw);
			pw.flush();
			osw.flush();
			fos.flush();
			fos.close();
		}
		
		// form.txt
		{
			File file = new File(schemaDir, "form.txt");
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);
			printForm(pw);
			pw.flush();
			osw.flush();
			fos.flush();
			fos.close();
		}
	}

	public JSONObject toJson() throws Exception {
		JSONObject jsonDef = new JSONObject();
		
		jsonDef.put("id", schemaId);
		jsonDef.put("group", groupName);
		
		if( null != label ) jsonDef.put("label", label);
		
		if( attributes.size() > 0 ){
			JSONArray jsonAttributes  = new JSONArray();
			
			for(SchemaAttribute attribute : attributes){
				JSONObject jsonAttr = attribute.toJson();
				jsonAttributes.put(jsonAttr);
			}
			
			jsonDef.put("attributes", jsonAttributes);
		}
		
		return jsonDef;
	}
	
	public JSONObject computeCreateField() throws Exception {
		JSONObject jsonCreate = new JSONObject();

		jsonCreate.put("nunaliit_schema", getSchemaName());
		
		JSONObject jsonDoc = new JSONObject();
		jsonCreate.put(getSchemaName(), jsonDoc);

		for(SchemaAttribute attribute : attributes){
			attribute.addCreateField(jsonCreate, jsonDoc);
		}
		
		return jsonCreate;
	}
	
	public void printBrief(PrintWriter pw) throws Exception {
		String schemaName = getSchemaName();

		if( null != label ){
			pw.print("<span class=\"n2s_localize\">");
			pw.print(label);
			pw.print("</span>");
		} else {
			pw.print(schemaName);
		}
		
		pw.print("(");

		for(SchemaAttribute attribute : attributes){
			attribute.printBrief(pw,schemaName);
		}
		
		pw.print(")");
	}
	
	public void printDisplay(PrintWriter pw) throws Exception {
		String schemaName = getSchemaName();
		
		pw.println("<div class=\"n2_documentDisplay\"><div class=\"n2_layoutFloat\">");
		pw.println("<div class=\""+schemaName+"\">");

		for(SchemaAttribute attribute : attributes){
			attribute.printDisplay(pw,schemaName);
		}
		
		pw.println("</div>");
		pw.println("</div></div>");
	}
	
	public void printForm(PrintWriter pw) throws Exception {
		String schemaName = getSchemaName();
		
		pw.println("<div class=\"n2_documentForm\"><div class=\"n2_layoutFloat\">");
		pw.println("<div class=\""+schemaName+"\">");

		for(SchemaAttribute attribute : attributes){
			attribute.printForm(pw,schemaName);
		}
		
		pw.println("</div>");
		pw.println("</div></div>");
	}
}