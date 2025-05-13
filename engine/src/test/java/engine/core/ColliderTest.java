package engine.core;

import javafx.geometry.BoundingBox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColliderTest {

	@Test
	void getIntersection() {
		Collider c1 = new Collider(null, 0, 0, 1, 1);
		Collider c2 = new Collider(null, 1, 1, 1, 1);

		Assertions.assertNull(c1.getIntersection(c2, 0, 0));
		Assertions.assertNotNull(c1.getIntersection(c2, 0.5, 0.5));
		Assertions.assertEquals(new BoundingBox(0, 0, 1, 1), c1.getIntersection(c1, 0, 0));
	}

	@Test
	void getDistanceTo() {
		Collider c1 = new Collider(null, 0, 0, 1, 1);
		Collider c2 = new Collider(null, 1, 0, 1, 1);

		Assertions.assertEquals(1, c1.getDistanceTo(c2));
		Assertions.assertEquals(0, c1.getDistanceTo(c1));
	}
}