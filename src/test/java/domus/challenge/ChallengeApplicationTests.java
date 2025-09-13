package domus.challenge;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({ "domus.challenge" })          // paquete raíz de tests
@IncludeClassNamePatterns({ ".*Test", ".*Tests" })
class ChallengeApplicationTests {

	/*@Test
	void contextLoads() {
	}*/

}
