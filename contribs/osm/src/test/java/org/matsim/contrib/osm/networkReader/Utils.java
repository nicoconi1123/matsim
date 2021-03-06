package org.matsim.contrib.osm.networkReader;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.pbf.seq.PbfWriter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.IdentityTransformation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Utils {

	static final CoordinateTransformation transformation = new IdentityTransformation();
	static final String MOTORWAY = "motorway";
	static final String TERTIARY = "tertiary";
	private static final Logger log = Logger.getLogger(Utils.class);


	static void writeOsmData(Collection<OsmNode> nodes, Collection<OsmWay> ways, Path file) {

		try (OutputStream outputStream = Files.newOutputStream(file)) {
			PbfWriter writer = new PbfWriter(outputStream, true);
			for (OsmNode node : nodes) {
				writer.write(node);
			}

			for (OsmWay way : ways) {
				writer.write(way);
			}
			writer.complete();
		} catch (IOException e) {
			log.error("could not write osm data");
			e.printStackTrace();
		}
	}

	static WaysAndLinks createSingleLink() {
		return createSingleLink(Collections.singletonList(new Tag(OsmTags.HIGHWAY, Utils.MOTORWAY)));
	}

	static WaysAndLinks createSingleLink(List<OsmTag> tags) {

		Node node1 = new Node(1, 0, 0);
		Node node2 = new Node(2, 100, 100);
		Node node3 = new Node(3, 0, 200);
		TLongArrayList nodeReference = new TLongArrayList(new long[]{node1.getId(), node2.getId(), node3.getId()});
		Way way = new Way(1, nodeReference, tags);

		return new WaysAndLinks(Arrays.asList(node1, node2, node3), Collections.singletonList(way));
	}

	static WaysAndLinks createTwoIntersectingLinksWithDifferentLevels() {
		Node node1 = new Node(1, 0, 0);
		Node node2 = new Node(2, 100, 100);
		Node node3 = new Node(3, 0, 200);
		Node node4 = new Node(4, 200, 0);
		Node node5 = new Node(5, 200, 200);
		TLongArrayList nodeReferenceForWay1 = new TLongArrayList(new long[]{node1.getId(), node2.getId(), node3.getId()});
		TLongArrayList nodeReferenceForWay2 = new TLongArrayList(new long[]{node4.getId(), node2.getId(), node5.getId()});
		Way way1 = new Way(1, nodeReferenceForWay1, Collections.singletonList(new Tag(OsmTags.HIGHWAY, Utils.MOTORWAY)));
		Way way2 = new Way(2, nodeReferenceForWay2, Collections.singletonList(new Tag(OsmTags.HIGHWAY, Utils.TERTIARY)));
		return new WaysAndLinks(Arrays.asList(node1, node2, node3, node4, node5), Arrays.asList(way1, way2));
	}

	static WaysAndLinks createGridWithDifferentLevels() {

		List<OsmNode> nodesList = Arrays.asList(
				new Node(1, 100, 0),
				new Node(2, 200, 0),
				new Node(3, 0, 100),
				new Node(4, 100, 100),
				new Node(5, 200, 100),
				new Node(6, 300, 100),
				new Node(7, 0, 200),
				new Node(8, 100, 200),
				new Node(9, 200, 200),
				new Node(10, 300, 200),
				new Node(11, 100, 300),
				new Node(12, 200, 300)
		);

		List<OsmWay> waysList = Arrays.asList(
				new Way(1, new TLongArrayList(new long[]{3, 4, 5, 6}), Collections.singletonList(new Tag("highway", MOTORWAY))),
				new Way(2, new TLongArrayList(new long[]{7, 8, 9, 10}), Collections.singletonList(new Tag("highway", MOTORWAY))),
				new Way(3, new TLongArrayList(new long[]{1, 4, 8, 11}), Collections.singletonList(new Tag("highway", TERTIARY))),
				new Way(4, new TLongArrayList(new long[]{2, 5, 9, 12}), Collections.singletonList(new Tag("highway", TERTIARY)))
		);

		return new WaysAndLinks(nodesList, waysList);
	}

	static void assertEquals(Network expected, Network actual) {
		// check that all element from expected result are in tested network
		for (Link link : expected.getLinks().values()) {
			Link testLink = actual.getLinks().get(link.getId());
			assertNotNull(testLink);
			testLinksAreEqual(link, testLink);
		}

		for (org.matsim.api.core.v01.network.Node node : expected.getNodes().values()) {
			org.matsim.api.core.v01.network.Node testNode = actual.getNodes().get(node.getId());
			assertNotNull(testNode);
			testNodesAreEqual(node, testNode);
		}

		// also check the other way around, to make sure there are no extra elements in the network
		for (Link link : actual.getLinks().values()) {
			Link expectedLink = expected.getLinks().get(link.getId());
			assertNotNull(expectedLink);
		}

		for (org.matsim.api.core.v01.network.Node node : actual.getNodes().values()) {
			org.matsim.api.core.v01.network.Node expectedNode = expected.getNodes().get(node.getId());
			assertNotNull(expectedNode);
		}
	}

	private static void testLinksAreEqual(Link expected, Link actual) {

		expected.getAllowedModes().forEach(mode -> assertTrue(actual.getAllowedModes().contains(mode)));
		Assert.assertEquals(expected.getCapacity(), actual.getCapacity(), 0.001);
		Assert.assertEquals(expected.getFlowCapacityPerSec(), actual.getFlowCapacityPerSec(), 0.001);
		Assert.assertEquals(expected.getFreespeed(), actual.getFreespeed(), 0.001);
		Assert.assertEquals(expected.getLength(), actual.getLength(), 0.001);
		Assert.assertEquals(expected.getNumberOfLanes(), actual.getNumberOfLanes(), 0.001);
		Assert.assertEquals(expected.getFromNode().getId(), actual.getFromNode().getId());
		Assert.assertEquals(expected.getToNode().getId(), actual.getToNode().getId());
	}

	private static void testNodesAreEqual(org.matsim.api.core.v01.network.Node expected, org.matsim.api.core.v01.network.Node actual) {

		// test x and y separately, so that we can have a delta.
		// In java version >=11 Assert.assertEquals(expected.getCoord(), actual.getCoord()) also works
		// keep this in as long as we use java-8
		Assert.assertEquals(expected.getCoord().getX(), actual.getCoord().getX(), 0.00000001);
		Assert.assertEquals(expected.getCoord().getY(), actual.getCoord().getY(), 0.00000001);
		expected.getOutLinks().forEach((id, link) -> Assert.assertEquals(link.getId(), actual.getOutLinks().get(id).getId()));
		expected.getInLinks().forEach((id, link) -> Assert.assertEquals(link.getId(), actual.getInLinks().get(id).getId()));
	}

	static class WaysAndLinks {

		private final List<OsmNode> nodes;
		private final List<OsmWay> ways;

		public WaysAndLinks(List<OsmNode> nodes, List<OsmWay> ways) {
			this.nodes = nodes;
			this.ways = ways;
		}

		public List<OsmNode> getNodes() {
			return nodes;
		}

		public List<OsmWay> getWays() {
			return ways;
		}
	}
}
