/* *********************************************************************** *
 * project: org.matsim.*
 * HouseholdsWriterV1
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.population;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.matsim.basic.v01.HouseholdsSchemaV1Names;
import org.matsim.basic.v01.Id;
import org.matsim.basic.v01.MatsimCommonWriter;
import org.matsim.interfaces.basic.v01.BasicHousehold;
import org.matsim.interfaces.basic.v01.BasicIncome;
import org.matsim.utils.collections.Tuple;
import org.matsim.writer.MatsimXmlWriter;


/**
 * @author dgrether
 *
 */
public class HouseholdsWriterV1 extends MatsimXmlWriter {
	
	private List<Tuple<String, String>> atts = new ArrayList<Tuple<String, String>>();
	private Map<Id, BasicHousehold> households;
	private MatsimCommonWriter matsimCommonWriter;
	
	public HouseholdsWriterV1(Map<Id, BasicHousehold> households) {
		this.households = households;
	}
	
	public void writeFile(String filename) throws FileNotFoundException, IOException {
		this.useCompression(useCompression);
		this.openFile(filename);
		this.matsimCommonWriter = new MatsimCommonWriter(this.writer);
		this.writeXmlHead();
		this.writeHouseholds(this.households);
		this.close();
	}
	
	private void writeHouseholds(Map<Id, BasicHousehold> hh) throws IOException {
		atts.clear();
		atts.add(this.createTuple(XMLNS, MatsimXmlWriter.MATSIM_NAMESPACE));
		atts.add(this.createTuple(XMLNS + ":xsi", DEFAULTSCHEMANAMESPACELOCATION));
		atts.add(this.createTuple("xsi:schemaLocation", MATSIM_NAMESPACE + " " + DEFAULT_DTD_LOCATION + "households_v1.00.xsd"));
		this.writeStartTag(HouseholdsSchemaV1Names.HOUSEHOLDS, atts);
		for (BasicHousehold h : hh.values()) {
			this.writeHousehold(h);
		}
		this.writeEndTag(HouseholdsSchemaV1Names.HOUSEHOLDS);
	}

	public void writeToWriter(Writer writer, int indentationLevel) throws IOException {
		this.writer = writer;
		this.setIndentationLevel(indentationLevel);
		this.matsimCommonWriter = new MatsimCommonWriter(this.writer);
		for (BasicHousehold h : this.households.values()) {
			this.writeHousehold(h);
		}
	}

	private void writeHousehold(BasicHousehold h) throws IOException {
		this.atts.clear();
		atts.add(this.createTuple(HouseholdsSchemaV1Names.ID, h.getId().toString()));
		this.writeStartTag(HouseholdsSchemaV1Names.HOUSEHOLD, atts);
		this.writeMembers(h.getMemberIds());
		this.matsimCommonWriter.writeLocation(h.getBasicLocation(), this.getIndentationLevel());
		if (h.getVehicleIds() != null) {
			for (Id id : h.getVehicleIds()){
				atts.clear();
				atts.add(this.createTuple(HouseholdsSchemaV1Names.REFID, id.toString()));
				this.writeStartTag(HouseholdsSchemaV1Names.VEHICLEDEFINITIONID, atts, true);
			}
		}
		if (h.getIncome() != null){
			this.writeIncome(h.getIncome());
		}
		if (h.getLanguage() != null) {
			atts.clear();
			atts.add(this.createTuple(HouseholdsSchemaV1Names.NAME, h.getLanguage()));
			this.writeStartTag(HouseholdsSchemaV1Names.LANGUAGE, atts, true);
		}
		
		this.writeEndTag(HouseholdsSchemaV1Names.HOUSEHOLD);
	}

	private void writeIncome(BasicIncome income) throws IOException {
		atts.clear();
		atts.add(this.createTuple(HouseholdsSchemaV1Names.CURRENCY,income.getCurrency()));
		atts.add(this.createTuple(HouseholdsSchemaV1Names.PERIOD, income.getIncomePeriod().toString()));
		this.writeStartTag(HouseholdsSchemaV1Names.INCOME, atts);
		this.writeContent(Double.toString(income.getIncome()), true);
		this.writeEndTag(HouseholdsSchemaV1Names.INCOME);
	}

	private void writeMembers(List<Id> memberIds) throws IOException {
		this.writeStartTag(HouseholdsSchemaV1Names.MEMBERS, null);
		for (Id id : memberIds){
			atts.clear();
			atts.add(this.createTuple(HouseholdsSchemaV1Names.REFID, id.toString()));
			this.writeStartTag(HouseholdsSchemaV1Names.PERSONID, atts, true);
		}
		this.writeEndTag(HouseholdsSchemaV1Names.MEMBERS);
	}


	
	
}
