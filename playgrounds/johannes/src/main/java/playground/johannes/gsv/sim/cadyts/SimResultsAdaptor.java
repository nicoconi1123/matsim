/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

package playground.johannes.gsv.sim.cadyts;

import org.matsim.api.core.v01.network.Link;

import playground.johannes.gsv.sim.LinkOccupancyCalculator;
import cadyts.measurements.SingleLinkMeasurement.TYPE;
import cadyts.supply.SimResults;

/**
 * @author johannes
 *
 */
public class SimResultsAdaptor implements SimResults<Link> {

	private static final long serialVersionUID = -2999625283320618974L;

	private final LinkOccupancyCalculator occupancy;
	
	private final double scale;
	
	public SimResultsAdaptor(LinkOccupancyCalculator occupancy, double scale) {
		this.occupancy = occupancy;
		this.scale = scale;
	}
	
	@Override
	public double getSimValue(Link link, int startTime_s, int endTime_s, TYPE type) {
	
		double val = occupancy.getOccupancy(link.getId());
	
		if(type == TYPE.COUNT_VEH) {
			return val * scale;
		} else if(type == TYPE.FLOW_VEH_H) {
			return val * scale / 86400.0;
		} else {
			throw new RuntimeException();
		}
		
	}

}