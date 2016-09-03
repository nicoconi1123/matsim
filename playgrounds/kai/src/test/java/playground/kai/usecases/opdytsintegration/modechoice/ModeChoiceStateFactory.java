package playground.kai.usecases.opdytsintegration.modechoice;

import org.matsim.api.core.v01.population.Population;

import floetteroed.utilities.math.Vector;
import opdytsintegration.MATSimState;
import opdytsintegration.MATSimStateFactory;

/**
 * 
 * @author Kai Nagel based on Gunnar Flötteröd
 *
 */
public class ModeChoiceStateFactory implements MATSimStateFactory<ModeChoiceDecisionVariable> {
	@Override public MATSimState newState(Population population, Vector stateVector, ModeChoiceDecisionVariable decisionVariable) {
		return new ModeChoiceState(population, stateVector);
	}

}
